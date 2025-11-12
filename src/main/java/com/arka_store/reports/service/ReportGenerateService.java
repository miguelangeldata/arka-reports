package com.arka_store.reports.service;

import com.arka_store.reports.cases.ReportGenerator;
import com.arka_store.reports.events.ReportCreatedEvent;
import com.arka_store.reports.resources.ReportResponse;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Service
public class ReportGenerateService implements ReportGenerator {
    private static final Path REPORT_OUTPUT_PATH = Paths.get(System.getProperty("user.dir"), "generated-reports");
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(REPORT_OUTPUT_PATH);
            System.out.println("✅ Report directory initialized at: " + REPORT_OUTPUT_PATH.toAbsolutePath());
        } catch (IOException e) {

            throw new RuntimeException("FATAL ERROR: Failed to initialize report output directory.", e);
        }
    }
    @Override
    public File generateCsv(ReportResponse metrics, String filename) throws IOException {

        String finalFilename = filename.endsWith(".csv") ? filename : filename + ".csv";
        Path tempPath = Paths.get(String.valueOf(REPORT_OUTPUT_PATH), finalFilename);
        File csvFile = tempPath.toFile();

        try (FileWriter writer = new FileWriter(csvFile)) {

            writer.append("Metric,Value\n");
            writer.append("Report Generated At,").append(metrics.reportGeneratedAt().toString()).append("\n");
            writer.append("Data Period,").append(metrics.dataPeriod()).append("\n");

            writer.append("\nOrder Metrics\n");
            writer.append("Total Orders,").append(String.valueOf(metrics.orderMetrics().totalOrders())).append("\n");
            writer.append("Accepted Orders,").append(String.valueOf(metrics.orderMetrics().acceptedOrders())).append("\n");
            writer.append("Pending Orders,").append(String.valueOf(metrics.orderMetrics().pendingOrders())).append("\n");
            writer.append("Total Sales Amount,").append(String.format("%.2f", metrics.orderMetrics().totalSalesAmount())).append("\n");
            writer.append("Average Order Value,").append(String.format("%.2f", metrics.orderMetrics().averageOrderValue())).append("\n");
            writer.append("Best-Selling Product (Name),").append(cleanProductName(metrics.orderMetrics().bestsellers().mostSoldProductName())).append("\n");
            writer.append("Best-Selling Product (Count),").append(String.valueOf(metrics.orderMetrics().bestsellers().mostSoldCount())).append("\n");
            writer.append("Least-Selling Product (Name),").append(cleanProductName(metrics.orderMetrics().bestsellers().leastSoldProductName())).append("\n");
            writer.append("Least-Selling Product (Count),").append(String.valueOf(metrics.orderMetrics().bestsellers().leastSoldCount())).append("\n");
            writer.append("\nPayment Metrics\n");
            writer.append("Total Payments Processed,").append(String.valueOf(metrics.paymentMetrics().getTotal())).append("\n");
            writer.append("Accepted Payments,").append(String.valueOf(metrics.paymentMetrics().getTotalAccepted())).append("\n");
            writer.append("Canceled Payments,").append(String.valueOf(metrics.paymentMetrics().getTotalCanceled())).append("\n");
            writer.append("Declined Payments,").append(String.valueOf(metrics.paymentMetrics().getTotalDeclined())).append("\n");
            writer.append("Max Payment Amount,").append(String.format("%.2f", metrics.paymentMetrics().getMax())).append("\n");
            writer.append("Min Payment Amount,").append(String.format("%.2f", metrics.paymentMetrics().getMin())).append("\n");
            writer.append("Average Payment Amount,").append(String.format("%.2f", metrics.paymentMetrics().getAvg())).append("\n");
            writer.append("Payments by Credit Card,").append(String.valueOf(metrics.paymentMetrics().getTotalPaymentByCreditCard())).append("\n");
            writer.append("Payments by PayPal,").append(String.valueOf(metrics.paymentMetrics().getTotalPaymentByPaypal())).append("\n");
            writer.append("Payments by PSE,").append(String.valueOf(metrics.paymentMetrics().getTotalByPse())).append("\n");


            writer.append("\nShipping Metrics\n");
            writer.append("Total Shipments Registered,").append(String.valueOf(metrics.shippingMetrics().getTotalOfShipping())).append("\n");
            writer.append("Shipments Sent,").append(String.valueOf(metrics.shippingMetrics().getTotalSend())).append("\n");
            writer.append("Shipments Received,").append(String.valueOf(metrics.shippingMetrics().getTotalReceived())).append("\n");
            writer.append("Shipments Returned,").append(String.valueOf(metrics.shippingMetrics().getTotalReturned())).append("\n");


            writer.append("\nProduct Metrics\n");
            writer.append("Registered Products,").append(String.valueOf(metrics.productMetrics().getProductsRegistered())).append("\n");
            writer.append("Unique Categories,").append(String.valueOf(metrics.productMetrics().getCategories())).append("\n");

            writer.flush();

        } catch (IOException e) {
            throw new IOException("Failed to generate CSV file.", e);
        }

        return csvFile;
    }

    @Override
    public File generatePdf(ReportResponse metrics, String filename) throws IOException {
        String finalFilename = filename.endsWith(".pdf") ? filename : filename + ".pdf";
        Path tempPath = Paths.get(String.valueOf(REPORT_OUTPUT_PATH) ,finalFilename);
        File pdfFile = tempPath.toFile();

        Document document = new Document();
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.DARK_GRAY);
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);

        try {
            PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();
            document.add(new Paragraph("System Metrics Report", titleFont));
            document.add(new Paragraph("Generated At: " + metrics.reportGeneratedAt() + " | Data Period: " + metrics.dataPeriod()));
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("Order Metrics", sectionFont));
            document.add(createMetricsTable(
                    new String[][]{
                            {"Total Orders", String.valueOf(metrics.orderMetrics().totalOrders())},
                            {"Accepted Orders", String.valueOf(metrics.orderMetrics().acceptedOrders())},
                            {"Pending Orders", String.valueOf(metrics.orderMetrics().pendingOrders())},
                            {"Total Sales Amount", String.format("$%.2f", metrics.orderMetrics().totalSalesAmount())},
                            {"Average Order Value", String.format("$%.2f", metrics.orderMetrics().averageOrderValue())},
                            {"Best-Selling Product", cleanProductName(metrics.orderMetrics().bestsellers().mostSoldProductName()) + " (x" + metrics.orderMetrics().bestsellers().mostSoldCount() + ")"},
                            {"Least-Selling Product", cleanProductName(metrics.orderMetrics().bestsellers().leastSoldProductName()) + " (x" + metrics.orderMetrics().bestsellers().leastSoldCount() + ")"}
                    }
            ));
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("Payment Metrics", sectionFont));
            document.add(createMetricsTable(
                    new String[][]{
                            {"Total Payments Processed", String.valueOf(metrics.paymentMetrics().getTotal())},
                            {"Accepted Payments", String.valueOf(metrics.paymentMetrics().getTotalAccepted())},
                            {"Canceled Payments", String.valueOf(metrics.paymentMetrics().getTotalCanceled())},
                            {"Declined Payments", String.valueOf(metrics.paymentMetrics().getTotalDeclined())},
                            {"Max Payment Amount", String.format("$%.2f", metrics.paymentMetrics().getMax())},
                            {"Min Payment Amount", String.format("$%.2f", metrics.paymentMetrics().getMin())},
                            {"Average Payment Amount", String.format("$%.2f", metrics.paymentMetrics().getAvg())},
                            {"Payments by Credit Card", String.valueOf(metrics.paymentMetrics().getTotalPaymentByCreditCard())},
                            {"Payments by PayPal", String.valueOf(metrics.paymentMetrics().getTotalPaymentByPaypal())},
                            {"Payments by PSE", String.valueOf(metrics.paymentMetrics().getTotalByPse())}
                    }
            ));
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("Shipping Metrics", sectionFont));
            document.add(createMetricsTable(
                    new String[][]{
                            {"Total Shipments Registered", String.valueOf(metrics.shippingMetrics().getTotalOfShipping())},
                            {"Shipments Sent", String.valueOf(metrics.shippingMetrics().getTotalSend())},
                            {"Shipments Received", String.valueOf(metrics.shippingMetrics().getTotalReceived())},
                            {"Shipments Returned", String.valueOf(metrics.shippingMetrics().getTotalReturned())}
                    }
            ));
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("Product Metrics", sectionFont));
            document.add(createMetricsTable(
                    new String[][]{
                            {"Registered Products", String.valueOf(metrics.productMetrics().getProductsRegistered())},
                            {"Unique Categories", String.valueOf(metrics.productMetrics().getCategories())}
                    }
            ));
            document.add(Chunk.NEWLINE);


            document.close();

        } catch (DocumentException | FileNotFoundException e) {
            throw new IOException("Failed to create the PDF document using iText.", e);
        }

        return pdfFile;
    }

    private PdfPTable createMetricsTable(String[][] data) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(5f);
        Stream.of("Metric", "Value").forEach(headerTitle -> {
            PdfPCell header = new PdfPCell();
            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
            header.setBorderWidth(1);
            header.setPhrase(new Phrase(headerTitle, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            table.addCell(header);
        });
        for (String[] row : data) {
            table.addCell(row[0]);
            table.addCell(row[1]);
        }
        return table;
    }

    private String cleanProductName(String jsonString) {
        if (jsonString == null) return "N/A";
        return jsonString.replaceAll("\\{\"name\":\"|\\\"\\}", "").trim();
    }

}
