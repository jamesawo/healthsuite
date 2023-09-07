package com.hmis.server.hmis.common.common.service;

import com.hmis.server.hmis.common.common.Iservice.IProductService;
import com.hmis.server.hmis.common.common.dto.*;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.model.DepartmentCategory;
import com.hmis.server.hmis.common.common.model.ProductService;
import com.hmis.server.hmis.common.common.model.RevenueDepartment;
import com.hmis.server.hmis.common.common.repository.ProductServiceRepository;
import com.hmis.server.hmis.common.constant.HmisCodeDefaults;
import com.hmis.server.hmis.common.constant.HmisExceptionMessage;
import com.hmis.server.hmis.common.constant.HmisGlobalSettingKeys;
import com.hmis.server.hmis.modules.billing.dto.BillViewTypeEnum;
import com.hmis.server.hmis.modules.others.dto.SchemeServicePriceDto;
import com.hmis.server.hmis.modules.others.dto.SearchFilterEnum;
import com.hmis.server.hmis.modules.others.dto.ServiceColumnDto;
import com.hmis.server.hmis.modules.others.dto.ServiceColumnEnum;
import com.hmis.server.hmis.modules.reports.dto.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class ProductServiceImpl implements IProductService {
	private final ProductServiceRepository productServiceRepository;
	private final CommonService commonService;
	private final DepartmentCategoryServiceImpl departmentCategoryService;
	private final GlobalSettingsImpl globalSettings;
	private final SchemeServicePriceImpl schemeServicePrice;
	private final RevenueDepartmentServiceImpl revenueDepartmentService;


	@Autowired
	public ProductServiceImpl(
			ProductServiceRepository productServiceRepository,
			CommonService commonService,
			DepartmentCategoryServiceImpl departmentCategoryService,
			GlobalSettingsImpl globalSettings, SchemeServicePriceImpl schemeServicePrice,
			@Lazy RevenueDepartmentServiceImpl revenueDepartmentService
	) {
		this.productServiceRepository = productServiceRepository;
		this.commonService = commonService;
		this.departmentCategoryService = departmentCategoryService;
		this.globalSettings = globalSettings;
		this.schemeServicePrice = schemeServicePrice;
		this.revenueDepartmentService = revenueDepartmentService;
	}


	@Override
	public ProductServiceDto findOne( Long id ) {
		try {
			Optional<ProductService> productService = this.productServiceRepository.findById( id );
			if ( !productService.isPresent() ) {
				throw new ResponseStatusException( HttpStatus.NOT_FOUND );
			}
			return productService.get().mapToDto();
		} catch ( Exception e ) {
			throw new ResponseStatusException( HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage() );
		}
	}

	@Override
	public Optional<ProductService> findOneRaw( Long id ) {
		return this.productServiceRepository.findById( id );
	}

	public ProductService findOneProductService( Long id ) {
		Optional<ProductService> optional = this.findOneRaw( id );
		if ( !optional.isPresent() ) {
			throw new ResponseStatusException( HttpStatus.NOT_FOUND, "Service Not Found" );
		}
		return optional.get();
	}

	@Override
	public List<ProductServiceDto> findAll() {
		try {
			return this.productServiceRepository.findAll( Sort.by( Sort.Order.asc( "id" ) ) ).stream().map( ProductService::mapToDto ).collect( Collectors.toList() );
		} catch ( Exception e ) {
			throw new ResponseStatusException( HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage() );
		}
	}

	@Override
	public List<ProductService> findAllRaw() {
		List<ProductService> list = new ArrayList<>();
		List<ProductService> all = this.productServiceRepository.findAll();
		if ( !all.isEmpty() ) {
			list = all;
		}
		return list;
	}

	@Override
	public ProductServiceDto createOne( ProductServiceDto serviceDto ) {
		if ( serviceDto.getRevenueDepartmentId() == null || serviceDto.getDepartmentId() == null ) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST );
		}
		try {

			serviceDto.setCode( this.generateProductServiceCode() );
			ProductService productService = this.productServiceRepository.save( mapToModel( serviceDto ) );
			serviceDto.setId( productService.getId() );
			return serviceDto;
		} catch ( Exception e ) {
			throw new ResponseStatusException( HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage() );
		}

	}

	public ResponseEntity<MessageDto> saveSchemeServicePrice( SchemeServicePriceDto dto ) {
		String value = this.globalSettings.findValueByKey( HmisGlobalSettingKeys.ENABLE_NHIS_SERVICE_PRICE );
		if ( value.equalsIgnoreCase( HmisGlobalSettingKeys.TRUE ) ) {
			if ( ObjectUtils.isEmpty( dto.getPrice() ) )
				throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Price is required" );
			if ( ObjectUtils.isEmpty( dto.getServiceId() ) )
				throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Service is required" );
			if ( ObjectUtils.isEmpty( dto.getSchemeId() ) )
				throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Scheme is required" );

			try {
				ProductService productService = this.findOneProductService( dto.getServiceId() );
				this.saveSchemePrice( dto.getSchemeId(), dto.getPrice(), productService );
				return ResponseEntity.ok().body( new MessageDto( "PRICE ADDED SUCCESSFULLY" ) );
			} catch ( Exception e ) {
				throw new ResponseStatusException( HttpStatus.BAD_REQUEST, e.getMessage() );
			}
		}
		throw new ResponseStatusException( HttpStatus.BAD_REQUEST, HmisExceptionMessage.FEATURE_IS_NOT_ENABLED );
	}

	public void saveSchemePrice( Long schemeId, double schemePrice, ProductService productService ) {
		this.schemeServicePrice.saveSchemePrice( schemeId, schemePrice, productService );
	}

	@Override
	public void updateOne( ProductServiceDto serviceDto ) {

	}

	@Override
	public void deactivateOne( ProductServiceDto serviceDto ) {

	}

	@Override
	public void deactivateInBatch( List<ProductServiceDto> serviceDtoList ) {
	}

	@Override
	public List<ProductServiceDto> createInBatch( List<ProductServiceDto> serviceDtoList ) {
		return null;
	}

	@Override
	public List<ProductServiceDto> searchProductService( String search ) {
		List<ProductServiceDto> serviceDtoList = new ArrayList<>();
		List<ProductService> serviceList = this.productServiceRepository.findAllByNameContainsIgnoreCaseOrCodeContainsIgnoreCase( search, search );
		if ( serviceList.size() > 0 ) {
			serviceDtoList = serviceList.stream().map( ProductService::mapToDto ).collect( Collectors.toList() );
		}
		return serviceDtoList;
	}

	@Override
	public List<ProductServiceDto> searchProductServiceByDepartmentCategory( String search, DepartmentCategory category ) {
		List<ProductServiceDto> serviceDtoList = new ArrayList<>();
		List<ProductService> serviceList = this.productServiceRepository.findAllByNameContainsIgnoreCaseOrCodeContainsIgnoreCaseAndDepartment_DepartmentCategory_Id( search, search, category.getId() );
		if ( serviceList.size() > 0 ) {
			for ( ProductService service : serviceList ) {
				if ( category != null && category.getId() != null ) {
					serviceDtoList.add( service.mapToDto() );
				}
			}
		}
		return serviceDtoList;
	}

	@Override
	public List<ProductServiceDto> searchSearchHandler( String search, String billType ) {
		if ( ObjectUtils.isNotEmpty( billType ) ) {
			BillViewTypeEnum viewTypeEnum = BillViewTypeEnum.values()[ Integer.parseInt( billType ) ];
			DepartmentCategory category = this.getDepartmentCategoryForBillType( viewTypeEnum );
			return this.searchProductServiceByDepartmentCategory( search, category );
		} else {
			return this.searchProductService( search );
		}
	}

	@Override
	public DepartmentCategory getDepartmentCategoryForBillType( BillViewTypeEnum viewTypeEnum ) {
		switch ( viewTypeEnum ) {
			case LAB_BILL:
				return this.departmentCategoryService.findByName( DepartmentCategoryEnum.Laboratory.name() );
			case RADIOLOGY_BILL:
				return this.departmentCategoryService.findByName( DepartmentCategoryEnum.Radiology.name() );
			default:
				return new DepartmentCategory();
		}
	}

	@Override
	public List<ProductServiceDto> findAllByRevenueDepartment( Long revenueDepartmentId ) {
		try {
			return this.productServiceRepository.findAllByRevenueDepartment( new RevenueDepartment( revenueDepartmentId ) ).stream().map( ProductService::mapToDto ).collect( Collectors.toList() );
		} catch ( Exception e ) {
			throw new ResponseStatusException( HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage() );
		}
	}

	@Override
	public List<ProductServiceDto> findAllByServiceDepartment( Long serviceDepartmentId ) {
		try {
			return this.productServiceRepository.findAllByDepartmentOrderByIdAsc( new Department( serviceDepartmentId ) ).stream().map( ProductService::mapToDto ).collect( Collectors.toList() );
		} catch ( Exception e ) {
			throw new ResponseStatusException( HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage() );
		}
	}

	@Override
	public List<ProductServiceDto> findAllByServiceName( String serviceName ) {
		try {
			return this.productServiceRepository.findAllByNameIsLike( serviceName ).stream().map( ProductService::mapToDto ).collect( Collectors.toList() );
		} catch ( Exception e ) {
			throw new ResponseStatusException( HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage() );
		}
	}

	@Override
	public List<ProductServiceDto> findByServiceId( Long id ) {
		List<ProductServiceDto> serviceDtoList = new ArrayList<>();
		serviceDtoList.add( this.findOne( id ) );
		return serviceDtoList;
	}

	@Override
	public List<ProductServiceDto> searchByFilterType( SearchFilterEnum searchBy, Long valueId ) {
		List<ProductServiceDto> serviceDtoList;

		if ( searchBy == SearchFilterEnum.REVENUE_DEPARTMENT ) {
			serviceDtoList = this.findAllByRevenueDepartment( valueId );

		} else if ( searchBy == SearchFilterEnum.SERVICE_DEPARTMENT ) {
			serviceDtoList = this.findAllByServiceDepartment( valueId );

		} else if ( searchBy == SearchFilterEnum.SERVICE_NAME ) {
			serviceDtoList = this.findByServiceId( valueId ); //redundant

		} else {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, HmisExceptionMessage.EXCEPTION_BAD_REQUEST );
		}
		System.out.println( serviceDtoList.size() );
		return serviceDtoList;
	}

	@Override
	@Transactional
	public ResponseDto<?> updateServiceByColumnType( ServiceColumnDto dto ) {
		ResponseDto responseDto = new ResponseDto();
		try {
			ProductService service = this.productServiceRepository.getOne( dto.getId() );
			this.updateService( service, dto.getColumn(), dto.getValue() );
			this.productServiceRepository.save( service );
			responseDto.setMessage( "Service Updated Successfully" );
			responseDto.setHttpStatusCode( 200 );
			return responseDto;
		} catch ( Exception e ) {
			throw new ResponseStatusException( HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage() );
		}
	}

	@Override
	public Map<String, Integer> createMany( List<ProductService> list ) {
		/*todo:: return scrap files for failed upload, with reason*/
		try {
			List<ProductService> duplicates = new ArrayList<>();
			for ( ProductService service : list ) {
				if ( isServiceExist( service ) ) {
					duplicates.add( service );
				} else {
					service.setCode( this.generateProductServiceCode() );
					this.productServiceRepository.save( service );
				}
			}

			//list.removeIf(this::isServiceExist); //using collection api
			int duplicatesNumber = duplicates.size();
			duplicates.forEach( list::remove );
			int uploadedNumber = list.size();

			Map<String, Integer> result = new HashMap<>();
			result.put( "Uploaded", uploadedNumber );
			result.put( "Duplicates", duplicatesNumber );
			return result;

		} catch ( Exception e ) {
			throw new ResponseStatusException( HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage() );
		}

	}

	public List<ProductService> findServicesForDailyCashReport( DailyCashCollectionSearchDto dto ) {
		List<ProductService> productServices = new ArrayList<>();
		if ( dto.getType().equals( DailyCollectionFilterTypeEnum.SERVICE ) ) {
			if ( dto.getService() != null && dto.getService().getId() != null ) {
				productServices.add( this.findOneProductService( dto.getService().getId() ) );
			} else {
				productServices = this.findAllRaw();
			}
		}
		return productServices;
	}

	public List<ProductServiceDto> getAll() {
		this.productServiceRepository.findAll();
		return null;
	}

	public List<ServiceChargeGroupedByRevenue> getServiceChargeGroupedByBoth() {
		List<ServiceChargeGroupedByRevenue> result = new ArrayList<>();
		List<RevenueDepartment> revenueDepartmentList = this.revenueDepartmentService.findAllRaw();
		if ( !revenueDepartmentList.isEmpty() ) {
			for ( RevenueDepartment revenueDepartment : revenueDepartmentList ) {
				ServiceChargeGroupedByRevenue groupedByRevenue = this.setServiceChargeSheetByRevenueDepartment( revenueDepartment );
				result.add( groupedByRevenue );
			}
		}
		return result;

	}

	public ServiceChargeGroupedByRevenue getServiceChargeGroupedByRevenue( RevenueDepartment revenueDepartment ) {
		return this.setServiceChargeSheetByRevenueDepartment( revenueDepartment );
	}

	public ServiceChargeGroupedByService getServiceChargeGroupedByService( Department serviceDepartment ) {
		ServiceChargeGroupedByService groupedByService = new ServiceChargeGroupedByService();
		groupedByService.setServiceDepartmentName( serviceDepartment.getName() );
		List<ProductService> serviceList = this.productServiceRepository.findAllByDepartment( serviceDepartment );
		if ( !serviceList.isEmpty() ) {
			List<ProductServiceWithNameAndPriceOnly> serviceWithNameAndPriceOnlyList = new ArrayList<>();
			for ( ProductService service : serviceList ) {
				ProductServiceWithNameAndPriceOnly serviceAndPrice = new ProductServiceWithNameAndPriceOnly();
				serviceAndPrice.setServiceName( service.getName() );
				serviceAndPrice.setNhisPrice( service.getNhisSellingPrice() );
				serviceAndPrice.setGeneralPrice( service.getRegularSellingPrice() );
				serviceWithNameAndPriceOnlyList.add( serviceAndPrice );

			}
			groupedByService.setServicesAndPriceList( new JRBeanCollectionDataSource( serviceWithNameAndPriceOnlyList ) );
		}
		return groupedByService;
	}

	private ServiceChargeGroupedByRevenue setServiceChargeSheetByRevenueDepartment( RevenueDepartment revenueDepartment ) {
		ServiceChargeGroupedByRevenue revenue = new ServiceChargeGroupedByRevenue();
		revenue.setRevenueDepartmentName( revenueDepartment.getName() );
		List<ServiceChargeGroupedByService> groupedByServices = new ArrayList<>();
		Map<Department, List<ProductService>> collect = this.productServiceRepository.findAllByRevenueDepartment( revenueDepartment ).stream().collect( groupingBy( ProductService::getDepartment ) );
		for ( Map.Entry<Department, List<ProductService>> entry : collect.entrySet() ) {
			ServiceChargeGroupedByService service = new ServiceChargeGroupedByService();
			service.setServiceDepartmentName( entry.getKey().getName() );
			List<ProductServiceWithNameAndPriceOnly> servicesAndPriceList = new ArrayList<>();
			for ( ProductService productService : entry.getValue() ) {
				ProductServiceWithNameAndPriceOnly priceOnly = new ProductServiceWithNameAndPriceOnly();
				priceOnly.setServiceName( productService.getName() );
				priceOnly.setGeneralPrice( productService.getRegularSellingPrice() );
				priceOnly.setNhisPrice( productService.getNhisSellingPrice() );
				servicesAndPriceList.add( priceOnly );
			}
			service.setServicesAndPriceList( new JRBeanCollectionDataSource( servicesAndPriceList ) );
			groupedByServices.add( service );
		}
		revenue.setGroupByService( new JRBeanCollectionDataSource( groupedByServices ) );
		return revenue;
	}

	private void updateService( ProductService service, ServiceColumnEnum column, String value ) {
		switch ( column ) {
			case SERVICE_NAME:
				service.setName( value );
				break;
			case REVENUE_DEPARTMENT:
				service.setRevenueDepartment( new RevenueDepartment( Long.valueOf( value ) ) );
				break;
			case SERVICE_DEPARTMENT:
				service.setDepartment( new Department( Long.valueOf( value ) ) );
				break;
			case USAGE:
				service.setUsedFor( value );
				break;
			case COST_PRICE:
				service.setCostPrice( Double.valueOf( value ) );
				break;
			case NHIS_PRICE:
				service.setNhisSellingPrice( Double.valueOf( value ) );
				break;
			case UNIT_PRICE:
				service.setUnitCost( Double.valueOf( value ) );
				break;
			case GENERAL_PRICE:
				service.setRegularSellingPrice( Double.valueOf( value ) );
				break;
			default:
				throw new ResponseStatusException( HttpStatus.BAD_REQUEST );
		}


	}

	private ProductService mapToModel( ProductServiceDto dto ) {
		ProductService productService = new ProductService();
		productService.setName( dto.getName() );
		productService.setApplyDiscount( dto.getApplyDiscount() );
		productService.setCostPrice( dto.getCostPrice() );
		productService.setUsedFor( dto.getUsedFor().name() );
		productService.setNhisSellingPrice( dto.getNhisSellingPrice() );
		if ( dto.getDiscount() != null ) {
			productService.setDiscount( dto.getDiscount() );
		}
		productService.setRegularSellingPrice( dto.getRegularSellingPrice() );
		productService.setApplyDiscount( dto.getApplyDiscount() );
		productService.setDepartment( new Department( dto.getDepartmentId() ) );
		productService.setRevenueDepartment( new RevenueDepartment( dto.getRevenueDepartmentId() ) );
		productService.setUnitCost( dto.getUnitCost() );

		if ( dto.getCode() != null ) {
			productService.setCode( dto.getCode() );
		}

		return productService;
	}

	private String generateProductServiceCode() {
		GenerateCodeDto generateCodeDto = new GenerateCodeDto();
		generateCodeDto.setDefaultPrefix( HmisCodeDefaults.PRODUCT_SERVICE_DEFAULT );
		generateCodeDto.setGlobalSettingKey( Optional.of( HmisGlobalSettingKeys.PRODUCT_SERVICE_CODE ) );
		generateCodeDto.setLastGeneratedCode( this.productServiceRepository.findTopByOrderByIdDesc().map( ProductService::getCode ) );
		return commonService.generateDataCode( generateCodeDto );
	}

	private boolean isServiceExist( ProductService service ) {
		//find service by name and service-department and rev-department
		Optional<ProductService> optionalService = this.productServiceRepository.findByNameAndDepartmentAndRevenueDepartment( service.getName(), service.getDepartment(), service.getRevenueDepartment() );
		return optionalService.isPresent();
	}

}
