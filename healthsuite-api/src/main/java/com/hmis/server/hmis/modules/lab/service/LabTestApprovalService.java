package com.hmis.server.hmis.modules.lab.service;

import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.lab.model.LabTestPathologistVerification;
import com.hmis.server.hmis.modules.lab.model.LabTestRequestItem;
import com.hmis.server.hmis.modules.lab.model.LabTestVerification;
import com.hmis.server.hmis.modules.lab.repository.LabTestPathologistVerificationRepository;
import com.hmis.server.hmis.modules.lab.repository.LabTestVerificationRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LabTestApprovalService {
    private final LabTestVerificationRepository verificationRepository;
    private final LabTestTrackerService trackerService;
    private final LabTestPathologistVerificationRepository pathologistVerificationRepository;

    public LabTestApprovalService( LabTestVerificationRepository verificationRepository, LabTestTrackerService trackerService, LabTestPathologistVerificationRepository pathologistVerificationRepository ) {
        this.verificationRepository = verificationRepository;
        this.trackerService = trackerService;
        this.pathologistVerificationRepository = pathologistVerificationRepository;
    }

    public Optional<LabTestVerification> getLabTestVerification( LabTestRequestItem labTestRequestItem ) {
        return this.verificationRepository.findByLabTestRequestItem( labTestRequestItem );
    }

    public void setLabTestVerification(
            LabTestRequestItem item,
            User approvedBy, Department approvedFrom, String note ) {
        Optional<LabTestVerification> optional = this.getLabTestVerification( item );
        LabTestVerification model = new LabTestVerification();
        optional.ifPresent( value -> model.setId( value.getId() ) );
        model.setLabTestRequestItem( item );
        model.setCapturedFrom( approvedFrom );
        model.setIsTestApproved( true );
        model.setTestApprovedBy( approvedBy );
        model.setApprovalLabNote( note );
        LabTestVerification verification = this.verificationRepository.save( model );
        this.trackerService.saveTestVerification( item.getLabTestRequest(), verification );
    }

    public void setLabTestPathologistVerification(
            LabTestRequestItem item,
            User approvedBy, Department approvedFrom, String note,
            String pathologistComment
    ) {
        Optional<LabTestPathologistVerification> optional = this.findLabTestPathologistVerification( item );
        LabTestPathologistVerification pathologistVerification = new LabTestPathologistVerification();
        optional.ifPresent( value -> pathologistVerification.setId( value.getId() ) );
        pathologistVerification.setLabTestRequestItem( item );
        pathologistVerification.setLabNote( note );
        pathologistVerification.setComment( pathologistComment );
        pathologistVerification.setCapturedFrom( approvedFrom );
        pathologistVerification.setApprovedByPathologist( approvedBy );
        pathologistVerification.setIsPathologistApproved( true );
        LabTestPathologistVerification save = this.pathologistVerificationRepository.save( pathologistVerification );
        this.trackerService.saveTestPathologist( item.getLabTestRequest(), save );
    }

    public Optional<LabTestPathologistVerification> findLabTestPathologistVerification( LabTestRequestItem requestItem ) {
        return this.pathologistVerificationRepository.findByLabTestRequestItem( requestItem );
    }
}
