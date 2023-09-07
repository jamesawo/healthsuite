package com.hmis.server.hmis.modules.others.controller;

import com.hmis.server.hmis.common.common.dto.MessageDto;
import com.hmis.server.hmis.common.common.dto.ProductServiceDto;
import com.hmis.server.hmis.common.common.dto.ResponseDto;
import com.hmis.server.hmis.common.common.dto.SchemeDto;
import com.hmis.server.hmis.common.common.service.ProductServiceImpl;
import com.hmis.server.hmis.common.common.service.SchemeServiceImpl;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.modules.others.dto.SchemeServicePriceDto;
import com.hmis.server.hmis.modules.others.dto.SearchFilterEnum;
import com.hmis.server.hmis.modules.others.dto.ServiceColumnDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping( HmisConstant.API_PREFIX + "service-register/" )
public class ServiceRegisterController {

	@Autowired
	SchemeServiceImpl schemeService;
	@Autowired
	private ProductServiceImpl productServiceImpl;

	@GetMapping( "one/{id}" )
	public ProductServiceDto findOne( @PathVariable Long id ) {
		return this.productServiceImpl.findOne( id );
	}

	@GetMapping( "all" )
	public List<ProductServiceDto> findAll() {
		return this.productServiceImpl.findAll();
	}

	@PostMapping( value = "create-one" )
	public ProductServiceDto createOne( @RequestBody ProductServiceDto payload ) {
		return this.productServiceImpl.createOne( payload );
	}

	@GetMapping( "search-product-services" )
	public List<ProductServiceDto> searchProductService(
			@RequestParam( value = "search" ) String search,
			@RequestParam( value = "viewType", required = false ) String viewType ) {
		return this.productServiceImpl.searchSearchHandler( search, viewType );
	}

	@GetMapping( "search-scheme-data" )
	public List<SchemeDto> searchSchemeData( @RequestParam( value = "search" ) String search ) {
		return this.schemeService.findByInsuranceNameOrCode( search );
	}

	@GetMapping( "find-by-search-filter" )
	public List<ProductServiceDto> searchByFilterType( @RequestParam( value = "searchBy" ) SearchFilterEnum searchBy, @RequestParam( value = "valueId" ) Long valueId ) {
		return this.productServiceImpl.searchByFilterType( searchBy, valueId );
	}

	@PatchMapping( "update-service-column" )
	public ResponseDto<?> updateServiceData( @RequestBody ServiceColumnDto dto ) {
		return this.productServiceImpl.updateServiceByColumnType( dto );
	}

	@PostMapping( value = "save-scheme-service-price" )
	public ResponseEntity<MessageDto> saveSchemeServicePrice( @RequestBody @Valid SchemeServicePriceDto dto ) {
		return this.productServiceImpl.saveSchemeServicePrice( dto );
	}


}
