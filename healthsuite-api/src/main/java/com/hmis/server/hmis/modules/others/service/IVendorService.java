package com.hmis.server.hmis.modules.others.service;

import com.hmis.server.hmis.modules.others.dto.VendorDto;
import com.hmis.server.hmis.modules.others.model.Vendor;
import java.util.List;

public interface IVendorService {
	Vendor findOne(Long id);

	VendorDto create(VendorDto vendorDto);

	VendorDto update(VendorDto dto);

	List<VendorDto> findVendorsByNameOrPhone(String name, String phone);
}
