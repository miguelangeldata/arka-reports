package com.arka_store.reports.cases;

import com.arka_store.reports.resources.ReportResponse;

import java.io.File;
import java.io.IOException;

public interface ReportGenerator {
    File generateCsv(ReportResponse metrics, String filename) throws IOException;
    File generatePdf(ReportResponse metrics, String filename) throws IOException;
}