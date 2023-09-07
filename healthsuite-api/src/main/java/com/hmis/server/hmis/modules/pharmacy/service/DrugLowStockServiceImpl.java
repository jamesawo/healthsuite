package com.hmis.server.hmis.modules.pharmacy.service;

import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.modules.pharmacy.iservice.IDrugLowStockService;
import com.hmis.server.hmis.modules.pharmacy.model.DrugLowStock;
import com.hmis.server.hmis.modules.pharmacy.model.DrugRegister;
import com.hmis.server.hmis.modules.pharmacy.repository.DrugLowStockRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DrugLowStockServiceImpl implements IDrugLowStockService {
	@Autowired
	private DrugLowStockRepository drugLowStockRepository;

	@Override
	public void addToLowStock(Department outlet, DrugRegister drug) {
		if( ! this.isItemExist(outlet.getId(), drug.getId()) ) {
			DrugLowStock lowStock = new DrugLowStock();
			lowStock.setOutlet(outlet);
			lowStock.setDrug(drug);
			this.drugLowStockRepository.save(lowStock);
		}
	}

	@Override
	public void removeLowStock(Department department, DrugRegister drugRegister) {
		if( this.isItemExist(department.getId(), drugRegister.getId()) ) {
			this.drugLowStockRepository.deleteByOutletEqualsAndDrugEquals(department, drugRegister);
		}
	}


	private boolean isItemExist(Long outletId, Long drugId){
		Optional< DrugLowStock > optional = this.drugLowStockRepository.findByOutletEqualsAndDrugEquals(new Department(outletId), new DrugRegister(drugId));
		return  optional.isPresent();
	}

}
