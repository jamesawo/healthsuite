package com.hmis.server.hmis.common.common.Iservice;

import com.hmis.server.hmis.common.common.dto.DepartmentCategoryEnum;
import com.hmis.server.hmis.common.common.dto.DepartmentDto;
import com.hmis.server.hmis.common.common.model.Department;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;

public interface IDepartmentService {
    DepartmentDto createOne(DepartmentDto departmentDto);

    List<DepartmentDto> createInBatch(List<DepartmentDto> departmentDtoList);

    Map<String, List<Object>> createInBatchFromExcel(MultipartFile file) throws IOException;

    List<DepartmentDto> findAllDepartment();

    DepartmentDto updateOne(DepartmentDto departmentDto);

    Department findByCode(String code);

    boolean isDepartmentExist(DepartmentDto departmentDto);

    Department findOneRaw(Optional<Long> id);

	Department findOne(Long id);

	List< Long > findMatchingClinicId(String name);

	List< Department > findDepartmentByDepartmentCategory(String categoryName);

    List<DepartmentDto> searchServiceDepartment(String search);

    List<DepartmentDto> searchServiceDepartmentByCategory(String searchTerm, DepartmentCategoryEnum categoryEnum);

    List<DepartmentDto> findAllWards();

	boolean isValidLocationFor(Long locationId, DepartmentCategoryEnum categoriesEnum);

	boolean isOutletStore(Department receivingDepartment);

	boolean isPharmacyLocation(Department department);
}
