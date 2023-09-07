package com.hmis.server.hmis.modules.emr.service;

import com.hmis.server.hmis.common.common.dto.MessageDto;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.service.HmisUtilService;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.common.constant.HmisExceptionMessage;
import com.hmis.server.hmis.common.exception.HmisApplicationException;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.clearking.service.PatientClerkActivityService;
import com.hmis.server.hmis.modules.emr.IService.IPatientTransferDetailService;
import com.hmis.server.hmis.modules.emr.dto.PatientReferralDto;
import com.hmis.server.hmis.modules.emr.dto.PatientTransferDetailDto;
import com.hmis.server.hmis.modules.emr.model.PatientClinicReferral;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.emr.model.PatientTransferDetail;
import com.hmis.server.hmis.modules.emr.repository.PatientClinicReferralRepository;
import com.hmis.server.hmis.modules.emr.repository.PatientTransferDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Service
public class PatientTransferDetailServiceImpl implements IPatientTransferDetailService {

    private final PatientTransferDetailRepository patientTransferDetailRepository;
    private final PatientClinicReferralRepository patientClinicReferralRepository;
    private final HmisUtilService utilService;
    private final PatientClerkActivityService patientClerkActivityService;

    @Autowired
    public PatientTransferDetailServiceImpl(
            PatientTransferDetailRepository patientTransferDetailRepository,
            PatientClinicReferralRepository patientClinicReferralRepository,
            HmisUtilService utilService,
            PatientClerkActivityService patientClerkActivityService
    ) {
        this.patientTransferDetailRepository = patientTransferDetailRepository;
        this.patientClinicReferralRepository = patientClinicReferralRepository;
        this.utilService = utilService;
        this.patientClerkActivityService = patientClerkActivityService;
    }

    @Override
    public PatientTransferDetail createPatientTransferDetail(PatientTransferDetailDto dto) {
        PatientTransferDetail detail = new PatientTransferDetail();
        this.setModel(dto, detail);
        return patientTransferDetailRepository.save(detail);
    }

    @Override
    public PatientTransferDetail updatePatientTransferDetail(PatientTransferDetailDto dto) {
        if (!dto.getId().isPresent()){
            throw new HmisApplicationException(HmisExceptionMessage.ID_NOT_PROVIDED + "patient transfer details");
        }
        PatientTransferDetail detail = this.findPatientTransferDetail(dto.getId().get());
        this.setModel(dto, detail);
        return patientTransferDetailRepository.save(detail);
    }

    private void setModel(PatientTransferDetailDto dto, PatientTransferDetail detail) {
        if (dto.getReasonForTransfer() != null)
            detail.setReasonForTransfer(dto.getReasonForTransfer());

        if (dto.getHospitalTransferFrom() != null)
            detail.setHospitalTransferFrom(dto.getHospitalTransferFrom());

        if (dto.getHospitalAddress() != null)
            detail.setHospitalAddress(dto.getHospitalAddress());

		if ( dto.getDateOfTransfer() != null ) {
			detail.setDateOfTransfer( this.utilService.transformToLocalDate( dto.getDateOfTransfer() ) );
		}

        if (dto.getAuthorizedDoctor() != null)
            detail.setAuthorizedDoctor(dto.getAuthorizedDoctor());
    }

    @Override
    public PatientTransferDetail findPatientTransferDetail(Long id) {
        Optional<PatientTransferDetail> detail = this.patientTransferDetailRepository.findById(id);
        if (!detail.isPresent()){
            throw new HmisApplicationException(HmisExceptionMessage.NOTHING_FOUND);
        }
        return detail.get();
    }

    @Override
    public void removePatientTransferDetail(Long id) {
        try{
            this.patientTransferDetailRepository.deleteById(id);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public ResponseEntity<MessageDto> referPatientToClinic(PatientReferralDto dto){
        this.validatePatientReferral(dto);
        try {
            PatientClinicReferral referral = new PatientClinicReferral();
            referral.setPatient(new PatientDetail(dto.getPatient().getPatientId()));
            referral.setReferredBy(new User(dto.getReferredBy().getId().get()));
            referral.setReferredToClinic(new Department(dto.getReferredToClinic().getId().get()));
            referral.setReferredFromClinic(new Department(dto.getReferredFromClinic().getId().get()));
            referral.setDate(LocalDate.now());
            referral.setTime(LocalTime.now());
            referral.setReferralNotes(dto.getReferralNotes());
            PatientClinicReferral patientClinicReferral = this.patientClinicReferralRepository.save(referral);
            this.patientClerkActivityService.saveClinicTransfer(patientClinicReferral);
            return ResponseEntity.status(HttpStatus.OK).body(new MessageDto(HmisConstant.SUCCESS_MESSAGE));
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void validatePatientReferral(PatientReferralDto dto) {
        if (dto.getPatient() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Patient is required");
        }
        if (dto.getReferredToClinic() ==  null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Referred to Clinic is required");
        }
        if (dto.getReferredBy() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Referred by is required");
        }
        if (dto.getReferredFromClinic() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Referred from clinic is required");
        }
        if (dto.getReferralNotes() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Referral Note is required");
        }
    }

}
