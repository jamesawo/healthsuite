package com.hmis.server.hmis.modules.lab.service;

import com.hmis.server.hmis.common.common.dto.BatchOrSingleEnum;
import com.hmis.server.hmis.common.common.dto.MessageDto;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.model.ProductService;
import com.hmis.server.hmis.common.common.service.CommonService;
import com.hmis.server.hmis.common.common.service.DepartmentServiceImpl;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.common.user.dto.UserDto;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.common.user.service.UserServiceImpl;
import com.hmis.server.hmis.modules.clearking.service.PatientClerkActivityService;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.emr.service.PatientDetailServiceImpl;
import com.hmis.server.hmis.modules.lab.dto.*;
import com.hmis.server.hmis.modules.lab.dto.parasitology.LabParasitologyTemplateDto;
import com.hmis.server.hmis.modules.lab.model.*;
import com.hmis.server.hmis.modules.lab.repository.LabTestPreparationRepository;
import com.hmis.server.hmis.modules.nurse.dto.SubReportDto;
import com.hmis.server.hmis.modules.nurse.service.NursePatientCardNoteServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.hmis.server.hmis.common.common.dto.BatchOrSingleEnum.SINGLE;
import static com.hmis.server.hmis.common.constant.HmisConstant.LAB_REPORT_FOOTER_TEXT;
import static com.hmis.server.hmis.common.constant.HmisConstant.NIL;

@Service
@Slf4j
public class LabResultPreparationService {
	private final LabTestRequestService labTestRequestService;
	private final LabParameterSetupService parameterSetupService;
	private final LabParameterRangeSetupService parameterRangeSetupService;
	private final LabTestTrackerService labTestTrackerService;
	private final PatientDetailServiceImpl patientDetailService;
	private final UserServiceImpl userService;
	private final DepartmentServiceImpl departmentService;
	private final LabTestPreparationRepository preparationRepository;
	private final PatientClerkActivityService patientClerkActivityService;
	private final LabTestResultService labTestResultService;
	private final LabTestApprovalService labTestApprovalService;
	private final NursePatientCardNoteServiceImpl cardNoteService;
	private final CommonService commonService;
	private final LabParasitologyTemplateService parasitologyTemplateService;


	private LabTestResult testResult = null;


	public LabResultPreparationService(
			LabTestRequestService labTestRequestService,
			LabParameterSetupService parameterSetupService,
			LabParameterRangeSetupService parameterRangeSetupService,
			LabTestTrackerService labTestTrackerService,
			PatientDetailServiceImpl patientDetailService,
			UserServiceImpl userService,
			DepartmentServiceImpl departmentService,
			LabTestPreparationRepository preparationRepository,
			PatientClerkActivityService patientClerkActivityService,
			LabTestResultService labTestResultService,
			LabTestApprovalService labTestApprovalService,
			@Lazy NursePatientCardNoteServiceImpl cardNoteService,
			CommonService commonService,
			LabParasitologyTemplateService parasitologyTemplateService ) {
		this.labTestRequestService = labTestRequestService;
		this.parameterSetupService = parameterSetupService;
		this.parameterRangeSetupService = parameterRangeSetupService;
		this.labTestTrackerService = labTestTrackerService;
		this.patientDetailService = patientDetailService;
		this.userService = userService;
		this.departmentService = departmentService;
		this.preparationRepository = preparationRepository;
		this.patientClerkActivityService = patientClerkActivityService;
		this.labTestResultService = labTestResultService;
		this.labTestApprovalService = labTestApprovalService;
		this.cardNoteService = cardNoteService;
		this.commonService = commonService;
		this.parasitologyTemplateService = parasitologyTemplateService;
	}

