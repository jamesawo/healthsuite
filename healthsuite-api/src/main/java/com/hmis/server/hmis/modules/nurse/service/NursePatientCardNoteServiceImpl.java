package com.hmis.server.hmis.modules.nurse.service;

import com.hmis.server.hmis.common.common.dto.MessageDto;
import com.hmis.server.hmis.common.common.service.CommonService;
import com.hmis.server.hmis.common.common.service.GlobalSettingsImpl;
import com.hmis.server.hmis.common.common.service.HmisUtilService;
import com.hmis.server.hmis.common.user.dto.UserDto;
import com.hmis.server.hmis.modules.clearking.dto.ClerkPatientActivityEnum;
import com.hmis.server.hmis.modules.clearking.dto.ConsultationDeskEnum;
import com.hmis.server.hmis.modules.clearking.model.*;
import com.hmis.server.hmis.modules.clearking.service.PatientClerkActivityService;
import com.hmis.server.hmis.modules.emr.model.PatientClinicReferral;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.emr.service.PatientDetailServiceImpl;
import com.hmis.server.hmis.modules.lab.dto.*;
import com.hmis.server.hmis.modules.lab.dto.parasitology.*;
import com.hmis.server.hmis.modules.lab.model.LabTestPreparation;
import com.hmis.server.hmis.modules.lab.model.LabTestRequestItem;
import com.hmis.server.hmis.modules.lab.service.LabResultPreparationService;
import com.hmis.server.hmis.modules.nurse.dto.*;
import com.hmis.server.hmis.modules.nurse.model.*;
import com.hmis.server.hmis.modules.pharmacy.dto.DrugRequestDto;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hmis.server.hmis.common.constant.HmisConstant.NIL;
import static com.hmis.server.hmis.common.constant.HmisGlobalSettingKeys.HOSPITAL_NAME;
import static com.hmis.server.hmis.modules.reports.dto.HmisReportFileEnum.*;

@Service
@Slf4j
public class NursePatientCardNoteServiceImpl {
	private final NurseObstetricsHistoryServiceImpl obstetricsHistoryService;
	private final PatientClerkActivityService patientClerkActivityService;
	private final HmisUtilService utilService;
	private final PatientDetailServiceImpl patientDetailService;
	private final GlobalSettingsImpl globalSettingsService;
	private final CommonService commonService;
	private final LabResultPreparationService labResultPreparationService;


	@Autowired
	public NursePatientCardNoteServiceImpl(
			NurseObstetricsHistoryServiceImpl obstetricsHistoryService,
			@Lazy PatientClerkActivityService patientClerkActivityService,
			HmisUtilService utilService,
			PatientDetailServiceImpl patientDetailService,
			GlobalSettingsImpl globalSettingsService, CommonService commonService,
			@Lazy LabResultPreparationService labResultPreparationService
	) {
		this.obstetricsHistoryService = obstetricsHistoryService;
		this.patientClerkActivityService = patientClerkActivityService;
		this.utilService = utilService;
		this.patientDetailService = patientDetailService;
		this.globalSettingsService = globalSettingsService;
		this.commonService = commonService;
		this.labResultPreparationService = labResultPreparationService;
	}

	public ResponseEntity<byte[]> downloadPatientAncCardNote( PatientCardNoteDto dto ) {
		return null;
	}

	public ResponseEntity<MessageDto> savePatientObstetricsHistory( NurseObstetricsHistoryDto dto ) {
		return this.obstetricsHistoryService.saveObstetricsHistory( dto );
	}

	public ResponseEntity<byte[]> downloadPatientEFolder( PatientCardNoteDto dto ) {
		try {
			PatientDetail patient = this.patientDetailService.findPatientDetailById( dto.getPatient().getPatientId() );
			LocalDate endDate = this.utilService.transformToLocalDate( dto.getEndDate() );
			LocalDate startDate = this.utilService.transformToLocalDate( dto.getStartDate() );
			String printedBy = dto.getUser().getLabel().orElse( NIL );
			PatientEFolderRecordTypeEnum recordType = dto.getRecordType();
			List<ClerkPatientActivity> patientActivities;
			if ( recordType != null && recordType.equals( PatientEFolderRecordTypeEnum.SPECIFIC ) ) {
				patientActivities = this.patientClerkActivityService.findPatientSpecificRecords( patient, endDate,
				                                                                                 startDate,
				                                                                                 dto.getSpecificTypes() );
			}
			else {
				patientActivities = this.patientClerkActivityService.findPatientAllRecords( patient, endDate,
				                                                                            startDate );
			}
			return this.compileAndGenerateEFolderReport( patientActivities, printedBy, patient );
		} catch ( Exception e ) {
			System.out.println( e.getMessage() );
			log.error( e.getMessage(), e );
			throw new RuntimeException( e );
		}
	}

	public Map<String, Object> prepPatientEFolderRecord(
			String footerText, String printedBy, PatientDetail patient, String title ) {
		Map<String, Object> map = new HashMap<>();
		map.put( "hospitalName", this.globalSettingsService.findValueByKey( HOSPITAL_NAME ).toUpperCase() );
		map.put( "logo", this.commonService.getLogoAsStream() );
		map.put( "reportHeader", title );
		map.put( "printedBy", printedBy != null ? printedBy : "" );
		map.put( "patientName", patient.getFullName() != null ? patient.getFullName() : "" );
		map.put( "patientNumber", patient.getPatientNumber() != null ? patient.getPatientNumber() : "" );
		map.put( "patientGender", patient.getGender().getName() != null ? patient.getGender().getName() : "" );
		map.put( "patientAge", String.valueOf( patient.getCurrentAge() ) );
		map.put( "patientAddress",
		         patient.getPatientContactDetail().getResidentialAddress() != null ? patient.getPatientContactDetail().getResidentialAddress() : "" );
		map.put( "patientCategory",
		         patient.getPatientCategory().name() != null ? patient.getPatientCategory().name() : "" );
		map.put( "patientPhone",
		         patient.getPatientContactDetail().getPhoneNumber() != null ? patient.getPatientContactDetail().getPhoneNumber() : "" );
		map.put( "patientReligion", patient.getReligion().getName() );
		map.put( "patientEthnic",
		         patient.getEthnicGroup().getName() != null ? patient.getEthnicGroup().getName() : "" );
		map.put( "patientNok",
		         patient.getPatientNokDetail().getName() != null ? patient.getPatientNokDetail().getName() : "" );
		map.put( "footerText", footerText != null ? footerText : "" );
		map.put( "footerBarcode", "1123466789" );
		return map;
	}

	public byte[] exportPatientCardNoteReport( Map<String, Object> param, List<?> fieldsList ) {
		try {
			JasperReport mainReportJasper = this.commonService.compileReportDesign( this.utilService,
			                                                                        CLERK_PATIENT_E_FOLDER );
			JasperPrint jasperPrint;
			if ( fieldsList != null && !fieldsList.isEmpty() ) {
				jasperPrint = JasperFillManager.fillReport( mainReportJasper, param,
				                                            new JRBeanCollectionDataSource( fieldsList ) );
			}
			else {
				jasperPrint = JasperFillManager.fillReport( mainReportJasper, param, new JREmptyDataSource() );
			}
			return JasperExportManager.exportReportToPdf( jasperPrint );
		} catch ( Exception e ) {
			throw new RuntimeException( e.getMessage() );
		}
	}

