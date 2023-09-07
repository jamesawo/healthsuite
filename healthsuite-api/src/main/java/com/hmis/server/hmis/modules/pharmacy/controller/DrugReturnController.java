package com.hmis.server.hmis.modules.pharmacy.controller;

import com.hmis.server.hmis.common.common.dto.MessageDto;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.modules.pharmacy.dto.DrugReturnDto;
import com.hmis.server.hmis.modules.pharmacy.service.DrugDispenseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( HmisConstant.API_PREFIX + "/drug-return" )
public class DrugReturnController {

    private final DrugDispenseServiceImpl drugDispenseService;

    @Autowired
    public DrugReturnController(DrugDispenseServiceImpl drugDispenseService) {
        this.drugDispenseService = drugDispenseService;
    }

    @PostMapping(value =  "handle-drug-return")
    public ResponseEntity<MessageDto> drugReturnHandler(@RequestBody DrugReturnDto dto){
        return this.drugDispenseService.handleDrugReturn(dto);
    }
}
