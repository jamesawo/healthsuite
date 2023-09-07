package com.hmis.server.hmis.modules.pharmacy.iservice;

import com.hmis.server.hmis.modules.pharmacy.dto.PharmacyReceivedGoodsDto;
import com.hmis.server.hmis.modules.pharmacy.model.PharmacyReceivedGoods;
import com.hmis.server.hmis.modules.pharmacy.model.PharmacyReceivedGoodsItem;
import java.util.List;
import org.springframework.http.ResponseEntity;

public interface IPharmacyReceivedGoodsService {
	PharmacyReceivedGoods findOne(Long id);

	ResponseEntity createPharmacyReceivedGoods(PharmacyReceivedGoodsDto dto);

	List< PharmacyReceivedGoodsItem > createManyReceivedGoodsItem(PharmacyReceivedGoods receivedGoods, PharmacyReceivedGoodsDto dto);

	boolean isReceivingOutletHasDrugOrder(Long receivingDepartmentId, Long drugOrderId);
}
