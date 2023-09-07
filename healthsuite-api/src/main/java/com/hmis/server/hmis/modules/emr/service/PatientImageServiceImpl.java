package com.hmis.server.hmis.modules.emr.service;

import com.hmis.server.hmis.modules.emr.IService.IPatientImageService;
import com.hmis.server.hmis.modules.emr.dto.PatientImageDto;
import com.hmis.server.hmis.modules.emr.model.PatientImage;
import com.hmis.server.hmis.modules.emr.repository.PatientImageRepository;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

@Service
public class PatientImageServiceImpl implements IPatientImageService {

    @Autowired
    PatientImageRepository imageRepository;

    @Override
    public PatientImage savePatientImage(PatientImageDto dto)  {
        try{
            PatientImage patientImage = new PatientImage();
            this.setModel(dto, patientImage);
            return this.imageRepository.save(patientImage);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }


    @Override
    public void updatePatientImage(byte[] imageBase64, long imageId) {
        try{
            PatientImage patientImage = this.getFile(imageId);
            patientImage.setData(imageBase64);
            this.imageRepository.save(patientImage);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public PatientImageDto setPatientImageDto(byte[] passportBase64, String fileName, String type) {
        PatientImageDto imageDto = new PatientImageDto();
        imageDto.setImageBase64(passportBase64);
        imageDto.setFileName(fileName);
        imageDto.setFileType(type);
        return imageDto;
    }

    @Override
    public PatientImage updateFile(MultipartFile file) {
        return null;
    }

    @Override
    public PatientImage getFile(Long id) {
        Optional<PatientImage> image = this.imageRepository.findById(id);
        if (!image.isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Patient Image Not Found");
        }
        return image.get();
    }

    @Override
    public void deleteFile(Long id) {

    }

    @Override
    public void writeFilesToDisk() {

    }

    @Override
    public String uploadFileToDisk(MultipartFile file) {
        return null;
    }

    @Override
    public void removePatientImage(long id) {
        try{
            this.imageRepository.deleteById(id);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void setModel(PatientImageDto dto, PatientImage patientImage){
        if (dto.getImageBase64() != null)
            patientImage.setData(dto.getImageBase64());

        if (dto.getFileName() != null)
            patientImage.setName(dto.getFileName());

        if (dto.getFileType() != null)
            patientImage.setType(dto.getFileType());
    }

}
