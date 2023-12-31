package com.hmis.server.hmis.modules.pharmacy.service;

import com.hmis.server.hmis.common.common.dto.GenerateCodeDto;
import com.hmis.server.hmis.common.common.dto.ValidationResponse;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.service.CommonService;
import com.hmis.server.hmis.common.common.service.DepartmentServiceImpl;
import com.hmis.server.hmis.common.common.service.GlobalSettingsImpl;
import com.hmis.server.hmis.common.common.service.HmisUtilService;
import com.hmis.server.hmis.common.user.service.UserServiceImpl;
import com.hmis.server.hmis.modules.others.service.VendorServiceImpl;
import com.hmis.server.hmis.modules.pharmacy.dto.PharmacyReceivedGoodsDto;
import com.hmis.server.hmis.modules.pharmacy.dto.PharmacyReceivedGoodsItemDto;
import com.hmis.server.hmis.modules.pharmacy.dto.ReceivedNoteItemDto;
import com.hmis.server.hmis.modules.pharmacy.iservice.IPharmacyReceivedGoodsService;
import com.hmis.server.hmis.modules.pharmacy.model.DrugOrder;
import com.hmis.server.hmis.modules.pharmacy.model.PharmacyReceivedGoods;
import com.hmis.server.hmis.modules.pharmacy.model.PharmacyReceivedGoodsItem;
import com.hmis.server.hmis.modules.pharmacy.repository.PharmacyReceivedGoodsItemRepository;
import com.hmis.server.hmis.modules.pharmacy.repository.PharmacyReceivedGoodsRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


import static com.hmis.server.hmis.common.constant.HmisCodeDefaults.PHARMACY_RECEIVED_GOODS_PREFIX_DEFAULT;
import static com.hmis.server.hmis.common.constant.HmisGlobalSettingKeys.HOSPITAL_NAME;
import static com.hmis.server.hmis.common.constant.HmisGlobalSettingKeys.PHARMACY_RECEIVED_GOODS_CODE_PREFIX;

@Service
@Slf4j
public class PharmacyReceivedGoodsServiceImpl implements IPharmacyReceivedGoodsService {
	@Autowired
	private PharmacyReceivedGoodsRepository receivedGoodsRepository;
	@Autowired
	private PharmacyReceivedGoodsItemRepository goodsItemRepository;
	@Autowired
	private DrugOrderServiceImpl drugOrderService;
	@Autowired
	private DepartmentServiceImpl departmentService;
	@Autowired
	private UserServiceImpl userService;
	@Autowired
	private VendorServiceImpl vendorService;
	@Autowired
	private HmisUtilService utilService;
	@Autowired
	private DrugRegisterServiceImpl drugRegisterService;
	@Autowired
	private OutletReconciliationServiceImpl outletReconciliationService;
	@Autowired
	private CommonService commonService;
	@Autowired
	private GlobalSettingsImpl globalSettingsService;

