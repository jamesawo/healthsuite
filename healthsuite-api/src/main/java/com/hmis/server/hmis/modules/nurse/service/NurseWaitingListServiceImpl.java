package com.hmis.server.hmis.modules.nurse.service;

import com.hmis.server.hmis.common.common.dto.ResponseDto;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.service.DepartmentServiceImpl;
import com.hmis.server.hmis.common.socket.SockDto;
import com.hmis.server.hmis.common.socket.SockMessageService;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.common.user.service.UserServiceImpl;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.emr.service.PatientAdmissionServiceImpl;
import com.hmis.server.hmis.modules.nurse.dto.*;
import com.hmis.server.hmis.modules.nurse.iservice.INurseWaitingListService;
import com.hmis.server.hmis.modules.nurse.model.NurseWaitingList;
import com.hmis.server.hmis.modules.nurse.model.NurseWaitingListHistory;
import com.hmis.server.hmis.modules.nurse.repository.NurseWaitingListHistoryRepository;
import com.hmis.server.hmis.modules.nurse.repository.NurseWaitingListRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


import static com.hmis.server.hmis.common.constant.HmisConfigConstants.DESTINATION_NURSE_WAITING_LIST;

@Service
public class NurseWaitingListServiceImpl implements INurseWaitingListService {
	@Autowired
	private NurseWaitingListRepository waitingListRepository;
	@Autowired
	private NurseWaitingListHistoryRepository waitingListHistoryRepository;
	@Autowired
	private SockMessageService sockMessageService;
	@Autowired
	private PatientAdmissionServiceImpl admissionService;
	@Autowired
	private UserServiceImpl userService;
	@Autowired
	private DepartmentServiceImpl departmentService;

	@Override
	public Optional< NurseWaitingList > findOneByPatientId(Long patientId) {
		return this.waitingListRepository.findByPatientDetailId(patientId);
	}

	@Override
	public void addPatientToWaitingList(PatientDetail patientDetail, List< Long > clinicIds) {
		if( ! this.findOneByPatientId(patientDetail.getId()).isPresent() ) {
			boolean onAdmission = this.admissionService.isPatientOnAdmission(patientDetail.getId());
			if( ! onAdmission ) {
				NurseWaitingList waitingList = new NurseWaitingList();
				waitingList.setPatientDetail(patientDetail);
				waitingList.setClinics(clinicIds);
				waitingList.setWaitingStatus(WaitingStatusEnum.WAITING.name());
				NurseWaitingList newWaitingList = this.waitingListRepository.save(waitingList);
				this.notifyWaitingListSockListener(newWaitingList, NWaitingNotifTypeEnum.ADD);
			}
		}
	}

	@Override
	public List< NurseWaitingDto > getActiveWaitingList() {
		//todo:: paginate data and implement infinite scroll in front end
		List<NurseWaitingDto> dtos = new ArrayList<>();
		List< NurseWaitingList > lists = this.waitingListRepository.findAll();
		if( ! lists.isEmpty() ) {
			dtos = lists.stream().map(this::mapModelToDto).collect(Collectors.toList());
		}
		return dtos;
	}

	@Override
	public ResponseDto removePatientFromWaitingList(NurseWaitingHistoryDto waitingHistoryDto) {
		Optional< NurseWaitingList > waiting = this.findOneByPatientId(waitingHistoryDto.getPatient().getPatientId());
		if( waiting.isPresent() ) {
			NurseWaitingList waitingList = waiting.get();
			NurseWaitingListHistory history = this.setNewWaitingHistory(waitingList, waitingHistoryDto);
			this.addToWaitingListHistory(history);
			this.waitingListRepository.deleteById(waitingList.getId());
			this.notifyWaitingListSockListener(waitingList, NWaitingNotifTypeEnum.REMOVE);
			return new ResponseDto(true);
		}
		return new ResponseDto(false);
	}

	@Override
	public List< NurseWaitingDto > getNurseAttendedList(AttendedDto attendedDto) {
		if( ObjectUtils.isEmpty(attendedDto.getAttendedBy()) ) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		if( ObjectUtils.isEmpty(attendedDto.getClinic()) ) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}

