package com.hmis.server.hmis.modules.pharmacy.service;

import com.hmis.server.hmis.common.common.dto.ProductServiceInvoiceItems;
import com.hmis.server.hmis.modules.billing.model.PatientBill;
import com.hmis.server.hmis.modules.emr.dto.PatientCategoryEnum;
import com.hmis.server.hmis.modules.pharmacy.dto.PharmacyBillItemDto;
import com.hmis.server.hmis.modules.pharmacy.iservice.IPharmacyBillItemService;
import com.hmis.server.hmis.modules.pharmacy.model.DrugRegister;
import com.hmis.server.hmis.modules.pharmacy.model.PharmacyBillItem;
import com.hmis.server.hmis.modules.pharmacy.repository.PharmacyBillItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PharmacyBillItemServiceImpl implements IPharmacyBillItemService {
	private final PharmacyBillItemRepository billItemRepository;
	private final DrugRegisterServiceImpl drugRegisterService;

	@Autowired
	public PharmacyBillItemServiceImpl(
			PharmacyBillItemRepository billItemRepository,
			@Lazy DrugRegisterServiceImpl drugRegisterService
	) {
		this.billItemRepository = billItemRepository;
		this.drugRegisterService = drugRegisterService;
	}


	@Override
	public List<PharmacyBillItem> createMany( List<PharmacyBillItemDto> billItemDtoList, PatientBill patientBill ) {
		List<PharmacyBillItem> billItems = new ArrayList<>();
		if ( billItemDtoList.size() > 0 ) {
			for ( PharmacyBillItemDto billDto : billItemDtoList ) {
				PharmacyBillItem model = new PharmacyBillItem();
				this.mapPharmacyBillItemDtoToModel( billDto, model, patientBill );
				model.setPatientBill( patientBill );
				billItems.add( model );
			}
			this.billItemRepository.saveAll( billItems );
		}
		return billItems;
	}

	@Override
	public void removeMany( List<PharmacyBillItem> pharmacyBillItems ) {
		if ( pharmacyBillItems != null && pharmacyBillItems.size() > 0 ) {
			this.billItemRepository.deleteInBatch( pharmacyBillItems );
		}
	}

	@Override
	public List<ProductServiceInvoiceItems> getItemsFromBill( PatientBill patientBill ) {
		List<PharmacyBillItem> listItems = this.billItemRepository.findAllByPatientBill( patientBill );
		List<ProductServiceInvoiceItems> productsItems = new ArrayList<>();
		if ( listItems != null && listItems.size() > 0 ) {
			for ( PharmacyBillItem item : listItems ) {
				String brandName = item.getDrugRegister().fullBrandName();
				String quantity = String.valueOf( item.getQuantity() );
				String netAmount = item.getNetAmount().toString();

				ProductServiceInvoiceItems pItem = new ProductServiceInvoiceItems( brandName, quantity, netAmount );
				productsItems.add( pItem );
			}
		}
		return productsItems;
	}

	@Override
	public List<PharmacyBillItemDto> getPharmacyBillItemsFromBill( PatientBill patientBill ) {
		List<PharmacyBillItemDto> dtoList = new ArrayList<>();
		if ( patientBill.getPharmacyBillItems() != null && patientBill.getPharmacyBillItems().size() > 0 ) {
			List<PharmacyBillItem> pharmacyBillItems = patientBill.getPharmacyBillItems();
			dtoList = pharmacyBillItems.stream().map( this::mapPharmacyBillItemToDto ).collect( Collectors.toList() );
		}
		return dtoList;
	}

	public PharmacyBillItem getOneById( Long id ) {
		return this.billItemRepository.findById( id ).orElseThrow(
				() -> new ResponseStatusException( HttpStatus.NOT_FOUND ) );
	}

	public PharmacyBillItem saveBill( PharmacyBillItem model ) {
		return this.billItemRepository.save( model );
	}

	public void handleCancelDrugBillItem( PharmacyBillItem item ) {
		item.setIsCancelled( true );
		item.setNetAmount( 0.00 );
		item.setGrossAmount( 0.00 );
		item.setDiscountAmount( 0.00 );
		this.billItemRepository.save( item );
	}

	public void mapPharmacyBillItemDtoToModel( PharmacyBillItemDto dto, PharmacyBillItem model, PatientBill bill ) {
		if ( dto.getIsPayCash() != null ) {
			model.setIsPayCash( dto.getIsPayCash() );
		}
		if ( dto.getNhisPercent() != null ) {
			model.setNhisPercent( dto.getNhisPercent() );
		}
		if ( dto.getFrequency() != null ) {
			model.setFrequency( dto.getFrequency() );
		}
		if ( dto.getDrugRegister() != null && dto.getDrugRegister().getId() != null ) {
			model.setDrugRegister( this.drugRegisterService.findOne( dto.getDrugRegister().getId() ) );
		}
		model.setDosage( dto.getDosage() );
		model.setDays( dto.getDays() );
		model.setAvailableQuantity( dto.getAvailableQuantity() );
		model.setQuantity( dto.getQuantity() );
		model.setDiscountAmount( dto.getDiscountAmount() );
		model.setNetAmount( dto.getNetAmount() );
		model.setGrossAmount( dto.getGrossAmount() );
		model.setPriceAmount( dto.getPrice() );
		if ( dto.getNhisPrice() != null ) {
			model.setNhisPrice( dto.getNhisPrice() );
		}

		// this.calculateBillItemAmount(dto.getDrugRegister().getId(), dto.getQuantity(), model, bill.getPatientCategoryEnum());
	}


	private void calculateBillItemAmount(
			Long drugId, double quantity, PharmacyBillItem model, PatientCategoryEnum patientCategory ) {
		DrugRegister drugRegister = this.drugRegisterService.findOne( drugId );
		Double regularSellingPrice = drugRegister.getRegularSellingPrice();
		Double nhisPrice = drugRegister.getNhisSellingPrice();

		double discount = drugRegister.getDiscountPercent();
		double price = patientCategory.equals( PatientCategoryEnum.SCHEME ) ? nhisPrice : regularSellingPrice;
		double netAmount = discount > 0 ? price * quantity - ( ( price * discount ) / 100 ) * quantity : price * quantity;
		double grossAmount = price * quantity;
		double discountAmount = grossAmount - netAmount;

		model.setNetAmount( netAmount );
		model.setGrossAmount( grossAmount );
		model.setDiscountAmount( discountAmount );
		model.setPriceAmount( price );
		model.setNhisPrice( nhisPrice );
	}

	private PharmacyBillItemDto mapPharmacyBillItemToDto( PharmacyBillItem model ) {
		PharmacyBillItemDto dto = new PharmacyBillItemDto();
		if ( model.getId() != null ) {
			dto.setId( model.getId() );
		}
		//		if( model.getIsCreditBill() != null ) {
		//			dto.setIsCreditBill(model.getIsCreditBill());
		//		}
		if ( model.getIsPayCash() != null ) {
			dto.setIsPayCash( model.getIsPayCash() );
		}
		if ( model.getNhisPercent() != null ) {
			dto.setNhisPercent( model.getNhisPercent() );
		}
		if ( model.getNhisPrice() != null ) {
			dto.setNhisPrice( model.getNhisPrice() );
		}
		if ( model.getFrequency() != null ) {
			dto.setFrequency( model.getFrequency() );
		}
		if ( model.getPriceAmount() != null ) {
			dto.setPrice( model.getPriceAmount() );
		}
		if ( model.getNetAmount() != null ) {
			dto.setNetAmount( model.getNetAmount() );
		}
		if ( model.getGrossAmount() != null ) {
			dto.setGrossAmount( model.getGrossAmount() );
		}
		if ( model.getDiscountAmount() != null ) {
			dto.setDiscountAmount( model.getDiscountAmount() );
		}
		if ( model.getDrugRegister() != null ) {
			dto.setDrugRegister( this.drugRegisterService.mapToDto( model.getDrugRegister() ) );
		}
		dto.setDosage( model.getDosage() );
		dto.setDays( model.getDays() );
		dto.setAvailableQuantity( model.getAvailableQuantity() );
		dto.setQuantity( model.getQuantity() );
		dto.setDescription( model.getDrugRegister().fullBrandName() );
		return dto;
	}
}
