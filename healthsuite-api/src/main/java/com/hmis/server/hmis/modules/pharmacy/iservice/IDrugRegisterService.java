package com.hmis.server.hmis.modules.pharmacy.iservice;

import com.hmis.server.hmis.modules.pharmacy.dto.DrugColumnDto;
import com.hmis.server.hmis.modules.pharmacy.dto.DrugRegisterDto;
import com.hmis.server.hmis.modules.pharmacy.model.DrugRegister;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;

public interface IDrugRegisterService {
	DrugRegisterDto registerDrug(DrugRegisterDto drugRegisterDto);

	Map< String, Integer > registerDrugsInBatchRaw(List< DrugRegister > list);

	boolean isDrugRegisterExist(DrugRegister drugRegister);

	List< DrugRegisterDto > searchDrugByBrandOrGenericName(String term, boolean loadStockCount, Long outletId, boolean loadIssOutletStockCount, Long issuingOutletId);

	List< DrugRegisterDto > searchByGenericName(String term);

	List< DrugRegisterDto > searchByBrandName(String term);

	List< DrugRegisterDto > searchDrugByBrandNameAndClassificationId(String term, Long drugClassificationId);

	List< DrugRegisterDto > searchDrugByBrandNameAndFormulationId(String term, Long formulationId);

	DrugRegisterDto updateDrugRegister(DrugRegisterDto dto);

	List< DrugRegisterDto > searchDrugAndFilter(String term, Long classificationId, Long formulationId);

	DrugRegister findOne(Long id);

	ResponseEntity updateByColumnValue(DrugColumnDto dto);

	List<DrugRegisterDto> searchByOutletFilter(String search, Long outletId, boolean excludeZeroItems);
}
