package com.hmis.server.hmis.modules.clearking.service;

import com.hmis.server.hmis.common.common.dto.DateDto;
import com.hmis.server.hmis.common.common.dto.DepartmentDto;
import com.hmis.server.hmis.common.common.dto.MessageDto;
import com.hmis.server.hmis.common.common.service.DepartmentServiceImpl;
import com.hmis.server.hmis.common.common.service.HmisUtilService;
import com.hmis.server.hmis.common.user.dto.UserDto;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.common.user.service.UserServiceImpl;
import com.hmis.server.hmis.modules.clearking.dto.ClerkDoctorRequestDto;
import com.hmis.server.hmis.modules.clearking.dto.ClerkRequestLabDto;
import com.hmis.server.hmis.modules.clearking.dto.ClerkTabEnum;
import com.hmis.server.hmis.modules.clearking.iservice.IDoctorRequestService;
import com.hmis.server.hmis.modules.clearking.model.*;
import com.hmis.server.hmis.modules.clearking.repository.*;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.emr.service.PatientDetailServiceImpl;
import com.hmis.server.hmis.modules.lab.dto.LabTestItemsDto;
import com.hmis.server.hmis.modules.nurse.model.NursePatientDrugChart;
import com.hmis.server.hmis.modules.nurse.service.NursePatientDrugChartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class DoctorRequestServiceImpl implements IDoctorRequestService {

	private final ClerkDoctorRequestRepository doctorRequestRepository;
	private final ClerkRequestDrugItemsRepository requestDrugItemsRepository;
	private final ClerkRequestDrugRepository requestDrugRepository;
	private final ClerkRequestLabItemsRepository requestLabItemsRepository;
	private final ClerkRequestLabRepository requestLabRepository;
	private final ClerkRequestRadiologyItemsRepository requestRadiologyItemsRepository;
	private final ClerkRequestRadiologyRepository requestRadiologyRepository;
	private final PatientDetailServiceImpl patientService;
	private final DepartmentServiceImpl departmentService;
	private final UserServiceImpl userService;
	private final HmisUtilService utilService;
	private final PatientClerkActivityService activityService;
	private final NursePatientDrugChartService drugChartService;

	public DoctorRequestServiceImpl(
			ClerkDoctorRequestRepository doctorRequestRepository,
			ClerkRequestDrugItemsRepository requestDrugItemsRepository,
			ClerkRequestDrugRepository requestDrugRepository,
			ClerkRequestLabItemsRepository requestLabItemsRepository,
			ClerkRequestLabRepository requestLabRepository,
			ClerkRequestRadiologyItemsRepository requestRadiologyItemsRepository,
			ClerkRequestRadiologyRepository requestRadiologyRepository,
			PatientDetailServiceImpl patientService,
			DepartmentServiceImpl departmentService,
			UserServiceImpl userService,
			HmisUtilService utilService,
			@Lazy PatientClerkActivityService activityService,
			NursePatientDrugChartService drugChartService ) {
		this.doctorRequestRepository = doctorRequestRepository;
		this.requestDrugItemsRepository = requestDrugItemsRepository;
		this.requestDrugRepository = requestDrugRepository;
		this.requestLabItemsRepository = requestLabItemsRepository;
		this.requestLabRepository = requestLabRepository;
		this.requestRadiologyItemsRepository = requestRadiologyItemsRepository;
		this.requestRadiologyRepository = requestRadiologyRepository;
		this.patientService = patientService;
		this.departmentService = departmentService;
		this.userService = userService;
		this.utilService = utilService;
		this.activityService = activityService;
		this.drugChartService = drugChartService;
	}

	@Transactional
	public void updateDrugRequestItem( Optional<String> comment, Long user, Long location, Long patient, Long reqId ) {
		Optional<ClerkRequestDrugItem> optional = this.requestDrugItemsRepository.findById( reqId );
		if ( !optional.isPresent() ) {
			throw new ResponseStatusException( HttpStatus.NOT_FOUND, "Drug Request Item Not Found" );
		}
		try {
			ClerkRequestDrugItem item = optional.get();
			item.setIsAdministered( true );
			item.setNurseComment( comment.orElse( null ) );
			User userFound = this.userService.findOneRaw( user );
			item.setAdministeredBy( userFound );
			// update drug request item data
			ClerkRequestDrugItem requestDrugItem = this.requestDrugItemsRepository.save( item );
			// save nurse patient drug chart (for drug administration)
			NursePatientDrugChart drugChart = this.drugChartService.saveDrugAdministration(
					requestDrugItem,
					this.patientService.findPatientDetailById( patient ),
					userFound,
					this.departmentService.findOneRaw( Optional.of( location ) )
			);
			// save the clerking activity for patient e folder usage
			this.activityService.saveDrugChartActivity( drugChart );
		} catch ( Exception e ) {
			throw new RuntimeException( e.getMessage(), e );
		}
	}


	@Override
	public List<ClerkRequestLab> getDoctorLabRequest( PatientDetail patient, LocalDate startDate, LocalDate endDate ) {
		List<ClerkRequestLab> labList = new ArrayList<>();
		List<ClerkDoctorRequest> doctorRequestList = this.doctorRequestRepository.findAllByPatientAndDateIsLessThanEqualAndDateIsGreaterThanEqualOrderByDateAsc( patient, endDate, startDate );
		if ( !doctorRequestList.isEmpty() ) {
			for ( ClerkDoctorRequest clerkDoctorRequest : doctorRequestList ) {
				if ( clerkDoctorRequest.getLabRequest() != null ) {
					ClerkRequestLab labRequest = clerkDoctorRequest.getLabRequest();
					labRequest.setPhysician( this.userService.mapToDtoClean( clerkDoctorRequest.getDoctor() ) );
					labRequest.setDate( clerkDoctorRequest.getDate() );
					labRequest.setDoctorRequestId( clerkDoctorRequest.getId() );
					labList.add( labRequest );
				}
			}
		}
		return labList;
	}

	// get doctor drug request by date range
	@Override
	public List<ClerkRequestDrug> getDoctorDrugRequest( PatientDetail patient, LocalDate startDate, LocalDate endDate ) {
		List<ClerkRequestDrug> drugList = new ArrayList<>();
		List<ClerkDoctorRequest> doctorRequestList = this.doctorRequestRepository.findAllByPatientAndDateIsLessThanEqualAndDateIsGreaterThanEqualOrderByDateAsc( patient, endDate, startDate );
		if ( !doctorRequestList.isEmpty() ) {
			for ( ClerkDoctorRequest clerkDoctorRequest : doctorRequestList ) {
				if ( clerkDoctorRequest.getDrugRequest() != null ) {
					ClerkRequestDrug drugRequest = clerkDoctorRequest.getDrugRequest();
					drugRequest.setPhysician( this.userService.mapToDtoClean( clerkDoctorRequest.getDoctor() ) );
					drugRequest.setDate( clerkDoctorRequest.getDate() );
					drugRequest.setDoctorRequestId( clerkDoctorRequest.getId() );
					drugList.add( drugRequest );
				}
			}
		}
		return drugList;
	}

	// get doctor radiology request for a patient by date range
	@Override
	public List<ClerkRequestRadiology> getDoctorRadiologyRequest( PatientDetail patient, LocalDate startDate, LocalDate endDate ) {
		List<ClerkRequestRadiology> radiologyList = new ArrayList<>();
		List<ClerkDoctorRequest> doctorRequestList = this.doctorRequestRepository.findAllByPatientAndDateIsLessThanEqualAndDateIsGreaterThanEqualOrderByDateAsc( patient, endDate, startDate );
		if ( !doctorRequestList.isEmpty() ) {
			for ( ClerkDoctorRequest clerkDoctorRequest : doctorRequestList ) {
				if ( clerkDoctorRequest.getRadiologyRequest() != null ) {
					ClerkRequestRadiology radiologyRequest = clerkDoctorRequest.getRadiologyRequest();
					radiologyRequest.setPhysician( this.userService.mapToDtoClean( clerkDoctorRequest.getDoctor() ) );
					radiologyRequest.setDate( clerkDoctorRequest.getDate() );
					radiologyRequest.setDoctorRequestId( clerkDoctorRequest.getId() );
					radiologyList.add( radiologyRequest );
				}
			}
		}
		return radiologyList;
	}

	@Override
	public ClerkDoctorRequest findById( Long id ) {
		Optional<ClerkDoctorRequest> request = this.doctorRequestRepository.findById( id );
		if ( !request.isPresent() ) {
			throw new ResponseStatusException( HttpStatus.NOT_FOUND, "Doctor Request Not Found" );
		}
		return request.get();
	}

	// search patient radiology request by date range
	@Override
	public List<ClerkRequestRadiology> searchRadiologyRequestByDataRange( Long patientId, DateDto startDate, DateDto endDate ) {
		LocalDate toStartDate = this.utilService.transformToLocalDate( startDate );
		LocalDate toEndDate = this.utilService.transformToLocalDate( endDate );
		PatientDetail patient = this.patientService.findPatientDetailById( patientId );
		return this.getDoctorRadiologyRequest( patient, toStartDate, toEndDate );
	}

	// search patient lab request by date range
	@Override
	public List<ClerkRequestLab> searchLabRequestByDataRange( Long patientId, DateDto startDate, DateDto endDate ) {
		LocalDate toStartDate = this.utilService.transformToLocalDate( startDate );
		LocalDate toEndDate = this.utilService.transformToLocalDate( endDate );
		PatientDetail patient = this.patientService.findPatientDetailById( patientId );
		return this.getDoctorLabRequest( patient, toStartDate, toEndDate );
	}

	// search patient drug request with date range
	@Override
	public List<ClerkRequestDrug> searchDrugRequestByDateRange( Long patientId, DateDto start, DateDto end ) {
		LocalDate toStartDate = this.utilService.transformToLocalDate( start );
		LocalDate toEndDate = this.utilService.transformToLocalDate( end );
		PatientDetail patient = this.patientService.findPatientDetailById( patientId );
		return this.getDoctorDrugRequest( patient, toStartDate, toEndDate );
	}

	@Override
	@Transactional
	public void saveDoctorRequest( ClerkDoctorRequestDto dto ) {
		try {
			ClerkDoctorRequest model = this.mapToModel( dto );
			PatientDetail patient = patientService.findPatientDetailById( dto.getPatient().getPatientId() );
			// save drug request
			if ( dto.getDrugRequest() != null && dto.getDrugRequest().getDrugItems() != null ) {
				if ( dto.getDrugRequest().getDrugItems().size() > 0 ) {
					ClerkRequestDrug drugRequest = this.saveDrugRequest( dto.getDrugRequest(), patient );
					this.activityService.saveDrugRequestActivity( drugRequest );
					model.setDrugRequest( drugRequest );
				}
			}
			// save lab request
			if ( dto.getLabRequest() != null && dto.getLabRequest().getLabItems() != null ) {
				if ( dto.getLabRequest().getLabItems().size() > 0 ) {
					ClerkRequestLab labRequest = this.saveLabRequest( dto.getLabRequest(), patient );
					this.activityService.saveLabRequestActivity( labRequest );
					model.setLabRequest( labRequest );
				}
			}
			// save radiology request
			if ( dto.getRadiologyRequest() != null && dto.getRadiologyRequest().getRadiologyItems() != null ) {
				if ( dto.getRadiologyRequest().getRadiologyItems().size() > 0 ) {
					ClerkRequestRadiology radiologyRequest = this.saveRadiologyRequest( dto.getRadiologyRequest(), patient );
					this.activityService.saveRadiologyRequestActivity( radiologyRequest );
					model.setRadiologyRequest( radiologyRequest );
				}
			}
			this.doctorRequestRepository.save( model );
		} catch ( Exception e ) {
			log.error( e.getMessage(), e );
			throw new RuntimeException( e );
		}
	}

	// save drug request
	@Override
	public ClerkRequestDrug saveDrugRequest( ClerkRequestDrug requestDrug, PatientDetail patient ) {
		requestDrug.setPatientDetail( patient );
		ClerkRequestDrug drug = this.requestDrugRepository.save( requestDrug );
		this.saveDrugItemsRequest( drug, requestDrug.getDrugItems() );
		return drug;
	}

	public ResponseEntity<MessageDto> saveDrugRequest( ClerkDoctorRequestDto dto ) {
		this.saveDoctorRequest( dto );
		return ResponseEntity.ok().body( new MessageDto( "REQUEST SAVED" ) );
	}

	// save drug items in drug request
	@Override
	public void saveDrugItemsRequest( ClerkRequestDrug requestDrug, List<ClerkRequestDrugItem> items ) {
		if ( !items.isEmpty() ) {
			for ( ClerkRequestDrugItem item : items ) {
				item.setRequestDrug( requestDrug );
				this.requestDrugItemsRepository.save( item );
			}
		}
	}

	// save lab request
	@Override
	public ClerkRequestLab saveLabRequest( ClerkRequestLab requestLab, PatientDetail patient ) {
		requestLab.setPatientDetail( patient );
		ClerkRequestLab lab = this.requestLabRepository.save( requestLab );
		this.saveLabItemsRequest( lab, requestLab.getLabItems() );
		return lab;
	}

	// save lab items in lab request
	@Override
	public void saveLabItemsRequest( ClerkRequestLab lab, List<ClerkRequestLabItems> items ) {
		if ( !items.isEmpty() ) {
			for ( ClerkRequestLabItems item : items ) {
				item.setLabRequest( lab );
				this.requestLabItemsRepository.save( item );
			}
		}
	}

	// save radiology request
	@Override
	public ClerkRequestRadiology saveRadiologyRequest( ClerkRequestRadiology requestRadiology, PatientDetail patient ) {
		requestRadiology.setPatientDetail( patient );
		ClerkRequestRadiology radiology = this.requestRadiologyRepository.save( requestRadiology );
		this.saveRadiologyItemsRequest( radiology, requestRadiology.getRadiologyItems() );
		return radiology;
	}

	// save radiology items in radiology request
	@Override
	public void saveRadiologyItemsRequest( ClerkRequestRadiology radiology, List<ClerkRequestRadiologyItems> items ) {
		if ( !items.isEmpty() ) {
			for ( ClerkRequestRadiologyItems item : items ) {
				item.setRadiologyRequest( radiology );
				this.requestRadiologyItemsRepository.save( item );
			}
		}
	}

	public ClerkRequestLabDto mapLabRequestToDto( ClerkRequestLab model ) {
		ClerkRequestLabDto dto = new ClerkRequestLabDto();
		if ( ObjectUtils.isNotEmpty( model.getId() ) ) {
			dto.setId( model.getId() );
		}
		if ( ObjectUtils.isNotEmpty( model.getOtherInformation() ) ) {
			dto.setOtherInformation( model.getOtherInformation() );
		}
		String code = model.getCode() != null ? model.getCode() : "";
		if ( !model.getLabItems().isEmpty() ) {
			List<LabTestItemsDto> itemList = new ArrayList<>();
			model.getLabItems().forEach( e -> itemList.add( new LabTestItemsDto( e.getId(), code, e.getService().getName() ) ) );
			dto.setTestItems( itemList );
		}
		dto.setPatient( null );
		dto.setCode( code );
		dto.setPhysician(
				new UserDto( model.getDoctorRequest().getDoctor().getId(), model.getDoctorRequest().getDoctor().getFullName() )
		);
		dto.setDate( model.getDate() );
		dto.setDoctorRequestId( model.getDoctorRequest().getId() );
		dto.setDepartmentDto( new DepartmentDto( model.getDoctorRequest().getDepartment().getId(), model.getDoctorRequest().getDepartment().getName() ) );
		return dto;
	}

	// model mapper
	private ClerkDoctorRequest mapToModel( ClerkDoctorRequestDto dto ) {
		ClerkDoctorRequest model = new ClerkDoctorRequest();
		if ( ObjectUtils.isNotEmpty( dto.getId() ) ) {
			model.setId( dto.getId() );
		}
		if ( ObjectUtils.isNotEmpty( dto.getPatient() ) ) {
			model.setPatient( this.patientService.findPatientDetailById( dto.getPatient().getPatientId() ) );
		}
		if ( ObjectUtils.isNotEmpty( dto.getDepartment() ) ) {
			model.setDepartment( this.departmentService.findOneRaw( dto.getDepartment().getId() ) );
		}
		if ( ObjectUtils.isNotEmpty( dto.getDoctor() ) && dto.getDoctor().getId().isPresent() ) {
			model.setDoctor( this.userService.findOneRaw( dto.getDoctor().getId().get() ) );
		}
		return model;
	}

	private String generateRequestCode( ClerkTabEnum tab ) {
		//        if (tab == ClerkTabEnum.LAB) {
		//            GenerateCodeDto codeDto = new GenerateCodeDto();
		//            codeDto.setDefaultPrefix(HmisCodeDefaults.CLERKING_DRUG_REQUEST);
		//            codeDto.setGlobalSettingKey(Optional.of(HmisGlobalSettingKeys.CLERKING_DRUG_REQUEST_PREFIX));
		//            codeDto.setLastGeneratedCode(requestDrugRepository.findTopByOrderByIdDesc().map(ClerkRequestDrug::getCode));
		//            return commonService.generateDataCode(codeDto);
		//        }
		return "";

	}
}
