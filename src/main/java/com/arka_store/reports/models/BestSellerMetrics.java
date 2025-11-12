package com.arka_store.reports.models;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class BestSellerMetrics {
    private Long mostSoldProductId;
    private Long leastSoldProductId;
    private Long mostSoldCount;
    private Long leastSoldCount;
}