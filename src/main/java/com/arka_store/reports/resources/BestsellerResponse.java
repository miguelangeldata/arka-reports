package com.arka_store.reports.resources;

import lombok.Builder;

@Builder
public record BestsellerResponse(
        Long mostSoldProductId,
        String mostSoldProductName,
        Long mostSoldCount,
        Long leastSoldProductId,
        String leastSoldProductName,
        Long leastSoldCount
) {
}
