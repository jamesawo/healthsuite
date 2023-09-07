package com.hmis.server.hmis.modules.lab.controller;

import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.modules.lab.dto.LabParameterDto;
import com.hmis.server.hmis.modules.lab.model.LabParameter;
import com.hmis.server.hmis.modules.lab.service.LabParameterServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(HmisConstant.API_PREFIX +"/lab-parameter")
public class LabParameterController {
    @Autowired
    private LabParameterServiceImpl labParameterService;

    @PostMapping(value = "create-many")
    public ResponseEntity<Boolean> createManyParameter(@RequestBody List<LabParameter> list){
        return this.labParameterService.createMany(list);
    }

    @GetMapping(value = "get-all")
    public List<LabParameter> getAll(){
        return this.labParameterService.findAll();
    }

    @PostMapping(value = "remove-many")
    public ResponseEntity<Boolean> removeMany(@RequestBody List<LabParameter> labParameters){
        return this.labParameterService.removeMany(labParameters);
    }

    @GetMapping( "search-param-name" )
    public List<LabParameterDto> searchParamName(
            @RequestParam( value = "search" ) String search
    ) {
        return this.labParameterService.searchLabParameter(search);
    }
}
