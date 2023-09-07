package com.hmis.server.hmis.modules.pharmacy.service;

import com.hmis.server.hmis.common.common.dto.GenerateCodeDto;
import com.hmis.server.hmis.common.common.service.CommonService;
import com.hmis.server.hmis.common.common.service.HmisUtilService;
import com.hmis.server.hmis.common.user.service.UserServiceImpl;
import com.hmis.server.hmis.modules.pharmacy.dto.DrugIssuanceDto;
import com.hmis.server.hmis.modules.pharmacy.dto.DrugRequisitionDto;
import com.hmis.server.hmis.modules.pharmacy.dto.IssuanceTypeEnum;
import com.hmis.server.hmis.modules.pharmacy.iservice.IDrugIssuanceService;
import com.hmis.server.hmis.modules.pharmacy.model.DrugIssuance;
import com.hmis.server.hmis.modules.pharmacy.model.DrugRequisition;
import com.hmis.server.hmis.modules.pharmacy.repository.DrugIssuanceRepository;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import static com.hmis.server.hmis.common.constant.HmisCodeDefaults.DRUG_ISSUANCE_PREFIX_DEFAULT;
import static com.hmis.server.hmis.common.constant.HmisGlobalSettingKeys.DRUG_ISSUANCE_CODE_PREFIX;

@Service
public class DrugIssuanceServiceImpl implements IDrugIssuanceService {
	@Autowired
	private DrugIssuanceRepository issuanceRepository;
	@Autowired
	private DrugRequisitionServiceImpl requisitionService;
	@Autowired
	private UserServiceImpl userService;
	@Autowired
	private HmisUtilService utilService;
	@Autowired
	private CommonService commonService;

	@Override
	public DrugIssuance findById(Long id) {
		Optional< DrugIssuance > issuance = this.issuanceRepository.findById(id);
		if( ! issuance.isPresent() ) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid Issuance Id");
		}
		return issuance.get();
	}

	public DrugIssuance findByDrugRequisitionId(Long id) {
		Optional< DrugIssuance > optional = this.issuanceRepository.findByDrugRequisitionId(id);
		return optional.orElseGet(DrugIssuance::new);
	}

	public DrugIssuanceDto findByDrugRequisitionIdDto(Long id) {
		Optional< DrugIssuance > optional = this.issuanceRepository.findByDrugRequisitionId(id);
		//		return optional.map(this::mapModelToDto).orElseGet(DrugIssuanceDto::new);
		if( optional.isPresent() ) {
			return this.mapModelToDto(optional.get());
		}
		return new DrugIssuanceDto();
	}

	@Override
	public DrugIssuance saveNewIssuance(DrugIssuanceDto drugIssuanceDto) {
		return null;
	}

	@Override
	public DrugIssuanceDto saveIssuanceFromRequisition(DrugRequisition requisition) {
		if( ObjectUtils.isEmpty(requisition.getId()) ) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Requisition Id is required");
		}
		DrugIssuance issuance = new DrugIssuance();
		issuance.setCode(this.generateCode());
		issuance.setIssuedFromOutlet(requisition.getIssuingDepartment());
		issuance.setIssuedBy(requisition.getOperatingUser());
		issuance.setType(IssuanceTypeEnum.REQUISITION);
		issuance.setDrugRequisition(requisition);

		DrugIssuance save = this.issuanceRepository.save(issuance);
		return this.mapModelToDto(save);
	}

	@Override
	public List< DrugRequisitionDto > findByOutletAndDateRange(DrugIssuanceDto drugIssuanceDto){
		return this.requisitionService.findAllByOutletAndDateRange(drugIssuanceDto.getOutlet(), drugIssuanceDto.getStartDate(), drugIssuanceDto.getEndDate());
	}

	public DrugIssuanceDto mapModelToDto(DrugIssuance model) {
		DrugIssuanceDto dto = new DrugIssuanceDto();
		if( ObjectUtils.isNotEmpty(model.getIssuedBy()) ) {
			dto.setUser(this.userService.mapModelToDto(model.getIssuedBy()));
		}
		if( ObjectUtils.isNotEmpty(model.getCode()) ) {
			dto.setIssuanceNumber(model.getCode());
		}
		if( ObjectUtils.isNotEmpty(model.getDate()) ) {
			dto.setDate(this.utilService.transformLocalDateToDate(model.getDate()));
			dto.setDateDto(this.utilService.transformToDateDto(model.getDate()));
		}
		return dto;
	}

	private String generateCode() {
		GenerateCodeDto generateCodeDto = new GenerateCodeDto();
		generateCodeDto.setDefaultPrefix(DRUG_ISSUANCE_PREFIX_DEFAULT);
		generateCodeDto.setGlobalSettingKey(Optional.of(DRUG_ISSUANCE_CODE_PREFIX));
		generateCodeDto.setLastGeneratedCode(this.issuanceRepository.findTopByOrderByIdDesc().map(DrugIssuance::getCode));
		return commonService.generateDataCode(generateCodeDto);
	}
}