	public LabResultPrepDto getLabTestResult( Long labTestItemId ) {
		try {
			LabTestRequestItem testItem = this.labTestRequestService.findOneLabTestItem( labTestItemId );
			LabTestRequest labTestRequest = testItem.getLabTestRequest();
			PatientDetail patient = labTestRequest.getPatient();
			LabTestPreparation preparation = this.findLabTestPreparationByTestItem( testItem )
					.orElseThrow( () -> new ResponseStatusException(
							HttpStatus.NOT_FOUND, "Lab Preparation not found: Prepare Lab Result First" ) );

			LabResultPrepDto dto = this.decideLabTestResultTemplateForVerification( preparation, patient, testItem );
			this.setLabTestVerification( testItem, dto, preparation );

			this.testResult = null;
			return dto;
		} catch ( Exception e ) {
			log.error( e.getMessage(), e );
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, e.getMessage() );
		}
	}

	private LabResultPrepDto decideLabTestResultTemplateForVerification( LabTestPreparation preparation,
	                                                                     PatientDetail patient,
	                                                                     LabTestRequestItem testItem ) {
		LabResultPrepDto dto = new LabResultPrepDto();
		switch ( preparation.getTypeEnum() ) {
			case MICROBIOLOGY:
				Optional<LabParasitologyResultTemplate> optional = this.parasitologyTemplateService
						.findByTestPreparation( preparation );
				LabParasitologyTemplateDto templateDto = new LabParasitologyTemplateDto();
				optional.ifPresent( resultTemplate -> {
					templateDto.setCulture( resultTemplate.getCulture() );
					templateDto.setMacroscopy( resultTemplate.getMacroscopy() );
					templateDto.setMicroscopy( resultTemplate.getMicroscopy() );
					templateDto.setNewLabNote( resultTemplate.getNewLabNote() );
				} );
				dto = this.mapLabTestItemToResultDto( testItem, null, false );
				dto.setParasitologyTemplate( templateDto );
				break;
			case CHEMICAL:
				System.out.println( "handle chemical lab test" );
				break;
			case ANATOMICAL:
				System.out.println( "handle anatomical lab test" );
				break;
			case HAEMATOLOGY:
				System.out.println( "handle haematology lab test" );
			case GENERAL:
				this.testResult = this.labTestResultService.findTestResult( patient, preparation, testItem );
				dto = this.mapLabTestItemToResultDto( testItem, SINGLE, true );
				break;
		}
		dto.setResultTypeEnum( preparation.getTypeEnum() );
		return dto;
	}

	/* get lab test by labTestRequest Item id */
	public LabResultPrepDto getLabTest( Long testItemId, BatchOrSingleEnum batchOrSingleEnum ) {
		try {
			LabTestRequestItem testItem = this.labTestRequestService.findOneLabTestItem( testItemId );
			return this.mapLabTestItemToResultDto( testItem, batchOrSingleEnum, false );
		} catch ( Exception e ) {
			log.error( e.getMessage(), e );
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, e.getMessage() );
		}
	}

	/* save lab result preparation data */
	@Transactional
	public ResponseEntity<MessageDto> saveLabResultPreparation( LabResultPrepDto dto ) {
		if ( dto.getResultTypeEnum() == null ) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Lab Result Department Template is Required" );
		}

		try {
			LabTestPreparation model = this.mapLabResultPrepToModel( dto );
			LabTestPreparation preparation = this.preparationRepository.save( model );
			LabTestRequest labTestRequest = this.labTestRequestService.findOneLabBillTest( dto.getTestRequestId() );

			// check template type and save result
			this.decideAndSaveTemplateResult( preparation, dto );
			this.labTestTrackerService.saveTestPrepared( labTestRequest, preparation );
			this.patientClerkActivityService.saveLabTestResultPreparation( preparation );
			return ResponseEntity.ok().body( new MessageDto( "Test Result " + HmisConstant.SAVED_SUCCESSFULLY ) );
		} catch ( Exception e ) {
			log.error( e.getMessage(), e );
			throw new RuntimeException( e.getMessage() );
		}
	}

	public void decideAndSaveTemplateResult( LabTestPreparation preparation, LabResultPrepDto dto ) {
		switch ( dto.getResultTypeEnum() ) {
			case MICROBIOLOGY:
				this.parasitologyTemplateService.saveParasitologyResult( preparation, dto.getParasitologyTemplate() );
				break;
			case CHEMICAL:
				System.out.println( "handle chemical lab test" );
				break;
			case ANATOMICAL:
				System.out.println( "handle anatomical lab test" );
				break;
			case HAEMATOLOGY:
				System.out.println( "handle haematology lab test" );
			case GENERAL:
				this.labTestResultService.updateLabTestResultValues( preparation, dto );
				break;
		}
	}

	public Optional<LabTestPreparation> findLabTestPreparationByTestItem( LabTestRequestItem testItem ) {
		return this.preparationRepository.findByLabTestRequestItem( testItem );
	}

	public LabTestPreparation findLabTestPreparation( PatientDetail patientDetail, LabTestRequest testRequest ) {
		Optional<LabTestPreparation> optional = this.preparationRepository.findByPatientDetailAndLabTestRequest( patientDetail, testRequest );
		return optional.orElseThrow( () -> new ResponseStatusException( HttpStatus.NOT_FOUND, "Cannot find lab test preparation record" ) );
	}

	/* approve lab test result */
	public ResponseEntity<MessageDto> approveLabTestResult( LabApproveDto dto ) {
		try {
			LabTestRequestItem testItem = this.labTestRequestService.findOneLabTestItem( dto.getTestItemId() );
			User user = this.userService.findOneRaw( dto.getApprovedBy().getId().get() );
			Department location = this.departmentService.findOne( dto.getApprovedFrom().getId().get() );
			String note = dto.getLabNote() != null ? dto.getLabNote() : NIL;
			String comment = dto.getPathologistComment() != null ? dto.getPathologistComment() : NIL;

			if ( dto.getViewType().equals( LabVerificationViewEnum.PATHOLOGIST ) ) {
				this.labTestApprovalService.setLabTestPathologistVerification( testItem, user, location, note, comment );
			} else {
				this.labTestApprovalService.setLabTestVerification( testItem, user, location, note );
			}

			return ResponseEntity.ok().body( new MessageDto( "Test Result " + HmisConstant.UPDATED_MESSAGE ) );
		} catch ( Exception e ) {

			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, e.getMessage() );
		}
	}

	/* get lab test result pdf */
	public ResponseEntity<byte[]> getLabTestResultPdf( LabApproveDto dto ) {
		try {
			LabResultPrepDto prepDto = this.getLabTestResult( dto.getTestItemId() );
			PatientDetail patient = this.patientDetailService.findPatientDetailById( prepDto.getPatientDetailDto().getPatientId() );
			String user = this.commonService.getAuthenticatedUser();
			Map<String, Object> map = this.cardNoteService.prepPatientEFolderRecord( LAB_REPORT_FOOTER_TEXT, user, patient, "LABORATORY REPORT" );
			List<SubReportDto> reportDto = this.wrapLabResultToSubReportDto( prepDto );
			byte[] bytes = this.cardNoteService.exportPatientCardNoteReport( map, reportDto );
			return ResponseEntity.ok().body( bytes );
		} catch ( Exception e ) {
			log.error( e.getMessage(), e );
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, e.getMessage() );
		}
	}

	/* set lab test verification in labResultPrepDto */
	private void setLabTestVerification( LabTestRequestItem testItem, LabResultPrepDto dto, LabTestPreparation preparation ) {
		try {
			Optional<LabTestVerification> optionalVerification = this.labTestApprovalService.getLabTestVerification( testItem );
			if ( optionalVerification.isPresent() ) {
				LabTestVerification verification = optionalVerification.get();
				UserDto preparedByUserDto = this.userService.mapToDtoClean( preparation.getCapturedBy() );
				dto.setPreparedBy( preparedByUserDto );
				dto.setLabNote( preparation.getLabNote() != null ? preparation.getLabNote() : NIL );
				dto.setLabNoteBy( preparedByUserDto.getLabel().orElse( NIL ) );
				dto.setVerifiedBy( this.userService.mapToDtoClean( verification.getTestApprovedBy() ) );
				dto.setVerifiedDate( verification.getDate() );
			}

			Optional<LabTestPathologistVerification> optionalPathologist = this.labTestApprovalService.findLabTestPathologistVerification( testItem );
			if ( optionalPathologist.isPresent() ) {
				LabTestPathologistVerification pathologistVerification = optionalPathologist.get();
				dto.setPathologistComment( pathologistVerification.getComment() );
				dto.setPathologistName( pathologistVerification.getApprovedByPathologist().getFullName() );
				dto.setPreparedLabNote( pathologistVerification.getLabNote() );
			}

			Optional<LabTestTracker> optionalLabTestTracker = this.labTestTrackerService.findByLabTestItem( testItem );
			if ( optionalLabTestTracker.isPresent() ) {
				LabTestTracker tracker = optionalLabTestTracker.get();
				LabSpecimenCollection specimenCollection = tracker.getSpecimenCollection();
				String provisionalDiagnosis = specimenCollection.getProvisionalDiagnosis();
				dto.setProvisionalDiagnosis( provisionalDiagnosis != null ? provisionalDiagnosis : NIL );
				dto.setProvisionalDiagnosisBy( specimenCollection.getCapturedBy().getFullName() );
			}
		} catch ( Exception e ) {
			log.error( e.getMessage(), e );
		}
	}

	private List<SubReportDto> wrapLabResultToSubReportDto( LabResultPrepDto prep ) {
		List<SubReportDto> fieldList = new ArrayList<>();
		SubReportDto labResultEFolderData = this.cardNoteService.getLabResultEFolderData( prep );
		fieldList.add( labResultEFolderData );
		return fieldList;
	}

	/* prepare the lab test result for saving */
	private LabTestPreparation mapLabResultPrepToModel( LabResultPrepDto dto ) {
		LabTestRequestItem testItem = this.labTestRequestService.findOneLabTestItem( dto.getSingleTestRequestItemId() );
		LabTestPreparation model = new LabTestPreparation();
		// check if test has been prepared before
		Optional<LabTestPreparation> optional = this.findLabTestPreparationByTestItem( testItem );
		optional.ifPresent( testPreparation -> model.setId( testPreparation.getId() ) );

		model.setPatientDetail( this.patientDetailService.findPatientDetailById( dto.getPatientDetailDto().getPatientId() ) );
		model.setCapturedBy( this.userService.findOneRaw( dto.getPreparedBy().getId().get() ) );
		model.setCapturedFrom( this.departmentService.findOne( dto.getPreparedFrom().getId().get() ) );
		model.setLabNote( dto.getLabNote() != null ? dto.getLabNote() : NIL );
		model.setLabTestRequest( this.labTestRequestService.findOneLabBillTest( dto.getTestRequestId() ) );
		model.setLabTestRequestItem( testItem );
		model.setTypeEnum( dto.getResultTypeEnum() );
		return model;
	}

	/* map lab test request item to prep dto for front end to enter test result */
	private LabResultPrepDto mapLabTestItemToResultDto( LabTestRequestItem testItem, BatchOrSingleEnum singleEnum, boolean addResultValue ) {
		if ( testItem.getSpecimen() == null ) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "NO SPECIMEN COLLECTION FOUND" );
		}
		LabTestRequest request = testItem.getLabTestRequest();
		LabResultPrepDto dto = new LabResultPrepDto();
		dto.setTestName( testItem.getBillItem().getProductService().getName() );
		dto.setTestRequestId( request.getId() );
		dto.setPatientDetailDto( request.getPatient().transformToDto() );
		dto.setRequestNumber( request.getCode() );
		dto.setSpecimen( testItem.getSpecimen().getName() );
		dto.setRequestingDoctor( request.getIsDoctorRequest() ? request.getBill().getDoctorRequest().getDoctor().getFullName() : "" );
		dto.setRequestDate( request.getDate().toString() );

		if ( singleEnum != null ) {
			List<LabResultTestParamDto> paramDtoList;
			if ( singleEnum.equals( SINGLE ) ) {
				paramDtoList = this.getOneResultTestParamDtoList( testItem, addResultValue );
			} else {
				paramDtoList = this.getResultTestParamDtoList( request.getTestItems(), addResultValue );
			}
			dto.setTestParameterList( paramDtoList );
		}

		return dto;
	}

	/* get result test parameter dto from testItems,
	use the test and lab parameter items to get range and range items
	 */
	private List<LabResultTestParamDto> getResultTestParamDtoList( List<LabTestRequestItem> testItems, boolean addResultValue ) {
		List<LabResultTestParamDto> list = new ArrayList<>();
		if ( !testItems.isEmpty() ) {
			for ( LabTestRequestItem item : testItems ) {
				this.setLabTestParamAndRangeDetails( item, list, addResultValue );
				// here
			}
		}
		return list;
	}

	private List<LabResultTestParamDto> getOneResultTestParamDtoList( LabTestRequestItem testRequestItem, boolean addResultValue ) {
		List<LabResultTestParamDto> list = new ArrayList<>();
		this.setLabTestParamAndRangeDetails( testRequestItem, list, addResultValue );
		return list;
	}

	// used in lab result preparation to get patient test and test parameter with test range
	private void setLabTestParamAndRangeDetails( LabTestRequestItem item, List<LabResultTestParamDto> list, boolean addResult ) {
		List<LabParameterRangeSetupItemDto> testRangeParamList = new ArrayList<>();
		ProductService test = item.getBillItem().getProductService();
		Optional<LabParameterSetup> parameterSetup = this.parameterSetupService.findLabParamSetupByTest( test );
		if ( parameterSetup.isPresent() ) {
			List<LabParameterSetupItem> setupItems = parameterSetup.get().getParameterSetupItems();
			if ( setupItems != null && !setupItems.isEmpty() ) {
				for ( LabParameterSetupItem labParameterSetupItem : setupItems ) {
					LabParameterRangeSetup rangeSetup = this.parameterRangeSetupService.findRangeByTestAndLabParamSetup( test, labParameterSetupItem );
					if ( rangeSetup != null ) {
						List<LabParameterRangeSetupItem> rangeSetupItems = rangeSetup.getLabParameterRangeSetupItems();
						if ( !rangeSetupItems.isEmpty() ) {
							for ( LabParameterRangeSetupItem rangeSetupItem : rangeSetupItems ) {
								testRangeParamList.add( this.addLabParamRangeItemDto( rangeSetupItem, addResult ) );
							}
						}
					}
				}
			}
			LabResultTestParamDto dto = this.setLabResultTestParamDto( item, test.getName(), testRangeParamList );
			list.add( dto );
		}
	}

	private LabResultTestParamDto setLabResultTestParamDto( LabTestRequestItem item, String testName, List<LabParameterRangeSetupItemDto> list ) {
		LabResultTestParamDto dto = new LabResultTestParamDto();
		dto.setTestParamId( item.getId() );
		dto.setFilmReport( item.getFilmLabel() != null ? item.getFilmLabel() : "" );
		dto.setTestParamName( testName != null ? testName : "" );
		dto.setComment( item.getComment() != null ? item.getComment() : "" );
		dto.setTestRangeParamList( list );
		return dto;
	}

	private LabParameterRangeSetupItemDto addLabParamRangeItemDto( LabParameterRangeSetupItem rangeSetupItem, boolean addResult ) {
		LabParameterRangeSetupItemDto dto = new LabParameterRangeSetupItemDto();
		dto.setId( rangeSetupItem.getId() );
		dto.setName( rangeSetupItem.getName() );
		dto.setUnit( rangeSetupItem.getUnit() );
		dto.setLowerLimit( rangeSetupItem.getLowerLimit() );
		dto.setUpperLimit( rangeSetupItem.getUpperLimit() );
		if ( addResult ) {
			LabTestResultItem testResultItem = this.labTestResultService.findTestResultItem( this.testResult, rangeSetupItem );
			dto.setValue( testResultItem.getTestValue() );
		}
		return dto;
	}

}
