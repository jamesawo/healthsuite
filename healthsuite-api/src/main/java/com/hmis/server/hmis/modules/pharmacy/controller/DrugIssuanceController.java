package com.hmis.server.hmis.modules.pharmacy.controller;

import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.modules.pharmacy.dto.DrugIssuanceDto;
import com.hmis.server.hmis.modules.pharmacy.service.DrugIssuanceServiceImpl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( HmisConstant.API_PREFIX + "/drug-issuance" )
public class DrugIssuanceController {

	@Autowired
	private DrugIssuanceServiceImpl issuanceService;


	@PostMapping( value = "fetch-by-date" )
	public List findByOutletAndDateRange(@RequestBody DrugIssuanceDto drugIssuanceDto) {
		return this.issuanceService.findByOutletAndDateRange(drugIssuanceDto);
	}
}
