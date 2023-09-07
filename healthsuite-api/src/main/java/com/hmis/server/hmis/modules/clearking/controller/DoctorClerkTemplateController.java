package com.hmis.server.hmis.modules.clearking.controller;

import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.modules.clearking.dto.ClerkingTemplateDto;
import com.hmis.server.hmis.modules.clearking.dto.ClinicDeskEnum;
import com.hmis.server.hmis.modules.clearking.dto.GeneralClerkDeskDto;
import com.hmis.server.hmis.modules.clearking.dto.OutPatientDeskDto;
import com.hmis.server.hmis.modules.clearking.service.DoctorClerkingTemplateServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping( HmisConstant.API_PREFIX + "/doctor-template" )
public class DoctorClerkTemplateController {
    @Autowired
    private DoctorClerkingTemplateServiceImpl templateService;

    @GetMapping("search")
    public List<ClerkingTemplateDto> searchTemplate(@RequestParam( value = "search" ) String search,
                                                    @RequestParam( value = "doctor" ) String doctor,
                                                    @RequestParam( value = "desk" ) String desk ){
        return this.templateService.searchDoctorTemplate(search, Long.valueOf(doctor), ClinicDeskEnum.valueOf(desk));
    }

    @GetMapping(value = "out-patient-desk/{templateId}")
    public OutPatientDeskDto findOutPatientTemplate(@PathVariable("templateId") String templateId){
        return this.templateService.findOutPatientDeskTemplate(Long.valueOf(templateId));
    }

    @GetMapping(value = "general-clerk-desk/{templateId}")
    public GeneralClerkDeskDto findGeneralClerkDeskTemplate(@PathVariable("templateId") String templateId){
        return this.templateService.findGeneralClerkDeskTemplate(Long.valueOf(templateId));
    }

}
