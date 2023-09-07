package com.hmis.server.hmis.modules.billing.iservice;

import com.hmis.server.hmis.common.common.model.PaymentMethod;
import com.hmis.server.hmis.common.common.model.RevenueDepartment;
import com.hmis.server.hmis.modules.billing.model.PatientBill;
import com.hmis.server.hmis.modules.billing.model.PatientPayment;

import java.time.LocalDate;
import java.util.List;

public interface IPatientPaymentService {
    double getTotalAmountForBillByRevenueDepartment(PatientBill bill, RevenueDepartment revenueDepartment);
    PatientPayment findById(Long id);
	PatientPayment savePatientPayment(PatientPayment patientPayment);
}
