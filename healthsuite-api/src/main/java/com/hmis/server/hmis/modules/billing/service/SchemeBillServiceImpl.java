package com.hmis.server.hmis.modules.billing.service;

import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.model.ProductService;
import com.hmis.server.hmis.common.common.model.RevenueDepartment;
import com.hmis.server.hmis.common.common.model.Scheme;
import com.hmis.server.hmis.common.common.service.HmisUtilService;
import com.hmis.server.hmis.modules.billing.dto.BillPatientTypeEnum;
import com.hmis.server.hmis.modules.billing.dto.PaymentTypeForEnum;
import com.hmis.server.hmis.modules.billing.dto.SchemeBillDto;
import com.hmis.server.hmis.modules.billing.iservice.ISchemeBillService;
import com.hmis.server.hmis.modules.billing.model.PatientBill;
import com.hmis.server.hmis.modules.billing.model.PatientServiceBillItem;
import com.hmis.server.hmis.modules.billing.model.SchemeBill;
import com.hmis.server.hmis.modules.billing.repository.SchemeBillRepository;
import com.hmis.server.hmis.modules.emr.model.PatientContactDetail;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.emr.model.PatientInsuranceDetail;
import com.hmis.server.hmis.modules.emr.service.PatientAdmissionServiceImpl;
import com.hmis.server.hmis.modules.emr.service.PatientDetailServiceImpl;
import com.hmis.server.hmis.modules.pharmacy.model.DrugRegister;
import com.hmis.server.hmis.modules.pharmacy.model.PharmacyBillItem;
import com.hmis.server.hmis.modules.reports.dto.SchemeConsumptionPropsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hmis.server.hmis.common.constant.HmisConstant.NIL;
import static com.hmis.server.hmis.modules.billing.dto.PaymentTypeForEnum.SERVICE;
import static java.util.stream.Collectors.groupingBy;

@Service
public class SchemeBillServiceImpl implements ISchemeBillService {
	private final SchemeBillRepository schemeBillRepository;
	private final PatientDetailServiceImpl patientDetailService;
	private final PatientAdmissionServiceImpl patientAdmissionService;

	@Autowired
	public SchemeBillServiceImpl(
			SchemeBillRepository schemeBillRepository,
			PatientDetailServiceImpl patientDetailService,
			@Lazy PatientAdmissionServiceImpl patientAdmissionService
	) {
		this.schemeBillRepository = schemeBillRepository;
		this.patientDetailService = patientDetailService;
		this.patientAdmissionService = patientAdmissionService;
	}

	@Override
	public void addBillToScheme( SchemeBillDto dto ) {
	}

	@Override
	public SchemeBill addBillToScheme( PatientBill savedBill ) {
		SchemeBill schemeBill = this.mapPatientBillToBill( savedBill );
		return this.schemeBillRepository.save( schemeBill );
	}


	@Override
	public SchemeBill mapPatientBillToBill( PatientBill savedBill ) {
		SchemeBill schemeBill = new SchemeBill();

		PatientDetail patient = savedBill.getPatient();
		Scheme scheme = this.patientDetailService.findPatientScheme( patient );
		if ( scheme == null ) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Patient Scheme is required" );
		}

		String patientSchemeApprovalCode =
				this.patientDetailService.findPatientSchemeApprovalCode( patient );
		String patientDiagnosis = this.patientDetailService.findPatientDiagnosis( patient );
		String nhisNumber = this.patientDetailService.findPatientSchemeCode( patient );

		schemeBill.setAmount( savedBill.getDiscountTotal() );
		schemeBill.setScheme( scheme );
		schemeBill.setSchemePlan( this.patientDetailService.findPatientSchemePlan( patient ) );
		schemeBill.setApprovalCode( patientSchemeApprovalCode == null ? "" : patientSchemeApprovalCode );
		schemeBill.setDiagnosis( patientDiagnosis == null ? "" : patientDiagnosis );
		schemeBill.setNhisNumber( nhisNumber == null ? "" : nhisNumber );
		schemeBill.setPatientBill( savedBill );

