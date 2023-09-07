package com.hmis.server.hmis.modules.emr.dto;

import lombok.Data;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class PatientNumberDto {

    private Optional<Long> id;

    private AtomicInteger number;

    private Long allocatedTo;
}
