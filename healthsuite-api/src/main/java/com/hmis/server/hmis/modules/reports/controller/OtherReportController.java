package com.hmis.server.hmis.modules.reports.controller;

import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.modules.reports.dto.ServiceChargeCategoryEnum;
import com.hmis.server.hmis.modules.reports.service.OtherReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( HmisConstant.API_PREFIX + "/report/other/" )
public class OtherReportController {

	private final OtherReportService otherReportService;

	@Autowired
	public OtherReportController( OtherReportService otherReportService ) {
		this.otherReportService = otherReportService;
	}

	@GetMapping( value = "/service-charge-sheet/{category}/{searchId}" )
	public ResponseEntity<byte[]> downloadServiceChargeSheet(
			@PathVariable String category, @PathVariable( required = false ) String searchId ) {
		ServiceChargeCategoryEnum value = ServiceChargeCategoryEnum.valueOf( category );
		return this.otherReportService.downloadServiceChargeSheetReport( value, searchId );
	}

}
