package com.arka_store.reports.resources;

import lombok.Builder;

@Builder
public record OrderMetricsResponse(
        Long totalOrders,
        Long acceptedOrders,
        Long pendingOrders,
        Double totalSalesAmount,
        Double averageOrderValue,
        BestsellerResponse bestsellers
) {
}