		List< NurseWaitingDto > attendedDtos = new ArrayList<>();
		User attendedBy = this.userService.findOneRaw(attendedDto.getAttendedBy().getId().get());
		Department clinic = this.departmentService.findOne(attendedDto.getClinic().getId().get());
		List< NurseWaitingListHistory > attendedList =
				this.waitingListHistoryRepository.findAllByAttendedByUserAndClinicAndWaitingStatusEnumAndAttendedDate(attendedBy, clinic, WaitingStatusEnum.ATTENDED, LocalDate.now());
		if( ! attendedList.isEmpty() ) {
			List< NurseWaitingDto > list = new ArrayList<>();
			for( NurseWaitingListHistory history : attendedList ) {
				NurseWaitingDto dto = new NurseWaitingDto();
				PatientDetail patientDetail = history.getPatientDetail();
				dto.setPatientId(patientDetail.getId());
				dto.setPatientNumber(patientDetail.getPatientNumber());
				dto.setPatientName(patientDetail.getFullName());
				dto.setPatientCategory(patientDetail.getPatientCategory().name());
				dto.setClinicIds(Collections.singletonList(clinic.getId()));
				list.add(dto);
			}
			attendedDtos = list;
		}
		return attendedDtos;

	}

	public void runClearWaitingListScheduler() {
		try {
			List< NurseWaitingList > waitingLists = this.waitingListRepository.findAll();
			if( waitingLists.size() > 0 ) {
				List< NurseWaitingListHistory > histories = waitingLists.stream().map(this::setUnAttendedHistory).collect(Collectors.toList());
				this.waitingListHistoryRepository.saveAll(histories);
				//remove all from waiting list
				this.removeAllFromWaitingList();
			}
		}
		catch( Exception e ) {
			System.out.println("Failed to clear waiting list " + e.getMessage());
		}
	}

	@Transactional
	public void removeAllFromWaitingList() {
		this.waitingListRepository.deleteAll();
	}

	private AttendedDto setAttendedDto(NurseWaitingListHistory history) {
		AttendedDto dto = new AttendedDto();
		if( history.getPatientDetail() != null ) {
			dto.setPatient(history.getPatientDetail().transformToDto());
		}
		return dto;
	}

	private NurseWaitingListHistory setUnAttendedHistory(NurseWaitingList waiting) {
		NurseWaitingListHistory history = new NurseWaitingListHistory();
		history.setPatientDetail(waiting.getPatientDetail());
		history.setWaitingStatusEnum(WaitingStatusEnum.UNATTENDED);
		history.setEnterDate(waiting.getDate());
		history.setEnterTime(waiting.getTime());
		history.setClinic(new Department(waiting.getClinics().get(0)));
		return history;
	}

	private NurseWaitingListHistory setNewWaitingHistory(NurseWaitingList waitingList, NurseWaitingHistoryDto dto) {
		NurseWaitingListHistory history = new NurseWaitingListHistory();
		history.setAttendedByUser(this.userService.findOneRaw(dto.getAttendedBy().getId().get()));
		history.setPatientDetail(waitingList.getPatientDetail());
		history.setClinic(this.departmentService.findOneRaw(dto.getClinic().getId()));
		history.setWaitingStatusEnum(WaitingStatusEnum.ATTENDED);
		history.setEnterDate(waitingList.getDate());
		history.setEnterTime(waitingList.getTime());
		history.setAttendedDate(LocalDate.now());
		history.setAttendedTime(LocalTime.now());
		return history;
	}

	private void addToWaitingListHistory(NurseWaitingListHistory model) {
		this.waitingListHistoryRepository.save(model);
	}

	private void notifyWaitingListSockListener(NurseWaitingList waitingList, NWaitingNotifTypeEnum type) {
		SockDto data = new SockDto();
		data.setTitle("Nurse Waiting List");
		PatientDetail patient = waitingList.getPatientDetail();
		JSONObject object = new JSONObject();
		JSONArray array = new JSONArray();

		object.put("patientId", patient.getId().toString());
		object.put("patientCategory", patient.getPatientCategory().name());
		object.put("patientName", patient.getFullName());
		object.put("patientNumber", patient.getPatientNumber());
		object.put("id", waitingList.getId());
		object.put("type", type);
		if( ! waitingList.getClinics().isEmpty() ) {
			for( Long id : waitingList.getClinics() ) {
				array.put(id);
			}
			object.put("clinicIds", array);
		}
		data.setContent(object.toString());
		this.sockMessageService.sendMessage(DESTINATION_NURSE_WAITING_LIST, data);
	}

	private NurseWaitingDto mapModelToDto(NurseWaitingList model){
		NurseWaitingDto dto = new NurseWaitingDto();
		if( ObjectUtils.isNotEmpty( model.getId())){
			dto.setId(model.getId());
		}
		if( ObjectUtils.isNotEmpty(model.getPatientDetail()) ){
			dto.setPatientNumber(model.getPatientDetail().getPatientNumber());
			dto.setPatientId(model.getPatientDetail().getId());
			dto.setPatientName(model.getPatientDetail().getFullName());
			dto.setPatientCategory(model.getPatientDetail().getPatientCategory().name());
		}

		if( ObjectUtils.isNotEmpty(model.getWaitingStatus()) ){
			dto.setWaitingStatus(model.getWaitingStatus());
		}
		if( ObjectUtils.isNotEmpty(model.getClinics()) ) {
			dto.setClinicIds(model.getClinics());
		}
		return dto;
	}

}
