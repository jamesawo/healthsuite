package com.hmis.server.hmis.modules.emr.service;

import com.hmis.server.hmis.common.common.model.*;
import com.hmis.server.hmis.common.common.service.GlobalSettingsImpl;
import com.hmis.server.hmis.common.common.service.HmisUtilService;
import com.hmis.server.hmis.common.constant.HmisExceptionMessage;
import com.hmis.server.hmis.common.constant.HmisGlobalSettingKeys;
import com.hmis.server.hmis.modules.emr.IService.IPatientDetailService;
import com.hmis.server.hmis.modules.emr.dto.*;
import com.hmis.server.hmis.modules.emr.model.*;
import com.hmis.server.hmis.modules.emr.repository.PatientDetailRepository;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PatientDetailServiceImpl implements IPatientDetailService {
	private final Logger LOGGER = LoggerFactory.getLogger( this.getClass() );
	private final PatientDetailRepository repository;
	private final PatientNumberServiceImpl patientNumberService;
	private final PatientCardHolderInfoServiceImpl cardHolderInfoService;
	private final PatientContactDetailServiceImpl contactDetailService;
	private final PatientInsuranceDetailServiceImpl insuranceDetailService;
	private final PatientMeansOfIdentificationServiceImpl meansOfIdentificationService;
	private final PatientNOKDetailServiceImpl nokDetailService;
	private final PatientTransferDetailServiceImpl transferDetailService;
	private final GlobalSettingsImpl globalSettingsService;
	private final HmisUtilService utilService;
	private final PatientImageServiceImpl imageService;
	private final PatientRevisitServiceImpl patientRevisitService;
	private final PatientCategoryUpdateLogService categoryUpdateLogService;

	@Autowired
	public PatientDetailServiceImpl(
			PatientDetailRepository repository,
			PatientNumberServiceImpl patientNumberService,
			PatientCardHolderInfoServiceImpl cardHolderInfoService,
			PatientContactDetailServiceImpl contactDetailService,
			PatientInsuranceDetailServiceImpl insuranceDetailService,
			PatientMeansOfIdentificationServiceImpl meansOfIdentificationService,
			PatientNOKDetailServiceImpl nokDetailService,
			PatientTransferDetailServiceImpl transferDetailService,
			GlobalSettingsImpl globalSettingsService,
			@Lazy HmisUtilService utilService,
			PatientImageServiceImpl imageService,
			@Lazy PatientRevisitServiceImpl patientRevisitService,
			PatientCategoryUpdateLogService categoryUpdateLogService
	) {
		this.repository = repository;
		this.patientNumberService = patientNumberService;
		this.cardHolderInfoService = cardHolderInfoService;
		this.contactDetailService = contactDetailService;
		this.insuranceDetailService = insuranceDetailService;
		this.meansOfIdentificationService = meansOfIdentificationService;
		this.nokDetailService = nokDetailService;
		this.transferDetailService = transferDetailService;
		this.globalSettingsService = globalSettingsService;
		this.utilService = utilService;
		this.imageService = imageService;
		this.patientRevisitService = patientRevisitService;
		this.categoryUpdateLogService = categoryUpdateLogService;
	}

	@Override
	public PatientDetail updatePatientDetail( PatientDetailDto detailDto ) {
		if ( detailDto.getPatientId() == null ) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Provide Patient ID" );
		}

		try {
			PatientDetail patientDetail = this.findPatientDetailById( detailDto.getPatientId() );
			this.setPatientBasicDetail( patientDetail, detailDto );

			//update patient means of identification
			if ( detailDto.getPatientMeansOfIdentification() != null && detailDto.getPatientMeansOfIdentification().getId().isPresent() ) {
				this.meansOfIdentificationService.updatePatientMeansOfIdentification(
						detailDto.getPatientMeansOfIdentification() );
			}

			//update patient contact details
			if ( detailDto.getPatientContactDetail() != null && detailDto.getPatientContactDetail().getId().isPresent() ) {
				this.contactDetailService.updatePatientContactDetail( detailDto.getPatientContactDetail() );
			}

			//update patient nok details
			if ( detailDto.getPatientNokDetail() != null && detailDto.getPatientNokDetail().getId().isPresent() ) {
				this.nokDetailService.updatePatientNOKDetail( detailDto.getPatientNokDetail() );
			}

			if ( detailDto.getPassportBase64() != null ) {
				if ( patientDetail.getPatientImage() != null ) {
					//if patient has image, update old patient image
					this.imageService.updatePatientImage( detailDto.getPassportBase64(),
					                                      patientDetail.getPatientImage().getId() );
				}
				else {
					//create new patient image
					PatientImageDto imageDto = this.imageService.setPatientImageDto( detailDto.getPassportBase64(),
					                                                                 patientDetail.getPatientNumber(),
					                                                                 "image/jpeg" );
					this.imageService.savePatientImage( imageDto );
				}
			}
			return this.repository.save( patientDetail );
		} catch ( Exception e ) {
			throw new ResponseStatusException( HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage() );
		}

	}

	@Override
	public PatientDetailDto createPatient( PatientDetailDto dto ) {
		this.checkBeforePatientRegister( dto );
		PatientDetail patientDetail = new PatientDetail();
		PatientInsuranceDetail patientInsuranceDetail = null;
		PatientCardHolderInfo patientCardHolderInfo = null;
		PatientImage patientImage = null;
		PatientContactDetail patientContactDetail = null;
		PatientMeansOfIdentification patientMeansOfIdentification = null;
		PatientNOKDetail patientNOKDetail = null;
		PatientTransferDetail patientTransferDetail = null;
		String patientNumber;

		try {
			// check if patient is a schemed patient
			if ( dto.getPatientCategoryEnum() == PatientCategoryEnum.SCHEME ) {
				// create and set patient insurance data
				if ( ObjectUtils.isNotEmpty( dto.getPatientInsurance() ) ) {
					patientInsuranceDetail = this.insuranceDetailService.createPatientInsuranceDetail(
							dto.getPatientInsurance() );
					patientDetail.setPatientInsuranceDetail( patientInsuranceDetail );
				}
				//create and set patient cardholder data
				if ( ObjectUtils.isNotEmpty( dto.getPatientCardHolder() ) ) {
					patientCardHolderInfo = this.cardHolderInfoService.createPatientCardHolderInfo(
							dto.getPatientCardHolder() );
					patientDetail.setPatientCardHolderInfo( patientCardHolderInfo );
				}
			}

			//create and set patient contact data
			if ( ObjectUtils.isNotEmpty( dto.getPatientContactDetail() ) ) {
				patientContactDetail = this.contactDetailService.createPatientContactDetail(
						dto.getPatientContactDetail() );
				patientDetail.setPatientContactDetail( patientContactDetail );
			}

			//create and set patient means of identification -> not required
			if ( ObjectUtils.isNotEmpty( dto.getPatientMeansOfIdentification() ) ) {
				patientMeansOfIdentification = this.meansOfIdentificationService.createPatientMeansOfIdentification(
						dto.getPatientMeansOfIdentification() );
				patientDetail.setPatientMeansOfIdentification( patientMeansOfIdentification );
			}

			//create and set patient nok data -> not required
			if ( ObjectUtils.isNotEmpty( dto.getPatientNokDetail() ) ) {
				patientNOKDetail = this.nokDetailService.createPatientNOKDetail( dto.getPatientNokDetail() );
				patientDetail.setPatientNokDetail( patientNOKDetail );
			}

			//create and set patient transfer data -> not required
			if ( ObjectUtils.isNotEmpty( dto.getPatientTransferDetails() ) ) {
				patientTransferDetail = this.transferDetailService.createPatientTransferDetail(
						dto.getPatientTransferDetails() );
				patientDetail.setPatientTransferDetail( patientTransferDetail );
			}

			//set patient number
			patientNumber = this.getPatientNumber( dto, this.getPatientRegistrationGlobalSetting().get(
					HmisGlobalSettingKeys.GENERATE_HOSPITAL_NUMBER_FOR_OLD_PATIENT ) );
			patientDetail.setPatientNumber( patientNumber );

			//save patient passport
			if ( dto.getPassportBase64() != null ) {
				//todo:: compress image size before saving
				PatientImageDto imageDto = this.imageService.setPatientImageDto( dto.getPassportBase64(), patientNumber,
				                                                                 "image/jpeg" );
				patientImage = this.imageService.savePatientImage( imageDto );
				patientDetail.setPatientImage( patientImage );
			}

			//set patient basic info
			this.setPatientBasicDetail( patientDetail, dto );

			//save patient
			PatientDetail detail = this.repository.save( patientDetail );

			//set new patient revisit status to true
			this.setPatientActiveVisit( detail.getId(), dto.getDepartmentId() );

			//set dto patient number and return dto to front end
			dto.setPatientNumber( detail.getPatientNumber() );
			return dto;

		} catch ( Exception e ) {
			//delete patient partial data if any breaking error occurs
			if ( patientInsuranceDetail != null ) {
				this.insuranceDetailService.removePatientInsuranceDetail( patientInsuranceDetail.getId() );
			}

			if ( patientCardHolderInfo != null ) {
				this.cardHolderInfoService.removePatientCardHolderInfo( patientCardHolderInfo.getId() );
			}

			if ( patientImage != null ) {
				this.imageService.removePatientImage( patientImage.getId() );
			}

			if ( patientContactDetail != null ) {
				this.contactDetailService.removePatientContactDetail( patientContactDetail.getId() );
			}

			if ( patientMeansOfIdentification != null ) {
				this.meansOfIdentificationService.removePatientMeansOfIdentification(
						patientMeansOfIdentification.getId() );
			}

			if ( patientNOKDetail != null ) {
				nokDetailService.removePatientNOKDetail( patientNOKDetail.getId() );
			}

			if ( patientTransferDetail != null ) {
				this.transferDetailService.removePatientTransferDetail( patientTransferDetail.getId() );
			}

			throw new ResponseStatusException( HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage() );
		}
	}

	@Override
	public PatientDetail findPatientDetailById( Long id ) {
		PatientDetail patientDetail = this.repository.getOne( id );
		if ( patientDetail == null ) {
			throw new ResponseStatusException( HttpStatus.NOT_FOUND,
			                                   HmisExceptionMessage.NOTHING_FOUND + ":  Patient ID" );
		}
		return patientDetail;
	}

	public PatientDetail findByPatientNumber( String patientNumber ) {
		Optional<PatientDetail> optional = this.repository.findByPatientNumber( patientNumber );
		return optional.orElseThrow( () -> new ResponseStatusException( HttpStatus.NOT_FOUND, "Patient Not Found" ) );
	}

	@Override
	public PatientDetail findPatientDetailByName( String name ) {
		return null;
	}

	@Override
	public void removePatientDetail( Long id ) {
	}

	@Override
	public Map<String, String> getPatientRegistrationGlobalSetting() {
		List<String> keys = Arrays.asList( HmisGlobalSettingKeys.ENABLE_REGISTRATION_VALIDATION,
		                                   HmisGlobalSettingKeys.GENERATE_HOSPITAL_NUMBER_FOR_OLD_PATIENT );
		return this.globalSettingsService.findValuesByKeyList( keys );
	}

	/**
	 * @param patientId
	 * @param status
	 * @deprecated use
	 */
	@Deprecated
	@Override
	public void updatePatientRevisitStatus( Long patientId, boolean status ) {
	}

	public void updatePatientDetail( PatientDetail detail ) {
		if ( detail != null ) {
			this.repository.save( detail );
		}
	}

	public void setPatientActiveVisit( Long patientId, Long clinicId ) {
		this.patientRevisitService.setPatientVisitRecordAfterRegistration( patientId, clinicId );
	}

	public void updatePatientScheme( PatientDetailDto patientDetailDto ) {
		this.insuranceDetailService.updatePatientInsuranceDetail( patientDetailDto.getPatientInsurance() );
		this.cardHolderInfoService.updatePatientCardHolderInfo( patientDetailDto.getPatientCardHolder() );
	}

	public void changePatientCategory( PatientCategoryUpdate payload ) {
		PatientDetailDto detailDto = payload.getPatient();
		PatientCategoryEnum newCategory = payload.getNewCategory();
		PatientDetail patientDetail = this.findPatientDetailById( detailDto.getPatientId() );
		PatientCategoryEnum patientCategory = patientDetail.getPatientCategory();

		if ( patientCategory.equals( newCategory ) ) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST,
			                                   "No update is required, Patient Category is same." );
		}

		try {
			if ( newCategory.equals( PatientCategoryEnum.SCHEME ) && !patientCategory.equals(
					PatientCategoryEnum.SCHEME ) ) {
				this.changeGeneralPatientToScheme( patientDetail, detailDto, payload );
			}
			else if ( newCategory.equals( PatientCategoryEnum.GENERAL ) && !patientCategory.equals(
					PatientCategoryEnum.GENERAL ) ) {
				this.changeSchemePatientToGeneral( patientDetail, payload, detailDto );
			}
		} catch ( Exception e ) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, e.getMessage() );
		}

	}


	public void changeGeneralPatientToScheme(
			PatientDetail patientDetail, PatientDetailDto detailDto, PatientCategoryUpdate payload ) {
		PatientInsuranceDetail insuranceDetail = this.insuranceDetailService.createPatientInsuranceDetail(
				detailDto.getPatientInsurance() );
		patientDetail.setPatientInsuranceDetail( insuranceDetail );

		PatientCardHolderInfo cardHolderInfo = this.cardHolderInfoService.createPatientCardHolderInfo(
				detailDto.getPatientCardHolder() );
		patientDetail.setPatientCardHolderInfo( cardHolderInfo );
		patientDetail.setPatientCategory( PatientCategoryEnum.SCHEME );
		this.repository.saveAndFlush( patientDetail );

		this.categoryUpdateLogService.addUpdateLog( patientDetail,
		                                            payload.getLocation().getId().get(),
		                                            payload.getUser().getId().get(),
		                                            detailDto.getPatientCategoryEnum(),
		                                            null );
	}

	public void changeSchemePatientToGeneral(
			PatientDetail patientDetail, PatientCategoryUpdate payload, PatientDetailDto detailDto ) {
		patientDetail.setPatientCategory( PatientCategoryEnum.GENERAL );
		PatientInsuranceDetail prevInsuranceDetails = patientDetail.getPatientInsuranceDetail();
		patientDetail.setPatientInsuranceDetail( null );
		this.repository.saveAndFlush( patientDetail );
		this.categoryUpdateLogService.addUpdateLog( patientDetail,
		                                            payload.getLocation().getId().get(),
		                                            payload.getUser().getId().get(),
		                                            detailDto.getPatientCategoryEnum(),
		                                            prevInsuranceDetails.getId()
		);

	}


	@Override
	public Scheme findPatientScheme( Long patientId ) {
		PatientDetail patientDetailById = this.findPatientDetailById( patientId );
		return patientDetailById.getPatientInsuranceDetail().getScheme();
	}

	@Override
	public Scheme findPatientScheme( PatientDetail patientDetail ) {
		if ( patientDetail.getPatientCategory() != PatientCategoryEnum.SCHEME ) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Patient must be Schemed" );
		}
		return patientDetail.getPatientInsuranceDetail().getScheme();
	}

	public SchemePlan findPatientSchemePlan( PatientDetail patient ) {
		if ( patient.getPatientCategory().equals( PatientCategoryEnum.SCHEME ) ) {
			return patient.getPatientInsuranceDetail().getSchemePlan();
		}
		return new SchemePlan();
	}

	@Override
	public String findPatientSchemeApprovalCode( PatientDetail patient ) {
		if ( patient.getPatientInsuranceDetail() == null ) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Patient must be Schemed" );
		}
		return patient.getPatientInsuranceDetail().getApprovalCode();
	}

	@Override
	public String findPatientDiagnosis( PatientDetail patient ) {
		if ( patient.getPatientInsuranceDetail() == null ) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Patient must be Schemed" );
		}
		return patient.getPatientInsuranceDetail().getDiagnosis();
	}

	@Override
	public String findPatientSchemeCode( PatientDetail patient ) {
		return this.findPatientScheme( patient ).getInsuranceCode();
	}

	@Override
	public PatientDetailDto mapToPatientDto( PatientDetail patientDetail ) {
		return patientDetail.transformToDto();
	}

	private String getPatientNumber( PatientDetailDto dto, String canGenerateOldPatientNumber ) {
		if ( dto.getPatientTypeEnum() == PatientTypeEnum.OLD && canGenerateOldPatientNumber.equals(
				HmisGlobalSettingKeys.FALSE ) ) {
			Optional<PatientDetail> patientDetail = this.repository.findByPatientNumber( dto.getPatientNumber() );
			if ( patientDetail.isPresent() ) {
				throw new ResponseStatusException( HttpStatus.BAD_REQUEST,
				                                   HmisExceptionMessage.EXCEPTION_DUPLICATE_PN );
			}
			return dto.getPatientNumber();
		}
		else {
			return this.patientNumberService.generatePatientNumber();
		}
	}

	private boolean isValidReceipt( String receiptNumber ) {
		// validate receipt
		//todo::every used receipt must be tied to what service it was used, and who used it.
		return true;
	}

	//todo: implement receipt validation in receipt service
	private void checkBeforePatientRegister( PatientDetailDto dto ) {
		Map<String, String> globalSettings = this.getPatientRegistrationGlobalSetting();
		// get global settings value pair
		if ( globalSettings.isEmpty() ) {
			throw new ResponseStatusException( HttpStatus.FORBIDDEN, HmisExceptionMessage.EXCEPTION_NO_GLOBAL_SETTING );
		}

		// validate receipt
		boolean shouldValidateReceipt = globalSettings.get(
				HmisGlobalSettingKeys.ENABLE_REGISTRATION_VALIDATION ).equalsIgnoreCase( HmisGlobalSettingKeys.TRUE );

		if ( shouldValidateReceipt && dto.getReceiptNumber().isEmpty() ) {
			throw new ResponseStatusException( HttpStatus.FORBIDDEN, HmisExceptionMessage.EXCEPTION_NO_RECEIPT );
		}

		if ( shouldValidateReceipt && this.isValidReceipt( dto.getReceiptNumber() ) ) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, HmisExceptionMessage.INVALID_RECEIPT );
		}

		if ( dto.getRegisteredBy() == null || !dto.getRegisteredBy().getId().isPresent() ) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Registered By Is Required" );
		}

		if ( dto.getRegisteredFrom() == null || !dto.getRegisteredFrom().getId().isPresent() ) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Registered From Is Required" );
		}
	}

	private void setPatientBasicDetail( PatientDetail patientDetail, PatientDetailDto dto ) {

		if ( dto.getPatientFirstName() != null ) {
			patientDetail.setFirstName( dto.getPatientFirstName() );
		}

		if ( dto.getPatientLastName() != null ) {
			patientDetail.setLastName( dto.getPatientLastName() );
		}

		if ( dto.getPatientOtherName() != null ) {
			patientDetail.setOtherName( dto.getPatientOtherName() );
		}

		if ( dto.getDepartmentId() != null ) {
			patientDetail.setDepartment( new Department( dto.getDepartmentId() ) );
		}

		if ( dto.getMaritalStatusId() != null ) {
			patientDetail.setMaritalStatus( new MaritalStatus( dto.getMaritalStatusId() ) );
		}

		if ( dto.getGenderId() != null ) {
			patientDetail.setGender( new Gender( dto.getGenderId() ) );
		}

		if ( dto.getReligionId() != null ) {
			patientDetail.setReligion( new Religion( dto.getReligionId() ) );
		}

		if ( dto.getEthnicGroupId() != null ) {
			patientDetail.setEthnicGroup( new EthnicGroup( dto.getEthnicGroupId() ) );
		}

		if ( dto.getPatientDateOfBirth() != null ) {
			if ( dto.getPatientDateOfBirth().getYear() >= 1 && dto.getPatientDateOfBirth().getMonth() >= 1 && dto.getPatientDateOfBirth().getDay() >= 1 ) {
				patientDetail.setDateOfBirth( this.utilService.transformToLocalDate( dto.getPatientDateOfBirth() ) );
			}
		}

		if ( dto.getPatientAge() != null ) {
			patientDetail.setAge( dto.getPatientAge() );
		}

		if ( dto.getPatientTypeEnum() != null ) {
			patientDetail.setPatientTypeEnum( dto.getPatientTypeEnum() );
		}

		if ( dto.getPatientCategoryEnum() != null ) {
			patientDetail.setPatientCategory( dto.getPatientCategoryEnum() );
		}

		if ( dto.getCardNumber() != null ) {
			patientDetail.setCardNumber( dto.getCardNumber() );
		}

		if ( dto.getFolderNumber() != null ) {
			patientDetail.setFolderNumber( dto.getFolderNumber() );
		}

		if ( dto.getRegisteredFrom().getId().isPresent() ) {
			patientDetail.setRegisterStaffLocation( new Department( dto.getRegisteredFrom().getId().get() ) );
		}

		if ( dto.getRegisteredBy().getId().isPresent() ) {
			patientDetail.setRegisteredBy( dto.getRegisteredBy().getId().get() );
		}
	}
}