	@Override
	public PharmacyReceivedGoods findOne(Long id) {
		Optional< PharmacyReceivedGoods > byId = this.receivedGoodsRepository.findById(id);
		if( ! byId.isPresent() ) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid Pharmacy Received Goods ID");
		}
		return byId.get();
	}

	@Override
	@Transactional
	public ResponseEntity createPharmacyReceivedGoods(PharmacyReceivedGoodsDto dto) {
		try {
			this.onValidateBeforeCreate(dto); // validate dto before saving
			PharmacyReceivedGoods receivedGoods = this.mapPharmacyReceivedGoodsDtoToModel(dto); // map dto to object model
			receivedGoods.setAutoGeneratedCode(this.generateReceivedGoodsCode()); //set auto generated code
			receivedGoods.setTotalAmountSupplied(this.getTotalAmountSupplied(dto));

			PharmacyReceivedGoods save = this.receivedGoodsRepository.save(receivedGoods); // save pharmacy received goods
			List< PharmacyReceivedGoodsItem > manyReceivedGoodsItem = this.createManyReceivedGoodsItem(save, dto);// create received goods items
			save.setReceivedGoodsItemsList(manyReceivedGoodsItem); // set items list

			this.updateOutletStockAfterReceivedGoods(save); // update outlet stock count for received items
			this.drugOrderService.setDrugOrderFulfilledStatus(dto.getDrugOrder().getId());

			//	byte[] bytes = this.generateGoodsReceivedNote(save); //todo:: generate received goods note as pdf (byte[])
			return ResponseEntity.ok().body(new ValidationResponse(true));
		}
		catch( RuntimeException e ) {
			log.debug(e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public List< PharmacyReceivedGoodsItem > createManyReceivedGoodsItem(PharmacyReceivedGoods receivedGoods, PharmacyReceivedGoodsDto dto) {
		List< PharmacyReceivedGoodsItem > itemList = new ArrayList<>();
		if( dto.getReceivedGoodsItemsList().size() > 0 ) {
			for( PharmacyReceivedGoodsItemDto item : dto.getReceivedGoodsItemsList() ) {
				PharmacyReceivedGoodsItem goodsItem = this.mapItemDtoToModel(item);
				goodsItem.setPharmacyReceivedGoods(receivedGoods);
				PharmacyReceivedGoodsItem receivedGoodsItem = this.goodsItemRepository.save(goodsItem);
				itemList.add(receivedGoodsItem);
			}
		}
		return itemList;
	}

	@Override
	public boolean isReceivingOutletHasDrugOrder(Long receivingDepartmentId, Long drugOrderId) {
		Department receivingOutlet = this.departmentService.findOne(receivingDepartmentId);
		DrugOrder drugOrder = this.drugOrderService.findOne(drugOrderId);

		return ! drugOrder.getFulfilled() && drugOrder.getDepartment().getId().equals(receivingOutlet.getId());
		//		return this.drugOrderService.isDrugOrderExistAndMatch(receivingOutlet, drugOrder);
	}

	private HashMap< String, Object > getReceivedNoteMap(PharmacyReceivedGoods receivedGoods) {
		HashMap< String, Object > map = new HashMap<>();
		map.put("hospitalLogo", null);
		map.put("orderItemList", new JRBeanCollectionDataSource(this.getOrderItemList(receivedGoods)));
		map.put("hospitalName", this.globalSettingsService.findValueByKey(HOSPITAL_NAME).toUpperCase());
		map.put("invoiceNumber", receivedGoods.getInvoiceNumber());
		map.put("goodsReceivedNumber", receivedGoods.getAutoGeneratedCode());
		map.put("orderNumber", receivedGoods.getDrugOrder().getCode());
		map.put("supplyDate", receivedGoods.getDate().toString());
		map.put("relatedInformation", receivedGoods.getRelatedInformation());
		map.put("receivingStaff", receivedGoods.getReceivedBy());
		map.put("receivingOutlet", receivedGoods.getReceivingDepartment().getName());
		map.put("suppliedBy", receivedGoods.getSupplyingCompany().getSupplierName());
		map.put("totalSuppliedAmount", this.commonService.formatAmount(receivedGoods.getTotalAmountSupplied()));
		return map;
	}

	private List< ReceivedNoteItemDto > getOrderItemList(PharmacyReceivedGoods receivedGoods) {
		List< ReceivedNoteItemDto > list = new ArrayList<>();
		if( receivedGoods.getReceivedGoodsItemsList().size() > 0 ) {
			list = receivedGoods.getReceivedGoodsItemsList().stream().map(this::prepareReceivedNoteItem).collect(Collectors.toList());
		} return list;
	}

	private ReceivedNoteItemDto prepareReceivedNoteItem(PharmacyReceivedGoodsItem item) {
		ReceivedNoteItemDto dto = new ReceivedNoteItemDto();
		if( item.getExpiryDate() != null ) {
			dto.setExpiryDate(item.getExpiryDate().toString());
		}
		if( item.getBatchNumber() != null ) {
			dto.setBatchNumber(item.getBatchNumber());
		}
		if( item.getDrugRegister() != null && item.getDrugRegister().fullBrandName() != null ) {
			dto.setDescription(item.getDrugRegister().fullBrandName());
		}
		if( item.getUnitOfIssue() != null ) {
			dto.setUnitOfIssue(item.getUnitOfIssue().toString());
		}
		if( item.getQuantitySupplied() != null ) {
			dto.setUnitOfIssue(item.getQuantitySupplied().toString());
		}
		if( item.getRate() != null ) {
			dto.setRate(item.getRate().toString());
		}
		if( item.getTotalCost() != null ) {
			dto.setAmount(this.commonService.formatAmount(item.getTotalCost()));
		}
		return dto;

	}

	private Double getTotalAmountSupplied(PharmacyReceivedGoodsDto dto) {
		Optional< Double > reduce = dto.getReceivedGoodsItemsList().stream().map(PharmacyReceivedGoodsItemDto::getTotalCost).reduce(Double::sum);
		return reduce.orElse(0.00);
	}

	private String getReceivedNoteJrxmlFilePath() {
		return "src/main/resources/reports/pharmacy_goods_received_note.jrxml";
	}

	private byte[] generateNoteByte(HashMap< String, Object > map, String filePath) {
		return this.commonService.generatePDFBytes(map, filePath);
	}

	private byte[] generateGoodsReceivedNote(PharmacyReceivedGoods receivedGoods) {
		//todo refactor & complete block
		HashMap< String, Object > map = this.getReceivedNoteMap(receivedGoods);
		String filePath = this.getReceivedNoteJrxmlFilePath();
		return this.generateNoteByte(map, filePath);
	}

	private void updateOutletStockAfterReceivedGoods(PharmacyReceivedGoods receivedGoods) {
		// add received goods to outlet stock balance
		this.outletReconciliationService
				.addStockFromSuppliersGoodsReceived(receivedGoods.getReceivingDepartment(), receivedGoods.getReceivedGoodsItemsList(), receivedGoods.getDrugOrder().getIsStore());
	}

	private void onValidateBeforeCreate(PharmacyReceivedGoodsDto dto) {
		if( dto.getDrugOrder() == null || dto.getDrugOrder().getId() == null ) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Drug Order Is Required");
		}

		if( dto.getReceivingDepartment() == null || ! dto.getReceivingDepartment().getId().isPresent() ) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Receiving Department Is Required ");
		}

		if( dto.getSupplyingCompany() == null ) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Supplying Company Is Required ");
		}

		if( ! this.isReceivingOutletHasDrugOrder(dto.getReceivingDepartment().getId().get(), dto.getDrugOrder().getId()) ) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Such Drug Order Found For Receiving Department");
		}
	}

	private PharmacyReceivedGoods mapPharmacyReceivedGoodsDtoToModel(PharmacyReceivedGoodsDto dto) {
		PharmacyReceivedGoods model = new PharmacyReceivedGoods();
		if( dto.getId() != null ) {
			model.setId(dto.getId());
		}
		if( dto.getDrugOrder() != null ) {
			model.setDrugOrder(this.drugOrderService.findOne(dto.getDrugOrder().getId()));
		}
		if( dto.getReceivingDepartment() != null && dto.getReceivingDepartment().getId().isPresent() ) {
			model.setReceivingDepartment(this.departmentService.findOne(dto.getReceivingDepartment().getId().get()));
		}
		if( dto.getSupplyingCompany() != null ) {
			model.setSupplyingCompany(this.vendorService.findOne(dto.getSupplyingCompany().getId()));
		}
		if( dto.getReceivedBy() != null ) {
			model.setReceivedBy(dto.getReceivedBy());
		}
		if( dto.getDeliveredBy() != null ) {
			model.setDeliveredBy(dto.getDeliveredBy());
		}
		if( dto.getInvoiceNumber() != null ) {
			model.setInvoiceNumber(dto.getInvoiceNumber());
		}
		if( dto.getInvoiceDate() != null ) {
			model.setInvoiceDate(this.utilService.transformToLocalDate(dto.getInvoiceDate()));
		}
		if( dto.getPurchaseOrderNumber() != null ) {
			model.setPurchaseOrderNumber(dto.getPurchaseOrderNumber());
		}
		if( dto.getDeliveryNoteNumber() != null ) {
			model.setDeliveryNoteNumber(dto.getDeliveryNoteNumber());
		}
		if( dto.getOutlet() != null && dto.getOutlet().getId().isPresent() ) {
			model.setLocation(this.departmentService.findOne(dto.getOutlet().getId().get()));
		}
		if( dto.getUser() != null && dto.getUser().getId().isPresent() ) {
			model.setUser(this.userService.findOneRaw(dto.getUser().getId().get()));
		}
		if( dto.getRelatedInformation() != null ) {
			model.setRelatedInformation(dto.getRelatedInformation());
		}
		return model;
	}

	private PharmacyReceivedGoodsDto mapPharmacyReceivedGoodsModelToDto(PharmacyReceivedGoods goods) {
		PharmacyReceivedGoodsDto dto = new PharmacyReceivedGoodsDto();
		if( goods.getId() != null ) {
			dto.setId(goods.getId());
		}
		if( goods.getDrugOrder() != null ) {
			dto.setDrugOrder(this.drugOrderService.mapDrugOrderModelToDto(goods.getDrugOrder()));
		}
		if( goods.getReceivingDepartment() != null ) {
			dto.setReceivingDepartment(this.departmentService.mapModelToDto(goods.getReceivingDepartment()));
		}
		if( goods.getSupplyingCompany() != null ) {
			dto.setSupplyingCompany(this.vendorService.mapToDto(goods.getSupplyingCompany()));
		}
		if( goods.getReceivedBy() != null ) {
			dto.setReceivedBy(goods.getReceivedBy());
		}
		if( goods.getDeliveredBy() != null ) {
			dto.setDeliveredBy(goods.getDeliveredBy());
		}
		if( goods.getInvoiceNumber() != null ) {
			dto.setInvoiceNumber(goods.getInvoiceNumber());
		}
		if( goods.getInvoiceDate() != null ) {
			dto.setInvoiceDate(this.utilService.transformToDateDto(goods.getInvoiceDate()));
		}
		if( goods.getPurchaseOrderNumber() != null ) {
			dto.setPurchaseOrderNumber(goods.getPurchaseOrderNumber());
		}
		if( goods.getDeliveryNoteNumber() != null ) {
			dto.setDeliveryNoteNumber(goods.getDeliveryNoteNumber());
		}
		if( goods.getLocation() != null ) {
			dto.setOutlet(this.departmentService.mapModelToDto(goods.getLocation()));
		}
		if( goods.getUser() != null ) {
			dto.setUser(this.userService.mapModelToDto(goods.getUser()));
		}
		if( goods.getReceivedGoodsItemsList() != null ) {
			dto.setReceivedGoodsItemsList(this.getReceivedGoodsItems(goods));
		}
		if( goods.getRelatedInformation() != null ) {
			dto.setRelatedInformation(goods.getRelatedInformation());
		}
		return dto;
	}

	private PharmacyReceivedGoodsItem mapItemDtoToModel(PharmacyReceivedGoodsItemDto dto) {
		PharmacyReceivedGoodsItem model = new PharmacyReceivedGoodsItem();
		if( dto.getId() != null ) {
			model.setId(dto.getId());
		}
		model.setQuantityOrdered(dto.getQuantityOrdered());
		model.setQuantityReceived(dto.getQuantityReceived());
		model.setQuantitySupplied(dto.getQuantitySupplied());
		model.setRate(dto.getRate());
		model.setUnitOfIssue(dto.getUnitOfIssue());
		model.setTotalCost(dto.getTotalCost());
		if( dto.getBatchNumber() != null ) {
			model.setBatchNumber(dto.getBatchNumber());
		}
		if( dto.getExpiryDate() != null ) {
			model.setExpiryDate(this.utilService.transformToLocalDate(dto.getExpiryDate()));
		}
		if( dto.getDrugRegister() != null ) {
			model.setDrugRegister(this.drugRegisterService.findOne(dto.getDrugRegister().getId()));
		}
		if( dto.getPharmacyReceivedGoods() != null ) {
			model.setPharmacyReceivedGoods(this.findOne(dto.getPharmacyReceivedGoods().getId()));
		}
		return model;
	}

	private PharmacyReceivedGoodsItemDto mapItemModelToDto(PharmacyReceivedGoodsItem item) {
		PharmacyReceivedGoodsItemDto dto = new PharmacyReceivedGoodsItemDto();
		dto.setId(item.getId());
		dto.setQuantityOrdered(item.getQuantityOrdered());
		dto.setQuantityReceived(item.getQuantityReceived());
		dto.setQuantitySupplied(item.getQuantitySupplied());
		dto.setRate(item.getRate());
		dto.setUnitOfIssue(item.getUnitOfIssue());
		dto.setTotalCost(item.getTotalCost());
		if( item.getBatchNumber() != null ) {
			dto.setBatchNumber(item.getBatchNumber());
		}
		if( item.getExpiryDate() != null ) {
			dto.setExpiryDate(this.utilService.transformToDateDto(item.getExpiryDate()));
		}
		if( item.getDrugRegister() != null ) {
			dto.setDrugRegister(this.drugRegisterService.mapToDto(item.getDrugRegister()));
		}
		return dto;
	}

	private List< PharmacyReceivedGoodsItemDto > getReceivedGoodsItems(PharmacyReceivedGoods model) {
		List< PharmacyReceivedGoodsItemDto > dtoList = new ArrayList<>();
		if( model.getReceivedGoodsItemsList().size() > 0 ) {
			dtoList = model.getReceivedGoodsItemsList().stream().map(this::mapItemModelToDto).collect(Collectors.toList());
		}
		return dtoList;
	}

	private String generateReceivedGoodsCode() {
		GenerateCodeDto generateCodeDto = new GenerateCodeDto();
		generateCodeDto.setDefaultPrefix(PHARMACY_RECEIVED_GOODS_PREFIX_DEFAULT);
		generateCodeDto.setGlobalSettingKey(Optional.of(PHARMACY_RECEIVED_GOODS_CODE_PREFIX));
		generateCodeDto.setLastGeneratedCode(this.receivedGoodsRepository.findTopByOrderByIdDesc().map(PharmacyReceivedGoods::getAutoGeneratedCode));
		return commonService.generateDataCode(generateCodeDto);
	}
}
