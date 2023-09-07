package com.hmis.server.hmis.modules.lab.dto;

import com.hmis.server.hmis.modules.billing.model.PatientBill;
import com.hmis.server.hmis.modules.billing.model.PatientServiceBillItem;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data @ToString
public class LabTestItemsWrapper {
    private List<PatientServiceBillItem> itemsList = new ArrayList<>();
    private PatientBill bill;
}
