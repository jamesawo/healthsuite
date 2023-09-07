package com.hmis.server.hmis.modules.nurse.service;

import com.hmis.server.hmis.common.common.dto.NursingNoteLabelDto;
import com.hmis.server.hmis.common.common.dto.ResponseDto;
import com.hmis.server.hmis.common.common.service.DepartmentServiceImpl;
import com.hmis.server.hmis.common.common.service.NursingNoteLabelServiceImpl;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.common.user.service.UserServiceImpl;
import com.hmis.server.hmis.modules.clearking.service.PatientClerkActivityService;
import com.hmis.server.hmis.modules.emr.service.PatientDetailServiceImpl;
import com.hmis.server.hmis.modules.nurse.dto.NurseNoteDto;
import com.hmis.server.hmis.modules.nurse.iservice.INurseNote;
import com.hmis.server.hmis.modules.nurse.model.NurseNote;
import com.hmis.server.hmis.modules.nurse.repository.NurseNoteRepository;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class NurseNoteServiceImpl implements INurseNote {

	private final NurseNoteRepository noteRepository;
	private final DepartmentServiceImpl departmentService;
	private final PatientDetailServiceImpl patientDetailService;
	private final UserServiceImpl userService;
	private final NursingNoteLabelServiceImpl labelService;
	private final PatientClerkActivityService activityService;

	@Autowired
	public NurseNoteServiceImpl(
			NurseNoteRepository noteRepository,
			DepartmentServiceImpl departmentService,
			PatientDetailServiceImpl patientDetailService,
			UserServiceImpl userService,
			NursingNoteLabelServiceImpl labelService,
			@Lazy PatientClerkActivityService activityService) {
		this.noteRepository = noteRepository;
		this.departmentService = departmentService;
		this.patientDetailService = patientDetailService;
		this.userService = userService;
		this.labelService = labelService;
		this.activityService = activityService;
	}

	@Override
	public ResponseDto createOne(NurseNoteDto dto) {
		try {
			this.onValidateBeforeSave(dto);
			NurseNote nurseNote = this.mapToModel(dto);
			NurseNote note = this.noteRepository.save(nurseNote);
			this.activityService.saveNurseNoteActivity(note);
			return new ResponseDto(HmisConstant.SUCCESS_MESSAGE);
		}catch( Exception e ){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}

	}

	public NurseNoteDto mapToDto(NurseNote model1){
		NurseNoteDto dto = new NurseNoteDto();
		if( ObjectUtils.isNotEmpty(model1.getId()) ){
			dto.setId(model1.getId());
		}
		if( ObjectUtils.isNotEmpty(model1.getCaptureFromLocation()) ){
			dto.setClinic(this.departmentService.mapModelToDto(model1.getCaptureFromLocation()));
		}
		if( ObjectUtils.isNotEmpty(model1.getCapturedBy()) ){
			dto.setNurse(this.userService.mapToDtoClean(model1.getCapturedBy()));
			dto.setCapturedByLabel(model1.getCapturedBy().getFullName());
		}
		if( ObjectUtils.isNotEmpty(model1.getLabel()) ){
			dto.setLabel(new NursingNoteLabelDto(
					Optional.of(model1.getLabel().getId()),
					Optional.of(model1.getLabel().getName())
			));
		}
		if( ObjectUtils.isNotEmpty(model1.getNote()) ){
			dto.setNote(model1.getNote());
		}
		if( ObjectUtils.isNotEmpty(model1.getPatient()) ){
			dto.setPatient(model1.getPatient().transformToDto());
		}
		return dto;
	}

	private NurseNote mapToModel(NurseNoteDto dto){
		NurseNote model = new NurseNote();
		if( ObjectUtils.isNotEmpty(dto.getId()) ){
			model.setId(dto.getId());
		}
		if( ObjectUtils.isNotEmpty(dto.getClinic()) ){
			model.setCaptureFromLocation(this.departmentService.findOneRaw(dto.getClinic().getId()));
		}
		if( ObjectUtils.isNotEmpty(dto.getNurse()) ){
			model.setCapturedBy(this.userService.findOneRaw(dto.getNurse().getId().get()));
		}
		if( ObjectUtils.isNotEmpty(dto.getLabel()) ){
			model.setLabel(this.labelService.findOneRaw(dto.getLabel().getId().get()));
		}
		if( ObjectUtils.isNotEmpty(dto.getNote()) ){
			model.setNote(dto.getNote());
		}
		if( ObjectUtils.isNotEmpty(dto.getPatient()) ){
			model.setPatient(this.patientDetailService.findPatientDetailById(dto.getPatient().getPatientId()));
		}
		return model;
	}

	private void onValidateBeforeSave(NurseNoteDto dto){
		if( ObjectUtils.isEmpty( dto.getClinic() ) ){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Clinic is required");
		}
		if( ObjectUtils.isEmpty(dto.getPatient()) ){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Patient is required");
		}
		if( ObjectUtils.isEmpty(dto.getNurse()) ){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nurse is required");
		}
		if( ObjectUtils.isEmpty(dto.getNote()) ){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Note is required");
		}
	}
}
