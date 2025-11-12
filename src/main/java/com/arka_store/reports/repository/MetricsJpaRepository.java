package com.arka_store.reports.repository;

import com.arka_store.reports.models.MetricsOfPayments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MetricsJpaRepository extends JpaRepository<MetricsOfPayments,Long> {

}
