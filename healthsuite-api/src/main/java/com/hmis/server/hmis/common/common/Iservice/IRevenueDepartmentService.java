package com.hmis.server.hmis.common.common.Iservice;

import com.hmis.server.hmis.common.common.dto.RevenueDepartmentDto;
import com.hmis.server.hmis.common.common.model.RevenueDepartment;
import java.util.List;

import com.hmis.server.hmis.modules.reports.dto.DailyCashCollectionSearchDto;
import org.springframework.web.multipart.MultipartFile;

public interface IRevenueDepartmentService {
    List<RevenueDepartment> findRevenueForDailyCollection(DailyCashCollectionSearchDto dto);

    RevenueDepartmentDto createOne(RevenueDepartmentDto revenueDepartmentDto);

	List< RevenueDepartmentDto > createInBatch(List< RevenueDepartmentDto > revenueDepartmentDtoList);

    List<RevenueDepartment> findAllRaw();

    List< RevenueDepartmentDto > findAll();

	List< RevenueDepartmentDto > findByRevenueDepartmentType(RevenueDepartmentDto revenueDepartmentDto);

	List< RevenueDepartmentDto > findByNameLike(RevenueDepartmentDto revenueDepartmentDto);

	RevenueDepartmentDto findByName(RevenueDepartmentDto revenueDepartmentDto);

	RevenueDepartmentDto findOne(RevenueDepartmentDto revenueDepartmentDto);

	RevenueDepartment findByCode(String code);

	List< RevenueDepartmentDto > updateInBatch(List< RevenueDepartmentDto > revenueDepartmentDtoList);

	RevenueDepartmentDto updateOne(RevenueDepartmentDto revenueDepartmentDto);

	void deactivateOne(RevenueDepartmentDto revenueDepartmentDto);

	void deactivateInBatch(List< RevenueDepartmentDto > revenueDepartmentDtoList);

	void activateOne(RevenueDepartmentDto revenueDepartmentDto);

	void activateInBatch(List< RevenueDepartmentDto > revenueDepartmentDtoList);

	boolean isRevenueDepartmentExist(RevenueDepartmentDto revenueDepartmentDto);

	List< RevenueDepartmentDto > createInBatchFromExcel(MultipartFile file);

	List< RevenueDepartmentDto > searchRevenueDepartment(String nameOrCode);

	RevenueDepartmentDto findDepositRevenueDepartment();

	RevenueDepartment findOneRaw(Long id);

	RevenueDepartment findById(Long revenueDepartmentId);

    boolean isDepositRevenueDepartment(RevenueDepartment department);
}
