package com.hmis.server.hmis.modules.shift.service;

import com.hmis.server.hmis.common.common.dto.DepartmentDto;
import com.hmis.server.hmis.common.common.dto.GenerateCodeDto;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.service.CommonService;
import com.hmis.server.hmis.common.constant.HmisCodeDefaults;
import com.hmis.server.hmis.common.constant.HmisGlobalSettingKeys;
import com.hmis.server.hmis.common.user.dto.UserDto;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.modules.shift.dto.CashierCompiledShiftDto;
import com.hmis.server.hmis.modules.shift.model.CashierCompiledShift;
import com.hmis.server.hmis.modules.shift.model.CashierShift;
import com.hmis.server.hmis.modules.shift.repository.CashierShiftCompileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CashierCompiledShiftServiceImpl {
    private final CashierShiftCompileRepository shiftCompileRepository;
    private final CommonService commonService;

    @Autowired
    public CashierCompiledShiftServiceImpl(
            CashierShiftCompileRepository shiftCompileRepository,
            CommonService commonService) {
        this.shiftCompileRepository = shiftCompileRepository;
        this.commonService = commonService;
    }

    public CashierCompiledShift findOneById(Long id) {
        Optional<CashierCompiledShift> optional = this.shiftCompileRepository.findById(id);
        if (!optional.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Compiled Shift Record Not Found");
        }
        return optional.get();
    }

    public CashierCompiledShiftDto getCompiledShiftRecord(CashierCompiledShift compiledShift) {
        return this.mapModelToDto(compiledShift);
    }

    public CashierCompiledShiftDto mapModelToDto(CashierCompiledShift model) {
        CashierCompiledShiftDto dto = new CashierCompiledShiftDto();
        if (model.getId() != null) {
            dto.setId(model.getId());
        }
        if (model.getCompiledDate() != null) {
            dto.setCompiledDate(model.getCompiledDate());
        }
        if (model.getCompiledTime() != null) {
            dto.setCompiledTime(model.getCompiledTime());
        }
        if (model.getCompiledBy() != null) {
            dto.setCompiledBy(new UserDto(Optional.ofNullable(model.getCompiledBy().getId()), Optional.ofNullable(model.getCompiledBy().getFullName())));
        }
        if (model.getCode() != null) {
            dto.setCode(model.getCode());
        }
        if (model.getLocation() != null) {
            dto.setLocation(new DepartmentDto(model.getLocation().getId(), model.getLocation().getName()));
        }
        return dto;
    }

    public CashierCompiledShift saveShiftsCompilation(List<CashierShift> cashierShifts, User compiledBy, Department location) {
        CashierCompiledShift shift = new CashierCompiledShift();
        shift.setCompiledDate(LocalDate.now());
        shift.setCompiledTime(LocalTime.now());
        shift.setCompiledBy(compiledBy);
        shift.setCode(this.generateCompilationCode());
        shift.setLocation(location);
        return this.shiftCompileRepository.save(shift);
    }

    private String generateCompilationCode() {
        GenerateCodeDto codeDto = new GenerateCodeDto();
        codeDto.setGlobalSettingKey(Optional.of(HmisGlobalSettingKeys.CASHIER_SHIFT_COMPILATION_CODE_PREFIX));
        codeDto.setDefaultPrefix(HmisCodeDefaults.CASHIER_SHIFT_COMPILATION_PREFIX);
        codeDto.setLastGeneratedCode(this.shiftCompileRepository.findTopByOrderByIdDesc().map(CashierCompiledShift::getCode));
        return this.commonService.generateDataCode(codeDto);
    }

    public void updateCompiledShiftsRecord(List<CashierShift> shifts, Long compiledId) {
        CashierCompiledShift compiled = this.shiftCompileRepository.getOne(compiledId);
        compiled.setCashierShifts(shifts);
        this.shiftCompileRepository.save(compiled);
    }
}
