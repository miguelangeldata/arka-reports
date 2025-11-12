package com.arka_store.reports.resources;

import com.arka_store.reports.models.MetricsForShipping;
import com.arka_store.reports.models.MetricsOfPayments;
import com.arka_store.reports.models.MetricsOfProducts;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReportResponse(

        LocalDateTime reportGeneratedAt,
        String dataPeriod,
        OrderMetricsResponse orderMetrics,
        MetricsOfPayments paymentMetrics,
        MetricsForShipping shippingMetrics,
        MetricsOfProducts productMetrics
) {}