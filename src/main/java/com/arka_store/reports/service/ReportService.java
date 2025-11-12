package com.arka_store.reports.service;

import com.arka_store.reports.clients.ProductClient;
import com.arka_store.reports.models.*;
import com.arka_store.reports.repository.MetricsJpaRepository;
import com.arka_store.reports.repository.OrderMetricsJpaRepository;
import com.arka_store.reports.repository.ProductMetricsJpaRepository;
import com.arka_store.reports.repository.ShippingMetricsJpaRepository;
import com.arka_store.reports.resources.BestsellerResponse;
import com.arka_store.reports.resources.OrderMetricsResponse;
import com.arka_store.reports.resources.ReportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ProductClient productClient;
    private final OrderMetricsJpaRepository orderMetricsJpaRepository;
    private final MetricsJpaRepository metricsOfPaymentsJpa;
    private final ProductMetricsJpaRepository productMetricsJpaRepository;
    private final ShippingMetricsJpaRepository shippingMetricsJpaRepository;

    public ReportResponse createReport(){
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

        return ReportResponse.builder()
                .reportGeneratedAt(LocalDateTime.now())
                .dataPeriod("Latest Snapshot")
                .orderMetrics(orderMetricsResponse)
                .paymentMetrics(paymentMetrics)
                .shippingMetrics(shippingMetrics)
                .productMetrics(productMetrics)
                .build();

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

}
