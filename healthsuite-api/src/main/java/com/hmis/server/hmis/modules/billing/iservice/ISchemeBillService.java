package com.hmis.server.hmis.modules.billing.iservice;

import com.hmis.server.hmis.modules.billing.dto.SchemeBillDto;
import com.hmis.server.hmis.modules.billing.model.PatientBill;
import com.hmis.server.hmis.modules.billing.model.SchemeBill;

public interface ISchemeBillService {

	void addBillToScheme(SchemeBillDto dto);

	SchemeBill addBillToScheme(PatientBill savedBill);

	SchemeBill mapPatientBillToBill(PatientBill savedBill);

	void removeBill(SchemeBill schemeBill);
}