		boolean patientOnAdmission = this.patientAdmissionService.isPatientOnAdmission( patient.getId() );
		schemeBill.setIsOutPatient( !patientOnAdmission );
		schemeBill.setPatientDetail( patient );
		schemeBill.setSchemePlan( patient.getPatientInsuranceDetail().getSchemePlan() );

		return schemeBill;
	}

	@Override
	public void removeBill( SchemeBill schemeBill ) {
		this.schemeBillRepository.delete( schemeBill );
	}

	public List<SchemeBill> findSchemeBillsByDateRange( LocalDate start, LocalDate end ) {
		return this.schemeBillRepository.findAllByDateIsLessThanEqualAndDateGreaterThanEqual( end, start );
	}

	public List<SchemeBill> findBySchemeAndDateRange( Scheme scheme, LocalDate start, LocalDate end ) {
		return this.schemeBillRepository.findAllBySchemeAndDateIsLessThanEqualAndDateGreaterThanEqual(
				scheme, end, start
		);
	}


	/**
	 * summary scheme consumption report methods
	 */
	public List<SchemeConsumptionPropsDto> getSchemeConsumptionReportSummaryByHmo(
			Scheme scheme, LocalDate start, LocalDate end
	) {
		List<SchemeConsumptionPropsDto> dtoList;
		List<SchemeBill> list = this.findBySchemeAndDateRange( scheme, start, end );
		dtoList = this.wrapSchemeBillListToSchemeConsumptionPropsList( list );
		return dtoList;
	}


	/**
	 * Get a list of scheme consumption summary report filter by revenue department
	 *
	 * @param revenueDepartment RevenueDepartment
	 * @param start             LocalDate
	 * @param end               LocalDate
	 * @return Map
	 */
	public Map<String, List<SchemeConsumptionPropsDto>> getSchemeConsumptionReportSummaryByRevenueDepartment(
			RevenueDepartment revenueDepartment, LocalDate start, LocalDate end
	) {
		Map<String, List<SchemeConsumptionPropsDto>> map = new HashMap<>();
		List<SchemeBill> schemeBillList = this.findSchemeBillsByDateRange( start, end );
		if ( !schemeBillList.isEmpty() ) {
			for ( SchemeBill schemeBill : schemeBillList ) {
				List<SchemeConsumptionPropsDto> schemePropsList = new ArrayList<>();
				PatientBill patientBill = schemeBill.getPatientBill();
				Long revenueDepartmentId = revenueDepartment.getId();
				String date = schemeBill.getDate().toString();
				String approvalCode = schemeBill.getApprovalCode();
				String typeOfCare = this.getTypeOfCareFromPatientBill( patientBill );
				String diagnosis = schemeBill.getDiagnosis();

				if ( patientBill.getBillTypeEnum().equals( PaymentTypeForEnum.DRUG ) ) {
					this.setDrugSchemeConsumptionFilterByRevenueDep(
							patientBill, revenueDepartmentId, date, approvalCode, typeOfCare, diagnosis, schemePropsList
					);
					map.put( schemeBill.getScheme().getInsuranceName(), schemePropsList );
				}
				else if ( patientBill.getBillTypeEnum().equals( SERVICE ) ) {
					this.setServiceSchemeConsumptionFilterByRevenueDep(
							patientBill, revenueDepartmentId, date, approvalCode, typeOfCare, diagnosis, schemePropsList
					);
					map.put( schemeBill.getScheme().getInsuranceName(), schemePropsList );
				}
			}
		}
		return map;
	}


	/**
	 * Get a list of scheme consumption summary report filter by service department
	 *
	 * @param serviceDepartment Department
	 * @param start             LocalDate
	 * @param end               LocalDate
	 * @return Map
	 */
	public Map<String, List<SchemeConsumptionPropsDto>> getSchemeConsumptionReportSummaryByServiceDepartment(
			Department serviceDepartment, LocalDate start, LocalDate end
	) {
		Map<String, List<SchemeConsumptionPropsDto>> map = new HashMap<>();
		List<SchemeBill> schemeBillList = this.findSchemeBillsByDateRange( start, end );
		if ( !schemeBillList.isEmpty() ) {
			for ( SchemeBill schemeBill : schemeBillList ) {
				PatientBill patientBill = schemeBill.getPatientBill();
				List<SchemeConsumptionPropsDto> schemePropsList = new ArrayList<>();
				if ( patientBill.hasServiceBillItems() ) {
					Long serviceDepartmentId = serviceDepartment.getId();
					String date = schemeBill.getDate().toString();
					String approvalCode = schemeBill.getApprovalCode();
					String diagnosis = schemeBill.getDiagnosis();
					for ( PatientServiceBillItem serviceBillItem : patientBill.getPatientServiceBillItems() ) {
						if ( serviceBillItem.getProductService().getDepartment().getId().equals(
								serviceDepartmentId ) ) {

							SchemeConsumptionPropsDto propsDto = new SchemeConsumptionPropsDto();
							this.setSchemeConsumptionPropsDto(
									propsDto, date, approvalCode, diagnosis,
									serviceBillItem.getPrice(), serviceBillItem.getProductService().getName(),
									serviceBillItem.getQuantity().intValue(),
									serviceBillItem.getDiscountAmount(),
									patientBill.getPatient() != null ? patientBill.getPatient() : new PatientDetail(),
									patientBill.getBillTypeEnum().name()
							);
							schemePropsList.add( propsDto );

						}

					}
				}
				map.put( schemeBill.getScheme().getInsuranceName(), schemePropsList );

			}
		}
		return map;
	}

	public List<SchemeConsumptionPropsDto> getSchemeConsumptionReportSummaryByPatient(
			PatientDetail patient, LocalDate start, LocalDate end ) {
		List<SchemeConsumptionPropsDto> dtoList;
		List<SchemeBill> result = this.schemeBillRepository.
				findAllByPatientDetailAndDateIsLessThanEqualAndDateGreaterThanEqual( patient, end, start );
		dtoList = this.wrapSchemeBillListToSchemeConsumptionPropsList( result );
		return dtoList;
	}

	/**
	 * detailed scheme consumption report
	 */
	public Map<String, List<SchemeConsumptionPropsDto>> getSchemeBillListGroupedByPatient(
			PatientDetail patient, LocalDate start, LocalDate end ) {
		Map<String, List<SchemeConsumptionPropsDto>> resultSet = new HashMap<>();
		List<SchemeBill> bills = this.findSchemeBillsByDateRange( start, end );
		PatientInsuranceDetail patientInsuranceDetail = patient.getPatientInsuranceDetail();
		String insuranceName = patientInsuranceDetail != null ? patientInsuranceDetail.getScheme().getInsuranceName() : "";
		if ( !bills.isEmpty() ) {
			Map<PatientDetail, List<SchemeBill>> collect = bills.stream().collect(
					groupingBy( SchemeBill::getPatientDetail ) );

			if ( !collect.isEmpty() ) {
				for ( Map.Entry<PatientDetail, List<SchemeBill>> entry : collect.entrySet() ) {
					this.setSchemeConsumptionFromMapValue( entry.getValue(), insuranceName, resultSet );
				}
			}
		}
		return resultSet;
	}

	public Map<String, List<SchemeConsumptionPropsDto>> getSchemeBillListGroupedByScheme(
			Scheme scheme, LocalDate startDate, LocalDate endDate
	) {
		Map<String, List<SchemeConsumptionPropsDto>> resultSet = new HashMap<>();

		List<SchemeBill> bills = this.findSchemeBillsByDateRange( startDate, endDate );
		if ( !bills.isEmpty() ) {
			Map<Scheme, List<SchemeBill>> collect = bills.stream().collect( groupingBy( SchemeBill::getScheme ) );
			if ( !collect.isEmpty() ) {
				for ( Map.Entry<Scheme, List<SchemeBill>> entry : collect.entrySet() ) {
					if ( entry.getKey().getId().equals( scheme.getId() ) ) {
						this.setSchemeConsumptionFromMapValue( entry.getValue(), scheme.getInsuranceName(), resultSet );
					}
				}
			}
		}
		return resultSet;
	}


	public Map<String, List<SchemeConsumptionPropsDto>> getSchemeBillListGroupedByServiceDepartment(
			Department service, LocalDate start, LocalDate end
	) {
		Map<String, List<SchemeConsumptionPropsDto>> collect = new HashMap<>();
		List<SchemeConsumptionPropsDto> propsDtoList = this.getSchemeBillsByServiceDepartment( service, start, end );
		collect.put( service.getName(), propsDtoList );
		return collect;
	}


	public Map<String, List<SchemeConsumptionPropsDto>> getSchemeBillListGroupedByRevenueDepartment(
			RevenueDepartment revenueDepartment, LocalDate start, LocalDate end
	) {
		Map<String, List<SchemeConsumptionPropsDto>> collect = new HashMap<>();
		List<SchemeConsumptionPropsDto> propsDtoList = this.getSchemeBillsByRevenueDepartment( revenueDepartment, start,
		                                                                                       end
		);
		collect.put( revenueDepartment.getName(), propsDtoList );
		return collect;
	}


	private String getTypeOfCareFromPatientBill( PatientBill patientBill ) {
		String typeOfCare = "";
		if ( patientBill.getPatient() != null && patientBill.getPatient().getPatientInsuranceDetail() != null ) {
			typeOfCare = patientBill.getPatient().getPatientInsuranceDetail().getTypeOfCare();
		}
		return typeOfCare;
	}

	private void setSchemeConsumptionFromMapValue(
			List<SchemeBill> entry, String groupTitle,
			Map<String, List<SchemeConsumptionPropsDto>> resultSet ) {
		List<SchemeConsumptionPropsDto> propsDtoList = new ArrayList<>();
		for ( SchemeBill bill : entry ) {
			String date = bill.getDate().toString();
			PatientBill patientBill = bill.getPatientBill();
			String typeOfCare = this.getTypeOfCareFromPatientBill( patientBill );

			switch ( patientBill.getBillTypeEnum() ) {
				case DRUG:
					this.setDrugSchemeConsumption(
							patientBill,
							propsDtoList,
							date, bill.getApprovalCode(), bill.getDiagnosis()
					);
					break;
				case SERVICE:
					this.setServiceSchemeConsumption( patientBill, date, bill.getApprovalCode(),
					                                  typeOfCare, bill.getDiagnosis(), propsDtoList
					);
					break;
			}


		}
		resultSet.put( groupTitle, propsDtoList );
	}


	/**
	 * get scheme bill  by service department and date range
	 */
	private List<SchemeConsumptionPropsDto> getSchemeBillsByServiceDepartment(
			Department serviceDepartment, LocalDate startDate, LocalDate endDate
	) {
		List<SchemeConsumptionPropsDto> dtoList = new ArrayList<>();
		List<SchemeBill> bills = findSchemeBillsByDateRange( startDate, endDate );
		if ( !bills.isEmpty() ) {
			for ( SchemeBill schemeBill : bills ) {
				PatientBill patientBill = schemeBill.getPatientBill();
				String date = schemeBill.getDate().toString();
				BillPatientTypeEnum billPatientType = BillPatientTypeEnum.getBillPatientTypeEnum(
						patientBill.getBillPatientType() );
				if ( billPatientType.equals(
						BillPatientTypeEnum.REGISTERED ) && patientBill.getBillTypeEnum() == SERVICE ) {
					PatientInsuranceDetail patientInsuranceDetail = patientBill.getPatient().getPatientInsuranceDetail();
					this.setServiceSchemeConsumptionFilterByServiceDep(
							patientBill,
							serviceDepartment.getId(),
							date, patientInsuranceDetail.getApprovalCode(),
							patientInsuranceDetail.getDiagnosis(), dtoList
					);
				}
			}
		}
		return dtoList;
	}

	/* get scheme bill  by revenue department and date range */
	private List<SchemeConsumptionPropsDto> getSchemeBillsByRevenueDepartment(
			RevenueDepartment revenueDepartment, LocalDate startDate, LocalDate endDate
	) {
		List<SchemeConsumptionPropsDto> dtoList = new ArrayList<>();
		List<SchemeBill> bills = findSchemeBillsByDateRange( startDate, endDate );
		if ( !bills.isEmpty() ) {
			for ( SchemeBill schemeBill : bills ) {
				PatientBill patientBill = schemeBill.getPatientBill();
				String approvalCode = "";
				String typeOfCare = "";
				String diagnosis = "";
				String date = schemeBill.getDate().toString();

				BillPatientTypeEnum billPatientType = BillPatientTypeEnum.getBillPatientTypeEnum(
						patientBill.getBillPatientType() );
				if ( billPatientType.equals( BillPatientTypeEnum.REGISTERED ) ) {
					PatientInsuranceDetail patientInsuranceDetail = patientBill.getPatient().getPatientInsuranceDetail();
					approvalCode = patientInsuranceDetail.getApprovalCode();
					typeOfCare = patientInsuranceDetail.getTypeOfCare();
					diagnosis = patientInsuranceDetail.getDiagnosis();
				}

				switch ( patientBill.getBillTypeEnum() ) {
					case DRUG:
						this.setDrugSchemeConsumptionFilterByRevenueDep(
								patientBill,
								revenueDepartment.getId(), date,
								approvalCode, typeOfCare, diagnosis, dtoList
						);
						break;
					case SERVICE:
						this.setServiceSchemeConsumptionFilterByRevenueDep(
								patientBill,
								revenueDepartment.getId(),
								date, approvalCode, typeOfCare,
								diagnosis, dtoList
						);
						break;
				}
			}
		}

		return dtoList;
	}

	/**
	 * set scheme consumption props dto drug filter by revenue department
	 *
	 * @param revenueDepartmentId Long
	 * @param patientBill         PatientBill
	 * @param date                String
	 * @param approvalCode        String
	 * @param typeOfCare          String
	 * @param diagnosis           String
	 */
	private void setDrugSchemeConsumptionFilterByRevenueDep(
			PatientBill patientBill, Long revenueDepartmentId, String date, String approvalCode,
			String typeOfCare, String diagnosis,
			List<SchemeConsumptionPropsDto> dtoList ) {
		List<PharmacyBillItem> pharmacyBillItems = patientBill.getPharmacyBillItems();
		if ( !pharmacyBillItems.isEmpty() ) {
			for ( PharmacyBillItem pharmacyBillItem : pharmacyBillItems ) {
				DrugRegister drugRegister = pharmacyBillItem.getDrugRegister();
				if ( drugRegister.getRevenueDepartment().getId().equals( revenueDepartmentId ) ) {
					SchemeConsumptionPropsDto propsDto = new SchemeConsumptionPropsDto();
					this.setSchemeConsumptionPropsDto(
							propsDto, date, approvalCode,
							diagnosis, pharmacyBillItem.getPriceAmount(),
							drugRegister.fullBrandName(), pharmacyBillItem.getQuantity(),
							pharmacyBillItem.getDiscountAmount(),
							patientBill.getPatient() != null ? patientBill.getPatient() : new PatientDetail(),
							patientBill.getBillTypeEnum().name()
					);
					dtoList.add( propsDto );
				}
			}
		}
	}

	/* set scheme consumption props dto service filter by revenue department */
	private void setServiceSchemeConsumptionFilterByRevenueDep(
			PatientBill patientBill, Long id, String date, String approvalCode, String typeOfCare, String diagnosis,
			List<SchemeConsumptionPropsDto> dtoList ) {
		List<PatientServiceBillItem> serviceBillItems = patientBill.getPatientServiceBillItems();
		if ( !serviceBillItems.isEmpty() ) {
			for ( PatientServiceBillItem serviceBillItem : serviceBillItems ) {
				ProductService productService = serviceBillItem.getProductService();
				if ( productService.getRevenueDepartment().getId().equals( id ) ) {
					SchemeConsumptionPropsDto propsDto = new SchemeConsumptionPropsDto();
					propsDto.setDate( date );
					propsDto.setApprovalCode( approvalCode );
					propsDto.setTypeOfCare( typeOfCare );
					propsDto.setDiagnosis( diagnosis );
					propsDto.setPrice( serviceBillItem.getPrice() );
					propsDto.setServiceTitle( productService.getName() );
					propsDto.setQuantity( serviceBillItem.getQuantity().intValue() );
					propsDto.setDiscount( serviceBillItem.getDiscountAmount() );
					dtoList.add( propsDto );
				}
			}
		}
	}

	/* set scheme consumption props dto filter by service department */
	private void setServiceSchemeConsumptionFilterByServiceDep(
			PatientBill patientBill,
			Long serviceDepartmentId, String date,
			String approvalCode,
			String diagnosis,
			List<SchemeConsumptionPropsDto> dtoList
	) {
		List<PatientServiceBillItem> serviceBillItems = patientBill.getPatientServiceBillItems();
		if ( !serviceBillItems.isEmpty() ) {
			for ( PatientServiceBillItem serviceBillItem : serviceBillItems ) {
				ProductService productService = serviceBillItem.getProductService();
				if ( productService.getDepartment().getId().equals( serviceDepartmentId ) ) {
					SchemeConsumptionPropsDto propsDto = new SchemeConsumptionPropsDto();
					this.setSchemeConsumptionPropsDto(
							propsDto, date, approvalCode, diagnosis,
							serviceBillItem.getPrice(), productService.getName(),
							serviceBillItem.getQuantity().intValue(),
							serviceBillItem.getDiscountAmount(),
							patientBill.getPatient() != null ? patientBill.getPatient() : new PatientDetail(),
							patientBill.getBillTypeEnum().name()
					);
					dtoList.add( propsDto );
				}

			}

		}
	}

	/**
	 * set scheme consumption props dto class
	 *
	 * @param price          Double;
	 * @param approvalCode   String ##### approval code for that bill
	 *                       <p>Note: approvalCode is stored on schemeBill because it can change often, hence it's necessary to store
	 *                       the approval code at the time of billing patient
	 * @param chargeType     String ### chargeType is what the bill was used for: can be
	 *                       <code>SERVICE</code>   OR <code>DRUG</code>
	 * @param date           String
	 * @param name           String ### service or drug name
	 * @param diagnosis      String
	 * @param discountAmount ### the amount charge to the scheme or HMO
	 * @param patient        PatientDetail ### the patient that consumed the service or drug
	 * @param propsDto       SchemeConsumptionPropsDto ### the wrapper object for report printing
	 * @param quantity       int ### quantity of item on patient bill
	 */
	private void setSchemeConsumptionPropsDto(
			SchemeConsumptionPropsDto propsDto, String date, String approvalCode,
			String diagnosis, Double price, String name,
			int quantity, Double discountAmount, PatientDetail patient,
			String chargeType ) {
		propsDto.setDate( date );
		propsDto.setApprovalCode( approvalCode );
		propsDto.setTypeOfCare( HmisUtilService.getSchemePatientTypeOfCare( patient ).orElse( "" ) );
		propsDto.setDiagnosis( diagnosis );
		propsDto.setPrice( price );
		propsDto.setServiceTitle( name );
		propsDto.setQuantity( quantity );
		propsDto.setDiscount( discountAmount );

		propsDto.setPatientName( patient.getFullName() != null ? patient.getFullName() : NIL );
		propsDto.setPatientNumber( patient.getPatientNumber() != null ? patient.getPatientNumber() : NIL );

		PatientContactDetail patientContactDetail = new PatientContactDetail();
		if ( patient != null && patient.getPatientContactDetail() != null ) {
			patientContactDetail = patient.getPatientContactDetail();
		}
		propsDto.setPatientPhone(
				patientContactDetail.getPhoneNumber() != null ? patientContactDetail.getPhoneNumber() : NIL );
		propsDto.setHmoAmount( discountAmount );
		propsDto.setChargeFor( chargeType != null ? chargeType : NIL );
		PatientInsuranceDetail insuranceDetail = patient.getPatientInsuranceDetail();
		if ( insuranceDetail != null ) {
			propsDto.setNhisNumber( insuranceDetail.getNhisNumber() != null ? insuranceDetail.getNhisNumber() : NIL );
		}

	}


	/* set scheme consumption props dto drug when searched by scheme (hmo) */
	private void setDrugSchemeConsumption(
			PatientBill patientBill,
			List<SchemeConsumptionPropsDto> propsDtoList,
			String date, String approvalCode,
			String diagnosis
	) {
		List<PharmacyBillItem> pharmacyBillItems = patientBill.getPharmacyBillItems();
		if ( !pharmacyBillItems.isEmpty() ) {
			for ( PharmacyBillItem pharmacyBillItem : pharmacyBillItems ) {
				DrugRegister drugRegister = pharmacyBillItem.getDrugRegister();
				SchemeConsumptionPropsDto propsDto = new SchemeConsumptionPropsDto();
				this.setSchemeConsumptionPropsDto(
						propsDto, date, approvalCode, diagnosis, pharmacyBillItem.getPriceAmount(),
						drugRegister.fullBrandName(), pharmacyBillItem.getQuantity(),
						pharmacyBillItem.getDiscountAmount(),
						patientBill.getPatient() != null ? patientBill.getPatient() : new PatientDetail(),
						patientBill.getBillTypeEnum().name()
				);
				propsDtoList.add( propsDto );
			}
		}
	}

	/* set scheme consumption props dto service when searched by scheme (hmo) */
	private void setServiceSchemeConsumption(
			PatientBill patientBill, String date, String approvalCode, String typeOfCare, String diagnosis,
			List<SchemeConsumptionPropsDto> propsDtoList ) {
		List<PatientServiceBillItem> serviceBillItems = patientBill.getPatientServiceBillItems();
		if ( !serviceBillItems.isEmpty() ) {
			for ( PatientServiceBillItem serviceBillItem : serviceBillItems ) {
				ProductService productService = serviceBillItem.getProductService();
				SchemeConsumptionPropsDto propsDto = new SchemeConsumptionPropsDto();
				this.setSchemeConsumptionPropsDto(
						propsDto, date, approvalCode, diagnosis, serviceBillItem.getPrice(),
						productService.getName(), serviceBillItem.getQuantity().intValue(),
						serviceBillItem.getDiscountAmount(),
						patientBill.getPatient() != null ? patientBill.getPatient() : new PatientDetail(),
						patientBill.getBillTypeEnum().name()
				);
				propsDtoList.add( propsDto );
			}
		}
	}

	/*
	check if patient bill is  service or drug bill
	 */
	private void decideIsServiceOrDrugBillToSetConsumptionProps(
			SchemeConsumptionPropsDto propsDto, PatientBill patientBill, String approvalCode, String date,
			String diagnosis
	) {
		Double discountTotal = patientBill.getDiscountTotal();
		Double grossTotal = patientBill.getGrossTotal();

		this.setSchemeConsumptionPropsDto(
				propsDto, date, approvalCode,
				diagnosis,
				grossTotal, NIL,
				1,
				discountTotal,
				patientBill.getPatient() != null ? patientBill.getPatient() : new PatientDetail(),
				patientBill.getBillTypeEnum().name()
		);
	}

	private List<SchemeConsumptionPropsDto> wrapSchemeBillListToSchemeConsumptionPropsList( List<SchemeBill> list ) {
		List<SchemeConsumptionPropsDto> dtoList = new ArrayList<>();
		if ( !list.isEmpty() ) {
			for ( SchemeBill schemeBill : list ) {
				PatientBill patientBill = schemeBill.getPatientBill();
				SchemeConsumptionPropsDto propsDto = new SchemeConsumptionPropsDto();
				this.decideIsServiceOrDrugBillToSetConsumptionProps(
						propsDto, patientBill, schemeBill.getApprovalCode(),
						schemeBill.getDate().toString(), schemeBill.getDiagnosis()
				);
				dtoList.add( propsDto );
			}
		}

		return dtoList;
	}

}
