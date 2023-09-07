package com.hmis.server.hmis.modules.clearking.dto;

import com.hmis.server.hmis.common.common.dto.DateDto;
import lombok.Data;

@Data
public class DoctorRequestSearchDto {
    Long patientId;
    DateDto startDate;
    DateDto endDate;
}
