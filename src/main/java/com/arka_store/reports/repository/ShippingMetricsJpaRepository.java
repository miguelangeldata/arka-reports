package com.arka_store.reports.repository;

import com.arka_store.reports.models.MetricsForShipping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShippingMetricsJpaRepository extends JpaRepository<MetricsForShipping,Long> {
}
