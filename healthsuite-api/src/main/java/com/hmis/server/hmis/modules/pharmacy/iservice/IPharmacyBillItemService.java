package com.hmis.server.hmis.modules.pharmacy.iservice;

import com.hmis.server.hmis.common.common.dto.ProductServiceInvoiceItems;
import com.hmis.server.hmis.modules.billing.model.PatientBill;
import com.hmis.server.hmis.modules.pharmacy.dto.PharmacyBillItemDto;
import com.hmis.server.hmis.modules.pharmacy.model.PharmacyBillItem;
import java.util.List;

public interface IPharmacyBillItemService {
	List< PharmacyBillItem > createMany(List< PharmacyBillItemDto > billItemDtoList, PatientBill patientBill);

	void removeMany(List< PharmacyBillItem > pharmacyBillItems);

	List< ProductServiceInvoiceItems > getItemsFromBill(PatientBill patientBill);

	List< PharmacyBillItemDto > getPharmacyBillItemsFromBill(PatientBill patientBill);
}
