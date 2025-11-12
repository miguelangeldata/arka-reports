package com.arka_store.reports.etl;

import com.arka_store.reports.clients.OrderClient;
import com.arka_store.reports.clients.PaymentClient;
import com.arka_store.reports.clients.ProductClient;
import com.arka_store.reports.clients.ShippingClient;
import com.arka_store.reports.models.*;
import com.arka_store.reports.repository.MetricsJpaRepository;
import com.arka_store.reports.repository.OrderMetricsJpaRepository;
import com.arka_store.reports.repository.ProductMetricsJpaRepository;
import com.arka_store.reports.repository.ShippingMetricsJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FetchData {
    private final MetricsJpaRepository repository;
    private final ProductMetricsJpaRepository productMetricsJpaRepository;
    private final PaymentClient paymentClient;
    private final ProductClient productClient;
    private final ShippingClient shippingClient;
    private final ShippingMetricsJpaRepository shippingMetricsJpaRepository;
    private final OrderMetricsJpaRepository orderMetricsJpaRepository;
    private final OrderClient orderClient;

    public void fetchData(){
        MetricsOfPayments metrics=getDataFromPayments();
        repository.save(metrics);
        MetricsOfProducts metricsOfProducts=getDataFromProducts();
        productMetricsJpaRepository.save(metricsOfProducts);
        MetricsForShipping metricsForShipping=getDataFromShipping();
        shippingMetricsJpaRepository.save(metricsForShipping);
        ComprehensiveOrderMetrics orderMetrics=getOrderMetrics();
        orderMetricsJpaRepository.save(orderMetrics);
    }
    private MetricsOfPayments getDataFromPayments(){
        return paymentClient.getMetricsOfPayments();
    }
    private MetricsOfProducts getDataFromProducts(){
        return productClient.getMetricsOfProducts();
    }
    private MetricsForShipping getDataFromShipping(){
        return shippingClient.getMetricsOfShipping();
    }
    private ComprehensiveOrderMetrics getOrderMetrics(){
        return  orderClient.getOrderMetrics();
    }
}
