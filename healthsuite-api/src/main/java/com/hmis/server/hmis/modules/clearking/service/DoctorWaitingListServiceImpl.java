package com.hmis.server.hmis.modules.clearking.service;

import com.hmis.server.hmis.common.socket.SockDto;
import com.hmis.server.hmis.common.socket.SockMessageService;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.common.user.service.UserServiceImpl;
import com.hmis.server.hmis.modules.clearking.dto.DoctorWaitingDto;
import com.hmis.server.hmis.modules.clearking.dto.DoctorListWrapper;
import com.hmis.server.hmis.modules.clearking.iservice.IDoctorWaitingListService;
import com.hmis.server.hmis.modules.clearking.model.DoctorWaitingList;
import com.hmis.server.hmis.modules.clearking.model.DoctorWaitingListHistory;
import com.hmis.server.hmis.modules.clearking.repository.DoctorWaitingListHistoryRepository;
import com.hmis.server.hmis.modules.clearking.repository.DoctorWaitingListRepository;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.emr.service.PatientDetailServiceImpl;
import com.hmis.server.hmis.modules.nurse.dto.WaitingStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.hmis.server.hmis.common.constant.HmisConfigConstants.DESTINATION_DOCTOR_WAITING_LIST;

@Service
@Slf4j
public class DoctorWaitingListServiceImpl implements IDoctorWaitingListService {
    @Autowired
    private DoctorWaitingListRepository doctorWaitingListRepository;
    @Autowired
    private DoctorWaitingListHistoryRepository waitingListHistoryRepository;
    @Autowired
    private SockMessageService sockMessageService;
    @Autowired
    private PatientDetailServiceImpl patientDetailService;
    @Autowired
    private UserServiceImpl userService;


    @Override
    public void addToWaitingList(DoctorListWrapper dto) {
        DoctorWaitingList waiting = new DoctorWaitingList();
        waiting.setPatientDetail(this.patientDetailService.findPatientDetailById(dto.getPatient().getId()));
        waiting.setClinic(dto.getClinic());
        waiting.setDate(LocalDate.now());
        if(dto.getDoctor() != null){
            waiting.setDoctor(dto.getDoctor());
        }
        DoctorWaitingList waitingList = this.doctorWaitingListRepository.save(waiting);
        this.notifyDoctorWaitingSockListener(waitingList);
    }

    @Override
    public void notifyDoctorWaitingSockListener(DoctorWaitingList waitingList) {
        SockDto data = new SockDto();
        PatientDetail patient = waitingList.getPatientDetail();
        data.setTitle("Doctor Waiting List");
        JSONObject object = new JSONObject();
        object.put("patientId", patient.getId().toString());
        object.put("patientCategory", patient.getPatientCategory().name());
        object.put("patientName", patient.getFullName());
        object.put("patientNumber", patient.getPatientNumber());
        object.put("id", waitingList.getId());
        object.put("waitingStatus", waitingList.getWaitingStatus());
        if (ObjectUtils.isNotEmpty(waitingList.getDoctor())) {
            object.put("doctorId", waitingList.getDoctor().getId());
        }
        if (ObjectUtils.isNotEmpty(waitingList.getClinic())) {
            object.put("clinicId", waitingList.getClinic().getId());
        }
        data.setContent(object.toString());
        this.sockMessageService.sendMessage(DESTINATION_DOCTOR_WAITING_LIST, data);
    }

    @Override
    public List<DoctorWaitingDto> getWaitingListByLocation(Long locationId) {
        List<DoctorWaitingDto> dtoList = new ArrayList<>();
        List<DoctorWaitingList> lists = this.doctorWaitingListRepository.findAllByClinic_Id(locationId);
        if (!lists.isEmpty()) {
            dtoList = lists.stream().map(this::mapToDto).collect(Collectors.toList());
        }
        return dtoList;
    }

    @Override
    public List<DoctorWaitingDto> getAllWaitingList() {
        List<DoctorWaitingDto> dtoList = new ArrayList<>();
        List<DoctorWaitingList> lists = this.doctorWaitingListRepository.findAll();
        if (!lists.isEmpty()) {
            dtoList = lists.stream().map(this::mapToDto).collect(Collectors.toList());
        }
        return dtoList;
    }

