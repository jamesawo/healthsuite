package com.hmis.server.hmis.modules.billing.iservice;

import com.hmis.server.hmis.common.common.model.RevenueDepartment;
import com.hmis.server.hmis.modules.billing.dto.*;
import com.hmis.server.hmis.modules.billing.model.PatientBill;
import com.hmis.server.hmis.modules.billing.model.SchemeBill;
import com.hmis.server.hmis.modules.emr.dto.PatientCategoryEnum;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import java.util.List;

public interface IBillService {

	PatientBill findById(Long id);

    PatientBill findOneByBillNumber(String billNumber);

    BillInvoiceDto createBill(PatientBillDto dto);

	boolean isBillRegisteredPatientType( PatientBillDto dto );

	boolean isBillForSchemePatient( PatientCategoryEnum billCategoryType, Long id );

	//	PatientBill createBillForUnBilledItem(PatientBillDto dto);

	SchemeBill addBillToScheme(PatientBill savedBill, PatientCategoryEnum categoryEnum);

	SearchPatientBillDto findPatientBillsWithDepositSum(String searchTerm, BillPaymentSearchByEnum searchBy);

	List< PatientBillDto > findBillsByBillNumber(String invoiceNumber, boolean loadDeposit);

	List< PatientBillDto > searchPatientBills(String searchTerm, BillPaymentSearchByEnum searchBy, boolean loadDeposit);

	List< PatientBillDto > findBillsByPatient(PatientDetail patientDetail, boolean loadDeposit);

	List< BillItemDto > getServiceBillItemsFromBill(PatientBill serviceBill);

	PaymentReceiptDto makeBillPayment(BillPaymentDto paymentDto);

	PatientBillDto mapServiceBillToDto(PatientBill serviceBill);

    PatientBillDto mapPatientBillToDto(PatientBill model);

	double getBillTotalByRevenueDepartment(RevenueDepartment revenueDepartment, PatientBill bill);
}
