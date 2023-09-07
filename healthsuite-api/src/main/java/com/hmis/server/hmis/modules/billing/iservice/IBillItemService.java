package com.hmis.server.hmis.modules.billing.iservice;

import com.hmis.server.hmis.common.common.dto.ProductServiceInvoiceItems;
import com.hmis.server.hmis.modules.billing.dto.BillItemDto;
import com.hmis.server.hmis.modules.billing.model.PatientBill;
import com.hmis.server.hmis.modules.billing.model.PatientServiceBillItem;
import java.util.List;

public interface IBillItemService {

	void createBillItems(BillItemDto dto);

	void addBillItemsToBill(Long billId);

	List< PatientServiceBillItem > getItemsFromBill(Long patientBillId);

	List< ProductServiceInvoiceItems > getItemsFromBill(PatientBill patientBill);

	List< PatientServiceBillItem > createManyBillItem(List< BillItemDto > list, PatientBill bill);

	void removeBills(List< PatientServiceBillItem > items);
}
