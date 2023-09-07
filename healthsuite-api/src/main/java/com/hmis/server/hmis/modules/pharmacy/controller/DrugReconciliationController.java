package com.hmis.server.hmis.modules.pharmacy.controller;

import com.hmis.server.hmis.common.common.dto.ResponseDto;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.modules.pharmacy.dto.OutletReconcileDto;
import com.hmis.server.hmis.modules.pharmacy.dto.OutletStockItemDto;
import com.hmis.server.hmis.modules.pharmacy.service.OutletReconciliationServiceImpl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(HmisConstant.API_PREFIX + "/drug-reconciliation")
public class DrugReconciliationController {
	@Autowired
	private OutletReconciliationServiceImpl outletReconciliationService;

	@PostMapping(value = "outlet-reconciliation")
	public ResponseDto reconcileStockBalance(@RequestBody OutletReconcileDto dto) {
		ResponseDto res = new ResponseDto();
		this.outletReconciliationService.reconcileOutletStock(dto);
		res.setMessage("Reconciliation Completed Successfully");
		return res;
	}

	@GetMapping(value = "get-stock-balance")
	public OutletStockItemDto getOutletStockBalance(
			@RequestParam(value = "outletId") Long outletId,
			@RequestParam(value = "drugId") Long drugId) {
		return this.outletReconciliationService.getOutletItemStock(outletId, drugId);
	}

	@GetMapping(value = "search-outlet-stock")
	public List<OutletStockItemDto> searchOutletStockBalance(
			@RequestParam(value = "searchTerm") String searchTerm,
			@RequestParam(value = "outletId") Long outletId) {
		return this.outletReconciliationService.searchOutletStockBalance(searchTerm, outletId);
	}

}
