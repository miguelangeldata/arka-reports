package com.arka_store.reports.repository;

import com.arka_store.reports.models.MetricsOfProducts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductMetricsJpaRepository extends JpaRepository<MetricsOfProducts,Long> {
}
