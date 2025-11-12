package com.arka_store.reports.cases;

import com.arka_store.reports.events.ReportCreatedEvent;

public interface ReportPublisher {
    void sendReportEvent(ReportCreatedEvent event);
}
