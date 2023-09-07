package com.hmis.server.hmis.modules.pharmacy.controller;

import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.modules.pharmacy.dto.PharmacyReceivedGoodsDto;
import com.hmis.server.hmis.modules.pharmacy.service.PharmacyReceivedGoodsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( HmisConstant.API_PREFIX + "/receive-goods" )
public class ReceiveGoodsController {
	@Autowired
	private PharmacyReceivedGoodsServiceImpl receivedGoodsService;

	@PostMapping( value = "save-received-goods" )
	public ResponseEntity saveReceivedGoods(@RequestBody PharmacyReceivedGoodsDto dto) {
		return this.receivedGoodsService.createPharmacyReceivedGoods(dto);
	}
}
