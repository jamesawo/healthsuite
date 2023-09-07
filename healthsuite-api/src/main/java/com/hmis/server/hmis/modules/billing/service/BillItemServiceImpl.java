package com.hmis.server.hmis.modules.billing.service;

import com.hmis.server.hmis.common.common.dto.DepartmentCategoryEnum;
import com.hmis.server.hmis.common.common.dto.ProductServiceInvoiceItems;
import com.hmis.server.hmis.common.common.model.ProductService;
import com.hmis.server.hmis.common.common.service.GlobalSettingsImpl;
import com.hmis.server.hmis.common.common.service.ProductServiceImpl;
import com.hmis.server.hmis.common.common.service.SchemeServicePriceImpl;
import com.hmis.server.hmis.common.socket.SockAsyncService;
import com.hmis.server.hmis.modules.billing.dto.BillItemDto;
import com.hmis.server.hmis.modules.billing.iservice.IBillItemService;
import com.hmis.server.hmis.modules.billing.model.PatientBill;
import com.hmis.server.hmis.modules.billing.model.PatientServiceBillItem;
import com.hmis.server.hmis.modules.billing.repository.BillItemRepository;
import com.hmis.server.hmis.modules.emr.dto.PatientCategoryEnum;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.lab.dto.LabTestItemsWrapper;
import com.hmis.server.hmis.modules.lab.service.LabTestRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.hmis.server.hmis.common.constant.HmisGlobalSettingKeys.ENABLE_NHIS_SERVICE_PRICE;
import static com.hmis.server.hmis.common.constant.HmisGlobalSettingKeys.TRUE;

@Service
public class BillItemServiceImpl implements IBillItemService {
	private final BillItemRepository billItemRepository;
	private final ProductServiceImpl productService;
	private final LabTestRequestService testRequestService;
	private final GlobalSettingsImpl globalSettings;
	private final SchemeServicePriceImpl schemeServicePriceService;


	public LabTestItemsWrapper testItemsWrapper = new LabTestItemsWrapper();

	@Autowired
	public BillItemServiceImpl(
			BillItemRepository billItemRepository,
			ProductServiceImpl productService,
			SockAsyncService asyncService,
			LabTestRequestService testRequestService,
			@Lazy GlobalSettingsImpl globalSettings,
			@Lazy SchemeServicePriceImpl schemeServicePriceService ) {
		this.billItemRepository = billItemRepository;
		this.productService = productService;
		this.testRequestService = testRequestService;

		this.globalSettings = globalSettings;
		this.schemeServicePriceService = schemeServicePriceService;
	}

	public PatientServiceBillItem getOneById( Long id ) {
		Optional<PatientServiceBillItem> optional = this.billItemRepository.findById( id );
		if ( !optional.isPresent() ) {
			throw new ResponseStatusException( HttpStatus.NOT_FOUND, "Bill Item Not Found" );
		}
		return optional.get();
	}

	@Override
	public void createBillItems( BillItemDto dto ) {
	}

	@Override
	public void addBillItemsToBill( Long billId ) {
	}

	@Override
	public List<PatientServiceBillItem> getItemsFromBill( Long patientBillId ) {
		return null;
	}

	// get service bill items from patient bill
	@Override
	public List<ProductServiceInvoiceItems> getItemsFromBill( PatientBill patientBill ) {
		List<PatientServiceBillItem> patientServiceBillItems = this.billItemRepository.findAllByPatientBill(
				patientBill );
		List<ProductServiceInvoiceItems> productsItems = new ArrayList<>();
		if ( patientServiceBillItems != null && patientServiceBillItems.size() > 0 ) {
			for ( PatientServiceBillItem item : patientServiceBillItems ) {
				ProductServiceInvoiceItems pItem = new ProductServiceInvoiceItems( item.getProductService().getName(),
				                                                                   item.getQuantity().toString(),
				                                                                   item.getNetAmount().toString()
				);
				productsItems.add( pItem );
			}
		}
		return productsItems;
	}

	@Override
	public List<PatientServiceBillItem> createManyBillItem( List<BillItemDto> list, PatientBill bill ) {
		List<PatientServiceBillItem> patientServiceBillItems = new ArrayList<>();
		if ( list.size() > 0 ) {
			List<PatientServiceBillItem> bills = new ArrayList<>();
			this.testItemsWrapper.setItemsList( new ArrayList<>() );
			for ( BillItemDto dto : list ) {
				PatientServiceBillItem patientServiceBillItem = new PatientServiceBillItem();
				this.mapToModel( dto, patientServiceBillItem, bill );
				patientServiceBillItem.setPatientBill( bill );
				PatientServiceBillItem savedItem = this.billItemRepository.save( patientServiceBillItem );
				bills.add( savedItem );
				// check if item is lab test, then add to testItemsWrapper
				this.addLabTestRequestItem( savedItem );
			}
			// set lab test request bill
			this.testItemsWrapper.setBill( bill );
			patientServiceBillItems = bills;
			// notify lab test service to save lab items from bill
			this.notifyLabTestHandler();
		}
		return patientServiceBillItems;
	}

