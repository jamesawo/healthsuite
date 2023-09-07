package com.hmis.server.hmis.modules.pharmacy.iservice;

import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.modules.pharmacy.model.DrugRegister;

public interface IDrugLowStockService  {
	void addToLowStock(Department outlet, DrugRegister drug);

	void removeLowStock(Department department, DrugRegister drugRegister);
}
