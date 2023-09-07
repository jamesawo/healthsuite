package com.hmis.server.hmis.common.socket;

import com.hmis.server.hmis.common.constant.HmisTaskScheduler;
import com.hmis.server.hmis.modules.clearking.service.DoctorWaitingListServiceImpl;
import com.hmis.server.hmis.modules.emr.service.PatientRevisitServiceImpl;
import com.hmis.server.hmis.modules.nurse.service.NurseWaitingListServiceImpl;

import java.util.Date;

import com.hmis.server.hmis.modules.shift.service.CashierShiftServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SockScheduler {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final PatientRevisitServiceImpl patientRevisitService;
    private final NurseWaitingListServiceImpl waitingListService;
    private final DoctorWaitingListServiceImpl doctorWaitingListService;

    private final CashierShiftServiceImpl shiftService;

    public SockScheduler(PatientRevisitServiceImpl patientRevisitService,
                         NurseWaitingListServiceImpl waitingListService,
                         DoctorWaitingListServiceImpl doctorWaitingListService,
                         CashierShiftServiceImpl shiftService
    ) {
        this.patientRevisitService = patientRevisitService;
        this.waitingListService = waitingListService;
        this.doctorWaitingListService = doctorWaitingListService;
        this.shiftService = shiftService;
    }

    @Scheduled(cron = HmisTaskScheduler.EVERY_DAY_AT_MIDNIGHT)
    public void runPatientRevisitStatusScheduler() {
        LOGGER.info("Running Patient Revisit Scheduler @:" + new Date());
        this.patientRevisitService.runPatientRevisitStatusScheduler();

        //clear waiting list for previous day
        LOGGER.info("Running Nurse Waiting List Scheduler @:" + new Date());
        this.waitingListService.runClearWaitingListScheduler();

        LOGGER.info("Running Doctor Waiting List Scheduler @:" + new Date());
        this.doctorWaitingListService.runClearWaitingListScheduler();

        //clear cashier shift record for previous day
        LOGGER.info("Running Cashier Shift Scheduler @: " + new Date());
        this.shiftService.runClearCashierShiftRecordSchedule();
    }
}

