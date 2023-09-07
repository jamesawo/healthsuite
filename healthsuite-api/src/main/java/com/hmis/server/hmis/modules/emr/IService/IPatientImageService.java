package com.hmis.server.hmis.modules.emr.IService;

import com.hmis.server.hmis.modules.emr.dto.PatientImageDto;
import com.hmis.server.hmis.modules.emr.model.PatientImage;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface IPatientImageService {

    PatientImage savePatientImage(PatientImageDto dto);

    PatientImage updateFile(MultipartFile file);

    PatientImage getFile(Long id);

    void deleteFile(Long id);

    void writeFilesToDisk() throws IOException;

    String uploadFileToDisk(MultipartFile file) throws FileUploadException;

    void removePatientImage(long id);

    void updatePatientImage(byte[] imageBase64, long imageId);

	PatientImageDto setPatientImageDto(byte[] passportBase64, String patientNumber, String type);
}
