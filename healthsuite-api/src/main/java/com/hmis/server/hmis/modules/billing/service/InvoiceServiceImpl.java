package com.hmis.server.hmis.modules.billing.service;

import com.hmis.server.hmis.common.common.service.GlobalSettingsImpl;
import com.hmis.server.hmis.modules.billing.iservice.iInvoiceService;
import com.hmis.server.hmis.modules.billing.model.DepositReceiptNumber;
import com.hmis.server.hmis.modules.billing.model.InvoiceNumber;
import com.hmis.server.hmis.modules.billing.model.PharmacyReceiptNumber;
import com.hmis.server.hmis.modules.billing.model.ServiceReceiptNumber;
import com.hmis.server.hmis.modules.billing.repository.DepositReceiptNumberRepository;
import com.hmis.server.hmis.modules.billing.repository.InvoiceNumberRepository;
import com.hmis.server.hmis.modules.billing.repository.PharmacyReceiptNumberRepository;
import com.hmis.server.hmis.modules.billing.repository.ServiceReceiptNumberRepository;
import com.hmis.server.hmis.modules.settings.dto.GlobalSettingsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static com.hmis.server.hmis.common.constant.HmisCodeDefaults.*;
import static com.hmis.server.hmis.common.constant.HmisGlobalSettingKeys.*;

@Service
public class InvoiceServiceImpl implements iInvoiceService {
	private final InvoiceNumberRepository invoiceNumberRepository;
	private final DepositReceiptNumberRepository depositReceiptNumberRepository;
	private final PharmacyReceiptNumberRepository pharmacyReceiptNumberRepository;
	private final ServiceReceiptNumberRepository serviceReceiptNumberRepository;
	private final GlobalSettingsImpl globalSettingsService;

	@Autowired
	public InvoiceServiceImpl(
			InvoiceNumberRepository invoiceNumberRepository,
			DepositReceiptNumberRepository depositReceiptNumberRepository,
			PharmacyReceiptNumberRepository pharmacyReceiptNumberRepository,
			ServiceReceiptNumberRepository serviceReceiptNumberRepository,
			GlobalSettingsImpl globalSettingsService ) {
		this.invoiceNumberRepository = invoiceNumberRepository;
		this.depositReceiptNumberRepository = depositReceiptNumberRepository;
		this.pharmacyReceiptNumberRepository = pharmacyReceiptNumberRepository;
		this.serviceReceiptNumberRepository = serviceReceiptNumberRepository;
		this.globalSettingsService = globalSettingsService;
	}

	@Override
	public String generatePatientServiceBillInvoiceNumber() {
		InvoiceNumber invoiceNumber = this.createServiceBillInvoiceNumber();
		return String.format( "%s%s", invoiceNumber.getPrefix(), invoiceNumber.getNumber() );
	}

	@Override
	public String generateServiceReceiptNumber() {
		ServiceReceiptNumber serviceReceiptNumber = this.createServiceReceiptNumber();
		return String.format( "%s%s", serviceReceiptNumber.getPrefix(), serviceReceiptNumber.getNumber() );
	}

	@Override
	public String generateDepositReceiptNumber() {
		DepositReceiptNumber depositReceiptNumber = this.createDepositReceiptNumber();
		return String.format( "%s%s", depositReceiptNumber.getPrefix(), depositReceiptNumber.getNumber() );
	}

	@Override
	public String generatePharmacyReceiptNumber() {
		PharmacyReceiptNumber pharmacyReceiptNumber = this.createPharmacyReceiptNumber();
		return String.format( "%s%s", pharmacyReceiptNumber.getPrefix(), pharmacyReceiptNumber.getNumber() );
	}

	private InvoiceNumber createServiceBillInvoiceNumber() {
		String invoiceNumberPrefix = this.getNumberPrefix( INVOICE_NUMBER_CODE_PREFIX, INVOICE_NUMBER_DEFAULT );

		Optional<InvoiceNumber> topByOrderByIdDesc = this.invoiceNumberRepository.findTopByOrderByIdDesc();
		String pNumberStartPoint = this.getNumberStartPoint();
		if ( topByOrderByIdDesc.isPresent() ) {
			InvoiceNumber invoiceNumber = new InvoiceNumber();
			invoiceNumber.setPrefix( invoiceNumberPrefix );
			invoiceNumber.setNumber( new AtomicInteger( topByOrderByIdDesc.get().getNumber().incrementAndGet() ) );
			return this.invoiceNumberRepository.save( invoiceNumber );
		}
		else {
			InvoiceNumber invoiceNumber = new InvoiceNumber();
			invoiceNumber.setPrefix( invoiceNumberPrefix );
			invoiceNumber.setNumber( new AtomicInteger( Integer.parseInt( pNumberStartPoint ) ) );
			return this.invoiceNumberRepository.save( invoiceNumber );
		}
	}

