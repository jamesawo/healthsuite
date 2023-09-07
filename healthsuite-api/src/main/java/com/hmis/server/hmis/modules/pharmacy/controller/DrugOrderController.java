package com.hmis.server.hmis.modules.pharmacy.controller;

import com.hmis.server.hmis.common.common.dto.ValidationResponse;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.modules.pharmacy.dto.DrugOrderDto;
import com.hmis.server.hmis.modules.pharmacy.service.DrugOrderServiceImpl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping( HmisConstant.API_PREFIX + "/drug-order" )
public class DrugOrderController {
	@Autowired
	private DrugOrderServiceImpl drugOrderService;

	@PostMapping( value = "outlet-drug-order" )
	public ResponseEntity< ValidationResponse > createOutletDrugOrder(@RequestBody DrugOrderDto dto) {
		return this.drugOrderService.createDrugOrder(dto);
	}

	@GetMapping( value = "search" )
	public List< DrugOrderDto > searchDrugOrder(@RequestParam( "search" ) String search) {
		//todo:: add filter to get drug order for specific outlet
		return this.drugOrderService.findDrugOrderByOrderCode(search);
	}

	@PatchMapping( value = "update-order" )
	public ResponseEntity< ValidationResponse > updateOutletDrugOrder(@RequestBody DrugOrderDto dto) {
		return this.drugOrderService.updateDrugOrder(dto);
	}

}
