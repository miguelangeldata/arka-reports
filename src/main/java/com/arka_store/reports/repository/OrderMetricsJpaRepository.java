package com.arka_store.reports.repository;


import com.arka_store.reports.models.ComprehensiveOrderMetrics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderMetricsJpaRepository extends JpaRepository<ComprehensiveOrderMetrics,Long> {
}
