package com.hmis.server.hmis.modules.others.controller;

import com.hmis.server.hmis.common.common.dto.ResponseDto;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.modules.others.service.FileUploadServiceImpl;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping( HmisConstant.API_PREFIX + "file/" )
public class FileUploadController {

	@Autowired
	FileUploadServiceImpl fileUploadService;

	@PostMapping( value = "upload/services" )
	public ResponseDto uploadBatchService(@RequestParam( "file" ) MultipartFile file) throws IOException {
		ResponseDto dto = new ResponseDto();
		Map< String, Integer > res = fileUploadService.handleServicesBatchUpload(file);
		dto.setData(res);
		dto.setMessage("Completed");
		return dto;
	}

	@PostMapping( value = "upload/drugs" )
	public ResponseDto uploadDrugList(@RequestParam( "file" ) MultipartFile file) throws IOException {
		try {
			ResponseDto dto = new ResponseDto();
			Map< String, Integer > res = fileUploadService.handleDrugBatchUpload(file);
			dto.setData(res);
			dto.setMessage("Completed");
			return dto;
		}
		catch( Exception e ) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


	@GetMapping( "download/{filename:.+}" )
	@ResponseBody
	public ResponseEntity< Resource > downloadFile(@PathVariable String filename) throws IOException {
		Resource file = this.fileUploadService.downloadFile(filename);
		Path path = file.getFile().toPath();
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(path)).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}

}
