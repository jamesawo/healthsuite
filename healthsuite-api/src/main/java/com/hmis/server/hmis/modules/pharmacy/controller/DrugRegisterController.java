package com.hmis.server.hmis.modules.pharmacy.controller;

import com.hmis.server.hmis.common.common.dto.ResponseDto;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.modules.pharmacy.dto.DrugColumnDto;
import com.hmis.server.hmis.modules.pharmacy.dto.DrugRegisterDto;
import com.hmis.server.hmis.modules.pharmacy.service.DrugRegisterServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(HmisConstant.API_PREFIX + "/drug-register")
public class DrugRegisterController {
    @Autowired
    private DrugRegisterServiceImpl drugRegisterService;

    @PostMapping("create-one")
    public ResponseDto singleDrugRegistration(@Validated @RequestBody DrugRegisterDto drugRegisterDto) {
        try {
            ResponseDto responseDto = new ResponseDto();
            DrugRegisterDto dto = this.drugRegisterService.registerDrug(drugRegisterDto);
            responseDto.setData(dto);
            return responseDto;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

	/*
			@Validated @RequestParam( value = "search" ) String search,
			@RequestParam( "loadAvailableStock" ) boolean loadAvailableStock,
			@RequestParam( "outlet" ) Long outlet,
			@RequestParam( "loadIssOutletStockCount" ) boolean loadIssOutletStockCount,
			@RequestParam( "issuingOutletId" ) Long issuingOutletId
	 */

    @GetMapping(value = "search-by-brand-generic-name")
    public List<DrugRegisterDto> searchDrugByBrandOrGenericName(@Validated @RequestParam(value = "search") String search,
                                                                @RequestParam("loadAvailableStock") boolean loadAvailableStock,
                                                                @RequestParam("outlet") Long outlet,
                                                                @RequestParam("loadIssOutletStockCount") boolean loadIssOutletStockCount,
                                                                @RequestParam("issuingOutletId") Long issuingOutletId
    ) {
        return this.drugRegisterService.searchDrugByBrandOrGenericName(search,
                loadAvailableStock,
                outlet,
                loadIssOutletStockCount,
                issuingOutletId);
    }

    // used in clerking tabs
    @GetMapping(value = "search-by-department-filter")
    public List<DrugRegisterDto> searchDrgByDepartmentOutlet(
            @Validated @RequestParam(value = "search") String search,
            @RequestParam(value = "outlet") String outletId,
            @RequestParam(value = "excludeZero") String excludeZeroQuantity) {
        try {

            return this.drugRegisterService.searchByOutletFilter(search, Long.valueOf(outletId), Boolean.valueOf(excludeZeroQuantity));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Input");
        }
    }

    @GetMapping(value = "search-by-generic-name")
    public List<DrugRegisterDto> searchDrugByGenericName(@Validated @RequestParam(value = "search") String search) {
        return this.drugRegisterService.searchByGenericName(search);
    }

    @GetMapping(value = "search-by-brand-name")
    public List<DrugRegisterDto> searchDrugByBrandName(@Validated @RequestParam(value = "search") String search) {
        return this.drugRegisterService.searchByBrandName(search);
    }

    @GetMapping(value = "search-drug-and-filter")
    public List<DrugRegisterDto> searchDrugAndFilter(@Validated @RequestParam(value = "term") String term, @Validated @RequestParam(value = "classificationId") Long classificationId,
                                                     @Validated @RequestParam(value = "formulationId") Long formulationId) {
        return this.drugRegisterService.searchDrugAndFilter(term, classificationId, formulationId);
    }

    @PutMapping(value = "update-one")
    public DrugRegisterDto updateSingleDrug(@Validated @RequestBody DrugRegisterDto dto) {
        return this.drugRegisterService.updateDrugRegister(dto);
    }

    @PatchMapping("update-drug-column")
    public ResponseEntity updateDrugData(@RequestBody DrugColumnDto dto) {
        return this.drugRegisterService.updateByColumnValue(dto);
    }

}
