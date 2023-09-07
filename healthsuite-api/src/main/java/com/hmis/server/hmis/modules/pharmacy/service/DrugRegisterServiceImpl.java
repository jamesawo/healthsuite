package com.hmis.server.hmis.modules.pharmacy.service;

import com.hmis.server.hmis.common.common.dto.GenerateCodeDto;
import com.hmis.server.hmis.common.common.model.DrugClassification;
import com.hmis.server.hmis.common.common.model.DrugFormulation;
import com.hmis.server.hmis.common.common.service.CommonService;
import com.hmis.server.hmis.common.common.service.DrugClassificationServiceImpl;
import com.hmis.server.hmis.common.common.service.DrugFormulationServiceImpl;
import com.hmis.server.hmis.common.common.service.RevenueDepartmentServiceImpl;
import com.hmis.server.hmis.modules.pharmacy.dto.DrugColumnDto;
import com.hmis.server.hmis.modules.pharmacy.dto.DrugColumnEnum;
import com.hmis.server.hmis.modules.pharmacy.dto.DrugRegisterDto;
import com.hmis.server.hmis.modules.pharmacy.dto.OutletStockItemDto;
import com.hmis.server.hmis.modules.pharmacy.iservice.IDrugRegisterService;
import com.hmis.server.hmis.modules.pharmacy.model.DrugRegister;
import com.hmis.server.hmis.modules.pharmacy.repository.DrugRegisterRepository;

import java.util.*;
import java.util.stream.Collectors;

import com.hmis.server.hmis.modules.reports.dto.DailyCashCollectionSearchDto;
import com.hmis.server.hmis.modules.reports.dto.DailyCollectionFilterTypeEnum;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static com.hmis.server.hmis.common.constant.HmisCodeDefaults.DRUG_REGISTER_PREFIX_DEFAULT;
import static com.hmis.server.hmis.common.constant.HmisGlobalSettingKeys.DRUG_REGISTER_CODE_PREFIX;

@Service
public class DrugRegisterServiceImpl implements IDrugRegisterService {
    @Autowired
    private DrugRegisterRepository drugRegisterRepository;
    @Autowired
    private CommonService commonService;
    @Autowired
    private RevenueDepartmentServiceImpl revenueDepartmentService;
    @Autowired
    @Lazy
    private DrugClassificationServiceImpl drugClassificationService;
    @Autowired
    private DrugFormulationServiceImpl drugFormulationService;
    @Autowired
    @Lazy
    private OutletReconciliationServiceImpl outletReconciliationService;

