package com.arka_store.reports.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter

public class ReportCreatedEvent {
    private String id= UUID.randomUUID().toString();
    private String toEmail;
    private String csvReportURl;
    private String pdfReportUrl;

    public ReportCreatedEvent(String pdfReportUrl, String csvReportURl, String toEmail) {
        this.pdfReportUrl = pdfReportUrl;
        this.csvReportURl = csvReportURl;
        this.toEmail = toEmail;
    }
}