	public SubReportDto getLabResultEFolderData( LabResultPrepDto prep ) {
		LabDepartmentTypeEnum resultTypeEnum = prep.getResultTypeEnum();
		List<LabResultReportDto> reportDtoList = new ArrayList<>();
		Map<String, Object> objectMap = new HashMap<>();
		JasperReport report = null;
		String testName = prep.getTestName() != null ? prep.getTestName() : NIL;
		String date;
		if ( prep.getReportDate() != null ) {
			date = prep.getReportDate().toString();
		}
		else {
			date = prep.getRequestDate() != null ? prep.getRequestDate() : NIL;
		}

		UserDto preparedBy = prep.getPreparedBy();

		if ( resultTypeEnum != null && resultTypeEnum.equals( LabDepartmentTypeEnum.MICROBIOLOGY ) ) {
			LabParasitologyTemplateDto template = prep.getParasitologyTemplate();
			LabCulture culture = template.getCulture();
			LabMacroscopy macroscopy = template.getMacroscopy();
			LabMicroscopy microscopy = template.getMicroscopy();
			String newLabNote = template.getNewLabNote();
			objectMap.put( "resultDate", date );
			objectMap.put( "resultForTest", testName );
			// macroscopy
			objectMap.put( "macroscopyAppearance",
			               macroscopy.getAppearance() != null ? macroscopy.getAppearance() : NIL );
			objectMap.put( "macroscopyColor", macroscopy.getColour() != null ? macroscopy.getColour() : NIL );
			objectMap.put( "macroscopySize", macroscopy.getSize() != null ? macroscopy.getSize() : NIL );
			objectMap.put( "macroscopyConsistency",
			               macroscopy.getConsistency() != null ? macroscopy.getConsistency() : NIL );
			objectMap.put( "macroscopyPalateCount",
			               macroscopy.getPlateletCount() != null ? macroscopy.getPlateletCount() : NIL );
			objectMap.put( "macroscopyLevelOfParasite",
			               macroscopy.getLevelOfParasitemia() != null ? macroscopy.getLevelOfParasitemia() : NIL );
			objectMap.put( "macroscopyProtein", macroscopy.getProtein() != null ? macroscopy.getProtein() : NIL );
			// culture
			objectMap.put( "cultureTemperature", culture.getTemperature() != null ? culture.getTemperature() : NIL );
			objectMap.put( "cultureAtmosphere", culture.getAtmosphere() != null ? culture.getAtmosphere() : NIL );
			objectMap.put( "culturePlate", culture.getPlate() != null ? culture.getPlate() : NIL );
			List<LabCultureLineOrganism> lineOrganism = culture.getLineOrganism();
			lineOrganism.forEach(
					obj -> obj.setAntibioticsBeans( new JRBeanCollectionDataSource( obj.getAntibiotics() ) ) );
			objectMap.put( "cultureLineOrganism",
			               lineOrganism.isEmpty() ? null : new JRBeanCollectionDataSource( lineOrganism ) );
			objectMap.put( "cultureIncubation", culture.getIncubation() != null ? culture.getIncubation() : NIL );
			objectMap.put( "resultPreparedComment",
			               prep.getPreparedLabNote() != null ? prep.getPreparedLabNote() : NIL );
			// microscopy
			objectMap.put( "microscopyHtml", this.getMicroscopyAsHtml( microscopy ) );
			objectMap.put( "microscopyType", this.utilService.replaceUnderscore( microscopy.getType().name() ) );
			objectMap.put( "labNote", newLabNote );
			objectMap.put( "labNoteBy", prep.getLabNoteBy() != null ? prep.getLabNoteBy() : NIL );
			objectMap.put( "resultPreparedBy", preparedBy != null ? preparedBy.getLabel().orElse( NIL ) : NIL );
			objectMap.put( "resultVerifiedBy",
			               prep.getVerifiedBy() != null ? prep.getVerifiedBy().getLabel().orElse( NIL ) : NIL );
			objectMap.put( "provisionalDiagnosis",
			               prep.getProvisionalDiagnosis() != null ? prep.getProvisionalDiagnosis() : NIL );
			objectMap.put( "provisionalDiagnosisBy",
			               prep.getProvisionalDiagnosisBy() != null ? prep.getProvisionalDiagnosisBy() : NIL );
			objectMap.put( "pathologistComment",
			               prep.getPathologistComment() != null ? prep.getPathologistComment() : NIL );
			objectMap.put( "pathologistCommentBy",
			               prep.getPathologistName() != null ? prep.getPathologistName() : NIL );

			report = this.commonService.compileReportDesign( this.utilService, CLERK_LAB_MICROBIOLOGY_RESULT );
		}
		else {
			List<LabResultTestParamDto> parameterList = prep.getTestParameterList();
			if ( parameterList != null && !parameterList.isEmpty() ) {
				for ( LabResultTestParamDto paramDto : parameterList ) {
					List<LabParameterRangeSetupItemDto> rangeList = paramDto.getTestRangeParamList();
					// testName = paramDto.getTestParamName();
					if ( rangeList != null && !rangeList.isEmpty() ) {
						for ( LabParameterRangeSetupItemDto range : rangeList ) {
							LabResultReportDto reportDto = new LabResultReportDto();
							reportDto.setParameter( range.getName() );
							reportDto.setResult( range.getValue().toString() );
							reportDto.setUnit( range.getUnit() );
							reportDto.setRange( String.format( "%s -- %s", range.getLowerLimit().toString(),
							                                   range.getUpperLimit().toString() ) );
							reportDtoList.add( reportDto );
						}
					}
				}
			}
			// date = prep.getReportDate() != null ? prep.getReportDate().toString() : "";
			String capturedBy = preparedBy != null ? preparedBy.getLabel().orElse( NIL ) : NIL;
			String commentValue = prep.getLabNote() != null ? prep.getLabNote() : NIL;
			String commentBy = prep.getVerifiedBy() != null ? prep.getVerifiedBy().getLabel().orElse( NIL ) : NIL;
			String commentDate = prep.getVerifiedDate() != null ? prep.getVerifiedDate().toString() : NIL;

			objectMap.put( "resultDate", date );
			objectMap.put( "resultData", new JRBeanCollectionDataSource( reportDtoList ) );
			objectMap.put( "resultCapturedBy", capturedBy );
			objectMap.put( "resultForTest", testName );
			objectMap.put( "commentDate", commentDate );
			objectMap.put( "commentValue", commentValue );
			objectMap.put( "commentBy", commentBy );
			report = this.commonService.compileReportDesign( this.utilService, CLERK_LAB_RESULT );
		}
		return new SubReportDto( objectMap, report, date );
	}