    @Override
    public DrugRegisterDto registerDrug(DrugRegisterDto drugRegisterDto) {
        DrugRegister drugRegister = this.mapToModel(drugRegisterDto);
        drugRegister.setAutoGeneratedCode(this.generateDrugCode());
        boolean registerExist = this.isDrugRegisterExist(drugRegister);
        if (registerExist) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "SIMILAR DRUG ALREADY EXIST");
        }

        try {
            DrugRegister drug = this.drugRegisterRepository.save(drugRegister);
            drugRegisterDto.setId(drug.getId());
            return drugRegisterDto;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public Map<String, Integer> registerDrugsInBatchRaw(List<DrugRegister> list) {
        if (list.size() < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Drug List is Empty");
        }

        try {
            List<DrugRegister> duplicates = new ArrayList<>();
            for (DrugRegister drug : list) {
                if (this.isDrugRegisterExist(drug)) {
                    duplicates.add(drug);
                } else {
                    drug.setAutoGeneratedCode(this.generateDrugCode());
                    this.drugRegisterRepository.save(drug);
                }
            }
            duplicates.forEach(list::remove);

            Map<String, Integer> result = new HashMap<>();
            result.put("Uploaded", list.size());
            result.put("Duplicates", duplicates.size());
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isDrugRegisterExist(DrugRegister drugRegister) {
        // find by drug formulation, drug classification, generic name, brand name
        if (ObjectUtils.isEmpty(drugRegister.getFormulation()) || ObjectUtils.isEmpty(drugRegister.getClassification())
                || ObjectUtils.isEmpty(drugRegister.getGenericName()) || ObjectUtils
                        .isEmpty(drugRegister.getBrandName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Drug Register For IsExistCheck");
        }

        Optional<DrugRegister> optional = this.drugRegisterRepository
                .findByFormulationAndClassificationAndGenericNameIgnoreCaseAndBrandNameIgnoreCase(
                        drugRegister.getFormulation(), drugRegister.getClassification(), drugRegister.getGenericName(),
                        drugRegister.getBrandName());
        return optional.isPresent();

    }

    @Override
    public List<DrugRegisterDto> searchDrugByBrandOrGenericName(String term,
            boolean loadStockCount,
            Long outletId,
            boolean loadIssOutletStockCount,
            Long issuingOutletId) {
        List<DrugRegisterDto> dtoList = new ArrayList<>();
        List<DrugRegister> modelList = this.drugRegisterRepository
                .findByBrandNameIsContainingIgnoreCaseOrGenericNameIsContainingIgnoreCase(term, term);
        if (modelList.size() > 0) {
            List<DrugRegisterDto> list = new ArrayList<>();
            for (DrugRegister drugRegister : modelList) {
                DrugRegisterDto registerDto = mapToDto(drugRegister);
                if (loadStockCount) {
                    OutletStockItemDto outletItemStock = this.outletReconciliationService.getOutletItemStock(outletId,
                            drugRegister.getId());
                    registerDto.setAvailableQty(outletItemStock.getCurrentBalance());
                }
                if (loadIssOutletStockCount) {
                    OutletStockItemDto outletItemStock = this.outletReconciliationService
                            .getOutletItemStock(issuingOutletId, drugRegister.getId());
                    registerDto.setIssuingOutletBal(outletItemStock.getCurrentBalance());
                }
                list.add(registerDto);
            }
            dtoList = list;
        }
        return dtoList;
    }

    @Override
    public List<DrugRegisterDto> searchByGenericName(String term) {
        List<DrugRegisterDto> dtoList = new ArrayList<>();
        // todo:: apply pagination
        List<DrugRegister> list = this.drugRegisterRepository.findByGenericNameIsContainingIgnoreCase(term);
        if (list.size() > 0) {
            dtoList = list.stream().map(this::mapToDto).collect(Collectors.toList());
        }
        return dtoList;
    }

    @Override
    public List<DrugRegisterDto> searchByBrandName(String term) {
        List<DrugRegisterDto> dtoList = new ArrayList<>();
        List<DrugRegister> list = this.drugRegisterRepository.findByBrandNameIsContainingIgnoreCase(term);
        if (list.size() > 0) { // todo filter the return list to show only unique brand names without
                               // duplicates that match search term
            dtoList = list.stream().map(this::mapToDto).collect(Collectors.toList());
        }
        return dtoList;
    }

    @Override
    public List<DrugRegisterDto> searchDrugByBrandNameAndClassificationId(String term, Long drugClassificationId) {
        List<DrugRegisterDto> result = new ArrayList<>();
        List<DrugRegister> list = this.drugRegisterRepository
                .findByBrandNameIsContainingIgnoreCaseAndClassificationId(term, drugClassificationId);
        if (list.size() > 0) {
            result = list.stream().map(this::mapToDto).collect(Collectors.toList());
        }
        return result;
    }

    @Override
    public List<DrugRegisterDto> searchDrugByBrandNameAndFormulationId(String term, Long formulationId) {
        List<DrugRegisterDto> result = new ArrayList<>();
        List<DrugRegister> list = this.drugRegisterRepository
                .findByBrandNameIsContainingIgnoreCaseAndFormulationId(term, formulationId);
        if (list.size() > 0) {
            result = list.stream().map(this::mapToDto).collect(Collectors.toList());
        }
        return result;
    }

    @Override
    public DrugRegisterDto updateDrugRegister(DrugRegisterDto dto) {
        if (dto.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Drug Id is Required");
        }
        this.drugRegisterRepository.save(this.mapToModel(dto));
        return dto;
    }

    @Override
    public List<DrugRegisterDto> searchDrugAndFilter(String term, Long classificationId, Long formulationId) {
        List<DrugRegisterDto> dtoList = new ArrayList<>();
        List<DrugRegister> list = this.drugRegisterRepository
                .findByBrandNameIsContainingIgnoreCaseOrGenericNameIsContainingIgnoreCase(term, term);
        if (isValidLong(classificationId) && isValidLong(formulationId)) {
            List<DrugRegister> filteredList = this.filterDrugSearchResult(list, classificationId, formulationId);
            if (filteredList.size() > 0) {
                dtoList = filteredList.stream().map(this::mapToDto).collect(Collectors.toList());
            }
        } else if (isValidLong(classificationId) && !isValidLong(formulationId)) {
            dtoList = this.searchDrugByBrandNameAndClassificationId(term, classificationId);
        } else if (!isValidLong(classificationId) && isValidLong(formulationId)) {
            dtoList = this.searchDrugByBrandNameAndFormulationId(term, formulationId);
        } else if (ObjectUtils.isNotEmpty(term) && !isValidLong(classificationId) && !isValidLong(formulationId)) {
            if (list.size() > 0) {
                dtoList = list.stream().map(this::mapToDto).collect(Collectors.toList());
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Search Term, Classification, Formulation Is Required");
        }

        return dtoList;
    }

    @Override
    public DrugRegister findOne(Long id) {
        Optional<DrugRegister> optional = this.drugRegisterRepository.findById(id);
        if (!optional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return optional.get();
    }

    public List<DrugRegister> findAllRaw() {
        return this.drugRegisterRepository.findAll();
    }

    @Override
    @Transactional
    public ResponseEntity updateByColumnValue(DrugColumnDto dto) {
        try {
            DrugRegister drug = this.drugRegisterRepository.getOne(dto.getId());
            this.updateDrugColumn(drug, dto.getColumn(), dto.getValue());
            this.drugRegisterRepository.save(drug);
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public List<DrugRegisterDto> searchByOutletFilter(String search, Long outletId, boolean excludeZeroItems) {
        List<DrugRegisterDto> dtoList = new ArrayList<>();
        List<DrugRegister> list = this.drugRegisterRepository
                .findByBrandNameIsContainingIgnoreCaseOrGenericNameIsContainingIgnoreCase(search, search);
        if (!list.isEmpty()) {
            for (DrugRegister register : list) {
                OutletStockItemDto stockItemDto = this.outletReconciliationService.getOutletItemStock(outletId,
                        register.getId());
                boolean quantityZero = isQuantityZero(stockItemDto.getCurrentBalance());
                if (excludeZeroItems && quantityZero) {
                    continue;
                }
                DrugRegisterDto registerDto = mapToDto(register);
                registerDto.setAvailableQty(stockItemDto.getCurrentBalance());
                dtoList.add(registerDto);
            }
        }
        return dtoList;
    }

    public DrugRegisterDto mapToDto(DrugRegister drugRegister) {
        DrugRegisterDto registerDto = new DrugRegisterDto();

        if (drugRegister.getId() != null) {
            registerDto.setId(drugRegister.getId());
        }

        if (drugRegister.getRevenueDepartment() != null) {
            registerDto.setRevenueDepartmentId(drugRegister.getRevenueDepartment().getId());
        }

        if (drugRegister.getBrandName() != null) {
            registerDto.setBrandName(drugRegister.getBrandName());
        }

        if (drugRegister.getFormulation() != null) {
            registerDto.setFormulationId(drugRegister.getFormulation().getId());
            registerDto.setFormulation(this.drugFormulationService.mapModelToDto(drugRegister.getFormulation()));
        }

        if (drugRegister.getClassification() != null) {
            registerDto.setClassificationId(drugRegister.getClassification().getId());
            registerDto
                    .setClassification(this.drugClassificationService.mapModelToDto(drugRegister.getClassification()));
        }

        if (drugRegister.getGenericName() != null) {
            registerDto.setGenericName(drugRegister.getGenericName());
        }

        if (drugRegister.getStrength() != null) {
            registerDto.setStrength(drugRegister.getStrength());
        }

        if (drugRegister.getUnitPerPack() != null) {
            registerDto.setUnitPerPack(drugRegister.getUnitPerPack());
        }

        if (drugRegister.getUnitOfIssue() != null) {
            registerDto.setUnitOfIssue(drugRegister.getUnitOfIssue());
        }

        if (drugRegister.getPacksPerPackingUnit() != null) {
            registerDto.setPacksPerPackingUnit(drugRegister.getPacksPerPackingUnit());
        }

        if (drugRegister.getCostPrice() != null) {
            registerDto.setCostPrice(drugRegister.getCostPrice());
        }

        if (drugRegister.getUnitCostPrice() != null) {
            registerDto.setUnitCostPrice(drugRegister.getUnitCostPrice());
        }

        if (drugRegister.getNhisMarkup() != null) {
            registerDto.setNhisMarkUp(drugRegister.getNhisMarkup());
        }

        if (drugRegister.getGeneralMarkUp() != null) {
            registerDto.setGeneralMarkUp(drugRegister.getGeneralMarkUp());
        }

        if (drugRegister.getRegularSellingPrice() != null) {
            registerDto.setRegularSellingPrice(drugRegister.getRegularSellingPrice());
        }

        if (drugRegister.getNhisSellingPrice() != null) {
            registerDto.setNhisSellingPrice(drugRegister.getNhisSellingPrice());
        }

        if (drugRegister.getDiscountPercent() != null) {
            registerDto.setDiscountPercent(drugRegister.getDiscountPercent());
        }

        registerDto.setSearchTitle(drugRegister.fullBrandName());
        registerDto.setDescription(drugRegister.fullBrandName());
        registerDto.setCode(drugRegister.getAutoGeneratedCode());
        return registerDto;
    }

    public DrugRegister mapToModel(DrugRegisterDto dto) {
        DrugRegister drugRegister = new DrugRegister();

        if (dto.getId() != null) {
            drugRegister.setId(dto.getId());
        }

        if (dto.getRevenueDepartmentId() != null) {
            drugRegister.setRevenueDepartment(this.revenueDepartmentService.findById(dto.getRevenueDepartmentId()));
        }

        if (dto.getFormulationId() != null) {
            drugRegister.setFormulation(this.drugFormulationService.findOne(dto.getFormulationId()));
        }

        if (dto.getClassificationId() != null) {
            drugRegister.setClassification(this.drugClassificationService.findOne(dto.getClassificationId()));
        }

        if (dto.getGenericName() != null) {
            drugRegister.setGenericName(dto.getGenericName());
        }

        if (dto.getBrandName() != null) {
            drugRegister.setBrandName(dto.getBrandName());
        }

        if (dto.getStrength() != null) {
            drugRegister.setStrength(dto.getStrength());
        }

        if (dto.getUnitPerPack() != null) {
            drugRegister.setUnitPerPack(dto.getUnitPerPack());
        }

        if (dto.getUnitOfIssue() != null) {
            drugRegister.setUnitOfIssue(dto.getUnitOfIssue());
        }

        if (dto.getPacksPerPackingUnit() != null) {
            drugRegister.setPacksPerPackingUnit(dto.getPacksPerPackingUnit());
        }

        if (dto.getCostPrice() != null) {
            drugRegister.setCostPrice(dto.getCostPrice());
        }

        if (dto.getUnitCostPrice() != null) {
            drugRegister.setUnitCostPrice(dto.getUnitCostPrice());
        }

        if (dto.getNhisMarkUp() != null) {
            drugRegister.setNhisMarkup(dto.getNhisMarkUp());
        }

        if (dto.getGeneralMarkUp() != null) {
            drugRegister.setGeneralMarkUp(dto.getGeneralMarkUp());
        }

        if (dto.getRegularSellingPrice() != null) {
            drugRegister.setRegularSellingPrice(dto.getRegularSellingPrice());
        }

        if (dto.getNhisSellingPrice() != null) {
            drugRegister.setNhisSellingPrice(dto.getNhisSellingPrice());
        }

        if (dto.getDiscountPercent() != null) {
            drugRegister.setDiscountPercent(dto.getDiscountPercent());
        }

        if (ObjectUtils.isNotEmpty(dto.getReorderLevel()) && dto.getReorderLevel() > 0) {
            drugRegister.setReorderLevel(dto.getReorderLevel());
        }

        return drugRegister;
    }

    public List<DrugRegister> findAllForDailyCashCollectionReport(DailyCashCollectionSearchDto dto) {
        List<DrugRegister> drugList = new ArrayList<>();
        if (dto.getType().equals(DailyCollectionFilterTypeEnum.DRUG)) {
            if (dto.getDrug() != null && dto.getDrug().getId() != null) {
                drugList.add(this.findOne(dto.getDrug().getId()));
            } else {
                drugList = this.findAllRaw();
            }
        }
        return drugList;
    }

    private boolean isQuantityZero(double qty) {
        return qty == 0;
    }

    private boolean isValidLong(Long value) {
        return value != null && value > 0;
    }

    private List<DrugRegister> filterDrugSearchResult(List<DrugRegister> drugList, Long classificationId,
            Long formulationId) {
        List<DrugRegister> filteredResult = new ArrayList<>();
        if (drugList.size() > 0) {
            for (DrugRegister drug : drugList) {
                if (drug.getClassification().getId().equals(classificationId)
                        && drug.getFormulation().getId().equals(formulationId)) {
                    filteredResult.add(drug);
                }
            }
        }
        return filteredResult;
    }

    private void updateDrugColumn(DrugRegister drug, DrugColumnEnum column, String value) {
        switch (column) {
            case BRAND:
                drug.setBrandName(value);
                break;
            case COST_PRICE:
                drug.setCostPrice(Double.valueOf(value));
                break;
            case GENERIC:
                drug.setGenericName(value);
                break;
            case STRENGTH:
                drug.setStrength(value);
                break;
            case UNIT_OF_ISSUE:
                drug.setUnitOfIssue(Integer.parseInt(value));
                break;
            case GENERAL_MARK_UP:
                drug.setGeneralMarkUp(Integer.parseInt(value));
                break;
            case UNIT_COST_PRICE:
                drug.setUnitCostPrice(Double.valueOf(value));
                break;
            case NHIS_SELLING_PRICE:
                drug.setNhisSellingPrice(Double.valueOf(value));
                break;
            case REGULAR_SELLING_PRICE:
                drug.setRegularSellingPrice(Double.valueOf(value));
                break;
            case PACKS_PER_PACKING_UNIT:
                drug.setPacksPerPackingUnit(Integer.parseInt(value));
                break;
            case CLASSIFICATION:
                drug.setClassification(new DrugClassification(Long.valueOf(value)));
                break;
            case FORMULATION:
                drug.setFormulation(new DrugFormulation(Long.valueOf(value)));
                break;
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

    }

    private String generateDrugCode() {
        GenerateCodeDto generateCodeDto = new GenerateCodeDto();
        generateCodeDto.setDefaultPrefix(DRUG_REGISTER_PREFIX_DEFAULT);
        generateCodeDto.setGlobalSettingKey(Optional.of(DRUG_REGISTER_CODE_PREFIX));
        generateCodeDto.setLastGeneratedCode(
                this.drugRegisterRepository.findTopByOrderByIdDesc().map(DrugRegister::getAutoGeneratedCode));
        return commonService.generateDataCode(generateCodeDto);
    }

}