    @Override
    public List<DoctorWaitingDto> getAttendedList(Long doctorId) {
        List<DoctorWaitingDto> dtoList = new ArrayList<>();
        List<DoctorWaitingListHistory> list = this.waitingListHistoryRepository.findAllByAttendedByUser_Id(doctorId);
        if (!list.isEmpty()) {
            dtoList = list.stream().map(this::mapHistoryToDto).collect(Collectors.toList());
        }
        return dtoList;
    }

    @Override
    public void runClearWaitingListScheduler() {
        try {
            List<DoctorWaitingList> all = this.doctorWaitingListRepository.findAll();
            if (!all.isEmpty()) {
                for (DoctorWaitingList waitingList : all) {
                    this.saveUnattendedPatientToWaitingHistory(waitingList);
                }
            }
            this.doctorWaitingListRepository.deleteAllInBatch();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            System.out.println("An Error Occurred While Clearing Doctor Waiting List " + e.getMessage());
        }

    }

    @Override
    public ResponseEntity<Boolean> removeFromWaitingList(String patientId, String docId) {
        Optional<DoctorWaitingList> optional = this.doctorWaitingListRepository.findDoctorWaitingListByPatientDetail_Id(Long.valueOf(patientId));
        if (optional.isPresent()){
            User doctor = this.userService.findOneRaw(Long.valueOf(docId));
            this.saveAttendedPatientToWaitingHistory(optional.get(), doctor);
            this.doctorWaitingListRepository.deleteById(optional.get().getId());
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.ok(false);
    }

    private void saveUnattendedPatientToWaitingHistory(DoctorWaitingList waiting) {
        DoctorWaitingListHistory history = this.setWaitingListHistory(waiting, WaitingStatusEnum.UNATTENDED);
        this.waitingListHistoryRepository.save(history);
    }

    private void saveAttendedPatientToWaitingHistory(DoctorWaitingList waiting, User doc){
        DoctorWaitingListHistory history = this.setWaitingListHistory(waiting, WaitingStatusEnum.ATTENDED);
        history.setAttendedByUser(doc);
        this.waitingListHistoryRepository.save(history);
    }

    private DoctorWaitingListHistory setWaitingListHistory(DoctorWaitingList waiting, WaitingStatusEnum statusEnum){
        DoctorWaitingListHistory history = new DoctorWaitingListHistory();
        history.setEnterDate(waiting.getDate());
        history.setEnterTime(waiting.getTime());
        history.setPatientDetail(waiting.getPatientDetail());
        history.setClinic(waiting.getClinic());
        history.setWaitingStatusEnum(statusEnum);
        return history;
    }

    private DoctorWaitingDto mapHistoryToDto(DoctorWaitingListHistory model) {
        DoctorWaitingDto dto = new DoctorWaitingDto();
        PatientDetail patient = model.getPatientDetail();
        this.setPatient(patient, dto);
        if (ObjectUtils.isNotEmpty(model.getClinic())) {
            dto.setClinicId(model.getId());
        }
        return dto;
    }

    private DoctorWaitingDto mapToDto(DoctorWaitingList model) {
        DoctorWaitingDto dto = new DoctorWaitingDto();
        PatientDetail patient = model.getPatientDetail();
        this.setPatient(patient, dto);

        if (ObjectUtils.isNotEmpty(model.getId())) {
            dto.setId(model.getId());
        }

        if (ObjectUtils.isNotEmpty(model.getWaitingStatus())) {
            dto.setWaitingStatus(model.getWaitingStatus());
        }

        if (ObjectUtils.isNotEmpty(model.getClinic())) {
            dto.setClinicId(model.getClinic().getId());
        }

        if (ObjectUtils.isNotEmpty(model.getDoctor())) {
            dto.setDoctorId(model.getDoctor().getId());
        }

        return dto;
    }

    private void setPatient(PatientDetail patient, DoctorWaitingDto dto) {
        if (ObjectUtils.isNotEmpty(patient)) {
            dto.setPatientId(patient.getId());
            dto.setPatientName(patient.getFullName());
            dto.setPatientNumber(patient.getPatientNumber());
            dto.setPatientCategory(patient.getPatientCategory().name());
        }
    }
}