	private DepositReceiptNumber createDepositReceiptNumber() {
		String depositReceiptPrefix = this.getNumberPrefix( DEPOSIT_RECEIPT_PREFIX, DEPOSIT_RECEIPT_PREFIX_DEFAULT );
		Optional<DepositReceiptNumber> topByOrderIdDesc = this.depositReceiptNumberRepository.findTopByOrderByIdDesc();
		if ( topByOrderIdDesc.isPresent() ) {
			DepositReceiptNumber depositReceiptNumber = new DepositReceiptNumber();
			depositReceiptNumber.setPrefix( depositReceiptPrefix );
			depositReceiptNumber.setNumber( new AtomicInteger( topByOrderIdDesc.get().getNumber().incrementAndGet() ) );
			return this.depositReceiptNumberRepository.save( depositReceiptNumber );
		}
		else {
			DepositReceiptNumber depositReceiptNumber = new DepositReceiptNumber();
			depositReceiptNumber.setPrefix( depositReceiptPrefix );
			depositReceiptNumber.setNumber( new AtomicInteger( Integer.parseInt( this.getNumberStartPoint() ) ) );
			return this.depositReceiptNumberRepository.save( depositReceiptNumber );
		}
	}

	private PharmacyReceiptNumber createPharmacyReceiptNumber() {
		String pharmacyReceiptPrefix = this.getNumberPrefix( PHARMACY_RECEIPT_PREFIX, PHARMACY_RECEIPT_PREFIX_DEFAULT );
		Optional<PharmacyReceiptNumber> topByOrderByIdDesc = this.pharmacyReceiptNumberRepository.findTopByOrderByIdDesc();
		if ( topByOrderByIdDesc.isPresent() ) {
			PharmacyReceiptNumber pharmacyReceiptNumber = new PharmacyReceiptNumber();
			pharmacyReceiptNumber.setPrefix( pharmacyReceiptPrefix );
			pharmacyReceiptNumber.setNumber(
					new AtomicInteger( topByOrderByIdDesc.get().getNumber().incrementAndGet() ) );
			return this.pharmacyReceiptNumberRepository.save( pharmacyReceiptNumber );
		}
		else {
			PharmacyReceiptNumber pharmacyReceiptNumber = new PharmacyReceiptNumber();
			pharmacyReceiptNumber.setPrefix( pharmacyReceiptPrefix );
			pharmacyReceiptNumber.setNumber( new AtomicInteger( Integer.parseInt( this.getNumberStartPoint() ) ) );
			return this.pharmacyReceiptNumberRepository.save( pharmacyReceiptNumber );
		}
	}

	private ServiceReceiptNumber createServiceReceiptNumber() {
		String serviceReceiptPrefix = this.getNumberPrefix( SERVICE_RECEIPT_PREFIX, SERVICE_RECEIPT_PREFIX_DEFAULT );
		Optional<ServiceReceiptNumber> topByOrderByIdDesc = this.serviceReceiptNumberRepository.findTopByOrderByIdDesc();
		if ( topByOrderByIdDesc.isPresent() ) {
			ServiceReceiptNumber serviceReceiptNumber = new ServiceReceiptNumber();
			serviceReceiptNumber.setPrefix( serviceReceiptPrefix );
			serviceReceiptNumber.setNumber(
					new AtomicInteger( topByOrderByIdDesc.get().getNumber().incrementAndGet() ) );
			return this.serviceReceiptNumberRepository.save( serviceReceiptNumber );
		}
		else {
			ServiceReceiptNumber serviceReceiptNumber = new ServiceReceiptNumber();
			serviceReceiptNumber.setPrefix( serviceReceiptPrefix );
			serviceReceiptNumber.setNumber( new AtomicInteger( Integer.parseInt( this.getNumberStartPoint() ) ) );
			return this.serviceReceiptNumberRepository.save( serviceReceiptNumber );
		}
	}

	private String getNumberStartPoint() {
		return globalSettingsService.findByKey(
				new GlobalSettingsDto( Optional.of( APP_CODE_START_NUMBER ) ) ).getValue().orElse(
				String.valueOf( APP_CODE_START_NUMBER_DEFAULT ) );
	}

	private String getNumberPrefix( String key, String fallback ) {
		return this.globalSettingsService.findByKey( new GlobalSettingsDto( Optional.of( key ) ) ).getValue().orElse(
				fallback );
	}
}
