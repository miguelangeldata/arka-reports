package com.arka_store.reports.service;

import com.arka_store.reports.cases.ReportPublisher;
import com.arka_store.reports.cases.StorageService;
import com.arka_store.reports.clients.ProductClient;
import com.arka_store.reports.events.ReportCreatedEvent;
import com.arka_store.reports.models.*;
import com.arka_store.reports.repository.MetricsJpaRepository;
import com.arka_store.reports.repository.OrderMetricsJpaRepository;
import com.arka_store.reports.repository.ProductMetricsJpaRepository;
import com.arka_store.reports.repository.ShippingMetricsJpaRepository;
import com.arka_store.reports.resources.BestsellerResponse;
import com.arka_store.reports.resources.OrderMetricsResponse;
import com.arka_store.reports.resources.ReportResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final ProductClient productClient;
    private final OrderMetricsJpaRepository orderMetricsJpaRepository;
    private final MetricsJpaRepository metricsOfPaymentsJpa;
    private final ProductMetricsJpaRepository productMetricsJpaRepository;
    private final ShippingMetricsJpaRepository shippingMetricsJpaRepository;
    private final ReportGenerateService reportGenerateService;
    private final StorageService storageService;
    private final ReportPublisher reportPublisher;

    private static final String BUCKET_NAME = "arka-metrics-reports";

    public ReportResponse createReport(String destinationEmail) throws IOException {
        ComprehensiveOrderMetrics orderMetrics = getComprehensiveOrder();
        MetricsForShipping shippingMetrics = getMetricsForShipping();
        MetricsOfPayments paymentMetrics = getMetricsOfPayments();
        MetricsOfProducts productMetrics = getMetricsOfProducts();

        Long mostSoldProductId = orderMetrics.getBestsellers().getMostSoldProductId();
        Long leastSoldProductId = orderMetrics.getBestsellers().getLeastSoldProductId();

        String nameOfMostSellerProduct = productInfo(mostSoldProductId);

        String nameOfLeastSellerProduct = productInfo(leastSoldProductId);
        BestsellerResponse bestsellerResponse = buildBestsellerResponse(
                orderMetrics.getBestsellers(),
                nameOfMostSellerProduct,
                nameOfLeastSellerProduct
        );
        OrderMetricsResponse orderMetricsResponse = buildOrderMetricsResponse(orderMetrics, bestsellerResponse);

        ReportResponse reportResponse= ReportResponse.builder()
                .reportGeneratedAt(LocalDateTime.now())
                .dataPeriod("Latest Snapshot")
                .orderMetrics(orderMetricsResponse)
                .paymentMetrics(paymentMetrics)
                .shippingMetrics(shippingMetrics)
                .productMetrics(productMetrics)
                .build();
        String baseFilename = fileNameGenerator(reportResponse);
        File csvFile=generateReportCsv(reportResponse);
        File pdfFile=generateReportPdf(reportResponse);
        try {
            List<String> urls = uploadFilesToS3(csvFile, pdfFile, baseFilename);
            log.info("Report was Successfully generatedURLs: {}", urls);
            String csvUrl = urls.get(0);
            String pdfUrl = urls.get(1);
            ReportCreatedEvent reportCreatedEvent=new ReportCreatedEvent(pdfUrl,csvUrl,destinationEmail);
            publishEvent(reportCreatedEvent);

        } catch (Exception e) {
            log.error("Error to upload to S3/LocalStack", e);
            throw new RuntimeException("Error to Upload", e);
        } finally {
            cleanupTempFiles(csvFile, pdfFile);
        }
        return reportResponse;

    }
    private String productInfo(Long productId){
        return productClient.getProductInfo(productId);
    }
    private ComprehensiveOrderMetrics getComprehensiveOrder(){
        return orderMetricsJpaRepository.findAll().getLast();
    }
    private MetricsForShipping getMetricsForShipping(){
        return shippingMetricsJpaRepository.findAll().getLast();
    }
    private MetricsOfPayments getMetricsOfPayments(){
        return metricsOfPaymentsJpa.findAll().getLast();
    }
    private MetricsOfProducts getMetricsOfProducts(){
        return productMetricsJpaRepository.findAll().getLast();
    }
    private BestsellerResponse buildBestsellerResponse(
            BestSellerMetrics metrics,
            String mostSoldName,
            String leastSoldName) {
        return BestsellerResponse.builder()
                .mostSoldProductId(metrics.getMostSoldProductId())
                .mostSoldProductName(mostSoldName)
                .mostSoldCount(metrics.getMostSoldCount())
                .leastSoldProductId(metrics.getLeastSoldProductId())
                .leastSoldProductName(leastSoldName)
                .leastSoldCount(metrics.getLeastSoldCount())
                .build();

    }

    private OrderMetricsResponse buildOrderMetricsResponse(
            ComprehensiveOrderMetrics metrics,
            BestsellerResponse bestsellers) {
        return OrderMetricsResponse.builder()
                .totalOrders(metrics.getTotalOrders())
                .acceptedOrders(metrics.getAcceptedOrders())
                .pendingOrders(metrics.getPendingOrders())
                .totalSalesAmount(metrics.getTotalSalesAmount())
                .averageOrderValue(metrics.getAverageOrderValue())
                .bestsellers(bestsellers)
                .build();
    }
    private File generateReportCsv(ReportResponse report) throws IOException {
        log.info("generating report csv");
        String baseFilename=fileNameGenerator(report);
        return reportGenerateService.generateCsv(report,baseFilename);
    }
    private File generateReportPdf(ReportResponse report) throws IOException {
        log.info("generating report pdf");
        String baseFilename=fileNameGenerator(report);
        return reportGenerateService.generatePdf(report,baseFilename);
    }
    private String fileNameGenerator(ReportResponse report){
        String timestamp = report.reportGeneratedAt().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return timestamp + "_metrics_report";
    }
    private List<String> uploadFilesToS3(File csvFile, File pdfFile, String baseFilename) throws Exception {
        List<String>urls=new ArrayList<>();
        String csvKey = baseFilename + ".csv";
        String pdfKey = baseFilename + ".pdf";

        String csvUrl = storageService.uploadFile(csvFile, BUCKET_NAME, csvKey);
        String pdfUrl = storageService.uploadFile(pdfFile, BUCKET_NAME, pdfKey);
        log.info("Archivos subidos a S3/LocalStack. CSV URL: {}", csvUrl);
        urls.add(csvUrl);
        urls.add(pdfUrl);
        return urls;

    }
    private void cleanupTempFiles(File... files) {
        for (File file : files) {
            if (file != null && file.exists()) {
                if (file.delete()) {
                    log.debug("Archivo temporal eliminado: {}", file.getName());
                } else {
                    log.warn("No se pudo eliminar archivo temporal: {}", file.getName());
                }
            }
        }
    }
    private void publishEvent(ReportCreatedEvent event){
        reportPublisher.sendReportEvent(event);
    }

}