	private String getMicroscopyAsHtml( LabMicroscopy microscopy ) {

		StringBuilder html = new StringBuilder();
		LabMicroscopyTypeEnum type = microscopy.getType();
		html.append( String.format( "<u>Type: %s </u><br><br>", this.utilService.replaceUnderscore( type.name() ) ) );
		switch ( type ) {
			case SFA:
				LabMicroscopySfa sfa = microscopy.getSfa();
				html.append( String.format( "<u>Morphology:</u><br>%s<br><br>",
				                            sfa.getMorphology() != null ? sfa.getMorphology() : NIL ) );
				html.append( String.format( "<u>Reaction:</u><br>%s<br><br>",
				                            sfa.getReaction() != null ? sfa.getReaction() : NIL ) );
				html.append( String.format( "<u>Abstinence:</u><br>%s<br><br>",
				                            sfa.getAbstinence() != null ? sfa.getAbstinence() : NIL ) );
				html.append( String.format( "<u>Col. To Test:</u><br>%s<br><br>",
				                            sfa.getToTest() != null ? sfa.getToTest() : NIL ) );
				html.append( String.format( "<u>Volume:</u><br>%s<br><br>",
				                            sfa.getVolume() != null ? sfa.getVolume() : NIL ) );
				html.append( String.format( "<u>Liquefaction:</u><br>%s<br><br>",
				                            sfa.getLiquefaction() != null ? sfa.getLiquefaction() : NIL ) );
				html.append( String.format( "<u>Viscosity:</u><br>%s<br><br>",
				                            sfa.getViscosity() != null ? sfa.getViscosity() : NIL ) );
				html.append( String.format( "<u>PH:</u><br>%s<br><br>", sfa.getPh() != null ? sfa.getPh() : NIL ) );
				html.append( String.format( "<u>WBC:</u><br>%s<br><br>", sfa.getWbc() != null ? sfa.getWbc() : NIL ) );
				html.append( String.format( "<u>RBC:</u><br>%s<br><br>", sfa.getRbc() != null ? sfa.getRbc() : NIL ) );
				html.append( String.format( "<u>Immature Cells:</u><br>%s<br><br>",
				                            sfa.getImmatureCells() != null ? sfa.getImmatureCells() : NIL ) );
				html.append( String.format( "<u>AntiSperm AB:</u><br>%s<br><br>",
				                            sfa.getAntiSpermAb() != null ? sfa.getAntiSpermAb() : NIL ) );
				html.append( String.format( "<u>Vitality:</u><br>%s<br><br>",
				                            sfa.getVitality() != null ? sfa.getVitality() : NIL ) );
				html.append(
						String.format( "<u>Odour:</u><br>%s<br><br>", sfa.getOdour() != null ? sfa.getOdour() : NIL ) );
				html.append( String.format( "<u>Appearance:</u><br>%s<br><br>",
				                            sfa.getAppearance() != null ? sfa.getAppearance() : NIL ) );
				html.append(
						String.format( "<u>WBC Cone:</u><br>%s<br><br>", sfa.getWbc() != null ? sfa.getWbc() : NIL ) );
				html.append( String.format( "<u>Agglutination:</u><br>%s<br><br>",
				                            sfa.getWbc() != null ? sfa.getWbc() : NIL ) );
				html.append( String.format( "<u>Aggregation:</u><br>%s<br><br>",
				                            sfa.getAggregation() != null ? sfa.getAggregation() : NIL ) );
				html.append( String.format( "<u>Acrosome T:</u><br>%s<br><br>",
				                            sfa.getAcrosomeT() != null ? sfa.getAcrosomeT() : NIL ) );
				html.append( String.format( "<u>Time of Production:</u><br>%s<br><br>",
				                            sfa.getTimeOfProduction() != null ? sfa.getTimeOfProduction() : NIL ) );
				html.append( String.format( "<u>Time Received:</u><br>%s<br><br>",
				                            sfa.getTimeReceived() != null ? sfa.getTimeReceived() : NIL ) );
				html.append( String.format( "<u>Time of Examination:</u><br>%s<br><br>",
				                            sfa.getTimeOfExamination() != null ? sfa.getTimeOfExamination() : NIL ) );
				html.append( String.format( "<u>Mode of production:</u><br>%s<br><br>",
				                            sfa.getModeOfProduction() != null ? sfa.getModeOfProduction() : NIL ) );
				break;
			case SMEAR:
				LabMicroscopySmear smear = microscopy.getSmear();
				LabMicroscopySmearTypeEnum smearType = smear.getType();
				html.append( String.format( "<u>Smear Type: </u>%s<br><br>",
				                            smearType.toString() != null ? smearType.toString() : NIL ) );
				switch ( smearType ) {
					case GRAM:
						LabMicroscopySmearGram gram = smear.getGram();
						html.append( String.format( "<u>Morphology:</u><br>%s<br><br>",
						                            gram.getMorphology() != null ? gram.getMorphology() : NIL ) );
						html.append( String.format( "<u>Reaction:</u><br>%s<br><br>",
						                            gram.getReaction() != null ? gram.getReaction() : NIL ) );
						html.append( String.format( "<u>Result:</u><br>%s<br><br>",
						                            gram.getResult() != null ? gram.getResult() : NIL ) );
						break;
					case GIEMSA:
						LabMicroscopySmearGiemsa giemsa = smear.getGiemsa();
						html.append( String.format( "<u>Morphology:</u><br>%s<br><br>",
						                            giemsa.getMorphology() != null ? giemsa.getMorphology() : NIL ) );
						html.append( String.format( "<u>Reaction:</u><br>%s<br><br>",
						                            giemsa.getReaction() != null ? giemsa.getReaction() : NIL ) );
						html.append( String.format( "<u>Parasite:</u><br>%s<br><br>",
						                            giemsa.getParasite() != null ? giemsa.getParasite() : NIL ) );
						html.append( String.format( "<u>Stage:</u><br>%s<br><br>",
						                            giemsa.getStage() != null ? giemsa.getStage() : NIL ) );
						html.append( String.format( "<u>Count:</u><br>%s<br><br>",
						                            giemsa.getCount() != null ? giemsa.getCount() : NIL ) );
						html.append( String.format( "<u>WBC:</u><br>%s<br><br>",
						                            giemsa.getWbc() != null ? giemsa.getWbc() : NIL ) );
						html.append( String.format( "<u>RBC:</u><br>%s<br><br>",
						                            giemsa.getRbc() != null ? giemsa.getRbc() : NIL ) );
						html.append( String.format( "<u>Cast:</u><br>%s<br><br>",
						                            giemsa.getCast() != null ? giemsa.getCast() : NIL ) );
						html.append( String.format( "<u>WHIFF:</u><br>%s<br><br>",
						                            giemsa.getWhiff() != null ? giemsa.getWhiff() : NIL ) );
						html.append( String.format( "<u>Epithelials:</u><br>%s<br><br>",
						                            giemsa.getEpithelials() != null ? giemsa.getEpithelials() : NIL ) );
						html.append( String.format( "<u>Fungi:</u><br>%s<br><br>",
						                            giemsa.getFungi() != null ? giemsa.getFungi() : NIL ) );
						html.append( String.format( "<u>Bacteria:</u><br>%s<br><br>",
						                            giemsa.getBacteria() != null ? giemsa.getBacteria() : NIL ) );
						html.append( String.format( "<u>Pus Cell:</u><br>%s<br><br>",
						                            giemsa.getPusCell() != null ? giemsa.getPusCell() : NIL ) );
						break;
					case ZEIHL_NEELSEN:
						LabMicroscopySmearZeihlNeelsen zeihlNeelsen = smear.getZeihlNeelsen();
						html.append( String.format( "<u>Morphology:</u><br>%s<br><br>",
						                            zeihlNeelsen.getMorphology() != null ? zeihlNeelsen.getMorphology() : NIL ) );
						html.append( String.format( "<u>Reaction:</u><br>%s<br><br>",
						                            zeihlNeelsen.getReaction() != null ? zeihlNeelsen.getReaction() : NIL ) );
						html.append( String.format( "<u>Parasite:</u><br>%s<br><br>",
						                            zeihlNeelsen.getParasite() != null ? zeihlNeelsen.getParasite() : NIL ) );
						html.append( String.format( "<u>Stage:</u><br>%s<br><br>",
						                            zeihlNeelsen.getStage() != null ? zeihlNeelsen.getStage() : NIL ) );
						html.append( String.format( "<u>Count:</u><br>%s<br><br>",
						                            zeihlNeelsen.getCount() != null ? zeihlNeelsen.getCount() : NIL ) );
						break;
					case OTHERS:
						LabMicroscopySmearOther others = smear.getOthers();
						html.append( String.format( "<u>Result:</u><br>%s<br><br>",
						                            others.getResult() != null ? others.getResult() : NIL ) );
						html.append( String.format( "<u>Morphology:</u><br>%s<br><br>",
						                            others.getMorphology() != null ? others.getMorphology() : NIL ) );
						html.append( String.format( "<u>Reaction:</u><br>%s<br><br>",
						                            others.getReaction() != null ? others.getReaction() : NIL ) );
						html.append( String.format( "<u>Parasite:</u><br>%s<br><br>",
						                            others.getParasite() != null ? others.getParasite() : NIL ) );
						html.append( String.format( "<u>Stage:</u><br>%s<br><br>",
						                            others.getStage() != null ? others.getStage() : NIL ) );
						html.append( String.format( "<u>Count:</u><br>%s<br><br>",
						                            others.getCount() != null ? others.getCount() : NIL ) );
						html.append( String.format( "<u>WBC:</u><br>%s<br><br>",
						                            others.getWbc() != null ? others.getWbc() : NIL ) );
						html.append( String.format( "<u>RBC:</u><br>%s<br><br>",
						                            others.getRbc() != null ? others.getRbc() : NIL ) );
						html.append( String.format( "<u>Cast:</u><br>%s<br><br>",
						                            others.getCast() != null ? others.getCast() : NIL ) );
						html.append( String.format( "<u>WHIFF:</u><br>%s<br><br>",
						                            others.getWhiff() != null ? others.getWhiff() : NIL ) );
						html.append( String.format( "<u>Epithelials:</u><br>%s<br><br>",
						                            others.getEpithelials() != null ? others.getEpithelials() : NIL ) );
						html.append( String.format( "<u>Fungi:</u><br>%s<br><br>",
						                            others.getFungi() != null ? others.getFungi() : NIL ) );
						html.append( String.format( "<u>Bacteria:</u><br>%s<br><br>",
						                            others.getBacteria() != null ? others.getBacteria() : NIL ) );
						html.append( String.format( "<u>Pus Cell:</u><br>%s<br><br>",
						                            others.getPusCell() != null ? others.getPusCell() : NIL ) );
						break;
					default:
						break;
				}
				break;
			case URINALYSIS:
				LabMicroscopyUrinalysis urinalysis = microscopy.getUrinalysis();
				html.append( String.format( "<u>Morphology:</u><br>%s<br><br>",
				                            urinalysis.getMorphology() != null ? urinalysis.getMorphology() : NIL ) );
				html.append( String.format( "<u>Reaction:</u><br>%s<br><br>",
				                            urinalysis.getReaction() != null ? urinalysis.getReaction() : NIL ) );
				html.append( String.format( "<u>WBC:</u><br>%s<br><br>",
				                            urinalysis.getWbc() != null ? urinalysis.getWbc() : NIL ) );
				html.append( String.format( "<u>RBC:</u><br>%s<br><br>",
				                            urinalysis.getRbc() != null ? urinalysis.getRbc() : NIL ) );
				html.append( String.format( "<u>Cast:</u><br>%s<br><br>",
				                            urinalysis.getCast() != null ? urinalysis.getCast() : NIL ) );
				html.append( String.format( "<u>Epithelials:</u><br>%s<br><br>",
				                            urinalysis.getEpithelials() != null ? urinalysis.getEpithelials() : NIL ) );
				html.append( String.format( "<u>PH:</u><br>%s<br><br>",
				                            urinalysis.getPh() != null ? urinalysis.getPh() : NIL ) );
				html.append( String.format( "<u>Appearance:</u><br>%s<br><br>",
				                            urinalysis.getAppearance() != null ? urinalysis.getAppearance() : NIL ) );
				html.append( String.format( "<u>Ketone:</u><br>%s<br><br>",
				                            urinalysis.getKetone() != null ? urinalysis.getKetone() : NIL ) );
				html.append( String.format( "<u>Urobilinogen:</u><br>%s<br><br>",
				                            urinalysis.getUrobilinogen() != null ? urinalysis.getUrobilinogen() : NIL ) );
				html.append( String.format( "<u>Nitrite:</u><br>%s<br><br>",
				                            urinalysis.getNitrite() != null ? urinalysis.getNitrite() : NIL ) );
				html.append( String.format( "<u>Trichomonas Vaginalis:</u><br>%s<br><br>",
				                            urinalysis.getTrichomonasVaginalis() != null ? urinalysis.getTrichomonasVaginalis() : NIL ) );
				html.append( String.format( "<u>Blood:</u><br>%s<br><br>",
				                            urinalysis.getBlood() != null ? urinalysis.getBlood() : NIL ) );
				html.append( String.format( "<u>Glucose:</u><br>%s<br><br>",
				                            urinalysis.getGlucose() != null ? urinalysis.getGlucose() : NIL ) );
				html.append( String.format( "<u>Specific Gravity:</u><br>%s<br><br>",
				                            urinalysis.getSpecificGravity() != null ? urinalysis.getSpecificGravity() : NIL ) );
				html.append( String.format( "<u>Crystal:</u><br>%s<br><br>",
				                            urinalysis.getCrystal() != null ? urinalysis.getCrystal() : NIL ) );
				html.append( String.format( "<u>Bilirubin:</u><br>%s<br><br>",
				                            urinalysis.getBilirubin() != null ? urinalysis.getBilirubin() : NIL ) );
				html.append( String.format( "<u>Bacteria:</u><br>%s<br><br>",
				                            urinalysis.getBacteria() != null ? urinalysis.getBacteria() : NIL ) );
				html.append( String.format( "<u>Protein:</u><br>%s<br><br>",
				                            urinalysis.getProtein() != null ? urinalysis.getProtein() : NIL ) );
				html.append( String.format( "<u>Yeast:</u><br>%s<br><br>",
				                            urinalysis.getYeast() != null ? urinalysis.getYeast() : NIL ) );
				html.append( String.format( "<u>Leucocyte:</u><br>%s<br><br>",
				                            urinalysis.getLeucocyte() != null ? urinalysis.getLeucocyte() : NIL ) );
				html.append( String.format( "<u>Other:</u><br>%s<br><br>",
				                            urinalysis.getOther() != null ? urinalysis.getOther() : NIL ) );
				break;
			case WET_AMOUNT:
				LabMicroscopyWetAmount wetAmount = microscopy.getWetAmount();
				LabMicroscopyWetAmountTypeEnum wetAmountType = wetAmount.getType();
				html.append( String.format( "<u>Type:</u><br>%s<br><br>",
				                            wetAmountType.toString() != null ? wetAmountType.toString() : NIL ) );
				html.append( String.format( "<u>Morphology:</u><br>%s<br><br>",
				                            wetAmount.getMorphology() != null ? wetAmount.getMorphology() : NIL ) );
				html.append( String.format( "<u>Reaction:</u><br>%s<br><br>",
				                            wetAmount.getReaction() != null ? wetAmount.getReaction() : NIL ) );
				html.append( String.format( "<u>Parasite:</u><br>%s<br><br>",
				                            wetAmount.getParasite() != null ? wetAmount.getParasite() : NIL ) );
				html.append( String.format( "<u>Stage:</u><br>%s<br><br>",
				                            wetAmount.getStage() != null ? wetAmount.getStage() : NIL ) );
				html.append( String.format( "<u>Count:</u><br>%s<br><br>",
				                            wetAmount.getCount() != null ? wetAmount.getCount() : NIL ) );
				html.append( String.format( "<u>WBC:</u><br>%s<br><br>",
				                            wetAmount.getWbc() != null ? wetAmount.getWbc() : NIL ) );
				html.append( String.format( "<u>RBC:</u><br>%s<br><br>",
				                            wetAmount.getRbc() != null ? wetAmount.getRbc() : NIL ) );
				html.append( String.format( "<u>Cast:</u><br>%s<br><br>",
				                            wetAmount.getCast() != null ? wetAmount.getCast() : NIL ) );
				html.append( String.format( "<u>WHIFF:</u><br>%s<br><br>",
				                            wetAmount.getWhiff() != null ? wetAmount.getWhiff() : NIL ) );
				html.append( String.format( "<u>Epithelials:</u><br>%s<br><br>",
				                            wetAmount.getEpithelials() != null ? wetAmount.getEpithelials() : NIL ) );
				html.append( String.format( "<u>Fungi:</u><br>%s<br><br>",
				                            wetAmount.getFungi() != null ? wetAmount.getFungi() : NIL ) );
				html.append( String.format( "<u>Bacteria:</u><br>%s<br><br>",
				                            wetAmount.getBacteria() != null ? wetAmount.getBacteria() : NIL ) );
				html.append( String.format( "<u>Pus Cell:</u><br>%s<br><br>",
				                            wetAmount.getPusCell() != null ? wetAmount.getPusCell() : NIL ) );
				html.append( String.format( "<u>Crystal:</u><br>%s<br><br>",
				                            wetAmount.getCrystal() != null ? wetAmount.getCrystal() : NIL ) );
				html.append( String.format( "<u>Yeast:</u><br>%s<br><br>",
				                            wetAmount.getYeast() != null ? wetAmount.getYeast() : NIL ) );
				html.append( String.format( "<u>P.Vaginalis:</u><br>%s<br><br>",
				                            wetAmount.getPVaginalis() != null ? wetAmount.getPVaginalis() : NIL ) );
				html.append( String.format( "<u>Others:</u><br>%s<br><br>",
				                            wetAmount.getOthers() != null ? wetAmount.getOthers() : NIL ) );
				break;
			default:
				break;
		}

		return html.toString();
	}

