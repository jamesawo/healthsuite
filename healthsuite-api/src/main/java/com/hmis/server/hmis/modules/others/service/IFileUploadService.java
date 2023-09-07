package com.hmis.server.hmis.modules.others.service;

import java.util.Map;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IFileUploadService {

    boolean isExcelFile(MultipartFile file);

    Map<String, Integer> handleServicesBatchUpload(MultipartFile file);

	Map< String, Integer > handleDrugBatchUpload(MultipartFile file);

	Resource downloadFile(String filename);
}
