package com.hmis.server.hmis.common.common.Iservice;

import com.hmis.server.hmis.common.common.dto.ProductServiceDto;
import com.hmis.server.hmis.common.common.dto.ResponseDto;
import com.hmis.server.hmis.common.common.model.DepartmentCategory;
import com.hmis.server.hmis.common.common.model.ProductService;
import com.hmis.server.hmis.modules.billing.dto.BillViewTypeEnum;
import com.hmis.server.hmis.modules.others.dto.SearchFilterEnum;
import com.hmis.server.hmis.modules.others.dto.ServiceColumnDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IProductService {

    ProductServiceDto findOne(Long id);

    Optional< ProductService > findOneRaw(Long id);

    List<ProductServiceDto> findAll();

    List<ProductService> findAllRaw();

    ProductServiceDto createOne(ProductServiceDto serviceDto);

    void updateOne(ProductServiceDto serviceDto);

    void deactivateOne(ProductServiceDto serviceDto);

    void deactivateInBatch(List<ProductServiceDto> serviceDtoList);

    List<ProductServiceDto> createInBatch(List<ProductServiceDto> serviceDtoList);

    List<ProductServiceDto> searchProductService(String search);

    List<ProductServiceDto> searchProductServiceByDepartmentCategory(String search, DepartmentCategory category);

    List<ProductServiceDto> searchSearchHandler(String search, String billType);

    DepartmentCategory getDepartmentCategoryForBillType(BillViewTypeEnum viewTypeEnum);

    List<ProductServiceDto> findAllByRevenueDepartment(Long revenueDepartmentId);

    List<ProductServiceDto> findAllByServiceDepartment(Long serviceDepartmentId);

    List<ProductServiceDto> findAllByServiceName(String serviceName);

    List<ProductServiceDto> findByServiceId(Long id);

    List<ProductServiceDto> searchByFilterType(SearchFilterEnum searchBy, Long valueId);

    ResponseDto updateServiceByColumnType(ServiceColumnDto dto);

    Map<String, Integer> createMany(List<ProductService> list);
}
