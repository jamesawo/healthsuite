package com.hmis.server.hmis.common.common.service;

import com.hmis.server.hmis.common.common.model.ProductService;
import com.hmis.server.hmis.common.common.model.Scheme;
import com.hmis.server.hmis.common.common.model.SchemeServicePrice;
import com.hmis.server.hmis.common.common.repository.SchemeServicePriceRepository;
import com.hmis.server.hmis.modules.emr.dto.PatientCategoryEnum;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.emr.service.PatientDetailServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@Slf4j
public class SchemeServicePriceImpl {
	private final SchemeServicePriceRepository schemeServicePriceRepository;
	private final SchemeServiceImpl schemeService;
	private final PatientDetailServiceImpl patientDetailService;
	private final ProductServiceImpl productService;

	@Autowired
	public SchemeServicePriceImpl(
			SchemeServicePriceRepository schemeServicePriceRepository,
			SchemeServiceImpl schemeService,
			@Lazy PatientDetailServiceImpl patientDetailService,
			@Lazy ProductServiceImpl productService ) {
		this.schemeServicePriceRepository = schemeServicePriceRepository;
		this.schemeService = schemeService;
		this.patientDetailService = patientDetailService;
		this.productService = productService;
	}

	public void saveSchemePrice( Long schemeId, double price, ProductService service ) {
		try {
			Scheme scheme = this.schemeService.findById( schemeId );
			SchemeServicePrice servicePrice = new SchemeServicePrice();
			Optional<SchemeServicePrice> optional = this.findByServiceAndScheme( scheme, service );
			optional.ifPresent( sp -> servicePrice.setId( sp.getId() ) );
			servicePrice.setPrice( price );
			servicePrice.setScheme( scheme );
			servicePrice.setService( service );
			this.schemeServicePriceRepository.save( servicePrice );
		} catch ( Exception e ) {
			log.error( e.getMessage(), e );
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, e.getMessage() );
		}
	}

	public Double getSchemeServicePriceByPatient( Long patientId, Long serviceId ) {
		try {
			PatientDetail patient = this.patientDetailService.findPatientDetailById( patientId );
			PatientCategoryEnum patientCategory = patient.getPatientCategory();
			Scheme scheme = patient.getPatientInsuranceDetail() != null ? patient.getPatientInsuranceDetail().getScheme() : null;

			if ( patientCategory == PatientCategoryEnum.SCHEME && scheme != null ) {
				ProductService service = this.productService.findOneProductService( serviceId );
				Optional<SchemeServicePrice> optional = this.schemeServicePriceRepository.findBySchemeAndService( scheme, service );
				double price = 0.0;
				if ( optional.isPresent() ) {
					price = optional.get().getPrice();
				}
				return price;
			}

			return 0.0;
		} catch ( Exception e ) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, e.getMessage() );
		}
	}

	public Optional<SchemeServicePrice> findByServiceAndScheme( Scheme scheme, ProductService service ) {
		return this.schemeServicePriceRepository.findBySchemeAndService( scheme, service );
	}
}
