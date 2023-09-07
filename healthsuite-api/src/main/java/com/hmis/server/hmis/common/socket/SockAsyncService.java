package com.hmis.server.hmis.common.socket;

import com.hmis.server.hmis.common.common.dto.ServiceUsageEnum;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.service.DepartmentServiceImpl;
import com.hmis.server.hmis.common.common.service.GlobalSettingsImpl;
import com.hmis.server.hmis.common.constant.HmisGlobalSettingKeys;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.billing.dto.BillPatientTypeEnum;
import com.hmis.server.hmis.modules.billing.model.PatientBill;
import com.hmis.server.hmis.modules.billing.model.PatientServiceBillItem;
import com.hmis.server.hmis.modules.clearking.dto.DoctorListWrapper;
import com.hmis.server.hmis.modules.clearking.service.DoctorWaitingListServiceImpl;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.emr.service.PatientRevisitServiceImpl;
import com.hmis.server.hmis.modules.lab.service.LabTestRequestService;
import com.hmis.server.hmis.modules.nurse.service.NurseWaitingListServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.hmis.server.hmis.common.common.dto.ServiceUsageEnum.CONSULTATION;
import static com.hmis.server.hmis.common.constant.HmisGlobalSettingKeys.TRUE;
import static com.hmis.server.hmis.modules.billing.dto.PaymentTypeForEnum.SERVICE;

@Service
public class SockAsyncService {
	private final NurseWaitingListServiceImpl waitingListService;
	private final DepartmentServiceImpl departmentService;
	private final PatientRevisitServiceImpl revisitService;
	private final GlobalSettingsImpl globalSettingsService;
	private final DoctorWaitingListServiceImpl doctorWaitingListService;
	private final LabTestRequestService labTestRequestService;


	public SockAsyncService(
			NurseWaitingListServiceImpl waitingListService,
			DepartmentServiceImpl departmentService,
			@Lazy PatientRevisitServiceImpl revisitService,
			GlobalSettingsImpl globalSettingsService,
			DoctorWaitingListServiceImpl doctorWaitingListService,
			LabTestRequestService labTestRequestService
	) {
		this.waitingListService = waitingListService;
		this.departmentService = departmentService;
		this.revisitService = revisitService;
		this.globalSettingsService = globalSettingsService;
		this.doctorWaitingListService = doctorWaitingListService;
		this.labTestRequestService = labTestRequestService;
	}


	@Async
	@Transactional
	public void prepNurseWaitingListAfterBillPayment(
			PatientBill patientBill,
			String cashPointName, String receiptNumber
	) {
		List<Long> clinicIds = this.departmentService.findMatchingClinicId( cashPointName );
		if ( clinicIds != null && !clinicIds.isEmpty() ) {
			if ( patientBill.getIsPaid() ) {
				if ( patientBill.getBillTypeEnum().equals( SERVICE ) ) {
					List<PatientServiceBillItem> billItems = patientBill.getPatientServiceBillItems();
					boolean hasConsultation = false;
					if ( billItems.size() > 0 ) {
						for ( PatientServiceBillItem e : billItems ) {
							if ( ObjectUtils.isNotEmpty( e.getProductService().getUsedFor() ) ) {
								hasConsultation = ServiceUsageEnum.valueOf( e.getProductService().getUsedFor() ).equals( CONSULTATION );
							}
						}
					}
					BillPatientTypeEnum patientTypeEnum = BillPatientTypeEnum.getBillPatientTypeEnum( patientBill.getBillPatientType() );
					if ( patientTypeEnum.equals( BillPatientTypeEnum.REGISTERED ) ) {
						if ( hasConsultation ) {
							PatientDetail patient = patientBill.getPatient();
							// get patient revisit status
							boolean isActive = this.revisitService.isPatientVisitActive( patient.getId() );
							if ( isActive ) {
								this.waitingListService.addPatientToWaitingList( patient, clinicIds );
							}
							else {
								String value = this.globalSettingsService.findValueByKey( HmisGlobalSettingKeys.ENABLE_AUTO_REVISIT );
								if ( value.equals( TRUE ) ) {
									// auto revisit patient
									boolean isVisited = this.revisitService.prepPatientAutoRevisit( patient, clinicIds.get( 0 ) );
									if ( isVisited ) {
										this.waitingListService.addPatientToWaitingList( patient, clinicIds );
									}
								}
							}
						}
					}
				}
			}

			this.checkLabTestBillPayment( patientBill, receiptNumber );
		}
	}

	@Async
	@Transactional
	public void addToDoctorWaitingList( Long patientId, Long clinicId, Optional<Long> doctorId ) {
		DoctorListWrapper model = new DoctorListWrapper();
		model.setPatient( new PatientDetail( patientId ) );
		model.setClinic( new Department( clinicId ) );
		doctorId.ifPresent( aLong -> model.setDoctor( new User( aLong ) ) );
		this.doctorWaitingListService.addToWaitingList( model );
	}

	public void checkLabTestBillPayment( PatientBill patientBill, String receiptNumber ) {
		this.labTestRequestService.updateLabTestReceiptNumber( patientBill.getInvoiceNumber(), patientBill.getReceiptNumber() );
	}


	public void closeThread() {
	}


}