	@Override
	public void removeBills( List<PatientServiceBillItem> items ) {
		this.billItemRepository.deleteInBatch( items );
	}

	public PatientServiceBillItem saveBill( PatientServiceBillItem billItem ) {
		return this.billItemRepository.save( billItem );
	}

	public void handleCancelServiceBillItem( PatientServiceBillItem item ) {
		item.setIsCancelled( true );
		item.setNetAmount( 0.00 );
		item.setGross( 0.00 );
		item.setNetAmount( 0.00 );
		item.setDiscountAmount( 0.00 );
		this.billItemRepository.save( item );
	}

	public void mapToModel( BillItemDto dto, PatientServiceBillItem model, PatientBill bill ) {

		if ( dto.getNhisPercent() != null ) {
			model.setNhisPercent( dto.getNhisPercent() );
		}

		if ( dto.getProductServiceId() != null ) {
			Optional<ProductService> productService = this.productService.findOneRaw( dto.getProductServiceId() );
			productService.ifPresent( model::setProductService );
		}

		if ( dto.getQuantity() != null ) {
			model.setQuantity( dto.getQuantity() );
		}

		if ( dto.getPayCash() != null ) {
			model.setPayCash( dto.getPayCash() );
		}

		if ( dto.getDiscountAmount() != null ) {
			model.setDiscountAmount( dto.getDiscountAmount() );
		}

		if ( bill.getPatientCategoryEnum() == null ) {
			bill.setPatientCategoryEnum( PatientCategoryEnum.GENERAL );
		}

		if ( dto.getGrossAmount() != null ) {
			model.setGross( dto.getGrossAmount() );
		}

		if ( dto.getNetAmount() != null ) {
			model.setNetAmount( dto.getNetAmount() );
		}

		if ( dto.getNhisPrice() != null ) {
			model.setNhisPrice( dto.getNhisPrice() );
		}

		if ( dto.getPrice() != null ) {
			model.setPrice( dto.getPrice() );
		}

		// this.calculateBillItemAmount( dto.getProductService().getId(), dto.getQuantity(), model, bill.getPatient() );
	}

	public void notifyLabTestHandler() {
		if ( this.testItemsWrapper.getItemsList().size() > 0 ) {
			this.testRequestService.saveLabTestItems( testItemsWrapper );
		}
	}

	private void addLabTestRequestItem( PatientServiceBillItem savedItem ) {
		// if its lab item then add this item to lab test request items
		boolean isLabItem = savedItem.getProductService().getDepartment().getDepartmentCategory()
				.getName().equals( DepartmentCategoryEnum.Laboratory.name() );
		// add it to member variable test items wrapper
		if ( isLabItem ) {
			this.testItemsWrapper.getItemsList().add( savedItem );
		}
	}

	private void calculateBillItemAmount(
			Long serviceId, double quantity, PatientServiceBillItem model, PatientDetail patient
	) {
		ProductService productService = this.productService.findOneProductService( serviceId );
		Double regularSellingPrice = productService.getRegularSellingPrice();
		Double nhisPrice = 0.0;

		String value = this.globalSettings.findValueByKey( ENABLE_NHIS_SERVICE_PRICE );
		if ( value.equalsIgnoreCase( TRUE ) ) {
			Double schemePrice = this.schemeServicePriceService
					.getSchemeServicePriceByPatient( patient.getId(), productService.getId() );
			nhisPrice = schemePrice > 0 ? schemePrice : productService.getNhisSellingPrice();
		}
		else {
			nhisPrice = productService.getNhisSellingPrice();
		}

		double discount = patient.isSchemePatient() ? productService.getDiscount() : 0;
		double price = patient.isSchemePatient() ? nhisPrice : regularSellingPrice;
		double netAmount = discount > 0 ? price * quantity - ( ( price * discount ) / 100 ) * quantity : price * quantity;
		double grossAmount = price * quantity;
		double discountAmount = grossAmount - netAmount;

		model.setNetAmount( netAmount );
		model.setGross( grossAmount );
		model.setDiscountAmount( discountAmount );
		model.setPrice( price );
		model.setNhisPrice( nhisPrice );
	}

}
