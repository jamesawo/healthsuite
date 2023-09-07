package com.hmis.server.hmis.modules.reports.controller;

import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.modules.reports.dto.DailyCashCollectionSearchDto;
import com.hmis.server.hmis.modules.reports.dto.SchemeConsumptionReportDto;
import com.hmis.server.hmis.modules.reports.service.AccountReportService;
import com.hmis.server.hmis.modules.reports.service.OtherReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping( HmisConstant.API_PREFIX + "/report/account/" )
public class AccountReportController {

	private final AccountReportService accountReportService;
	private final OtherReportService otherReportService;

	@Autowired
	public AccountReportController(
			AccountReportService accountReportService,
			OtherReportService otherReportService ) {
		this.accountReportService = accountReportService;
		this.otherReportService = otherReportService;
	}

	@PostMapping( value = "/daily-cash-collection-pdf" )
	public ResponseEntity<byte[]> fetchDailyCashCollectionReport(
			HttpServletResponse response,
			@RequestBody DailyCashCollectionSearchDto dto ) {
		try {
			byte[] bytes = this.accountReportService.generateDailyCashCollection( dto );
			return ResponseEntity.ok( bytes );
		} catch ( Exception e ) {
			e.printStackTrace();
			throw new RuntimeException( e.getMessage() );
		}
	}
	
	@PostMapping( value = "download-scheme-consumption-pdf" )
	public ResponseEntity<byte[]> getSchemeConsumptionReport( @RequestBody SchemeConsumptionReportDto dto ) {
		return this.otherReportService.downloadSchemeConsumptionReport( dto );
	}


}
