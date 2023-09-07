package com.hmis.server.hmis.common.common.service;

import com.hmis.server.hmis.common.common.Iservice.IDepartmentService;
import com.hmis.server.hmis.common.common.dto.DepartmentCategoryDto;
import com.hmis.server.hmis.common.common.dto.DepartmentCategoryEnum;
import com.hmis.server.hmis.common.common.dto.DepartmentDto;
import com.hmis.server.hmis.common.common.dto.GenerateCodeDto;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.model.DepartmentCategory;
import com.hmis.server.hmis.common.common.repository.DepartmentRepository;
import com.hmis.server.hmis.common.constant.HmisCodeDefaults;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.common.constant.HmisGlobalSettingKeys;
import com.hmis.server.hmis.common.exception.HmisApplicationException;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.hmis.server.hmis.modules.reports.dto.DailyCashCollectionSearchDto;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DepartmentServiceImpl implements IDepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DepartmentCategoryServiceImpl departmentCategoryService;

    @Autowired
    private GlobalSettingsImpl globalSettingsService;

    @Autowired
    private CommonService commonService;

    @Override
    public DepartmentDto createOne(DepartmentDto departmentDto) {
        if (!departmentDto.getId().isPresent()) {
            departmentDto.setCode(Optional.of(generateDepartmentCode()));
        }
        return mapModelToDto(departmentRepository.save(mapDtoToModel(departmentDto)));
    }

    @Override
    public List<DepartmentDto> createInBatch(List<DepartmentDto> departmentDtoList) {
        return null;
    }

    /*
     * TODO:: check duplicate entry
     * ----> check if entity exist
     * -----> check depCategory is valid
     * ----> save
     */
    @Override
    public Map<String, List<Object>> createInBatchFromExcel(MultipartFile files) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(files.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);

        List<Department> departmentList = new ArrayList<>();

        List<DepartmentDto> errorDepartmentListDto = new ArrayList<>();
        Map<String, List<Object>> resultMap = new HashMap<>();

        for (int index = 0; index < worksheet.getPhysicalNumberOfRows(); index++) {
            if (index > 0) {
                Department department = new Department();
                XSSFRow row = worksheet.getRow(index);
                String departmentName = row.getCell(0) != null ? row.getCell(0).getStringCellValue() : "";
                String departmentCategory = row.getCell(1) != null ? row.getCell(1).getStringCellValue() : "";

                DepartmentDto departmentDto = new DepartmentDto(Optional.of(departmentName));

                // todo:: add a reason while the data import failed before returning to client
                if (isDepartmentExist(departmentDto) || departmentCategory.isEmpty()) {
                    // todo:: if department exist add that as a reason
                    // todo:: if category is empty or invalid add that as a reason
                    errorDepartmentListDto.add(departmentDto);
                } else {
                    department.setName(departmentName);
                    department.setCode(generateDepartmentCode());

                    Optional<DepartmentCategory> categoryByName = departmentCategoryService
                            .findByName(new DepartmentCategoryDto(Optional.of(departmentCategory)));
                    categoryByName.ifPresent(department::setDepartmentCategory);

                    departmentList.add(department);
                    departmentRepository.save(department);
                }
            }
        }
        // Todo:: Add count of successUpload and failedUpload to map
        resultMap.put("successCount", Collections.singletonList(departmentList.size()));
        resultMap.put("failedCount", Collections.singletonList(errorDepartmentListDto.size()));
        resultMap.put("successfullyUpload", Collections.singletonList(mapModelListToDtoList(departmentList)));
        resultMap.put("failedUpload", Collections.singletonList(errorDepartmentListDto));
        return resultMap;
    }

    @Override
    public List<DepartmentDto> findAllDepartment() {
        List<Department> departments = departmentRepository.findAll().stream()
                .filter(e -> e.getIsVisible().equals(true)).sorted(Comparator.comparing(Department::getId))
                .collect(Collectors.toList());
        if (!departments.isEmpty()) {
            return mapModelListToDtoList(departments);
        } else {
            return new ArrayList<>();
        }

    }

    public Map<String, Object> mapDepartmentWithDepartmentCategories() {
        Map<String, Object> map = new HashMap<>();
        map.put("departmentList", findAllDepartment());
        map.put("departmentCategoryList", departmentCategoryService.findAll());
        return map;
    }

    public List<Department> findAllDepartmentRaw() {
        return departmentRepository.findAll();
    }

    @Override
    public DepartmentDto updateOne(DepartmentDto departmentDto) {
        if (departmentDto.getId().isPresent()) {
            Optional<Department> department = departmentRepository.findById(departmentDto.getId().get());
            if (department.isPresent()) {
                return mapModelToDto(
                        departmentRepository.save(setModelFromDtoToUpdate(department.get(), departmentDto)));
            } else {
                throw new HmisApplicationException("Cannot Find Department.");
            }

        } else {
            throw new HmisApplicationException("Provide Department ID");
        }
    }

    @Override
    public Department findByCode(String code) {
        try {
            return this.departmentRepository.findByCode(code);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    public Department findByName(String name) {
        try {
            return this.departmentRepository.findByName(name).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find department with name"));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Override
    public boolean isDepartmentExist(DepartmentDto departmentDto) {
        if (departmentDto.getName().isPresent()) {
            return departmentRepository.findAll().stream().anyMatch(
                    department -> department.getName().compareToIgnoreCase(departmentDto.getName().get()) == 0);
        } else {
            throw new HmisApplicationException("Provide Department Name Before Checking If It Exist.");
        }
    }

    @Override
    public Department findOneRaw(Optional<Long> id) {
        if (id.isPresent()) {
            return departmentRepository.getOne(id.get());
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No department found Id: " + id.get());
    }

    @Override
    public Department findOne(Long id) {
        Optional<Department> optional = this.departmentRepository.findById(id);
        if (!optional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid Department ID");
        }
        return optional.get();
    }

    public Department findOneFromDto(DepartmentDto departmentDto) {
        if (departmentDto == null || !departmentDto.getId().isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Department/Location Id Not Present");
        }
        return this.findOne(departmentDto.getId().get());
    }

    public Optional<Department> findOneOptional(Long id) {
        return this.departmentRepository.findById(id);
    }

    @Override
    public List<Long> findMatchingClinicId(String name) { // list of long because there might be more than one clinic in
                                                          // a department (big hospitals)
        List<Long> idList = new ArrayList<>(); // here
        DepartmentCategory category = this.departmentCategoryService.findByName(DepartmentCategoryEnum.Clinic.name());
        String[] split = StringUtils.split(name);
        Set<Department> clinicList = new HashSet<>();

        for (int i = 0; i < split.length; i++) {
            List<Department> contains = this.departmentRepository
                    .findAllByNameContainsIgnoreCaseAndDepartmentCategory(split[i], category);
            if (!contains.isEmpty()) {
                clinicList.addAll(contains);
            }
        }
        if (clinicList.size() > 0) {
            idList = clinicList.stream().map(this::getIdFromDepartment).collect(Collectors.toList());
        }
        return idList;
    }

    @Override
    public List<Department> findDepartmentByDepartmentCategory(String categoryName) {
        DepartmentCategory category = this.departmentCategoryService.findByName(categoryName);
        return category.getDepartments();
    }

    @Override
    public List<DepartmentDto> searchServiceDepartment(String search) {
        List<DepartmentDto> departmentDtoList = new ArrayList<>();
        List<Department> departmentList = this.departmentRepository
                .findAllByNameContainsIgnoreCaseOrCodeContainsIgnoreCase(search, search);
        if (departmentList.size() > 0) {
            departmentDtoList = departmentList.stream().map(this::mapModelToDto).collect(Collectors.toList());
        }
        return departmentDtoList;
    }

    @Override
    public List<DepartmentDto> searchServiceDepartmentByCategory(String searchTerm,
            DepartmentCategoryEnum categoryEnum) {
        List<DepartmentDto> departmentDtoList = new ArrayList<>();
        DepartmentCategory category = this.departmentCategoryService.findByName(categoryEnum.name());
        List<Department> list = this.departmentRepository
                .findAllByNameContainsIgnoreCaseAndDepartmentCategory_Id(searchTerm, category.getId());
        if (!list.isEmpty()) {
            departmentDtoList = list.stream().map(this::mapModelToDto).collect(Collectors.toList());
        }
        return departmentDtoList;
    }

    @Override
    public List<DepartmentDto> findAllWards() {
        String categoryName = HmisConstant.WARD_DEP_CATEGORY_NAME;
        List<DepartmentDto> dtoList = new ArrayList<>();
        List<Department> wardsList = this.findDepartmentByDepartmentCategory(categoryName);
        if (wardsList.size() > 0) {
            dtoList = wardsList.stream().map(this::mapModelToDto).collect(Collectors.toList());
        }
        return dtoList;
    }

    @Override
    public boolean isValidLocationFor(Long locationId, DepartmentCategoryEnum categoriesEnum) {
        Department department = this.findOne(locationId);
        return department.getDepartmentCategory().getName().toLowerCase().contains(categoriesEnum.name().toLowerCase());
    }

    @Override
    public boolean isOutletStore(Department department) {
        boolean store = department.getName().toLowerCase().contains("store");
        return this.isPharmacyLocation(department) && store;
    }

    @Override
    public boolean isPharmacyLocation(Department department) {
        return department.getDepartmentCategory().getName().toLowerCase()
                .contains(DepartmentCategoryEnum.Pharmacy.name().toLowerCase());
    }

    public Department mapDtoToModel(DepartmentDto departmentDto) {
        Department department = new Department();
        if (departmentDto.getId().isPresent()) {
            department.setId(departmentDto.getId().get());
        }
        if (departmentDto.getName().isPresent()) {
            department.setName(departmentDto.getName().get());
        }
        if (departmentDto.getCode().isPresent()) {
            department.setCode(departmentDto.getCode().get());
        }
        if (departmentDto.getDescription().isPresent()) {
            department.setDescription(departmentDto.getDescription().get());
        }
        if (departmentDto.getDepartmentCategory().isPresent()) {
            if (departmentDto.getDepartmentCategory().get().getId().isPresent()) {
                department.setDepartmentCategory(departmentCategoryService
                        .findOneDepartmentCategory(departmentDto.getDepartmentCategory().get()));
            }
        }
        return department;
    }

    public DepartmentDto mapModelToDto(Department department) {
        DepartmentDto departmentDto = new DepartmentDto();
        if (department.getId() != null) {
            departmentDto.setId(Optional.ofNullable(department.getId()));
        }
        if (department.getName() != null) {
            departmentDto.setName(Optional.ofNullable(department.getName()));
        }
        if (department.getDepartmentCategory() != null) {
            departmentDto.setDepartmentCategory(
                    Optional.of(departmentCategoryService.mapModelToDto(department.getDepartmentCategory())));
        }
        if (department.getCode() != null) {
            departmentDto.setCode(Optional.of(department.getCode()));
        }

        return departmentDto;
    }

    private Long getIdFromDepartment(Department department) {
        return department.getId();
    }

    private Department setModelFromDtoToUpdate(Department department, DepartmentDto departmentDto) {
        // if (departmentDto.getId().isPresent())
        // department.setId(departmentDto.getId().get());

        if (departmentDto.getName().isPresent() && !departmentDto.getName().get().isEmpty()) {
            department.setName(departmentDto.getName().get());
        }

        if (departmentDto.getCode().isPresent() && !departmentDto.getCode().get().isEmpty()) {
            department.setCode(departmentDto.getCode().get());
        }

        if (departmentDto.getDescription().isPresent() && !departmentDto.getDescription().get().isEmpty()) {
            department.setDescription(departmentDto.getDescription().get());
        }

        if (departmentDto.getDepartmentCategory().isPresent()) {
            if (departmentDto.getDepartmentCategory().get().getId().isPresent()) {
                department.setDepartmentCategory(departmentCategoryService
                        .findOneDepartmentCategory(departmentDto.getDepartmentCategory().get()));
            }
        }
        return department;
    }

    private List<Department> mapDtoListToModelList(List<DepartmentDto> departmentDtoList)
            throws HmisApplicationException {
        if (!departmentDtoList.isEmpty()) {
            List<Department> departmentList = new ArrayList<>();
            for (DepartmentDto departmentDto : departmentDtoList) {
                Department department = new Department();
                if (departmentDto.getId().isPresent()) {
                    department.setId(departmentDto.getId().get());
                }
                if (departmentDto.getName().isPresent()) {
                    department.setName(departmentDto.getName().get());
                }
                if (departmentDto.getCode().isPresent()) {
                    department.setCode(departmentDto.getCode().get());
                }
                departmentList.add(department);
            }
            return departmentList;
        } else {
            throw new HmisApplicationException("Department List is Empty");
        }
    }

    private List<DepartmentDto> mapModelListToDtoList(List<Department> departmentList) {
        List<DepartmentDto> departmentDtoList = new ArrayList<>();
        if (!departmentList.isEmpty()) {
            for (Department department : departmentList) {
                DepartmentDto departmentDto = new DepartmentDto();
                departmentDto.setId(Optional.of(department.getId()));
                departmentDto.setName(Optional.of(department.getName()));
                departmentDto.setCode(Optional.of(department.getCode()));
                departmentDto.setDepartmentCategory(
                        Optional.of(departmentCategoryService.mapModelToDto(department.getDepartmentCategory())));
                departmentDtoList.add(departmentDto);
            }
        }
        return departmentDtoList;
    }

    private String generateDepartmentCode() {
        GenerateCodeDto codeDto = new GenerateCodeDto();
        codeDto.setGlobalSettingKey(Optional.of(HmisGlobalSettingKeys.DEPARTMENT_CODE_PREFIX));
        codeDto.setDefaultPrefix(HmisCodeDefaults.DEPARTMENT_CODE_DEFAULT_PREFIX);
        codeDto.setLastGeneratedCode(departmentRepository.findTopByOrderByIdDesc().map(Department::getCode));
        return commonService.generateDataCode(codeDto);
    }

    public List<Department> getDepartmentForDailyCollectionReport(DailyCashCollectionSearchDto dto) {
        List<Department> list = new ArrayList<>();
        if (dto.getServiceDepartment() != null && dto.getServiceDepartment().getId().isPresent()) {
            list.add(this.findOne(dto.getServiceDepartment().getId().get()));
        } else {
            list = this.findAllDepartmentRaw();
        }
        return list;
    }

}