	private ResponseEntity<byte[]> compileAndGenerateEFolderReport(
			List<ClerkPatientActivity> patientActivities,
			String printedBy, PatientDetail patientDetail ) {
		try {
			Map<String, Object> param = this.prepPatientEFolderRecord( null, printedBy, patientDetail,
			                                                           "PATIENT CARD-NOTE / E-FOLDER" );
			List<SubReportDto> fieldsList = this.getEFolderSubReports( patientActivities );
			byte[] bytes = this.exportPatientCardNoteReport( param, fieldsList );
			return ResponseEntity.status( HttpStatus.OK ).body( bytes );
		} catch ( Exception e ) {
			e.printStackTrace();
			log.error( e.getMessage(), e );
			throw new RuntimeException( e.getMessage() );
		}
	}

	private List<SubReportDto> getEFolderSubReports( List<ClerkPatientActivity> patientActivities ) {
		List<SubReportDto> sub = new ArrayList<>();
		if ( !patientActivities.isEmpty() ) {
			for ( ClerkPatientActivity activity : patientActivities ) {
				ClerkPatientActivityEnum activityEnum = activity.getActivityEnum();
				String time = this.utilService.formatTimeToAM_PM( activity.getTime() );
				String dateTime = String.format( "%s %s", activity.getDate().toString(), time );
				switch ( activityEnum ) {
					case VITAL_SIGN:
						if ( activity.getVitalSign() != null ) {
							sub.add( this.getVitalSignEFolderData( activity.getVitalSign(), dateTime ) );
						}
						break;
					case WARD_TRANSFER:
						if ( activity.getWardTransfer() != null ) {
							sub.add( this.getWardTransferEFolderData( activity.getWardTransfer(), dateTime ) );
						}
						break;
					case CONSULTATION:
						if ( activity.getConsultation() != null ) {
							sub.add( this.getConsultationEFolderData( activity.getConsultation(), dateTime ) );
						}
						break;
					case CLINIC_TRANSFER:
						if ( activity.getClinicTransfer() != null ) {
							sub.add( this.getClinicTransferEFolderData( activity.getClinicTransfer(), dateTime ) );
						}
						break;
					case OBSTETRICS_HISTORY:
						if ( activity.getObstetricHistory() != null ) {
							sub.add( this.getObstetricHistoryEFolderData( activity.getObstetricHistory(), dateTime ) );
						}
						break;
					case DIAGNOSIS:
						break;
					case NURSE_NOTE:
						if ( activity.getNote() != null ) {
							sub.add( this.getNurseNoteEFolderData( activity.getNote(), dateTime ) );
						}
						break;
					case DRUG_CHART:
						if ( activity.getDrugChart() != null ) {
							sub.add( this.getPatientDrugChartData( activity.getDrugChart() ) );
						}
						break;
					case LAB_RESULT:
						LabTestPreparation labTestPreparation = activity.getLabTestPreparation();
						if ( labTestPreparation != null ) {
							LabTestRequestItem labTestRequestItem = labTestPreparation.getLabTestRequestItem();
							if ( labTestPreparation != null ) {
								LabResultPrepDto dto = this.labResultPreparationService.getLabTestResult(
										labTestRequestItem.getId() );
								sub.add( this.getLabResultEFolderData( dto ) );
							}
						}
						break;
					case LAB_REQUEST:
						if ( activity.getLabRequest() != null ) {
							sub.add( this.getLabRequestEFolderData( activity.getLabRequest(), dateTime ) );
						}
						break;
					case DRUG_REQUEST:
						if ( activity.getDrugRequest() != null ) {
							sub.add( this.getDrugRequestEFolderData( activity.getDrugRequest(), dateTime ) );
						}
						break;
					case UPLOAD_FILES:
						break;
					case OPERATION_NOTE:
						break;
					case PROCEDURE_REPORT:
						break;
					case RADIOLOGY_REPORT:
						break;
					case RADIOLOGY_REQUEST:
						if ( activity.getRadiologyRequest() != null ) {
							sub.add( this.getRadiologyRequestEFolderData( activity.getRadiologyRequest(), dateTime ) );
						}
					case ANC_FOLLOW_UP_VISIT:
						break;
					default:
						break;
				}
			}
		}
		return sub;
	}

