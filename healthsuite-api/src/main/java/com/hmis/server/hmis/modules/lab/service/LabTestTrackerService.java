package com.hmis.server.hmis.modules.lab.service;

import com.hmis.server.hmis.modules.billing.model.PatientBill;
import com.hmis.server.hmis.modules.lab.dto.LabTestTrackerDto;
import com.hmis.server.hmis.modules.lab.model.*;
import com.hmis.server.hmis.modules.lab.repository.LabTestTrackerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class LabTestTrackerService {
    private final LabTestTrackerRepository labTestTrackerRepository;

    public LabTestTrackerService(
            LabTestTrackerRepository labTestTrackerRepository
    ) {
        this.labTestTrackerRepository = labTestTrackerRepository;
    }

    public LabTestTrackerDto findTracker( LabTestRequest test, Long testItemId ) {
        Optional<LabTestTracker> optional = this.labTestTrackerRepository.findByLabTestRequest_Id( test.getId() ); //todo;; replace with testRequestITem
        if ( !optional.isPresent() ) {
            throw new ResponseStatusException( HttpStatus.NOT_FOUND, "Nothing Found" );
        }
        LabTestTracker tracker = optional.get();
        return this.mapTrackerModelToDto( tracker, testItemId );
    }


    public LabTestTracker findOrCreateTracker( LabTestRequest test ) {
        Optional<LabTestTracker> optional = this.labTestTrackerRepository.findByLabTestRequest_Id( test.getId() );
        return optional.orElseGet( () -> this.labTestTrackerRepository.save( new LabTestTracker( test.getCode(), test ) ) );
    }

//    public LabTestTracker findOrCreateTestTracker(LabTestRequestItem item, String code) {
//        Optional<LabTestTracker> optional = this.labTestTrackerRepository.findByLabTestRequestItem(item);
//        return optional.orElseGet(() -> this.labTestTrackerRepository.save(new LabTestTracker(code, item.getLabBillTestRequest())));
//    }


    public Optional<LabTestTracker> findByLabTestItem( LabTestRequestItem item ) {
        return this.labTestTrackerRepository.findByLabTestRequestItem( item );
    }

    public void saveTrackerBill( LabTestRequest billTestRequest, boolean isCredit ) {
        LabTestTracker tracker = this.findOrCreateTracker( billTestRequest );
        tracker.setBillType( isCredit ? "Credit Bill" : "Pay Now" );
        this.labTestTrackerRepository.save( tracker );
    }

    public void saveSampleCollection( LabTestRequest billTestRequest, LabSpecimenCollection specimenCollection ) {
        LabTestTracker tracker = this.findOrCreateTracker( billTestRequest );
        tracker.setIsSpecimenCollected( true );
        tracker.setSpecimenCollection( specimenCollection );
        this.labTestTrackerRepository.save( tracker );
    }

    public void saveSampleAck( LabTestRequest billTestRequest, LabTestRequestItem labTestRequestItem, LabSpecimenAck specimenAck ) {
        LabTestTracker tracker = this.findOrCreateTracker( billTestRequest );
        tracker.setIsSpecimenAck( true );
        tracker.setSpecimenAck( specimenAck );
        tracker.setLabTestRequestItem( labTestRequestItem );
        this.labTestTrackerRepository.save( tracker );
    }

    public void saveTestPrepared( LabTestRequest billTestRequest, LabTestPreparation testPreparation ) {
        LabTestTracker tracker = this.findOrCreateTracker( billTestRequest );
        tracker.setIsLabTestPrepared( true );
        tracker.setLabTestPreparation( testPreparation );
        this.labTestTrackerRepository.save( tracker );
    }

    public void saveTestVerification( LabTestRequest billTestRequest, LabTestVerification testVerification ) {
        LabTestTracker tracker = this.findOrCreateTracker( billTestRequest );
        tracker.setIsLabTestVerified( true );
        tracker.setLabTestVerification( testVerification );
        this.labTestTrackerRepository.save( tracker );
    }

    public void saveTestPathologist( LabTestRequest billTestRequest, LabTestPathologistVerification pathologistVerification ) {
        LabTestTracker tracker = this.findOrCreateTracker( billTestRequest );
        tracker.setIsPathologistVerified( true );
        tracker.setPathologistVerification( pathologistVerification );
        this.labTestTrackerRepository.save( tracker );
    }

    private LabTestTrackerDto mapTrackerModelToDto( LabTestTracker model, Long testId ) {
        LabTestTrackerDto dto = new LabTestTrackerDto();
        if ( model != null ) {
            dto.setId( model.getId() );
            PatientBill bill = model.getLabTestRequest().getBill();
            dto.setBillType( bill.getIsCredit() ? "Credit Bill" : "PayNow" );
            dto.setPaymentStatus( bill.getIsPaid() ? "Paid" : "Not Paid" );
            dto.setLabTestCode( model.getLabTestCode() );
            Boolean isSpecimenCollected = model.getIsSpecimenCollected();
            dto.setIsSpecimenCollected( isSpecimenCollected );
            if ( isSpecimenCollected ) {
                LabSpecimenCollection specimenCollection = model.getSpecimenCollection();
                dto.setSpecimenCollectedByUser( specimenCollection.getCapturedBy().getFullName() );
                dto.setSpecimenCollectedDate( specimenCollection.getDate().toString() );
            }
            Boolean isSpecimenAck = model.getIsSpecimenAck();
            dto.setIsSpecimenAck( isSpecimenAck );
            if ( isSpecimenAck ) {
                LabSpecimenAck specimenAck = model.getSpecimenAck();
                dto.setSpecimenAckByUser( specimenAck.getCapturedBy().getFullName() );
                dto.setSpecimenAckDate( specimenAck.getDate().toString() );
                model.getLabTestRequest().getTestItems().stream()
                        .filter( labTestRequestItem -> testId.equals( labTestRequestItem.getId() ) )
                        .findAny().ifPresent( requestItem -> dto.setSpecimenAckStatus( requestItem.getAcknowledgement() ) );
            }
            Boolean isLabTestPrepared = model.getIsLabTestPrepared();
            dto.setIsLabTestPrepared( isLabTestPrepared );
            if ( isLabTestPrepared ) {
                LabTestPreparation labTestPreparation = model.getLabTestPreparation();
                dto.setSpecimenPrepByUser( labTestPreparation.getCapturedBy().getFullName() );
                dto.setSpecimenPrepDate( labTestPreparation.getDate().toString() );
            }
            Boolean isLabTestVerified = model.getIsLabTestVerified();
            dto.setIsLabTestVerified( isLabTestVerified );
            if ( isLabTestVerified ) {
                LabTestVerification labTestVerification = model.getLabTestVerification();
                dto.setTestVerifiedByUser( labTestVerification.getTestApprovedBy().getFullName() );
                dto.setTestVerifiedDate( labTestVerification.getDate().toString() );
                dto.setTestVerifiedApproved( labTestVerification.getIsTestApproved() ? "Yes" : "No" );
            }
            Boolean isPathologistVerified = model.getIsPathologistVerified();
            dto.setIsPathologistVerified( isPathologistVerified );
            if ( isPathologistVerified ) {
                LabTestPathologistVerification pathologistVerification = model.getPathologistVerification();
                dto.setTestVerifiedByPathologistUser( pathologistVerification.getApprovedByPathologist().getFullName() );
                dto.setTestVerifiedByPathologistDate( pathologistVerification.getDate().toString() );
                dto.setTestVerifiedByPathologistApproved( pathologistVerification.getIsPathologistApproved() ? "Yes" : "No" );
            }
        }
        return dto;
    }
}
