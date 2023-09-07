package com.hmis.server.hmis.modules.lab.service;

import com.hmis.server.hmis.common.common.dto.LabSpecimenDto;
import com.hmis.server.hmis.common.common.dto.MessageDto;
import com.hmis.server.hmis.common.common.model.LabSpecimen;
import com.hmis.server.hmis.common.common.model.ProductService;
import com.hmis.server.hmis.common.common.service.DepartmentServiceImpl;
import com.hmis.server.hmis.common.common.service.ProductServiceImpl;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.common.user.service.UserServiceImpl;
import com.hmis.server.hmis.modules.lab.dto.LabParameterDto;
import com.hmis.server.hmis.modules.lab.dto.LabParameterSetupDto;
import com.hmis.server.hmis.modules.lab.dto.LabParameterSetupItemDto;
import com.hmis.server.hmis.modules.lab.model.LabParameter;
import com.hmis.server.hmis.modules.lab.model.LabParameterSetup;
import com.hmis.server.hmis.modules.lab.model.LabParameterSetupItem;
import com.hmis.server.hmis.modules.lab.repository.LabParameterSetupItemRepository;
import com.hmis.server.hmis.modules.lab.repository.LabParameterSetupRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LabParameterSetupService {
    private final LabParameterSetupItemRepository setupItemRepository;
    private final LabParameterSetupRepository setupRepository;
    private final ProductServiceImpl productService;
    private final UserServiceImpl userService;
    private final DepartmentServiceImpl departmentService;

    public LabParameterSetupService(
            LabParameterSetupItemRepository setupItemRepository,
            LabParameterSetupRepository setupRepository,
            ProductServiceImpl productService,
            UserServiceImpl userService,
            DepartmentServiceImpl departmentService
    ) {
        this.setupItemRepository = setupItemRepository;
        this.setupRepository = setupRepository;
        this.productService = productService;
        this.userService = userService;
        this.departmentService = departmentService;
    }

    // save new parameter setup
    public ResponseEntity<MessageDto> saveLabParameterSetup(LabParameterSetupDto dto) {
        Optional<LabParameterSetup> parameterExist = this.findByTestService(dto.getTest().getId());
        try {
            LabParameterSetup model = this.mapParamSetupModel(dto);
            LabParameterSetup parameterSetup = this.saveOrUpdateLabParameter(model, parameterExist, dto);
            this.saveParameterSetupItems(parameterSetup, dto.getParameterSetupItems());
            return ResponseEntity.ok().body(new MessageDto(HmisConstant.SUCCESS_MESSAGE));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // before saving check if lab parameter already exist, if yes, then remove the old parameters before saving new parameters
    public LabParameterSetup saveOrUpdateLabParameter(LabParameterSetup model, Optional<LabParameterSetup> optional, LabParameterSetupDto dto) {
        if (optional.isPresent()) {
            LabParameterSetup labParameterSetup = optional.get();
            // this.setupItemRepository.deleteAll(labParameterSetup.getParameterSetupItems());
            // cannot delete, test range depends on parameter setup
            model.setId(labParameterSetup.getId());
        }
        return this.setupRepository.save(model);
    }

    // update parameter setup to avoid duplicate entries
    public ResponseEntity<MessageDto> updateLabParameterSetup(LabParameterSetupDto dto) {
        return null;
    }

    public Optional<LabParameterSetup> findByTestService(Long productServiceId) {
        ProductService service = this.productService.findOneProductService(productServiceId);
        return this.setupRepository.findByTest(service);
    }

    public Optional<LabParameterSetup> findLabParamSetupByTest(ProductService test) {
        return this.setupRepository.findByTest(test);
    }

    public LabParameterSetupDto searchByTest(Long testId) {
        Optional<LabParameterSetup> optional = this.findByTestService(testId);
        return optional.map(this::mapParamSetupModelToDto).orElseGet(LabParameterSetupDto::new);
    }

    private boolean isTestParameterExist(Long productServiceId) {
        Optional<LabParameterSetup> optional = this.findByTestService(productServiceId);
        return optional.isPresent();
    }

    private void saveParameterSetupItems(LabParameterSetup setup, List<LabParameterSetupItemDto> items) {
        if (items != null && !items.isEmpty()) {
            for (LabParameterSetupItemDto item : items) {
                LabParameterSetupItem setupItem = this.mapParamSetupItemModel(setup, item);
                Optional<LabParameterSetupItem> optionalItem = this.isLabParameterSetupItemExist(setup, setupItem.getParameter());
                if (!optionalItem.isPresent()) {
                    this.setupItemRepository.save(setupItem);
                } else {
                    LabParameterSetupItem existSetupItem = optionalItem.get();
                    if (existSetupItem.isDeleted()) {
                        existSetupItem.setDeleted(false);
                        this.setupItemRepository.save(existSetupItem);

                    }
                }
            }
        }
    }

    private Optional<LabParameterSetupItem> isLabParameterSetupItemExist(LabParameterSetup setup, LabParameter labParameter) {
        return this.setupItemRepository.findByLabParameterSetupAndParameter(setup, labParameter);
    }

    // map parameter setup item model
    private LabParameterSetupItem mapParamSetupItemModel(LabParameterSetup setup, LabParameterSetupItemDto dto) {
        LabParameterSetupItem model = new LabParameterSetupItem();
        if (ObjectUtils.isNotEmpty(dto.getId())) {
            model.setId(dto.getId());
        }
        if (ObjectUtils.isNotEmpty(dto.getParameter())) {
            model.setParameter(new LabParameter(dto.getParameter().getId()));
        }
        if (ObjectUtils.isNotEmpty(dto.getParameterHierarchy())) {
            model.setParameterHierarchy(dto.getParameterHierarchy());
        }
        model.setLabParameterSetup(setup);
        return model;
    }

    private LabParameterSetupItemDto mapParamSetupItemModelToDto(LabParameterSetupItem model) {
        LabParameterSetupItemDto dto = new LabParameterSetupItemDto();
        dto.setId(model.getId());
        dto.setParameter(new LabParameterDto(model.getParameter().getId(), model.getParameter().getTitle()));
        dto.setParameterHierarchy(model.getParameterHierarchy());
        return dto;
    }

    // map parameter setup model
    private LabParameterSetup mapParamSetupModel(LabParameterSetupDto dto) {
        LabParameterSetup model = new LabParameterSetup();
        if (dto.getTest() != null && dto.getTest().getId() != null) {
            model.setTest(this.productService.findOneProductService(dto.getTest().getId()));
        }
        if (dto.getSpecimen() != null && dto.getSpecimen().getId().isPresent()) {
            model.setSpecimen(new LabSpecimen(dto.getSpecimen().getId().get()));
        }
        if (dto.getSpecimenColor() != null) {
            model.setSpecimenColor(dto.getSpecimenColor());
        }
        if (dto.getCapturedBy() != null && dto.getCapturedBy().getId().isPresent()) {
            model.setCapturedBy(this.userService.findOneRaw(dto.getCapturedBy().getId().get()));
        }
        if (dto.getCapturedFrom() != null && dto.getCapturedFrom().getId().isPresent()) {
            model.setCapturedFrom(this.departmentService.findOne(dto.getCapturedFrom().getId().get()));
        }
        model.setIsRequirePathologist(dto.getIsRequirePathologist() != null ? dto.getIsRequirePathologist() : false);
        model.setIsParasitologTest(dto.getIsParasitologTest() != null ? dto.getIsParasitologTest() : false);
        model.setIsBoneMarrowTest(dto.getIsBoneMarrowTest() != null ? dto.getIsBoneMarrowTest() : false);
        model.setIsHistopathologySFATest(dto.getIsHistopathologySFA() != null ? dto.getIsHistopathologySFA() : false);
        model.setIsImmunoTest(dto.getIsImmunoTest() != null ? dto.getIsImmunoTest() : false);
        model.setIsSpecialTest(dto.getIsSpecialTest() != null ? dto.getIsSpecialTest() : false);
        model.setDepartmentTypeEnum(dto.getDepartmentTypeEnum());
        return model;
    }

    // map parameter setup model
    private LabParameterSetupDto mapParamSetupModelToDto(LabParameterSetup setup) {
        LabParameterSetupDto dto = new LabParameterSetupDto();
        dto.setId(setup.getId());
        dto.setTest(setup.getTest().mapToDto());
        dto.setSpecimen(new LabSpecimenDto(setup.getSpecimen().getId(), setup.getSpecimen().getName()));
        dto.setSpecimenColor(setup.getSpecimenColor());
        dto.setIsParasitologTest(setup.getIsParasitologTest());
        dto.setIsRequirePathologist(setup.getIsRequirePathologist());
        List<LabParameterSetupItem> items = setup.getParameterSetupItems();
        List<LabParameterSetupItemDto> itemDtos = new ArrayList<>();
        if (!items.isEmpty()) {
            itemDtos = items.stream().map(this::mapParamSetupItemModelToDto).collect(Collectors.toList());
        }
        dto.setParameterSetupItems(itemDtos);
        return dto;
    }
}