	private SubReportDto getVitalSignEFolderData( PatientVitalSign vitalSign, String date ) {
		JasperReport report = this.commonService.compileReportDesign( this.utilService, CLERK_VITAL_SIGN );
		Map<String, Object> map = new HashMap<>();
		map.put( "temperature", vitalSign.getTemperature() );
		map.put( "weight", vitalSign.getWeight() );
		map.put( "systolicBp", vitalSign.getSystolicBp() );
		map.put( "randomBloodSugar", vitalSign.getRandomBloodSugar() );
		map.put( "oxygenSaturation", vitalSign.getOxygenSaturation() );
		map.put( "respiratoryRate", vitalSign.getRespiratoryRate() );
		map.put( "height", vitalSign.getHeight() );
		map.put( "diastolicBp", vitalSign.getDiastolicBp() );
		map.put( "fastBloodSugar", vitalSign.getFastBloodSugar() );
		map.put( "painScore", vitalSign.getPainScore() );
		map.put( "pulseRate", vitalSign.getPulseRate() );
		map.put( "bodyMassIndex", vitalSign.getBodyMassIndex() );
		map.put( "bodySurfaceArea", vitalSign.getBodySurfaceArea() );
		map.put( "urineAnalysis", vitalSign.getUrineAnalysis() );
		map.put( "commentRemark", vitalSign.getCommentRemark() );
		map.put( "capturedByLabel", vitalSign.getCapturedBy().getFullName() );
		return new SubReportDto( map, report, date );
	}

