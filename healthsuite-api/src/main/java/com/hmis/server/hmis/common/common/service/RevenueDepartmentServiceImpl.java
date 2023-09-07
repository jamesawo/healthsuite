package com.hmis.server.hmis.common.common.service;

import com.hmis.server.hmis.common.common.Iservice.IRevenueDepartmentService;
import com.hmis.server.hmis.common.common.dto.GenerateCodeDto;
import com.hmis.server.hmis.common.common.dto.RevenueDepartmentDto;
import com.hmis.server.hmis.common.common.model.RevenueDepartment;
import com.hmis.server.hmis.common.common.repository.RevenueDepartmentRepository;
import com.hmis.server.hmis.common.constant.HmisCodeDefaults;
import com.hmis.server.hmis.common.constant.HmisGlobalSettingKeys;
import com.hmis.server.hmis.common.exception.HmisApplicationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.hmis.server.hmis.modules.reports.dto.DailyCashCollectionSearchDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RevenueDepartmentServiceImpl implements IRevenueDepartmentService {
    @Autowired
    RevenueDepartmentRepository revenueDepartmentRepository;
    @Autowired
    RevenueDepartmentTypeServiceImpl revenueDepartmentTypeService;
    @Autowired
    CommonService commonService;

    @Override
    public List<RevenueDepartment> findRevenueForDailyCollection(DailyCashCollectionSearchDto dto) {
        List<RevenueDepartment> revenueDepartmentList = new ArrayList<>();
        if (dto.getRevenueDepartment() != null && dto.getRevenueDepartment().getId().isPresent()) {
            revenueDepartmentList.add(this.findOneRaw(dto.getRevenueDepartment().getId().get()));
            return revenueDepartmentList;
        }
        return this.findAllRaw();
    }

    @Override
    public RevenueDepartmentDto createOne(RevenueDepartmentDto revenueDepartmentDto) {
        if (!revenueDepartmentDto.getRevenueDepartmentTypeDto().isPresent()) {
            throw new HmisApplicationException("Cannot Create Revenue Department Without Type");
        }
        this.onCheckCanHandleDeposit(revenueDepartmentDto);
        revenueDepartmentDto.setCode(Optional.ofNullable(generateRevenueDepartmentCode()));
        RevenueDepartment revenueDepartment = revenueDepartmentRepository.save(mapDtoToModel(revenueDepartmentDto));
        return mapModelToDto(revenueDepartment);
    }

    @Override
    public List<RevenueDepartmentDto> createInBatch(List<RevenueDepartmentDto> revenueDepartmentDtoList) {
        return null;
    }

    @Override
    public List<RevenueDepartment> findAllRaw() {
        List<RevenueDepartment> list = new ArrayList<>();
        List<RevenueDepartment> all = this.revenueDepartmentRepository.findAll();
        if (!all.isEmpty()) {
            return all;
        } else {
            return list;
        }
    }

    @Override
    public List<RevenueDepartmentDto> findAll() {
        return mapModelListToDtoList(revenueDepartmentRepository.findAll());
    }

    @Override
    public List<RevenueDepartmentDto> findByRevenueDepartmentType(RevenueDepartmentDto revenueDepartmentDto) {
        return null;
    }

    @Override
    public List<RevenueDepartmentDto> findByNameLike(RevenueDepartmentDto revenueDepartmentDto) {
        return null;
    }

    @Override
    public RevenueDepartmentDto findByName(RevenueDepartmentDto revenueDepartmentDto) {
        return null;
    }

    @Override
    public RevenueDepartmentDto findOne(RevenueDepartmentDto revenueDepartmentDto) {
        return null;
    }

    @Override
    public RevenueDepartment findByCode(String code) {
        return this.revenueDepartmentRepository.findByCode(code);
    }

    @Override
    public List<RevenueDepartmentDto> updateInBatch(List<RevenueDepartmentDto> revenueDepartmentDtoList) {
        return null;
    }

    @Override
    public RevenueDepartmentDto updateOne(RevenueDepartmentDto revenueDepartmentDto) {
        return null;
    }

    @Override
    public void deactivateOne(RevenueDepartmentDto revenueDepartmentDto) {

    }

    @Override
    public void deactivateInBatch(List<RevenueDepartmentDto> revenueDepartmentDtoList) {

    }

    @Override
    public void activateOne(RevenueDepartmentDto revenueDepartmentDto) {

    }

    @Override
    public void activateInBatch(List<RevenueDepartmentDto> revenueDepartmentDtoList) {
    }

    @Override
    public boolean isRevenueDepartmentExist(RevenueDepartmentDto revenueDepartmentDto) {
        if (revenueDepartmentDto.getName().isPresent()) {
            return revenueDepartmentRepository.findAll().stream().anyMatch(revenueDepartment -> revenueDepartment.getName().compareToIgnoreCase(revenueDepartmentDto.getName().get()) == 0);
        } else {
            throw new HmisApplicationException("Provide Revenue Department Name");
        }

    }

    @Override
    public List<RevenueDepartmentDto> createInBatchFromExcel(MultipartFile file) {
        return null;
    }

    @Override
    public List<RevenueDepartmentDto> searchRevenueDepartment(String nameOrCode) {
        List<RevenueDepartmentDto> revenueDepartmentDtoList = new ArrayList<>();
        List<RevenueDepartment> departmentList = this.revenueDepartmentRepository.
                findAllByNameContainsIgnoreCaseOrCodeContainsIgnoreCase(nameOrCode, nameOrCode);

        if (departmentList.size() > 0) {
            revenueDepartmentDtoList = departmentList.stream().map(this::mapModelToDto).collect(Collectors.toList());
        }
        return revenueDepartmentDtoList;
    }

    @Override
    public RevenueDepartmentDto findDepositRevenueDepartment() {
        List<RevenueDepartment> departments = this.revenueDepartmentRepository.findByHandleDepositIsTrue();
        int size = departments.size();
        RevenueDepartmentDto revenueDepartmentDto = new RevenueDepartmentDto();
        if (size == 1) {
            revenueDepartmentDto = this.mapModelToDto(departments.get(0));
        }
        // todo: remove ln 164 - 176 (only 1 revenue department can handle deposit
        else {
            if (size > 1) {
                RevenueDepartment reDepartment = new RevenueDepartment();
                for (RevenueDepartment revenueDepartment : departments) {
                    if (revenueDepartment.getName().toUpperCase().contains("DEPOSIT")) {
                        reDepartment = revenueDepartment;
                    }
                }
                revenueDepartmentDto = this.mapModelToDto(reDepartment);
            } else {
                revenueDepartmentDto = new RevenueDepartmentDto();
            }
        }

        return revenueDepartmentDto;
    }

    @Override
    public RevenueDepartment findOneRaw(Long id) {
        return this.revenueDepartmentRepository.getOne(id);
    }

    @Override
    public RevenueDepartment findById(Long revenueDepartmentId) {
        Optional<RevenueDepartment> one = this.revenueDepartmentRepository.findById(revenueDepartmentId);
        if (!one.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Cannot Find Revenue Department With ID: %s", revenueDepartmentId));
        }
        return one.get();
    }

    @Override
    public boolean isDepositRevenueDepartment(RevenueDepartment department) {
        return department.isHandleDeposit();
    }

    public boolean hasAnyDepositRevenueDepartment(){
        boolean flag = false;
        List<RevenueDepartment> allRaw = this.findAllRaw();
        if (!allRaw.isEmpty()){
            for (RevenueDepartment depart : allRaw) {
                if (depart.isHandleDeposit()) {
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    private RevenueDepartment mapDtoToModel(RevenueDepartmentDto revenueDepartmentDto) {
        if (revenueDepartmentDto != null) {
            RevenueDepartment revenueDepartment = new RevenueDepartment();
            if (revenueDepartmentDto.getId() != null && revenueDepartmentDto.getId().isPresent()) {
                revenueDepartment.setId(revenueDepartmentDto.getId().get());
            }
            if (revenueDepartmentDto.getCode() != null && revenueDepartmentDto.getCode().isPresent()) {
                revenueDepartment.setCode(revenueDepartmentDto.getCode().get());
            }
            if (revenueDepartmentDto.getName() != null && revenueDepartmentDto.getName().isPresent()) {
                revenueDepartment.setName(revenueDepartmentDto.getName().get());
            }
            if (revenueDepartmentDto.getRevenueDepartmentTypeDto() != null && revenueDepartmentDto.getRevenueDepartmentTypeDto().isPresent()) {
                revenueDepartment.setRevenueDepartmentType(revenueDepartmentTypeService.mapDtoToModel(revenueDepartmentDto.getRevenueDepartmentTypeDto().get()));
            }
            if (revenueDepartmentDto.getIsAttachedToDeposit()) {
                revenueDepartment.setHandleDeposit(revenueDepartmentDto.getIsAttachedToDeposit());
            }
            return revenueDepartment;
        } else {
            throw new HmisApplicationException("Cannot Map Empty Revenue Department Dto To Model");
        }
    }

    private RevenueDepartmentDto mapModelToDto(RevenueDepartment revenueDepartment) {
        if (revenueDepartment != null) {
            RevenueDepartmentDto revenueDepartmentDto = new RevenueDepartmentDto();
            setDto(revenueDepartmentDto, revenueDepartment);
            return revenueDepartmentDto;
        } else {
            throw new HmisApplicationException("Cannot Map Empty Revenue Department to Dto");
        }
    }

    private void setDto(RevenueDepartmentDto revenueDepartmentDto, RevenueDepartment revenueDepartment) {
        if (revenueDepartment.getId() != null) {
            revenueDepartmentDto.setId(Optional.of(revenueDepartment.getId()));
        }
        revenueDepartmentDto.setIsAttachedToDeposit(revenueDepartment.isHandleDeposit());
        if (revenueDepartment.getName() != null) {
            revenueDepartmentDto.setName(Optional.of(revenueDepartment.getName()));
        }
        if (revenueDepartment.getCode() != null) {
            revenueDepartmentDto.setCode(Optional.of(revenueDepartment.getCode()));
        }
        if (revenueDepartment.getRevenueDepartmentType() != null) {
            revenueDepartmentDto.setRevenueDepartmentTypeDto(Optional.of(revenueDepartmentTypeService.mapModelToDto(revenueDepartment.getRevenueDepartmentType())));
        }
        if (revenueDepartment.getName() != null && revenueDepartment.getCode() != null) {
            revenueDepartmentDto.setNameAndCode(Optional.of(revenueDepartment.getName() + " - " + revenueDepartment.getCode()));
        }
    }

    private List<RevenueDepartment> mapDtoListToModelList(List<RevenueDepartmentDto> revenueDepartmentDtoList) {
        return null;
    }

    private List<RevenueDepartmentDto> mapModelListToDtoList(List<RevenueDepartment> revenueDepartmentList) {
        List<RevenueDepartmentDto> revenueDepartmentDtoList = new ArrayList<>();
        if (!revenueDepartmentList.isEmpty()) {
            revenueDepartmentList.forEach(revenueDepartment -> {
                RevenueDepartmentDto revenueDepartmentDto = new RevenueDepartmentDto();
                setDto(revenueDepartmentDto, revenueDepartment);
                revenueDepartmentDtoList.add(revenueDepartmentDto);
            });
        }
        return revenueDepartmentDtoList;
    }

    private String generateRevenueDepartmentCode() {
        GenerateCodeDto generateCodeDto = new GenerateCodeDto();
        generateCodeDto.setDefaultPrefix(HmisCodeDefaults.REVENUE_DEPARTMENT_DEFAULT_CODE);
        generateCodeDto.setGlobalSettingKey(Optional.of(HmisGlobalSettingKeys.REVENUE_DEPARTMENT_CODE));
        generateCodeDto.setLastGeneratedCode(revenueDepartmentRepository.findTopByOrderByIdDesc().map(RevenueDepartment::getCode));
        return commonService.generateDataCode(generateCodeDto);
    }

    private void onCheckCanHandleDeposit(RevenueDepartmentDto dto) {
        if (dto.getIsAttachedToDeposit() && this.hasIsAssignedToDeposit()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Deposit Is Already Assigned A Different Revenue Department");
        }
    }

    private boolean hasIsAssignedToDeposit() {
        return !this.revenueDepartmentRepository.findByHandleDepositIsTrue().isEmpty();
    }

}
