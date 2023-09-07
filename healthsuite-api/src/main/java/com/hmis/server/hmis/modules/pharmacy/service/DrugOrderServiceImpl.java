package com.hmis.server.hmis.modules.pharmacy.service;

import com.hmis.server.hmis.common.common.dto.GenerateCodeDto;
import com.hmis.server.hmis.common.common.dto.ValidationResponse;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.service.CommonService;
import com.hmis.server.hmis.common.common.service.DepartmentServiceImpl;
import com.hmis.server.hmis.common.user.service.UserServiceImpl;
import com.hmis.server.hmis.modules.others.service.VendorServiceImpl;
import com.hmis.server.hmis.modules.pharmacy.dto.DrugOrderDto;
import com.hmis.server.hmis.modules.pharmacy.dto.DrugOrderItemDto;
import com.hmis.server.hmis.modules.pharmacy.dto.DrugOrderSupplyCategoryEnum;
import com.hmis.server.hmis.modules.pharmacy.iservice.IDrugOrderService;
import com.hmis.server.hmis.modules.pharmacy.model.DrugOrder;
import com.hmis.server.hmis.modules.pharmacy.model.DrugOrderItem;
import com.hmis.server.hmis.modules.pharmacy.repository.DrugOrderItemRepository;
import com.hmis.server.hmis.modules.pharmacy.repository.DrugOrderRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import static com.hmis.server.hmis.common.constant.HmisCodeDefaults.DRUG_ORDER_PREFIX_DEFAULT;
import static com.hmis.server.hmis.common.constant.HmisGlobalSettingKeys.DRUG_ORDER_CODE_PREFIX;

@Service
@Slf4j
public class DrugOrderServiceImpl implements IDrugOrderService {
	@Autowired
	private DrugOrderRepository drugOrderRepository;
	@Autowired
	private DrugOrderItemRepository drugOrderItemRepository;
	@Autowired
	private DepartmentServiceImpl departmentService;
	@Autowired
	private UserServiceImpl userService;
	@Autowired
	private VendorServiceImpl vendorService;
	@Autowired
	private CommonService commonService;
	@Autowired
	private DrugRegisterServiceImpl drugRegisterService;

