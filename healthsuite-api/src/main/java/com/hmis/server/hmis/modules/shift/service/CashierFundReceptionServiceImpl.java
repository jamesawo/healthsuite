package com.hmis.server.hmis.modules.shift.service;

import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.common.user.service.UserServiceImpl;
import com.hmis.server.hmis.modules.shift.dto.CashierFundReceptionDto;
import com.hmis.server.hmis.modules.shift.model.CashierFundReception;
import com.hmis.server.hmis.modules.shift.model.CashierShift;
import com.hmis.server.hmis.modules.shift.repository.CashierFundReceptionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Service
@Slf4j
public class CashierFundReceptionServiceImpl {
    private final CashierFundReceptionRepository receptionRepository;
    private final UserServiceImpl userService;

    @Autowired
    public CashierFundReceptionServiceImpl(
            CashierFundReceptionRepository receptionRepository,
            UserServiceImpl userService
    ) {
        this.receptionRepository = receptionRepository;
        this.userService = userService;
    }

    public Optional<CashierFundReception> findByShiftId(Long shiftId){
        return this.receptionRepository.findByShift_Id(shiftId);
    }

    public CashierFundReception getFundReceptionByShift(CashierShift shift) {
        Optional<CashierFundReception> optional = this.findByShiftId(shift.getId());
        if (!optional.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Cannot Find Fund Reception For Shit: %d", shift.getId()));
        }
        return optional.get();
    }

    public CashierFundReceptionDto getFundReceptionRecordByShift(CashierShift model) {
        CashierFundReception fund = this.getFundReceptionByShift(model);
        return this.mapModelToDto(fund);
    }

    public CashierFundReception acknowledgeCashierShift(User user, Department department, CashierShift cashierShift){
        CashierFundReception fundReception = new CashierFundReception();
        fundReception.setShift(cashierShift);
        fundReception.setDate(LocalDate.now());
        fundReception.setTime(LocalTime.now());
        fundReception.setReceivedBy(user);
        fundReception.setLocation(department);
        return this.receptionRepository.save(fundReception);
    }

    private CashierFundReceptionDto mapModelToDto(CashierFundReception model) {
        CashierFundReceptionDto dto = new CashierFundReceptionDto();
        if (model.getId() != null) {
            dto.setId(model.getId());
        }
        if (model.getDate() != null) {
            dto.setDate(model.getDate());
        }
        if (model.getReceivedBy() != null) {
            dto.setReceivedBy(this.userService.mapToDtoClean(model.getReceivedBy()));
        }
        if (model.getTime() != null) {
            dto.setTime(model.getTime());
        }
        return dto;
    }
}
