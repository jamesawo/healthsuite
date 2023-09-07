package com.hmis.server.hmis.modules.billing.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hmis.server.hmis.common.common.dto.DateDto;
import com.hmis.server.hmis.common.common.dto.DepartmentDto;
import com.hmis.server.hmis.common.user.dto.UserDto;
import com.hmis.server.hmis.modules.clearking.dto.ClerkDoctorRequestDto;
import com.hmis.server.hmis.modules.emr.dto.PatientCategoryEnum;
import com.hmis.server.hmis.modules.emr.dto.PatientDetailDto;
import com.hmis.server.hmis.modules.pharmacy.dto.PharmacyBillItemDto;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class PatientBillDto {
	private PatientCategoryEnum billPatientCategory;
	private BillPatientTypeEnum billPatientType;
	@NotNull(message = "Bill Search Type Cannot Be Null")
	private BillSearchTypeEnum billSearchType;
	@NotNull(message = "Bill Total Cannot Be Null")
	private BillTotalDto billTotal;
	@NotNull(message = "Provide Cost Date")
	private DateDto costDate;
	@NotNull(message = "Cashier Info Is Required")
	private Long costById;
	@NotNull(message = "Provide Location Info")
	private Long locationId;
	private List< BillItemDto > billItems;
	private List< PharmacyBillItemDto > pharmacyBillItems;
	private Long id;
	private Long patientId;
	private PatientDetailDto patientDetailDto;
	private UserDto costByDto;
	private DepartmentDto locationDto;
	private String invoiceNumber;
	private DepositSumDto deposit;
	private WalkInPatientDto walkInPatient;
	@NotNull( message = "Payment Type For Bill is Required, Hint: Service/Drug" )
	private PaymentTypeForEnum billTypeFor;
	private Boolean isCredit;
	private Boolean isDoctorRequest;
	private ClerkDoctorRequestDto doctorRequest;
	private String comment;
	private Boolean isLabBill;
}