	@Override
	public DrugOrder findOne(Long drugOrderId) {
		Optional< DrugOrder > drugOrder = this.drugOrderRepository.findById(drugOrderId);
		if( ! drugOrder.isPresent() ) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Drug Order Not Found");
		}
		return drugOrder.get();
	}

	@Override
	public boolean isDrugOrderExistAndMatch(Department department, DrugOrder drugOrder) {
		if( this.isDrugOrderExist(department) ) {
			boolean flag = false;
			List< DrugOrder > list = this.drugOrderRepository.findAllByDepartmentAndStatusIsFalse(department);
			if( list.size() > 0 ) {
				for( DrugOrder drugOrder1 : list ) {
					flag = drugOrder.getId().equals(drugOrder1.getId());
				}
			}
			return flag;
		}
		return false;
	}

	@Override
	public boolean isDrugOrderExist(Department department) {
		//		return this.drugOrderRepository.existsByDepartmentAndStatusIsTrue(department);
		return this.drugOrderRepository.existsByDepartmentAndFulfilledIsFalse(department);
	}

	@Override
	public ResponseEntity< ValidationResponse > createDrugOrder(DrugOrderDto dto) {
		try {
			ValidationResponse validationResponse = this.onValidateDrugOrderBeforeSave(dto);
			if( ! validationResponse.getStatus().equals(true) ) {
				return ResponseEntity.badRequest().body(validationResponse);
			}
			DrugOrder order = new DrugOrder();
			this.mapDrugOrderDtoToModel(dto, order);
			order.setCode(this.generateDrugOrderCode());
			DrugOrder save = this.drugOrderRepository.save(order);

			this.createManyDrugOrderItem(dto.getDrugOrderItems(), save);
			ValidationResponse res = new ValidationResponse();
			res.addMessage(save.getCode());
			res.setStatus(true);

			return ResponseEntity.ok(res);
		}
		catch( Exception e ) {
			log.debug(e.getMessage(), e.getCause());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}

	}

	@Override
	public String generateDrugOrderCode() {
		GenerateCodeDto codeDto = new GenerateCodeDto();
		codeDto.setGlobalSettingKey(Optional.of(DRUG_ORDER_CODE_PREFIX));
		codeDto.setDefaultPrefix(DRUG_ORDER_PREFIX_DEFAULT);
		codeDto.setLastGeneratedCode(this.drugOrderRepository.findTopByOrderByIdDesc().map(DrugOrder::getCode));
		return commonService.generateDataCode(codeDto);
	}

	@Override
	public List< DrugOrderItemDto > createManyDrugOrderItem(List< DrugOrderItemDto > items, DrugOrder drugOrder) {
		if( items.size() < 1 ) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Order Items");
		}
		for( DrugOrderItemDto dto : items ) {
			DrugOrderItem model = new DrugOrderItem();
			this.mapDrugOrderItemDtoToModel(dto, model);
			model.setDrugOrder(drugOrder);
			DrugOrderItem orderItem = this.drugOrderItemRepository.save(model);
			dto.setId(orderItem.getId());
		}
		return items;
	}

	@Override
	public void replaceAllDrugOrderItem(List< DrugOrderItemDto > items, DrugOrder drugOrder) {
		this.drugOrderItemRepository.deleteByDrugOrder(drugOrder);
		this.createManyDrugOrderItem(items, drugOrder);
	}

	@Override
	public  List< DrugOrderDto > findDrugOrderByOrderCode(String code){
		List< DrugOrderDto > dtoList = new ArrayList<>();
		List< DrugOrder > list = this.drugOrderRepository.findAllByCodeIsContainingIgnoreCase(code);
		if( list.size() > 0 ){
			List< DrugOrderDto > result = new ArrayList<>();
			for( DrugOrder order : list ) {
				if( order.getFulfilled().equals(false) ) {
					DrugOrderDto drugOrderDto = mapDrugOrderModelToDto(order);
					result.add(drugOrderDto);
				}
			}
			dtoList = result;
		}
		return dtoList;
	}

	@Override
	public DrugOrderDto mapDrugOrderModelToDto(DrugOrder model) {
		DrugOrderDto dto = new DrugOrderDto();
		if( model.getId() != null ){
			dto.setId(model.getId());
		}
		if( model.getCode() != null ){
			dto.setCode(model.getCode());
		}
		if( model.getDepartment() != null ){
			dto.setOutlet(this.departmentService.mapModelToDto(model.getDepartment()));
		}
		if( model.getSupplyCategory() != null ){
			dto.setSupplyCategory(DrugOrderSupplyCategoryEnum.valueOf(model.getSupplyCategory()));
		}
		if( model.getVendor() != null ){
			dto.setVendor(this.vendorService.mapToDto(model.getVendor()));
		}
		if( model.getDrugOrderItems() != null ){
			dto.setDrugOrderItems(this.mapDrugOrderItemListModelToDto(model.getDrugOrderItems()));
			dto.setOrderItemsCount(model.getDrugOrderItems().size());
		}
		dto.setIsStore(model.getIsStore());
		dto.setFulfilled(model.getFulfilled());
		return dto;
	}

	@Override
	public ResponseEntity< ValidationResponse > updateDrugOrder(DrugOrderDto dto) {
		try {
			if( ObjectUtils.isEmpty(dto.getId()) ) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Drug Order Is Required!");
			}
			ValidationResponse validationResponse = this.onValidateDrugOrderBeforeSave(dto);
			if( ! validationResponse.getStatus().equals(true) ) {
				return ResponseEntity.badRequest().body(validationResponse);
			}
			DrugOrder order = this.findOne(dto.getId());
			this.mapDrugOrderDtoToModel(dto, order);
			DrugOrder save = this.drugOrderRepository.save(order);
			this.replaceAllDrugOrderItem(dto.getDrugOrderItems(), save);
			return ResponseEntity.ok(new ValidationResponse(true));
		}
		catch( Exception e ) {
			log.debug(e.getMessage(), e.getCause());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@Override
	public void setDrugOrderFulfilledStatus(Long id) {
		this.drugOrderRepository.setDrugOrderIsFulfilled(true, id);
	}

	private ValidationResponse onValidateDrugOrderBeforeSave(DrugOrderDto orderDto) {
		ValidationResponse response = new ValidationResponse();
		response.setStatus(true);
		if( orderDto.getVendor() == null || ObjectUtils.isEmpty(orderDto.getVendor().getId()) ) {
			response.setStatus(false);
			response.addMessage("Vendor is Required");
		}

		if( orderDto.getSupplyCategory() == null ) {
			response.setStatus(false);
			response.addMessage("Supply Category is Required");
		}

		if( ObjectUtils.isEmpty(orderDto.getDrugOrderItems()) || orderDto.getDrugOrderItems().size() == 0 ) {
			response.setStatus(false);
			response.addMessage("Order Items is Required");
		}

		if( ObjectUtils.isEmpty(orderDto.getUser()) ) {
			response.setStatus(false);
			response.addMessage("Order Officer is Required");
		}

		if( ObjectUtils.isEmpty(orderDto.getOutlet()) ) {
			response.setStatus(false);
			response.addMessage("Order Outlet is Required");
		}

		return response;
	}

	private List<DrugOrderItemDto> mapDrugOrderItemListModelToDto(List<DrugOrderItem> modelList){
		List<DrugOrderItemDto> dtoList = new ArrayList<>();
		if( modelList.size() > 0 ) {
			dtoList = modelList.stream().map(this::mapDrugOrderItemModelToDto).collect(Collectors.toList());
		}
		return dtoList;
	}

	private DrugOrderItemDto mapDrugOrderItemModelToDto(DrugOrderItem model){
		DrugOrderItemDto dto = new DrugOrderItemDto();
		if( model.getId() != null ){
			dto.setId(model.getId());
		}
		if(model.getTotalAmount() != null){
			dto.setTotalAmount(model.getTotalAmount());
		}
		if( model.getDrugRegister() != null ){
			dto.setDrugRegister(this.drugRegisterService.mapToDto(model.getDrugRegister()));
		}
		dto.setQuantity(model.getQuantity());
		dto.setRate(model.getRate());
		return dto;
	}

	private void mapDrugOrderItemDtoToModel(DrugOrderItemDto dto, DrugOrderItem model) {
		if( dto.getId() != null ) {
			model.setId(dto.getId());
		}
		if( dto.getDrugOrder() != null && dto.getDrugOrder().getId() != null ) {
			model.setDrugOrder(this.findOne(dto.getDrugOrder().getId()));
		}
		if( dto.getDrugRegister() != null ){
			model.setDrugRegister(this.drugRegisterService.mapToModel(dto.getDrugRegister()));
		}
		//		if( ObjectUtils.isNotEmpty(dto.getQuantity()) && ObjectUtils.isNotEmpty(dto.getRate()) ) {
		//			double total = ( dto.getQuantity() * dto.getRate() );
		//			model.setTotalAmount(total);
		//		}
		model.setQuantity(dto.getQuantity());
		model.setRate(dto.getRate());
		model.setTotalAmount(dto.getTotalAmount());
	}

	private void mapDrugOrderDtoToModel(DrugOrderDto dto, DrugOrder model) {
		if( dto.getId() != null ) {
			model.setId(dto.getId());
		}
		if( dto.getCode() != null ) {
			model.setCode(dto.getCode());
		}
		if( dto.getOutlet() != null && dto.getOutlet().getId().isPresent() ) {
			model.setDepartment(departmentService.findOne(dto.getOutlet().getId().get()));
		}
		if( dto.getUser() != null && dto.getUser().getId().isPresent() ) {
			model.setUser(this.userService.findOneRaw(dto.getUser().getId().get()));
		}
		if( dto.getVendor() != null ) {
			model.setVendor(this.vendorService.findOne(dto.getVendor().getId()));
		}
		if( dto.getSupplyCategory() != null ) {
			model.setSupplyCategory(dto.getSupplyCategory().name());
		}
		if( dto.getIsStore() != null ) {
			model.setIsStore(dto.getIsStore());
		}
	}
}