	private SubReportDto getWardTransferEFolderData( ClerkPatientWardTransfer ward, String dateTime ) {
		Map<String, Object> map = new HashMap<>();
		map.put( "transferredBy", ward.getTransferredBy().getFullName() );
		map.put( "transferTo", ward.getNewWard().getDepartment().getName() );
		map.put( "transferNote", ward.getTransferNote() );
		JasperReport report = this.commonService.compileReportDesign( this.utilService, CLERK_WARD_TRANSFER );
		return new SubReportDto( map, report, dateTime );
	}

	private SubReportDto getDrugRequestEFolderData( ClerkRequestDrug drugRequest, String dateTime ) {
		List<DrugRequestDto> items = new ArrayList<>();
		if ( !drugRequest.getDrugItems().isEmpty() ) {
			for ( ClerkRequestDrugItem item : drugRequest.getDrugItems() ) {
				DrugRequestDto newItem = new DrugRequestDto();
				newItem.setName( item.getDrugRegister().fullBrandName() );
				newItem.setDosage( item.getDosage().toString() );
				newItem.setDays( item.getDays().toString() );
				newItem.setFrequency( item.getFrequency() );
				newItem.setAdminRoute( item.getAdminRoute() );
				newItem.setRaisedBy( drugRequest.getDoctorRequest().getDoctor().getFullName() );
				items.add( newItem );
			}
		}
		JasperReport report = this.commonService.compileReportDesign( this.utilService, CLERK_DRUG_PRESCRIPTION );
		return new SubReportDto( new JRBeanCollectionDataSource( items ), report, dateTime );
	}

	private SubReportDto getLabRequestEFolderData( ClerkRequestLab labRequest, String dateTime ) {
		List<LabRequestItemDto> items = new ArrayList<>();
		if ( !labRequest.getLabItems().isEmpty() ) {
			for ( ClerkRequestLabItems labItem : labRequest.getLabItems() ) {
				LabRequestItemDto itemDto = new LabRequestItemDto();
				itemDto.setComment( labItem.getComment() );
				itemDto.setName( labItem.getService().getName() );
				itemDto.setRaisedBy( labRequest.getDoctorRequest().getDoctor().getFullName() );
				itemDto.setSpecimen( labItem.getSpecimen() );
				items.add( itemDto );
			}
		}
		JasperReport report = this.commonService.compileReportDesign( this.utilService, CLERK_LAB_REQUEST );
		return new SubReportDto( new JRBeanCollectionDataSource( items ), report, dateTime );
	}

	private SubReportDto getNurseNoteEFolderData( NurseNote note, String date ) {
		Map<String, Object> map = new HashMap<>();
		map.put( "note", note.getNote() );
		map.put( "capturedBy", note.getCapturedBy().getFullName() );
		JasperReport report = this.commonService.compileReportDesign( this.utilService, CLERK_PATIENT_NOTES );
		return new SubReportDto( map, report, date );
	}

	private SubReportDto getPatientDrugChartData( NursePatientDrugChart drugChart ) {
		Map<String, Object> map = new HashMap<>();
		if ( drugChart.getTypeEnum().equals( DrugChartTypeEnum.DRUG_REQUEST ) ) {
			map.put( "drugAdministration", drugChart.getAdministeredDrugRequestItem() );
			map.put( "name", drugChart.getAdministeredDrugRequestItem().getDrugRegister().fullBrandName() );
			map.put( "formulation",
			         drugChart.getAdministeredDrugRequestItem().getDrugRegister().getFormulation().getName() );
			map.put( "dosage", drugChart.getAdministeredDrugRequestItem().getDosage().toString() );
			map.put( "frequency", drugChart.getAdministeredDrugRequestItem().getFrequency() );
			map.put( "days", drugChart.getAdministeredDrugRequestItem().getDays().toString() );
			map.put( "quantity", "" );
			map.put( "comment",
			         drugChart.getAdministeredDrugRequestItem().getNurseComment() != null ? drugChart.getAdministeredDrugRequestItem().getNurseComment() : "" );
			map.put( "performedBy", drugChart.getAdministeredDrugRequestItem().getAdministeredBy().getFullName() );
			JasperReport report = this.commonService.compileReportDesign( this.utilService, CLERK_DRUG_ADMINISTRATION );
			return new SubReportDto( map, report, drugChart.getDate().toString() );
		}
		else {
			return null;
		}
	}

	private SubReportDto getRadiologyRequestEFolderData( ClerkRequestRadiology radiologyRequest, String dateTime ) {
		return null;
	}

