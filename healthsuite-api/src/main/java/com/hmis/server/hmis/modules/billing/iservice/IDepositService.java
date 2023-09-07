package com.hmis.server.hmis.modules.billing.iservice;

import com.hmis.server.hmis.modules.billing.dto.DepositDto;
import com.hmis.server.hmis.modules.billing.dto.DepositSumDto;
import com.hmis.server.hmis.modules.billing.dto.PaymentReceiptDto;
import com.hmis.server.hmis.modules.billing.model.PatientDepositLog;
import com.hmis.server.hmis.modules.billing.model.PatientDepositSum;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import java.util.Optional;

public interface IDepositService {
	PaymentReceiptDto handlePatientDepositPayment(DepositDto depositDto);

	PatientDepositLog createPatientDepositLog(DepositDto depositDto, PatientDepositSum patientDepositSum);

	// reduce allocated bill amount from deposit total
	void settleFromDepositSum(Double totalBillAllocation, PatientDetail patientDetail);

	// Deposit Main
	PatientDepositSum getOrCreatePatientDepositSum(PatientDetail patientDetail);

	// Deposit Main
	// void addAmountToPatientDeposit(PatientDetail patientDetail, Double amount);

	boolean isPatientDepositExist(PatientDetail patientDetail);

	void updateDepositSum(PatientDetail patientDetail, Double depositAmount);

	DepositSumDto findPatientDepositSum(PatientDetail patient);

	Double findPatientDepositAmount(PatientDetail patientDetail);

	Optional< PatientDepositSum > findDepositSumByPatientDetail(PatientDetail patientDetail);
}
