package com.arka_store.reports.service;

import com.arka_store.reports.cases.ReportPublisher;
import com.arka_store.reports.events.ReportCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class Publisher implements ReportPublisher {
    private  final StreamBridge streamBridge;
    @Override
    public void sendReportEvent(ReportCreatedEvent event) {
        final String BINDING_NAME = "reportSend-out-0";
        boolean success = streamBridge.send(BINDING_NAME, event);

        if (success) {
            log.info(" Event Shipping Send Publisher: {}", event.getId());
        } else {
            log.error(" Critical Error publishing event: {}", event.getId());
            throw new RuntimeException("Fatal Error.");
        }

    }
}