	private SubReportDto getObstetricHistoryEFolderData( NurseObstetricHistory history, String date ) {
		Map<String, Object> map = new HashMap<>();
		map.put( "specialAttention", history.getGeneralForm().getSpecialAttention() );
		map.put( "edd", history.getGeneralForm().getEdd() );
		map.put( "lmp", history.getGeneralForm().getLmp() );
		map.put( "ga", history.getGeneralForm().getGa() );
		map.put( "specialPoints", history.getGeneralForm().getSpecialPoints() );
		map.put( "heartDisease", history.getPreviousMedical().getHeartDisease() );
		map.put( "chestDisease", history.getPreviousMedical().getChestDisease() );
		map.put( "kidneyDisease", history.getPreviousMedical().getKidneyDisease() );
		map.put( "bloodTransfusion", history.getPreviousMedical().getBloodTransfusion() );
		map.put( "othersIncludingOperation", history.getPreviousMedical().getOthers() );
		map.put( "familyMultiplePregnancy", history.getFamilyHistory().getMultiplePregnancy() );
		map.put( "familyHeartDisease", history.getFamilyHistory().getHeartDisease() );
		map.put( "familyHypertension", history.getFamilyHistory().getHypertension() );
		map.put( "familyTuberculosis", history.getFamilyHistory().getTuberculosis() );
		map.put( "familyOthers", history.getFamilyHistory().getOthers() );
		map.put( "presentBleeding", history.getHisOfPresentPregnancy().getBleeding() );
		map.put( "presentDischarge", history.getHisOfPresentPregnancy().getDischarge() );
		map.put( "presentUrinarySymptom", history.getHisOfPresentPregnancy().getUrinarySymptom() );
		map.put( "presentSwellingOfAnkles", history.getHisOfPresentPregnancy().getSwellingOfAnkles() );
		map.put( "presentOtherSymptoms", history.getHisOfPresentPregnancy().getOtherSymptoms() );
		map.put( "physicalGeneralCondition", history.getPhysicalExam().getGeneralCondition() );
		map.put( "physicalRespiratorySystem", history.getPhysicalExam().getRespiratorySystem() );
		map.put( "physicalCardiovascularSystem", history.getPhysicalExam().getCardiovascularSystem() );
		map.put( "physicalBreastAndNipples", history.getPhysicalExam().getBreastAndNipples() );
		map.put( "physicalAbdomen", history.getPhysicalExam().getAbdomen() );
		map.put( "physicalVaginalExamination", history.getPhysicalExam().getVaginalExamination() );
		map.put( "physicalOtherAbnormalities", history.getPhysicalExam().getOtherAbnormalities() );
		map.put( "physicalComment", history.getPhysicalExam().getComment() );
		map.put( "physicalSpecialInstructions", history.getPhysicalExam().getSpecialInstructions() );
		map.put( "measWeight", history.getMeasurement().getWeight() );
		map.put( "measHeight", history.getMeasurement().getHeight() );
		map.put( "measBloodPressure", history.getMeasurement().getBloodPressure() );
		map.put( "measBreastAndNipples", history.getMeasurement().getBreastAndNipples() );
		map.put( "measAbdomen", history.getMeasurement().getAbdomen() );
		map.put( "measVaginalExamination", history.getMeasurement().getVaginalExamination() );
		map.put( "measOtherAbnormalities", history.getMeasurement().getOtherAbnormalities() );
		map.put( "measComment", history.getMeasurement().getComment() );
		map.put( "measSpecialInstructions", history.getMeasurement().getSpecialInstructions() );
		map.put( "promontory", history.getInstruction().getPromontory() );
		map.put( "sacrum", history.getInstruction().getSacrum() );
		map.put( "sideWalls", history.getInstruction().getSidewalls() );
		map.put( "iSchialSP", history.getInstruction().getISchialSp() );
		map.put( "subPub", history.getInstruction().getSubPub() );
		map.put( "coccyx", history.getInstruction().getCoccyx() );
		map.put( "comment", history.getInstruction().getComments() );
		map.put( "specialInstructions", history.getInstruction().getDeliveryInstructions() );
		map.put( "recordedBy", history.getByUser().getFullName() );
		map.put( "prevPregnancies",
		         new JRBeanCollectionDataSource( this.getPrevPregnancies( history.getPrevPregnancies() ) ) );
		JasperReport report = this.commonService.compileReportDesign( this.utilService, CLERK_OBSTETRIC_HISTORY );
		return new SubReportDto( map, report, date );
	}

	private List<SubReportPrevPregnancy> getPrevPregnancies( List<NurseObPrevPregnancy> prevPregnancies ) {
		List<SubReportPrevPregnancy> list = new ArrayList<>();
		if ( prevPregnancies != null && !prevPregnancies.isEmpty() ) {
			for ( NurseObPrevPregnancy pregnancy : prevPregnancies ) {
				SubReportPrevPregnancy prev = new SubReportPrevPregnancy();
				prev.setDuration( ObjectUtils.isNotEmpty( pregnancy.getDurationOfPregnancy() ) ? String.valueOf(
						pregnancy.getDurationOfPregnancy() ) : "" );
				prev.setOutcome(
						ObjectUtils.isNotEmpty( pregnancy.getOutcome() ) ? pregnancy.getOutcome().name() : "" );
				prev.setGender( ObjectUtils.isNotEmpty( pregnancy.getSex() ) ? pregnancy.getSex().getName() : "" );
				prev.setDateOfBirth( ObjectUtils.isNotEmpty(
						pregnancy.getDateOfBirth() ) ? pregnancy.getDateOfBirth().toString() : "" );
				prev.setBirthWeight( ObjectUtils.isNotEmpty( pregnancy.getBirthWeight() ) ? String.valueOf(
						pregnancy.getBirthWeight() ) : "" );
				prev.setAlive( ObjectUtils.isNotEmpty( pregnancy.getAlive() ) ? pregnancy.getAlive().name() : "" );
				prev.setComment( ObjectUtils.isNotEmpty( pregnancy.getComment() ) ? pregnancy.getComment() : "" );
				list.add( prev );
			}
		}
		return list;
	}

	private SubReportDto getConsultationEFolderData( ClerkConsultation consultation, String dateTime ) {
		ConsultationDeskEnum consultationDeskEnum = consultation.getDeskEnum();
		switch ( consultationDeskEnum ) {
			case GENERAL_CLERKING_DESK:
				return this.getGeneralConsultationActivity( consultation.getGeneralClerkDesk(), dateTime );
			case GENERAL_OUT_PATIENT_DESK:
				return this.getGeneralOutPatientConsultationActivity( consultation.getOutPatientDesk(), dateTime );
			case INPATIENT_CLERKING_DESK:
				return null;
		}
		return null;
	}

	// todo:: move all consultation activity to consultation Service
	private SubReportDto getGeneralConsultationActivity( ClerkGeneralClerkDesk clerkDesk, String dateTime ) {
		Map<String, Object> map = new HashMap<>();
		map.put( "historyPresentingComplaint", clerkDesk.getBackgroundHistory().getHistoryOfPresentingComplaint() );
		map.put( "presentingComplaint", clerkDesk.getBackgroundHistory().getPresentingComplaint() );
		map.put( "reviewOfSystem", clerkDesk.getBackgroundHistory().getReviewOfSystem() );
		map.put( "pastMedSurgHistory", clerkDesk.getBackgroundHistory().getPastMedicalAndSurgicalHistory() );
		map.put( "drugHistory",
		         clerkDesk.getBackgroundHistory().getDrugHistory() != null ? clerkDesk.getBackgroundHistory().getDrugHistory() : "" );
		map.put( "psychiatricHistory", clerkDesk.getBackgroundHistory().getPsychiatricHistory() );
		map.put( "OGHistory", clerkDesk.getBackgroundHistory().getObstetricsAndGynaecologyHistory() );
		map.put( "provisionalDiagnosis", clerkDesk.getProvisionalDiagnosis() );
		map.put( "performedBy", clerkDesk.getClerkedBy().getFullName() );
		JasperReport report = this.commonService.compileReportDesign( this.utilService, CLERK_GENERAL_CONSULTATION );
		return new SubReportDto( map, report, dateTime );
	}

