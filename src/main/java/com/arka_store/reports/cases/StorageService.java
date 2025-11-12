package com.arka_store.reports.cases;

import java.io.File;

public interface StorageService {
    String uploadFile(File file, String bucketName, String key);
}
