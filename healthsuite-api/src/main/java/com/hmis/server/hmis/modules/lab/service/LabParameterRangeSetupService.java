package com.hmis.server.hmis.modules.lab.service;

import com.hmis.server.hmis.common.common.dto.MessageDto;
import com.hmis.server.hmis.common.common.model.ProductService;
import com.hmis.server.hmis.common.common.service.DepartmentServiceImpl;
import com.hmis.server.hmis.common.common.service.ProductServiceImpl;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.common.user.service.UserServiceImpl;
import com.hmis.server.hmis.modules.lab.dto.LabParameterDto;
import com.hmis.server.hmis.modules.lab.dto.LabParameterRangeSetupDto;
import com.hmis.server.hmis.modules.lab.dto.LabParameterRangeSetupItemDto;
import com.hmis.server.hmis.modules.lab.dto.LabParameterSetupItemDto;
import com.hmis.server.hmis.modules.lab.model.LabParameterRangeSetup;
import com.hmis.server.hmis.modules.lab.model.LabParameterRangeSetupItem;
import com.hmis.server.hmis.modules.lab.model.LabParameterSetupItem;
import com.hmis.server.hmis.modules.lab.repository.LabParameterRangeSetupItemRepository;
import com.hmis.server.hmis.modules.lab.repository.LabParameterRangeSetupRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Lazy;
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
public class LabParameterRangeSetupService {
    private final LabParameterRangeSetupItemRepository rangeSetupItemRepository;
    private final LabParameterRangeSetupRepository rangeSetupRepository;
    private final ProductServiceImpl productService;
    private final UserServiceImpl userService;
    private final DepartmentServiceImpl departmentService;

    public LabParameterRangeSetupService(
            LabParameterRangeSetupItemRepository rangeSetupItemRepository,
            LabParameterRangeSetupRepository rangeSetupRepository,
            @Lazy ProductServiceImpl productService,
            @Lazy UserServiceImpl userService,
            @Lazy DepartmentServiceImpl departmentService) {
        this.rangeSetupItemRepository = rangeSetupItemRepository;
        this.rangeSetupRepository = rangeSetupRepository;
        this.productService = productService;
        this.userService = userService;
        this.departmentService = departmentService;
    }