	private SubReportDto getGeneralOutPatientConsultationActivity(
			ClerkingGeneralOutPatientDesk outPatientDesk, String dateTime ) {
		Map<String, Object> map = new HashMap<>();
		map.put( "presentingComplaint", outPatientDesk.getBackgroundHistory().getPresentingComplaint() );
		map.put( "historyOfPresentingComplaint",
		         outPatientDesk.getBackgroundHistory().getHistoryOfPresentingComplaint() );
		map.put( "reviewOfSystems", outPatientDesk.getBackgroundHistory().getReviewOfSystem() );
		map.put( "pastMedHistory", outPatientDesk.getBackgroundHistory().getPastMedicalAndSurgicalHistory() );
		map.put( "drugHistory",
		         outPatientDesk.getBackgroundHistory().getDrugHistory() != null ? outPatientDesk.getBackgroundHistory().getDrugHistory() : "" );
		map.put( "physLevelOfCon", outPatientDesk.getPhysicalExamination().getLevelOfConsciousness() );
		map.put( "physPatientType", outPatientDesk.getPhysicalExamination().getPatientType() );
		map.put( "physPallor", outPatientDesk.getPhysicalExamination().getPallor() );
		map.put( "physDehydration", outPatientDesk.getPhysicalExamination().getDehydration() );
		map.put( "physJaundice", outPatientDesk.getPhysicalExamination().getJaundice() );
		map.put( "physCyanosis", outPatientDesk.getPhysicalExamination().getCyanosis() );
		map.put( "physFebril", outPatientDesk.getPhysicalExamination().getFebril() );
		map.put( "sysDyspnoea", outPatientDesk.getSystemicExamination().getDyspnoea() );
		map.put( "sysRespRate", outPatientDesk.getSystemicExamination().getRespiratoryRate() );
		map.put( "sysParaxymal", outPatientDesk.getSystemicExamination().getParoxysmalNocturnalDyspnoea() );
		map.put( "sysOrthopnoea", outPatientDesk.getSystemicExamination().getOrthopnoea() );
		map.put( "sysChestMov", outPatientDesk.getSystemicExamination().getChestMovement() );
		map.put( "sysPositionTrachea", outPatientDesk.getSystemicExamination().getPositionOfTrachea() );
		map.put( "sysPercussionNote", outPatientDesk.getSystemicExamination().getPercussionNote() );
		map.put( "sysAuscultation", outPatientDesk.getSystemicExamination().getAuscultation() );
		map.put( "cardioPulseRate", outPatientDesk.getCardioVascularExamination().getPulseRate() );
		map.put( "cardioBloodPressure", outPatientDesk.getCardioVascularExamination().getBloodPressure() );
		map.put( "cardioApexBeat", outPatientDesk.getCardioVascularExamination().getApexBeat() );
		map.put( "cardioJagularVenous", outPatientDesk.getCardioVascularExamination().getJugularVenousPressure() );
		if ( outPatientDesk.getCardioVascularExamination() != null &&
				outPatientDesk.getCardioVascularExamination().getHeartSound() != null ) {
			List<String> heartSound = outPatientDesk.getCardioVascularExamination().getHeartSound();
			map.put( "heartSoundS1", heartSound.contains( "S1" ) ? "YES" : "NO" );
			map.put( "heartSoundS2", heartSound.contains( "S2" ) ? "YES" : "NO" );
			map.put( "heartSoundS3", heartSound.contains( "S3" ) ? "YES" : "NO" );
			map.put( "heartSoundS4", heartSound.contains( "S4" ) ? "YES" : "NO" );
		}
		else {
			map.put( "heartSoundS1", "NO" );
			map.put( "heartSoundS2", "NO" );
			map.put( "heartSoundS3", "NO" );
			map.put( "heartSoundS4", "NO" );
		}
		map.put( "periExternalGenita", outPatientDesk.getPerineumExamination().getExternalGenitalia() );
		map.put( "periOtherLesion", outPatientDesk.getPerineumExamination().getAnyOtherLesions() );
		map.put( "perPerRectum", outPatientDesk.getPerineumExamination().getPerRectumExamination() );
		map.put( "perVaginalExam", outPatientDesk.getPerineumExamination().getVaginalExamination() );
		map.put( "perChaperone", outPatientDesk.getPerineumExamination().getChaperone() );
		map.put( "muscMuscleBulk", outPatientDesk.getMusculoSkeletalExamination().getMuscleBulk() );
		map.put( "muscTone", outPatientDesk.getMusculoSkeletalExamination().getTone() );
		map.put( "muscSpasticity", outPatientDesk.getMusculoSkeletalExamination().getSpasticity() );
		map.put( "muscRigidity", outPatientDesk.getMusculoSkeletalExamination().getRigidity() );
		map.put( "muscReflexes", outPatientDesk.getMusculoSkeletalExamination().getReflexe() );
		map.put( "neurGait", outPatientDesk.getNeurologicalExamination().getGait() );
		map.put( "neurAbno", outPatientDesk.getNeurologicalExamination().getAbnormalMovement() );
		map.put( "clinicAssessment", outPatientDesk.getClinicalAssessment().getClinicalAssessment() );
		map.put( "provisionalDiagnosis", outPatientDesk.getClinicalAssessment().getProvisionalDiagnosis() );
		map.put( "treatmentPlan", outPatientDesk.getClinicalAssessment().getTreatmentPlan() );
		map.put( "recordInvResult", outPatientDesk.getClinicalAssessment().getRecordInvestigationResults() );
		map.put( "capturedByLabel", outPatientDesk.getCapturedBy().getFullName() );
		JasperReport report = this.commonService.compileReportDesign( this.utilService,
		                                                              CLERK_GENERAL_OUT_PATIENT_CONSULTATION );
		return new SubReportDto( map, report, dateTime );
	}

	private SubReportDto getInpatientConsultationActivity() {
		return null;
	}

	private SubReportDto getClinicTransferEFolderData( PatientClinicReferral clinicTransfer, String dateTime ) {
		Map<String, Object> map = new HashMap<>();
		map.put( "transferredBy", clinicTransfer.getReferredBy().getFullName() );
		map.put( "transferTo", clinicTransfer.getReferredToClinic().getName() );
		map.put( "transferNote", clinicTransfer.getReferralNotes() );
		JasperReport report = this.commonService.compileReportDesign( this.utilService, CLERK_CLINIC_TRANSFER );
		return new SubReportDto( map, report, dateTime );
	}

	private SubReportDto getDrugAdministrationEFolderData() {
        /*
            name
            formulation
            dosage
            frequency
            days
            quantity
            comment
            performedBy
         */
		return null;
	}

}
