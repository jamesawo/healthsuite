package com.hmis.server.hmis.modules.others.controller;

import com.hmis.server.hmis.common.common.dto.ResponseDto;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.modules.others.dto.VendorDto;
import com.hmis.server.hmis.modules.others.service.VendorServiceImpl;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping( HmisConstant.API_PREFIX +"vendor-management/")
public class VendorController {
	@Autowired
	private VendorServiceImpl vendorService;

	@PostMapping( "register-vendor" )
	public ResponseDto createVendor(@Valid @RequestBody VendorDto vendorDto){
		ResponseDto response =  new ResponseDto();
		VendorDto dto = this.vendorService.create(vendorDto);
		response.setData(dto);
		return response;
	}

	@GetMapping("search-vendor")
	public List< VendorDto > searchVendor(@RequestParam( "search" ) String search) {
		return this.vendorService.findVendorsByNameOrPhone(search, search);
	}

	@PutMapping("update-vendor")
	public ResponseDto updateVendor(@Valid @RequestBody VendorDto vendorDto){
		ResponseDto response =  new ResponseDto();
		VendorDto update = this.vendorService.update(vendorDto);
		response.setData(update);
		return response;

	}

}
