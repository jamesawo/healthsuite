package com.hmis.server.hmis.modules.settings.service;

import com.hmis.server.hmis.common.common.dto.*;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.model.EthnicGroup;
import com.hmis.server.hmis.common.common.service.*;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.common.exception.BadRequestException;
import com.hmis.server.hmis.common.exception.EntityExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class SeedDataService<T> {

	private final Logger LOGGER = LoggerFactory.getLogger( this.getClass() );
	private final GenderServiceImpl genderService;
	private final MaritalStatusServiceImpl maritalStatusService;
	private final CommonService commonService;
	private final RelationshipServiceImpl relationshipService;
	private final ReligionServiceImpl religionService;
	private final RoleServiceImpl roleService;
	private final DepartmentServiceImpl departmentService;
	private final DepartmentCategoryServiceImpl departmentCategoryService;
	private final SurgeryServiceImpl surgeryService;
	private final DrugClassificationServiceImpl drugClassificationService;
	private final DrugFormulationServiceImpl drugFormulationService;
	private final WardServiceImpl wardService;
	private final RevenueDepartmentServiceImpl revenueDepartmentService;
	private final RevenueDepartmentTypeServiceImpl revenueDepartmentTypeService;
	private final SpecialityUnitServiceImpl specialityUnitService;
	private final PharmacyPatientCategoryServiceImpl pharmacyPatientCategoryService;
	private final PharmacyPatientCategoryTypeServiceImpl pharmacyPatientCategoryTypeService;
	private final PaymentMethodServiceImpl paymentMethodService;
	private final PharmacySupplierCategoryServiceImpl pharmacySupplierCategoryService;
	private final NursingNoteLabelServiceImpl nursingNoteLabelService;
	private final LabSpecimenServiceImpl labSpecimenService;
	private final OrganismServiceImpl organismService;
	private final AntibioticsServiceImpl antibioticsService;
	private final BillWaiverCategoryServiceImpl billWaiverCategoryService;
	private final NationalityServiceImpl nationalityService;
	private final EthnicGroupServiceImpl ethnicGroupService;
	private final MeansOfIdentificationServiceImpl meansOfIdentificationService;
	private final SchemeServiceImpl schemeService;

	@Autowired
	public SeedDataService(
			GenderServiceImpl genderService,
			MaritalStatusServiceImpl maritalStatusService,
			CommonService commonService,
			RelationshipServiceImpl relationshipService,
			ReligionServiceImpl religionService,
			RoleServiceImpl roleService,
			DepartmentServiceImpl departmentService,
			DepartmentCategoryServiceImpl departmentCategoryService,
			SurgeryServiceImpl surgeryService,
			DrugClassificationServiceImpl drugClassificationService,
			DrugFormulationServiceImpl drugFormulationService,
			WardServiceImpl wardService,
			RevenueDepartmentServiceImpl revenueDepartmentService,
			RevenueDepartmentTypeServiceImpl revenueDepartmentTypeService,
			SpecialityUnitServiceImpl specialityUnitService,
			PharmacyPatientCategoryServiceImpl pharmacyPatientCategoryService,
			PharmacyPatientCategoryTypeServiceImpl pharmacyPatientCategoryTypeService,
			PaymentMethodServiceImpl paymentMethodService,
			PharmacySupplierCategoryServiceImpl pharmacySupplierCategoryService,
			NursingNoteLabelServiceImpl nursingNoteLabelService,
			LabSpecimenServiceImpl labSpecimenService,
			OrganismServiceImpl organismService,
			AntibioticsServiceImpl antibioticsService,
			BillWaiverCategoryServiceImpl billWaiverCategoryService,
			NationalityServiceImpl nationalityService,
			EthnicGroupServiceImpl ethnicGroupService,
			MeansOfIdentificationServiceImpl meansOfIdentificationService,
			SchemeServiceImpl schemeService
	) {
		this.genderService = genderService;
		this.maritalStatusService = maritalStatusService;
		this.commonService = commonService;
		this.relationshipService = relationshipService;
		this.religionService = religionService;
		this.roleService = roleService;
		this.departmentService = departmentService;
		this.departmentCategoryService = departmentCategoryService;
		this.surgeryService = surgeryService;
		this.drugClassificationService = drugClassificationService;
		this.drugFormulationService = drugFormulationService;
		this.wardService = wardService;
		this.revenueDepartmentService = revenueDepartmentService;
		this.revenueDepartmentTypeService = revenueDepartmentTypeService;
		this.specialityUnitService = specialityUnitService;
		this.pharmacyPatientCategoryService = pharmacyPatientCategoryService;
		this.pharmacyPatientCategoryTypeService = pharmacyPatientCategoryTypeService;
		this.paymentMethodService = paymentMethodService;
		this.pharmacySupplierCategoryService = pharmacySupplierCategoryService;
		this.nursingNoteLabelService = nursingNoteLabelService;
		this.labSpecimenService = labSpecimenService;
		this.organismService = organismService;
		this.antibioticsService = antibioticsService;
		this.billWaiverCategoryService = billWaiverCategoryService;
		this.nationalityService = nationalityService;
		this.ethnicGroupService = ethnicGroupService;
		this.meansOfIdentificationService = meansOfIdentificationService;
		this.schemeService = schemeService;
	}

	/* Seed Gender Data */
	public ResponseDto<?> createOneGender( GenderDto genderDto ) {
		ResponseDto<?> responseDto = new ResponseDto<>();
		try {
			if ( genderService.isGenderExist( genderDto ) ) {
				responseDto.setMessage( HmisConstant.ENTITY_EXIST );
				responseDto.setHttpStatusText( HmisConstant.STATUS_409 );
				responseDto.setHttpStatusCode( 409 );
				return responseDto;
			} else {
				responseDto.setData( genderService.createOne( genderDto ) );
				responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
				responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
				responseDto.setHttpStatusText( HmisConstant.STATUS_200 );
				return responseDto;
			}
		} catch ( Exception e ) {
			e.printStackTrace();
			responseDto.setMessage( e.toString() );
			responseDto.setHttpStatusText( HmisConstant.ERROR_MESSAGE );
			LOGGER.debug( e.toString() );
			return responseDto;
		}
	}

	public ResponseDto findAllGender() {
		ResponseDto responseDto = new ResponseDto();
		String message = " Get all gender ";
		try {
			responseDto.setData( genderService.findAll() );
			responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
			responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
			return responseDto;
		} catch ( Exception e ) {
			responseDto.setMessage( e.getMessage() );
			responseDto.setHttpStatusText( HmisConstant.ERROR_MESSAGE );
			LOGGER.debug( commonService.getLoggerMessage( message,
					HmisConstant.LOGGER_TYPE_FAILED ) );
			LOGGER.debug( e.toString() );
			return responseDto;
		}

	}

	/* Seed Marital Status */
	public ResponseDto createOneMaritalStatus( MaritalStatusDto maritalStatusDto ) {
		ResponseDto responseDto = new ResponseDto();
		final String message = " Create Martial Status ";
		try {
			if ( maritalStatusDto == null ) {
				throw new BadRequestException( HmisConstant.STATUS_400, HmisConstant.STATUS_400 );
			} else {
				if ( maritalStatusService.isMaritalStatusExist( maritalStatusDto ) ) {
					throw new EntityExistsException( HmisConstant.ENTITY_EXIST );
				}
			}
			responseDto.setData( maritalStatusService.createOne( maritalStatusDto ) );
			responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
			responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
		} catch ( Exception e ) {
			responseDto.setMessage( e.getMessage() );
			responseDto.setHttpStatusText( e.toString() );
			LOGGER.debug( commonService.getLoggerMessage( message, HmisConstant.LOGGER_TYPE_FAILED ) );
			LOGGER.debug( Arrays.toString( e.getStackTrace() ), e.getMessage() );
		}
		return responseDto;
	}

	public ResponseDto findAllMaritalStatus() {
		ResponseDto responseDto = new ResponseDto();
		final String message = " Get all marital status";
		try {
			responseDto.setData( maritalStatusService.findAll() );
			responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
			responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
			responseDto.setHttpStatusText( HmisConstant.STATUS_200 );

		} catch ( Exception e ) {
			responseDto.setHttpStatusText( e.toString() );
			responseDto.setMessage( Arrays.toString( e.getStackTrace() ) );
			LOGGER.info( commonService.getLoggerMessage( message, HmisConstant.LOGGER_FAILED ) );
			LOGGER.debug( Arrays.toString( e.getStackTrace() ), e.getMessage() );

		}
		return responseDto;

	}

	/* Seed Relationship */
	public ResponseDto createRelationship( RelationshipDto relationshipDto ) {
		ResponseDto responseDto = new ResponseDto();
		final String message = " Create Relationship";
		if ( relationshipService.isRelationshipExist( relationshipDto ) ) {
			throw new EntityExistsException( HmisConstant.ENTITY_EXIST );
		}
		try {
			responseDto.setData( relationshipService.createOne( relationshipDto ) );
			responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
			responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
			responseDto.setHttpStatusText( HmisConstant.STATUS_200 );
		} catch ( Exception e ) {
			responseDto.setMessage( e.getMessage() );
			LOGGER.debug( commonService.getLoggerMessage( message, HmisConstant.LOGGER_TYPE_FAILED ) );
		}
		return responseDto;
	}

	public ResponseDto findAllRelationship() {
		ResponseDto responseDto = new ResponseDto();
		try {
			responseDto.setData( relationshipService.findAll() );
			responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
			responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
			responseDto.setHttpStatusText( HmisConstant.STATUS_200 );
		} catch ( Exception e ) {
			responseDto.setHttpStatusText( e.toString() );
			responseDto.setMessage( Arrays.toString( e.getStackTrace() ) );
			//            commonService.setLOGGER(HmisConstant.DEBUG, message, Optional.of(HmisConstant.LOGGER_FAILED));
			commonService.setLOGGER( HmisConstant.DEBUG, Arrays.toString( e.getStackTrace() ), Optional.of( "" ) );
		}
		return responseDto;
	}

	/* Seed Religion */
	public ResponseDto findAllReligion() {
		ResponseDto responseDto = new ResponseDto();
		try {

			responseDto.setData( religionService.findAll() );
			responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
			responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
			responseDto.setHttpStatusText( HmisConstant.STATUS_200 );
		} catch ( Exception e ) {
			responseDto.setMessage( e.getMessage() );
			LOGGER.debug( commonService.getLoggerMessage( " Get All Religion ", HmisConstant.LOGGER_TYPE_FAILED ) );
		}
		return responseDto;
	}

	public ResponseDto createOneReligion( ReligionDto religionDto ) {
		ResponseDto responseDto = new ResponseDto();
		final String message = " Create One Religion ";
		try {
			if ( religionService.isReligionExist( religionDto ) ) {
				responseDto.setHttpStatusText( HmisConstant.STATUS_409 );
				responseDto.setHttpStatusCode( 409 );
				responseDto.setMessage( HmisConstant.ENTITY_EXIST );
				return responseDto;
			} else {
				responseDto.setData( religionService.createOne( religionDto ) );
				responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
				responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
				responseDto.setHttpStatusText( HmisConstant.STATUS_200 );
				return responseDto;
			}
		} catch ( Exception e ) {
			responseDto.setMessage( Arrays.toString( e.getStackTrace() ) );
			commonService.setLOGGER( HmisConstant.DEBUG, message, Optional.of( HmisConstant.LOGGER_FAILED ) );
		}
		return responseDto;
	}

	/* Seed Roles */
	public ResponseDto findAllRoles() {
		ResponseDto responseDto = new ResponseDto();
		final String message = " Get All Roles ";
		try {
			responseDto.setData( roleService.findAll() );
			responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
			responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
			responseDto.setHttpStatusText( HmisConstant.STATUS_200 );
			return responseDto;
		} catch ( Exception e ) {
			responseDto.setMessage( Arrays.toString( e.getStackTrace() ) );
			commonService.setLOGGER( HmisConstant.DEBUG, message, Optional.of( HmisConstant.LOGGER_FAILED ) );
		}
		return responseDto;
	}

	public ResponseDto createOneRole( RoleDto roleDto ) {
		ResponseDto responseDto = new ResponseDto();
		final String message = " Create Role ";
		try {
			if ( roleService.isRoleExist( roleDto ) ) {
				responseDto.setHttpStatusText( HmisConstant.STATUS_409 );
				responseDto.setHttpStatusCode( 409 );
				responseDto.setMessage( HmisConstant.ENTITY_EXIST );
				return responseDto;
			} else {
				responseDto.setData( roleService.createOne( roleDto ) );
				responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
				responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
				responseDto.setHttpStatusText( HmisConstant.STATUS_200 );
				return responseDto;
			}
		} catch ( Exception e ) {
			responseDto.setHttpStatusText( Arrays.toString( e.getStackTrace() ) );
			responseDto.setMessage( e.getMessage() );
			commonService.setLOGGER( HmisConstant.DEBUG, message, Optional.of( HmisConstant.LOGGER_FAILED ) );
		}
		return responseDto;
	}

	public ResponseDto updateOneRole( RoleDto roleDto ) {
		ResponseDto responseDto = new ResponseDto();
		final String message = " Update Role ";
		try {
			responseDto.setData( roleService.updateOneWithoutPermissions( roleDto ) );
			responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
			responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
			responseDto.setHttpStatusText( HmisConstant.STATUS_200 );

		} catch ( Exception e ) {
			responseDto.setHttpStatusText( Arrays.toString( e.getStackTrace() ) );
			responseDto.setMessage( e.getMessage() );
			commonService.setLOGGER( HmisConstant.DEBUG, message, Optional.of( HmisConstant.LOGGER_FAILED ) );
		}
		return responseDto;
	}

	/* Seed Department */

	public ResponseDto findAllDepartment() {
		ResponseDto responseDto = new ResponseDto();
		final String message = " Get All Department";
		try {
			responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
			responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
			responseDto.setHttpStatusText( HmisConstant.STATUS_200 );
			responseDto.setData( departmentService.findAllDepartment() );
		} catch ( Exception e ) {
			responseDto.setHttpStatusCode( HmisConstant.SERVER_ERROR );
			responseDto.setHttpStatusText( e.toString() );
			responseDto.setMessage( e.getMessage() );
			commonService.setLOGGER( HmisConstant.DEBUG, message, Optional.of( HmisConstant.LOGGER_FAILED ) );
		}
		return responseDto;
	}

	public ResponseDto createOneDepartment( DepartmentDto departmentDto ) {
		ResponseDto responseDto = new ResponseDto();
		final String message = " Create One Department";
		if ( departmentService.isDepartmentExist( departmentDto ) ) {
			responseDto.setMessage( HmisConstant.ENTITY_EXIST );
			responseDto.setHttpStatusText( HmisConstant.STATUS_409 );
			responseDto.setHttpStatusCode( 409 );
			return responseDto;
		} else {
			try {
				responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
				responseDto.setData( departmentService.createOne( departmentDto ) );
				responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
				responseDto.setHttpStatusText( HmisConstant.STATUS_200 );
			} catch ( Exception e ) {
				responseDto.setHttpStatusText( e.toString() );
				responseDto.setMessage( e.getMessage() );
				commonService.setLOGGER( HmisConstant.DEBUG, message, Optional.of( HmisConstant.LOGGER_FAILED ) );
				throw new ResponseStatusException( HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage() );
			}
			return responseDto;
		}
	}

	public DepartmentDto findOneDepartment( Long departmentId ) {
		Department oneRaw = this.departmentService.findOneRaw( Optional.of( departmentId ) );
		return this.departmentService.mapModelToDto( oneRaw );
	}

	public DepartmentDto findOneDepartmentByCode( String depCode ) {
		Department oneRaw = this.departmentService.findByCode( depCode );
		return this.departmentService.mapModelToDto( oneRaw );
	}

	public DepartmentDto findOneDepartmentByName( String departmentName ) {
		Department oneRaw = this.departmentService.findByName( departmentName );
		return this.departmentService.mapModelToDto( oneRaw );
	}

	public ResponseDto isLocationValid( Long id, String catName ) {
		ResponseDto responseDto = new ResponseDto();
		Department oneRaw = this.departmentService.findOneRaw( Optional.ofNullable( id ) );
		boolean contains = oneRaw.getDepartmentCategory().getName().toLowerCase().contains( catName.toLowerCase() );
		responseDto.setData( contains );
		return responseDto;
	}

	public ResponseDto createDepartmentInBatch( MultipartFile file ) {
		ResponseDto responseDto = new ResponseDto();
		final String message = "Creating Department in Batch";
		try {
			responseDto.setData( departmentService.createInBatchFromExcel( file ) );
			responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
			responseDto.setHttpStatusText( HmisConstant.STATUS_200 );
			responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
		} catch ( Exception e ) {
			responseDto.setHttpStatusText( Arrays.toString( e.getStackTrace() ) );
			responseDto.setMessage( e.getMessage() );
			commonService.setLOGGER( HmisConstant.DEBUG, message, Optional.of( HmisConstant.LOGGER_FAILED ) );
		}
		return responseDto;
	}

	public ResponseDto updateOneDepartment( DepartmentDto departmentDto ) {
		ResponseDto responseDto = new ResponseDto();
		final String message = " Update One Department";
		if ( departmentService.isDepartmentExist( departmentDto ) ) {
			responseDto.setMessage( HmisConstant.ENTITY_EXIST );
			responseDto.setHttpStatusText( HmisConstant.STATUS_409 );
			responseDto.setHttpStatusCode( 409 );
			return responseDto;
		} else {
			try {
				responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
				responseDto.setData( departmentService.updateOne( departmentDto ) );
				responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
				responseDto.setHttpStatusText( HmisConstant.STATUS_200 );
			} catch ( Exception e ) {
				responseDto.setHttpStatusText( e.toString() );
				responseDto.setMessage( e.getMessage() );
				commonService.setLOGGER( HmisConstant.DEBUG, message, Optional.of( HmisConstant.LOGGER_FAILED ) );
			}
			return responseDto;
		}
	}

	/* Seed Department Category */
	public ResponseDto findAllDepartmentCategory() {
		ResponseDto responseDto = new ResponseDto();
		final String message = " Get All Department Category";
		try {
			responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
			responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
			responseDto.setHttpStatusText( HmisConstant.STATUS_200 );
			responseDto.setData( departmentCategoryService.findAll() );
		} catch ( Exception e ) {
			responseDto.setHttpStatusText( Arrays.toString( e.getStackTrace() ) );
			responseDto.setMessage( e.getMessage() );
			commonService.setLOGGER( HmisConstant.DEBUG, message, Optional.of( HmisConstant.LOGGER_FAILED ) );
		}
		return responseDto;
	}

	public ResponseDto findAllDepartmentWithAllDepartmentCategory() {
		ResponseDto responseDto = new ResponseDto();
		final String message = " Get All Department With All Category";
		try {
			responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
			responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
			responseDto.setHttpStatusText( HmisConstant.STATUS_200 );
			responseDto.setData( departmentService.mapDepartmentWithDepartmentCategories() );
		} catch ( Exception e ) {
			responseDto.setHttpStatusText( Arrays.toString( e.getStackTrace() ) );
			responseDto.setMessage( e.getMessage() );
			commonService.setLOGGER( HmisConstant.DEBUG, message, Optional.of( HmisConstant.LOGGER_FAILED ) );
		}
		return responseDto;
	}

	/* Seed Surgery */
	public ResponseDto findAllSurgery() {
		ResponseDto responseDto = new ResponseDto();
		final String message = " Get All Surgery";
		try {
			responseDto.setData( surgeryService.findAll() );
			responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
			responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
			responseDto.setHttpStatusText( HmisConstant.STATUS_200 );
			LOGGER.debug( commonService.getLoggerMessage( message, HmisConstant.LOGGER_TYPE_PASSED ) );
		} catch ( Exception e ) {
			responseDto.setHttpStatusText( e.toString() );
			responseDto.setMessage( e.getMessage() );
			commonService.setLOGGER( HmisConstant.DEBUG, message, Optional.of( HmisConstant.LOGGER_FAILED ) );
		}
		return responseDto;
	}

	public ResponseDto createOneSurgery( SurgeryDto surgeryDto ) {
		ResponseDto responseDto = new ResponseDto();
		final String message = " Create One Surgery";
		if ( surgeryService.isSurgeryExist( surgeryDto ) ) {
			responseDto.setMessage( HmisConstant.ENTITY_EXIST );
			responseDto.setHttpStatusText( HmisConstant.STATUS_409 );
			responseDto.setHttpStatusCode( 409 );
			return responseDto;
		} else {
			try {
				responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
				responseDto.setData( surgeryService.createOne( surgeryDto ) );
				responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
				responseDto.setHttpStatusText( HmisConstant.STATUS_200 );
			} catch ( Exception e ) {
				responseDto.setHttpStatusText( e.toString() );
				responseDto.setMessage( e.getMessage() );
				commonService.setLOGGER( HmisConstant.DEBUG, message, Optional.of( HmisConstant.LOGGER_FAILED ) );
			}
			return responseDto;
		}

	}

	/* Seed Drug Classification */
	public ResponseDto createOneDrugClassification( DrugClassificationDto drugClassificationDto ) {
		ResponseDto responseDto = new ResponseDto();
		final String message = " Create One Drug Classification";
		if ( drugClassificationService.isDrugClassificationExist( drugClassificationDto ) ) {
			responseDto.setMessage( HmisConstant.ENTITY_EXIST );
			responseDto.setHttpStatusText( HmisConstant.STATUS_409 );
			responseDto.setHttpStatusCode( 409 );
			return responseDto;
		} else {
			try {
				responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
				responseDto.setData( drugClassificationService.createOne( drugClassificationDto ) );
				responseDto.setHttpStatusText( HmisConstant.STATUS_200 );
				responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
			} catch ( Exception e ) {
				responseDto.setHttpStatusText( e.toString() );
				responseDto.setMessage( e.getMessage() );
				commonService.setLOGGER( HmisConstant.DEBUG, message, Optional.of( HmisConstant.LOGGER_FAILED ) );
			}
			return responseDto;
		}
	}

	public ResponseDto findAllDrugClassification() {
		ResponseDto responseDto = new ResponseDto();
		final String message = " Get All Drug Classification";
		try {
			responseDto.setData( drugClassificationService.findAll() );
			responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
			responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
			responseDto.setHttpStatusText( HmisConstant.STATUS_200 );
		} catch ( Exception e ) {
			responseDto.setHttpStatusText( e.toString() );
			responseDto.setMessage( e.getMessage() );
			commonService.setLOGGER( HmisConstant.DEBUG, message, Optional.of( HmisConstant.LOGGER_FAILED ) );
		}
		return responseDto;
	}

	/* Seed Drug Formulation */
	public ResponseDto findAllDrugFormulation() {
		ResponseDto responseDto = new ResponseDto();
		final String message = " Get All Drug Formulation";
		try {
			responseDto.setData( drugFormulationService.findAll() );
			responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
			responseDto.setHttpStatusText( HmisConstant.STATUS_200 );
			responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
		} catch ( Exception e ) {
			responseDto.setHttpStatusText( e.toString() );
			responseDto.setMessage( e.getMessage() );
			commonService.setLOGGER( HmisConstant.DEBUG, message, Optional.of( HmisConstant.LOGGER_FAILED ) );
		}
		return responseDto;
	}

	public ResponseDto createOneDrugFormulation( DrugFormulationDto drugFormulationDto ) throws BadRequestException {
		ResponseDto responseDto = new ResponseDto();
		final String message = " Create One Drug Formulation";
		if ( drugFormulationService.isDrugFormulationExist( drugFormulationDto ) ) {
			responseDto.setMessage( HmisConstant.ENTITY_EXIST );
			responseDto.setHttpStatusText( HmisConstant.STATUS_409 );
			responseDto.setHttpStatusCode( 409 );
			return responseDto;
		} else {
			try {
				responseDto.setData( drugFormulationService.createOne( drugFormulationDto ) );
				responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
				responseDto.setHttpStatusText( HmisConstant.STATUS_200 );
				responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
			} catch ( Exception e ) {
				responseDto.setHttpStatusText( e.toString() );
				responseDto.setMessage( e.getMessage() );
				commonService.setLOGGER( HmisConstant.DEBUG, message, Optional.of( HmisConstant.LOGGER_FAILED ) );
			}
			return responseDto;
		}
	}

	/* Seed Ward */
	public ResponseDto findAllWard() {
		ResponseDto responseDto = new ResponseDto();
		final String message = " Get All Ward";
		try {
			responseDto.setData( departmentService.findAllWards() );
			responseDto.setHttpStatusText( HmisConstant.STATUS_200 );
			responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
			responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
		} catch ( Exception e ) {
			responseDto.setHttpStatusText( e.toString() );
			responseDto.setMessage( e.getMessage() );
			commonService.setLOGGER( HmisConstant.DEBUG, message, Optional.of( HmisConstant.LOGGER_FAILED ) );
		}
		return responseDto;
	}

	public ResponseDto findWardsWithBedCount() {
		ResponseDto responseDto = new ResponseDto();
		try {
			responseDto.setData( this.wardService.findAll() );
			responseDto.setHttpStatusCode( 200 );
			return responseDto;
		} catch ( Exception e ) {
			responseDto.setHttpStatusText( e.getMessage() );
			responseDto.setMessage( e.toString() );
			throw new ResponseStatusException( HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage() );
		}

	}


	public ResponseDto createBeds( WardDto wardDto ) {
		ResponseDto responseDto = new ResponseDto();
		final String message = " Create Beds & Allocate to Ward";
		try {
			responseDto.setData( this.wardService.addBedsToWard( wardDto ) );
			responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
			responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
			responseDto.setHttpStatusText( HmisConstant.STATUS_200 );
			return responseDto;
		} catch ( Exception e ) {
			responseDto.setHttpStatusText( e.getMessage() );
			responseDto.setMessage( e.toString() );
			commonService.setLOGGER( HmisConstant.DEBUG, message, Optional.of( HmisConstant.LOGGER_FAILED ) );
			throw new ResponseStatusException( HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage() );
		}
	}


	public ResponseDto findBedsByWard( Long wardId ) {
		ResponseDto responseDto = new ResponseDto();
		responseDto.setData( this.wardService.findOne( wardId ) );
		responseDto.setHttpStatusCode( 200 );
		return responseDto;
	}


	/*  Seed Revenue Department */
	public ResponseDto findAllRevenueDepartment() {
		ResponseDto responseDto = new ResponseDto();
		final String message = " Get All Revenue Department";
		try {
			responseDto.setData( revenueDepartmentService.findAll() );
			responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
			responseDto.setHttpStatusText( HmisConstant.STATUS_200 );
		} catch ( Exception e ) {
			responseDto.setHttpStatusText( e.getMessage() );
			responseDto.setMessage( e.toString() );
			commonService.setLOGGER( HmisConstant.DEBUG, message, Optional.of( HmisConstant.LOGGER_FAILED ) );
		}
		return responseDto;
	}

	public ResponseDto createOneRevenueDepartment( RevenueDepartmentDto revenueDepartmentDto ) {
		ResponseDto responseDto = new ResponseDto();
		final String message = " Create One Revenue Department";
		if ( revenueDepartmentService.isRevenueDepartmentExist( revenueDepartmentDto ) ) {
			responseDto.setHttpStatusCode( 409 );
			responseDto.setHttpStatusText( HmisConstant.STATUS_409 );
			responseDto.setMessage( HmisConstant.ENTITY_EXIST );
			return responseDto;
		} else {
			try {
				responseDto.setData( revenueDepartmentService.createOne( revenueDepartmentDto ) );
				responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
				responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
				responseDto.setHttpStatusText( HmisConstant.STATUS_200 );
			} catch ( Exception e ) {
				responseDto.setHttpStatusText( e.getMessage() );
				responseDto.setMessage( e.toString() );
				commonService.setLOGGER( HmisConstant.DEBUG, message, Optional.of( HmisConstant.LOGGER_FAILED ) );
			}
			return responseDto;
		}
	}

	public ResponseDto createRevenueDepartmentInBatch( List<RevenueDepartmentDto> revenueDepartmentDtoList ) {
		return null;
	}

	public ResponseDto createRevenueDepartmentFromExcel( MultipartFile file ) {
		return null;
	}

	/* Seed Revenue Type */
	public ResponseDto createOneRevenueDepartmentType( RevenueDepartmentTypeDto revenueDepartmentTypeDto ) {
		return null;
	}

	// not in use
	public ResponseDto seedDefaultRevenueDepartmentType() {
		ResponseDto responseDto = new ResponseDto();
		responseDto.setData( revenueDepartmentTypeService.seedDefaultRevenueDepartmentTypes() );
		responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
		responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
		responseDto.setHttpStatusText( HmisConstant.STATUS_200 );

		return responseDto;
	}

	public ResponseDto getAllRevenueDepartmentType() {
		ResponseDto responseDto = new ResponseDto();
		try {
			responseDto.setData( revenueDepartmentTypeService.findAll() );
			responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
			responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
			responseDto.setHttpStatusText( HmisConstant.STATUS_200 );
		} catch ( Exception e ) {
			responseDto.setMessage( e.getMessage() );
		}
		return responseDto;
	}

	/* Seed Speciality Unit */
	public ResponseDto findAllSpecialityUnit() {
		ResponseDto responseDto = new ResponseDto();
		final String message = " Find All Speciality Unit";
		try {
			responseDto.setData( specialityUnitService.findAll() );
			responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
			responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
			responseDto.setHttpStatusText( HmisConstant.STATUS_200 );
		} catch ( Exception e ) {
			responseDto.setHttpStatusText( e.getMessage() );
			responseDto.setMessage( e.toString() );
			commonService.setLOGGER( HmisConstant.DEBUG, message, Optional.of( HmisConstant.LOGGER_FAILED ) );
		}
		return responseDto;
	}

	public ResponseDto createOneSpecialityUnit( SpecialityUnitDto specialityUnitDto ) {
		ResponseDto responseDto = new ResponseDto();
		final String message = " Create One Speciality Unit";
		if ( specialityUnitService.isSpecialityUnitExist( specialityUnitDto ) ) {
			responseDto.setHttpStatusCode( 409 );
			responseDto.setHttpStatusText( HmisConstant.STATUS_409 );
			responseDto.setMessage( HmisConstant.ENTITY_EXIST );
			return responseDto;
		} else {
			try {
				responseDto.setData( specialityUnitService.createOne( specialityUnitDto ) );
				responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
				responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
				responseDto.setHttpStatusText( HmisConstant.STATUS_200 );
			} catch ( Exception e ) {
				responseDto.setHttpStatusText( e.getMessage() );
				responseDto.setMessage( e.toString() );
				commonService.setLOGGER( HmisConstant.DEBUG, message, Optional.of( HmisConstant.LOGGER_FAILED ) );
			}
			return responseDto;
		}
	}

	/* Seed Pharmacy Patient Category */
	public ResponseDto findAllPharmacyPatientCategory() {
		ResponseDto responseDto = new ResponseDto();
		final String message = " Find All Pharmacy Patient Category";
		try {
			responseDto.setData( pharmacyPatientCategoryService.findAll() );
			responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
			responseDto.setHttpStatusText( HmisConstant.STATUS_200 );
			responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
		} catch ( Exception e ) {
			responseDto.setHttpStatusText( e.getMessage() );
			responseDto.setMessage( e.toString() );
			commonService.setLOGGER( HmisConstant.DEBUG, message, Optional.of( HmisConstant.LOGGER_FAILED ) );
		}
		return responseDto;
	}

	public ResponseDto createOnePharmacyPatientCategory( PharmacyPatientCategoryDto pharmacyPatientCategoryDto ) {
		ResponseDto responseDto = new ResponseDto();
		final String message = " Create One Pharmacy Patient Category";
		if ( pharmacyPatientCategoryService.isPharmacyPatientCategoryExist( pharmacyPatientCategoryDto ) ) {
			responseDto.setHttpStatusCode( 409 );
			responseDto.setHttpStatusText( HmisConstant.STATUS_409 );
			responseDto.setMessage( HmisConstant.ENTITY_EXIST );
			return responseDto;
		} else {
			try {
				responseDto.setData( pharmacyPatientCategoryService.createOne( pharmacyPatientCategoryDto ) );
				responseDto.setHttpStatusText( HmisConstant.STATUS_200 );
				responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
				responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
			} catch ( Exception e ) {
				responseDto.setHttpStatusText( e.getMessage() );
				responseDto.setMessage( e.toString() );
				commonService.setLOGGER( HmisConstant.DEBUG, message, Optional.of( HmisConstant.LOGGER_FAILED ) );
			}
			return responseDto;
		}
	}

	// Seed Payment Method
//	public ResponseDto seedDefaultPaymentMethod() {
//		ResponseDto responseDto = new ResponseDto();
//		responseDto.setData(paymentMethodService.seedDefaultData());
//		responseDto.setHttpStatusCode(HmisConstant.OK_CODE);
//		responseDto.setMessage(HmisConstant.SUCCESS_MESSAGE);
//		responseDto.setHttpStatusText(HmisConstant.STATUS_200);
//		return responseDto;
//	}

	public ResponseDto getAllPaymentMethod() {
		ResponseDto responseDto = new ResponseDto();
		try {
			responseDto.setData( paymentMethodService.findAll() );
			responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
			responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
			responseDto.setHttpStatusText( HmisConstant.STATUS_200 );
		} catch ( Exception e ) {
			responseDto.setHttpStatusText( HmisConstant.ERROR_MESSAGE );
			responseDto.setMessage( e.getMessage() );
		}
		return responseDto;
	}

	// Pharmacy Patient Category Types
	public ResponseDto seedDefaultPharmacyPatientCategoryTypes() {
		ResponseDto responseDto = new ResponseDto();
		responseDto.setData( pharmacyPatientCategoryTypeService.seedDefaultData() );
		responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
		responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
		responseDto.setHttpStatusText( HmisConstant.STATUS_200 );

		return responseDto;
	}

	public ResponseDto getAllPharmacyPatientCategoryTypes() {
		ResponseDto responseDto = new ResponseDto();
		try {
			responseDto.setData( pharmacyPatientCategoryTypeService.findAll() );
			responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
			responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
			responseDto.setHttpStatusText( HmisConstant.STATUS_200 );
		} catch ( Exception e ) {
			responseDto.setMessage( e.getMessage() );
			responseDto.setHttpStatusCode( HmisConstant.BAD_REQUEST );
		}
		return responseDto;
	}


	/* Seed Pharmacy Supplier Category  */
	public ResponseDto findAllPharmacySupplierCategory() {
		ResponseDto responseDto = new ResponseDto();
		final String message = " Find All Pharmacy Supplier Category";
		try {
			responseDto.setData( pharmacySupplierCategoryService.findAll() );
			responseDto.setHttpStatusText( HmisConstant.STATUS_200 );
			responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
			responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
		} catch ( Exception e ) {
			responseDto.setHttpStatusText( e.getMessage() );
			responseDto.setMessage( e.toString() );
			commonService.setLOGGER( HmisConstant.DEBUG, message, Optional.of( HmisConstant.LOGGER_FAILED ) );
		}
		return responseDto;
	}

	public ResponseDto createOnePharmacySupplierCategory( PharmacySupplierCategoryDto supplierCategoryDto ) {
		ResponseDto responseDto = new ResponseDto();
		final String message = " Create One Pharmacy Supplier Category";
		if ( pharmacySupplierCategoryService.isPharmacySupplierCategoryExist( supplierCategoryDto ) ) {
			responseDto.setHttpStatusCode( 409 );
			responseDto.setHttpStatusText( HmisConstant.STATUS_409 );
			responseDto.setMessage( HmisConstant.ENTITY_EXIST );
			return responseDto;
		} else {
			try {
				responseDto.setData( pharmacySupplierCategoryService.createOne( supplierCategoryDto ) );
				responseDto.setHttpStatusText( HmisConstant.STATUS_200 );
				responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
				responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
			} catch ( Exception e ) {
				responseDto.setHttpStatusText( e.getMessage() );
				responseDto.setMessage( e.toString() );
				commonService.setLOGGER( HmisConstant.DEBUG, message, Optional.of( HmisConstant.LOGGER_FAILED ) );
			}
			return responseDto;
		}
	}

	/*Seed Nursing Note Label*/
	public ResponseDto findAllNursingNoteLabel() {
		ResponseDto responseDto = new ResponseDto();
		final String message = " Find All Nursing Note Label";
		try {
			responseDto.setData( nursingNoteLabelService.findAll() );
			responseDto.setHttpStatusText( HmisConstant.STATUS_200 );
			responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
			responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
		} catch ( Exception e ) {
			responseDto.setHttpStatusText( e.getMessage() );
			responseDto.setMessage( e.toString() );
			commonService.setLOGGER( HmisConstant.DEBUG, message, Optional.of( HmisConstant.LOGGER_FAILED ) );
		}
		return responseDto;
	}

	public ResponseDto createOneNursingNoteLabel( NursingNoteLabelDto nursingNoteLabelDto ) {
		ResponseDto responseDto = new ResponseDto();
		final String message = " Create One Nursing Note Label";
		if ( nursingNoteLabelService.isNursingNoteLabelExist( nursingNoteLabelDto ) ) {
			responseDto.setHttpStatusCode( 409 );
			responseDto.setHttpStatusText( HmisConstant.STATUS_409 );
			responseDto.setMessage( HmisConstant.ENTITY_EXIST );
			return responseDto;
		} else {
			try {
				responseDto.setData( nursingNoteLabelService.createOne( nursingNoteLabelDto ) );
				responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
				responseDto.setHttpStatusText( HmisConstant.STATUS_200 );
				responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
			} catch ( Exception e ) {
				responseDto.setHttpStatusText( e.getMessage() );
				responseDto.setMessage( e.toString() );
				commonService.setLOGGER( HmisConstant.DEBUG, message, Optional.of( HmisConstant.LOGGER_FAILED ) );
			}
			return responseDto;
		}
	}

	/*Seed Lab Specimen*/
	public ResponseDto findAllLabSpecimen() {
		ResponseDto responseDto = new ResponseDto();
		final String message = " Find All Lab Specimen";
		try {
			responseDto.setData( labSpecimenService.findAll() );
			responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
			responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
			responseDto.setHttpStatusText( HmisConstant.STATUS_200 );
		} catch ( Exception e ) {
			responseDto.setHttpStatusText( e.getMessage() );
			responseDto.setMessage( e.toString() );
			commonService.setLOGGER( HmisConstant.DEBUG, message, Optional.of( HmisConstant.LOGGER_FAILED ) );
		}
		return responseDto;
	}

	public ResponseDto createOneLabSpecimen( LabSpecimenDto labSpecimenDto ) {
		ResponseDto responseDto = new ResponseDto();
		final String message = " Create One Lab Specimen";
		if ( labSpecimenService.isLabSpecimenExist( labSpecimenDto ) ) {
			responseDto.setHttpStatusCode( 409 );
			responseDto.setHttpStatusText( HmisConstant.STATUS_409 );
			responseDto.setMessage( HmisConstant.ENTITY_EXIST );
			return responseDto;
		} else {
			try {
				responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
				responseDto.setData( labSpecimenService.createOne( labSpecimenDto ) );
				responseDto.setHttpStatusText( HmisConstant.STATUS_200 );
				responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
			} catch ( Exception e ) {
				responseDto.setHttpStatusText( e.getMessage() );
				responseDto.setMessage( e.toString() );
				commonService.setLOGGER( HmisConstant.DEBUG, message, Optional.of( HmisConstant.LOGGER_FAILED ) );
			}
			return responseDto;
		}
	}

	/*Seed Organism*/
	public ResponseDto findAllOrganism() {
		ResponseDto responseDto = new ResponseDto();
		final String message = " Find All Organism";
		try {
			responseDto.setData( organismService.findAll() );
			responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
			responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
			responseDto.setHttpStatusText( HmisConstant.STATUS_200 );
		} catch ( Exception e ) {
			responseDto.setMessage( e.toString() );
			responseDto.setHttpStatusText( e.getMessage() );
			commonService.setLOGGER( HmisConstant.DEBUG, message, Optional.of( HmisConstant.LOGGER_FAILED ) );
		}
		return responseDto;
	}

	public ResponseDto createOneOrganism( OrganismDto organismDto ) {
		ResponseDto responseDto = new ResponseDto();
		final String message = " Create One Organism";
		if ( organismService.isOrganismExist( organismDto ) ) {
			responseDto.setHttpStatusCode( 409 );
			responseDto.setHttpStatusText( HmisConstant.STATUS_409 );
			responseDto.setMessage( HmisConstant.ENTITY_EXIST );
			return responseDto;
		} else {
			try {
				responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
				responseDto.setData( organismService.createOne( organismDto ) );
				responseDto.setHttpStatusText( HmisConstant.STATUS_200 );
				responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
			} catch ( Exception e ) {
				responseDto.setMessage( e.toString() );
				responseDto.setHttpStatusText( e.getMessage() );
				commonService.setLOGGER( HmisConstant.DEBUG, message, Optional.of( HmisConstant.LOGGER_FAILED ) );
			}
			return responseDto;
		}
	}

	/*Seed Antibiotics*/
	public ResponseDto findAllAntibiotics() {
		ResponseDto responseDto = new ResponseDto();
		final String message = " Find All Antibiotics";
		try {
			responseDto.setData( antibioticsService.findAll() );
			responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
			responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
			responseDto.setHttpStatusText( HmisConstant.STATUS_200 );
		} catch ( Exception e ) {
			responseDto.setMessage( e.toString() );
			responseDto.setHttpStatusText( e.getMessage() );
			commonService.setLOGGER( HmisConstant.DEBUG, message, Optional.of( HmisConstant.LOGGER_FAILED ) );
		}
		return responseDto;
	}

	public ResponseDto createOneAntibiotics( AntibioticsDto antibioticsDto ) {
		ResponseDto responseDto = new ResponseDto();
		final String message = " Create One Antibiotics";
		if ( antibioticsService.isAntibioticsExist( antibioticsDto ) ) {
			responseDto.setHttpStatusCode( 409 );
			responseDto.setHttpStatusText( HmisConstant.STATUS_409 );
			responseDto.setMessage( HmisConstant.ENTITY_EXIST );
			return responseDto;
		} else {
			try {
				responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
				responseDto.setData( antibioticsService.createOne( antibioticsDto ) );
				responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
				responseDto.setHttpStatusText( HmisConstant.STATUS_200 );
			} catch ( Exception e ) {
				responseDto.setMessage( e.toString() );
				responseDto.setHttpStatusText( e.getMessage() );
				commonService.setLOGGER( HmisConstant.DEBUG, message, Optional.of( HmisConstant.LOGGER_FAILED ) );
			}
			return responseDto;
		}
	}

	/*Seed Bill Waiver Category*/
	public ResponseDto findAllBillWaiver() {
		ResponseDto responseDto = new ResponseDto();
		final String message = " Find All Bill Waiver Category";
		try {
			responseDto.setData( billWaiverCategoryService.findAll() );
			responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
			responseDto.setHttpStatusText( HmisConstant.STATUS_200 );
			responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
		} catch ( Exception e ) {
			responseDto.setMessage( e.toString() );
			responseDto.setHttpStatusText( e.getMessage() );
			commonService.setLOGGER( HmisConstant.DEBUG, message, Optional.of( HmisConstant.LOGGER_FAILED ) );
		}
		return responseDto;
	}

	public ResponseDto createOneBillWaiverCategory( BillWaiverCategoryDto billWaiverCategoryDto ) {
		ResponseDto responseDto = new ResponseDto();
		final String message = " Create One Bill Waiver Category";
		if ( billWaiverCategoryService.isBillWaiverExist( billWaiverCategoryDto ) ) {
			responseDto.setHttpStatusCode( 409 );
			responseDto.setHttpStatusText( HmisConstant.STATUS_409 );
			responseDto.setMessage( HmisConstant.ENTITY_EXIST );
			return responseDto;
		} else {
			try {
				responseDto.setData( billWaiverCategoryService.createOne( billWaiverCategoryDto ) );
				responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
				responseDto.setHttpStatusCode( HmisConstant.OK_CODE );
				responseDto.setHttpStatusText( HmisConstant.STATUS_200 );
			} catch ( Exception e ) {
				responseDto.setHttpStatusText( e.getMessage() );
				commonService.setLOGGER( HmisConstant.DEBUG, message, Optional.of( HmisConstant.LOGGER_FAILED ) );
				responseDto.setMessage( e.toString() );
			}
			return responseDto;
		}
	}


	public ResponseDto findAllNationality() {
		ResponseDto responseDto = new ResponseDto();
		responseDto.setData( this.nationalityService.findParentOnly() );
		return responseDto;
	}


	public ResponseDto findNationalityGroupByParent() {
		ResponseDto responseDto = new ResponseDto();
		responseDto.setData( this.nationalityService.findAllGroupByParent() );
		return responseDto;
	}

	public ResponseDto createNationality( NationalityDto nationalityDto ) {
		ResponseDto responseDto = new ResponseDto();
		try {
			responseDto.setData( this.nationalityService.createParentAndChildren( nationalityDto ) );
		} catch ( Exception e ) {
			responseDto.setHttpStatusText( e.getMessage() );
		}
		return responseDto;
	}


	public ResponseDto findAllNationalityParent() {
		ResponseDto responseDto = new ResponseDto();
		try {
			responseDto.setData( this.nationalityService.findParentOnly() );
		} catch ( Exception e ) {
			responseDto.setHttpStatusText( e.getMessage() );
		}
		return responseDto;
	}

	/* Ethnic Group*/
	public ResponseDto createEthnicGroup( EthnicGroupDto ethnicGroup ) {
		ResponseDto responseDto = new ResponseDto();
		try {
			responseDto.setData( this.ethnicGroupService.createEthnicGroup( ethnicGroup.getName() ) );
		} catch ( Exception e ) {
			responseDto.setHttpStatusText( e.getMessage() );
		}
		return responseDto;
	}

	public ResponseDto updateEthnicGroup( EthnicGroupDto ethnicGroup ) {
		ResponseDto responseDto = new ResponseDto();
		EthnicGroup ethnicGroup1 = this.ethnicGroupService.updateEthnicGroup( ethnicGroup.getId(), ethnicGroup.getName() );
		responseDto.setData( ethnicGroup1 );
		return responseDto;
	}

	public ResponseDto findAllEthnicGroup() {
		ResponseDto responseDto = new ResponseDto();
		try {
			responseDto.setData( this.ethnicGroupService.findAllEthnicGroup() );
		} catch ( Exception e ) {
			responseDto.setHttpStatusText( e.getMessage() );
		}
		return responseDto;
	}

	public ResponseDto findAllMeansOfIdentification() {
		ResponseDto responseDto = new ResponseDto();
		try {
			responseDto.setData( this.meansOfIdentificationService.findAll() );
		} catch ( Exception e ) {
			responseDto.setHttpStatusText( e.getMessage() );
		}
		return responseDto;
	}

	public ResponseDto createMeansOfIdentification( MeansOfIdentificationDto dto ) {
		ResponseDto responseDto = new ResponseDto();
		try {
			responseDto.setData( this.meansOfIdentificationService.createMeansOfIdentification( dto ) );
		} catch ( Exception e ) {
			responseDto.setHttpStatusText( e.getMessage() );
		}
		return responseDto;
	}

	public ResponseDto findAllNationalityWithChildren() {
		ResponseDto responseDto = new ResponseDto();
		try {
			responseDto.setData( this.nationalityService.findAllNationality() );
		} catch ( Exception e ) {
			responseDto.setHttpStatusText( e.getMessage() );
		}
		return responseDto;
	}

	public ResponseDto findAllScheme() {
		ResponseDto responseDto = new ResponseDto();
		try {
			this.schemeService.findAll();
			responseDto.setData( null );
		} catch ( Exception e ) {
			responseDto.setHttpStatusText( e.getMessage() );
		}
		return responseDto;
	}

	public ResponseDto<?> createScheme( SchemeDto schemeDto ) {
		if ( schemeDto.getSchemePlans() == null || schemeDto.getSchemePlans().size() == 0 ) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Scheme Plan is required" );
		}
		ResponseDto<?> responseDto = new ResponseDto<>();
		try {
			responseDto.setData( this.schemeService.createOne( schemeDto ) );

		} catch ( Exception e ) {
			responseDto.setMessage( e.getMessage() );
		}
		return responseDto;
	}

	public List<Department> findDepartmentByDepartmentCategory( String departmentCategoryName ) {
		return this.departmentService.findDepartmentByDepartmentCategory( departmentCategoryName );
	}

	public List<WardDto> searchWard( String search ) {
		return this.wardService.searchWard( search );
	}

	public ResponseDto updateWardBedCount( WardDto wardDto ) {
		ResponseDto responseDto = new ResponseDto();
		try {
			responseDto.setData( this.wardService.updateWardBedCount( wardDto ) );
			responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
		} catch ( Exception e ) {
			responseDto.setHttpStatusText( e.getMessage() );
			responseDto.setMessage( e.getMessage() );
		}
		return responseDto;
	}

	public List<SpecialityUnitDto> searchSpeciality( String search ) {
		try {
			return this.specialityUnitService.searchByNameOrCode( search );
		} catch ( Exception e ) {
			throw new ResponseStatusException( HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage() );
		}
	}

	public ResponseDto findDepositRevenueDepartment() {
		try {
			ResponseDto responseDto = new ResponseDto();
			responseDto.setData( this.revenueDepartmentService.findDepositRevenueDepartment() );
			responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
			return responseDto;
		} catch ( Exception e ) {
			throw new ResponseStatusException( HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage() );
		}

	}

	public ResponseDto updateOneGender( GenderDto genderDto ) {
		try {
			ResponseDto responseDto = new ResponseDto();
			responseDto.setData( this.genderService.updateOne( genderDto ) );
			responseDto.setMessage( HmisConstant.SUCCESS_MESSAGE );
			return responseDto;
		} catch ( Exception e ) {
			throw new ResponseStatusException( HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage() );
		}
	}
}