    public ResponseEntity<MessageDto> saveParameterRangeSetup(LabParameterRangeSetupDto dto) {
        try {
            LabParameterRangeSetup setup = this.mapRangeSetupDtoToModel(dto);
            LabParameterRangeSetup range = this.findRangeByTestAndLabParamSetup(setup.getTest(), setup.getLabParameterSetupItem());
            if (range != null) {
                setup.setId(range.getId());
            }
            LabParameterRangeSetup rangeSetup = this.rangeSetupRepository.save(setup);
            this.saveParameterRangeSetupItems(rangeSetup, dto.getRangeItems());
            return ResponseEntity.ok().body(new MessageDto(HmisConstant.SUCCESS_MESSAGE));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public void saveParameterRangeSetupItems(LabParameterRangeSetup rangeSetup,
                                             List<LabParameterRangeSetupItemDto> items) {
        if (items != null && !items.isEmpty()) {
            for (LabParameterRangeSetupItemDto item : items) {
                LabParameterRangeSetupItem model = this.mapRangeSetupItemDtoToModel(rangeSetup, item);
                Optional<LabParameterRangeSetupItem> optional = this.rangeSetupItemRepository.findByNameIgnoreCaseAndRangeSetup(model.getName(), model.getRangeSetup());
                optional.ifPresent(rangeSetupItem -> model.setId(rangeSetupItem.getId()));
                this.rangeSetupItemRepository.save(model);
            }
        }
    }


    public LabParameterRangeSetup findRangeByTestAndLabParamSetup(ProductService test, LabParameterSetupItem labParameterSetupItem) {
        Optional<LabParameterRangeSetup> optional = this.rangeSetupRepository.findByTestAndLabParameterSetupItem(test, labParameterSetupItem);
        return optional.orElse(null);
    }

    public LabParameterRangeSetupDto searchRangeByTestAndParameter(Long testId, Long parameterId) {
        LabParameterRangeSetup range = this.findRangeByTestAndLabParamSetup(new ProductService(testId), new LabParameterSetupItem(parameterId));
        if (range != null) {
            return this.mapRangeSetupToDto(range);
        }
        return new LabParameterRangeSetupDto();
    }

    private LabParameterRangeSetupItem mapRangeSetupItemDtoToModel(
            LabParameterRangeSetup rangeSetup,
            LabParameterRangeSetupItemDto dto) {
        LabParameterRangeSetupItem model = new LabParameterRangeSetupItem();

        if (ObjectUtils.isNotEmpty(dto.getId())) {
            model.setId(dto.getId());
        }
        if (ObjectUtils.isNotEmpty(dto.getName())) {
            model.setName(dto.getName());
        }
        if (ObjectUtils.isNotEmpty(dto.getUnit())) {
            model.setUnit(dto.getUnit());
        }
        if (ObjectUtils.isNotEmpty(dto.getLowerLimit())) {
            model.setLowerLimit(dto.getLowerLimit());
        }

        if (ObjectUtils.isNotEmpty(dto.getUpperLimit())) {
            model.setUpperLimit(dto.getUpperLimit());
        }
        model.setRangeSetup(rangeSetup);
        return model;
    }

    private LabParameterRangeSetup mapRangeSetupDtoToModel(LabParameterRangeSetupDto dto) {
        LabParameterRangeSetup model = new LabParameterRangeSetup();
        if (dto.getId() != null) {
            model.setId(dto.getId());
        }
        if (dto.getTest() != null && dto.getTest().getId() != null) {
            model.setTest(this.productService.findOneProductService(dto.getTest().getId()));
        }
        if (dto.getLabParameterSetupItem() != null && dto.getLabParameterSetupItem().getId() != null) {
            model.setLabParameterSetupItem(new LabParameterSetupItem(dto.getLabParameterSetupItem().getId()));
        }

        if (dto.getCapturedBy() != null && dto.getCapturedBy().getId().isPresent()) {
            model.setCapturedBy(this.userService.findOneRaw(dto.getCapturedBy().getId().get()));
        }

        if (dto.getCapturedFrom() != null && dto.getCapturedFrom().getId().isPresent()) {
            model.setCapturedFrom(this.departmentService.findOneRaw(dto.getCapturedFrom().getId()));
        }
        return model;
    }

    private LabParameterRangeSetupDto mapRangeSetupToDto(LabParameterRangeSetup model) {
        LabParameterRangeSetupDto dto = new LabParameterRangeSetupDto();
        if (model.getId() != null) dto.setId(model.getId());
        if (model.getTest() != null) dto.setTest(model.getTest().mapToDto());
        LabParameterSetupItem parameterSetupItem = model.getLabParameterSetupItem();
        if (parameterSetupItem != null) {
            dto.setLabParameterSetupItem(
                    new LabParameterSetupItemDto(
                            parameterSetupItem.getId(),
                            new LabParameterDto(
                                    parameterSetupItem.getParameter().getId(),
                                    parameterSetupItem.getParameter().getTitle()
                            ),
                            parameterSetupItem.getParameterHierarchy()
                    )
            );
        }
        List<LabParameterRangeSetupItemDto> rangeItems = new ArrayList<>();
        if (!model.getLabParameterRangeSetupItems().isEmpty()) {
            rangeItems = model.getLabParameterRangeSetupItems().stream().map(this::mapRangeSetupItemToDto).collect(Collectors.toList());
            dto.setRangeItems(rangeItems);
        }
        return dto;
    }

    private LabParameterRangeSetupItemDto mapRangeSetupItemToDto(LabParameterRangeSetupItem model) {
        LabParameterRangeSetupItemDto dto = new LabParameterRangeSetupItemDto();
        if (model.getId() != null) dto.setId(model.getId());
        if (model.getName() != null) dto.setName(model.getName());
        if (model.getUnit() != null) dto.setUnit(model.getUnit());
        if (model.getLowerLimit() != null) dto.setLowerLimit(model.getLowerLimit());
        if (model.getUpperLimit() != null) dto.setUpperLimit(model.getUpperLimit());
        return dto;
    }


}
