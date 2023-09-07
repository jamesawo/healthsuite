package com.hmis.server.hmis.common.runner;

import com.hmis.server.hmis.common.common.model.*;
import com.hmis.server.hmis.common.common.repository.*;
import com.hmis.server.hmis.common.constant.HmisCodeDefaults;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.common.constant.PaymentMethodEnum;
import com.hmis.server.hmis.common.user.model.User;
import com.hmis.server.hmis.common.user.repository.UserRepository;
import com.hmis.server.hmis.modules.settings.model.GlobalSettings;
import com.hmis.server.hmis.modules.settings.repository.GlobalSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static com.hmis.server.hmis.common.common.dto.DepartmentCategoryEnum.*;
import static com.hmis.server.hmis.common.common.dto.PermissionConstants.*;
import static com.hmis.server.hmis.common.constant.HmisCodeDefaults.DEPARTMENT_CODE_DEFAULT_PREFIX;
import static com.hmis.server.hmis.common.constant.HmisConstant.DEFAULT_DEPARTMENT;
import static com.hmis.server.hmis.common.constant.HmisConstant.MOBILE_MONEY_DEPARTMENT;
import static com.hmis.server.hmis.common.constant.HmisGlobalSettingKeys.*;

@Service
public class Runner {
	@Autowired
	private PermissionRepository permissionRepository;
	@Autowired
	private GlobalSettingsRepository globalSettingsRepository;
	@Autowired
	private DepartmentCategoryRepository departmentCategoryRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private RevenueDepartmentTypeRepository revenueDepartmentTypeRepository;
	@Autowired
	private PaymentMethodRepository paymentMethodRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private DepartmentRepository departmentRepository;

	@Value(value = "${hmis.system.username}")
	private String username;

	@Value(value = "${hmis.system.password}")
	private String password;


	private List<Permission> seedPermissions;
	private Role seedRole;

	public Runner() {
		this.seedPermissions = new ArrayList<>();
		this.seedRole = new Role();
	}

	// seed default system role
	public void seedPermissions() {
		if (permissionRepository.findAll().isEmpty()) {

			List<Permission> permissions = new ArrayList<>();
			permissions.add(new Permission(EMR_PATIENT_REGISTRATION, "EMR", "EMR - Patient Registration"));
			permissions.add(new Permission(EMR_EDIT_PATIENT_DETAILS, "EMR", "EMR - Edit Patient Details"));
			permissions.add(new Permission(EMR_RECORD_VISIT, "EMR", "EMR - Record Visit"));
			permissions.add(new Permission(EMR_PATIENT_FOLDER_MOVEMENT, "EMR", "EMR - Patient Folder Movement"));
			permissions.add(
					new Permission(EMR_PATIENT_CATEGORY_MANAGEMENT, "EMR", "EMR - Patient Category Management"));
			permissions.add(new Permission(EMR_EDIT_SCHEME_DETAILS, "EMR", "EMR - Edit Scheme Details"));
			permissions.add(new Permission(EMR_PATIENT_ADMISSION, "EMR", "EMR - Patient Admission"));
			permissions.add(new Permission(EMR_ADMISSION_DETAILS, "EMR", "EMR - Admission Details"));
			permissions.add(new Permission(EMR_WARD_TRANSFER, "EMR", "EMR - Ward Transfer"));
			permissions.add(new Permission(EMR_CODING_AND_INDEXING, "EMR", "EMR - Coding and Indexing"));
			permissions.add(new Permission(EMR_APPOINTMENT_BOOKING, "EMR", "EMR - Appointment Booking"));
			permissions.add(new Permission(EMR_CLINICAL_ATTENDANCE, "EMR", "EMR - Clinical Attendance"));
			permissions.add(new Permission(EMR_DEATH_REGISTER, "EMR", "EMR - Death Register"));
			permissions.add(new Permission(EMR_PATIENT_DISCHARGE, "EMR", "EMR - Patient Discharge"));
			permissions.add(new Permission(EMR_APPROVAL_CODE_OFFICE, "EMR", "EMR - Approval Code Office"));
			permissions.add(new Permission(EMR_BIRTH_REGISTER, "EMR", "EMR - Birth Register"));
			permissions.add(new Permission(EMR_PATIENT_SEARCH_INDEX, "EMR", "EMR - Patient Search Index"));
			permissions.add(
					new Permission(EMR_EDIT_ORGANIZATION_DETAILS, "EMR", "EMR - Edit Organization Details"));
			permissions.add(
					new Permission(EMR_APPOINTMENT_BOOKING_SETUP, "EMR", "EMR - Appointment Booking Setup"));

			// PHARMACY - 17 Sub Modules
			permissions.add(new Permission(PHARMACY_DRUG_REGISTER, "Pharmacy", "Pharmacy - Drug Register "));
			permissions.add(new Permission(PHARMACY_PHARMACY_BILLING, "Pharmacy", "Pharmacy - Pharmacy Billing"));
			permissions.add(new Permission(PHARMACY_DRUG_ORDER, "Pharmacy", "Pharmacy - Drug Order"));
			permissions.add(new Permission(PHARMACY_RECEIVE_GOODS_FROM_SUPPLIERS, "Pharmacy",
					"Pharmacy - Receive " + "Goods From Suppliers"));
			permissions.add(new Permission(PHARMACY_OUTLET_RECONCILIATION, "Pharmacy",
					"Pharmacy - Outlet " + "Reconciliation"));
			permissions.add(new Permission(PHARMACY_STORE_RECONCILIATION, "Pharmacy",
					"Pharmacy - Store " + "Reconciliation"));
			permissions.add(new Permission(PHARMACY_TO_STORE_REQUISITION, "Pharmacy",
					"Pharmacy - To Store " + "Requisition"));
			permissions.add(new Permission(PHARMACY_TO_OUTLET_REQUISITION, "Pharmacy",
					"Pharmacy - To Outlet " + "Requisition"));
			permissions.add(new Permission(PHARMACY_STORE_ISSUANCE, "Pharmacy", "Pharmacy - Store Issuance "));
			permissions.add(new Permission(PHARMACY_OUTLET_ISSUANCE, "Pharmacy", "Pharmacy - Outlet Issuance"));
			permissions.add(new Permission(PHARMACY_DRUG_DISPENSING, "Pharmacy", "Pharmacy - Drug Dispensing"));
			permissions.add(new Permission(PHARMACY_BATCH_DRUG_DISPENSING, "Pharmacy",
					"Pharmacy - Batch Drug " + "Dispensing"));
			permissions.add(new Permission(PHARMACY_DRUG_WITHDRAWALS, "Pharmacy", "Pharmacy - Drug Withdrawals"));
			permissions.add(
					new Permission(PHARMACY_PATIENT_DRUG_RETURN, "Pharmacy", "Pharmacy - Patient Drug " + "Return"));
			permissions.add(new Permission(PHARMACY_DRUG_RETURN_NOTE, "Pharmacy", "Pharmacy - Drug Return Note"));
			permissions.add(new Permission(PHARMACY_PRIVATE_WING_PHARMACY_BILLING, "Pharmacy",
					"Pharmacy - Private " + "Wing Pharmacy Billing"));
			permissions.add(new Permission(PHARMACY_EXECUTIVE_WING_PHARMACY_BILLING, "Pharmacy",
					"Pharmacy - " + "Executive Wing Pharmacy Billing"));
			permissions.add(new Permission(PHARMACY_PHARMACY_NOTIFICATIONS, "Pharmacy",
					"Pharmacy - Low Stock Notifications"));

			// BILLING 12 Sub Modules
			permissions.add(new Permission(BILLING_PATIENT_BILLING, "Billing", "Billing - Patient Billing"));
			permissions.add(new Permission(BILLING_PATIENT_SCHEME_BILLING, "Billing",
					"Billing - Patient Scheme " + "Billing"));
			permissions.add(new Permission(BILLING_BILL_ADJUSTMENT, "Billing", "Billing - Bill Adjustment"));
			permissions.add(new Permission(BILLING_PAYMENT_MANAGER, "Billing", "Billing - Payment Manager"));
			permissions.add(new Permission(BILLING_PATIENT_DEPOSIT, "Billing", "Billing - Patient Deposit"));
			permissions.add(new Permission(BILLING_BILL_WAIVERS, "Billing", "Billing - Bill Waivers"));
			permissions.add(
					new Permission(BILLING_RECEIPT_CANCELLATION, "Billing", "Billing - Receipt " + "Cancellation"));
			permissions.add(new Permission(BILLING_HMO_BILLING, "Billing", "Billing - Hmo Billing"));
			permissions.add(new Permission(BILLING_RET_ORGANIZATION_BILLING, "Billing",
					"Billing - Ret. " + "Organization Billing"));
			permissions.add(new Permission(BILLING_SCHEME_BILL_ADJUSTMENT, "Billing",
					"Billing - Scheme Bill " + "Adjustment"));
			permissions.add(new Permission(BILLING_REFUND, "Billing", "Billing - Refund"));
			permissions.add(new Permission(BILLING_PATIENT_DEPOSIT_TRANSFER, "Billing",
					"Billing - Patient Deposit " + "Transfer"));

			// SHIFT MANAGEMENT 5 Sub Modules
			permissions.add(
					new Permission(SHIFTMANAGEMENT_SHIFT_MANAGER, "ShiftManagement", "Shift - Shift Manager"));
			permissions.add(new Permission(SHIFTMANAGEMENT_CASHIER_SHIFT_REPORT, "ShiftManagement",
					"Shift - Cashier Shift Report"));
			permissions.add(new Permission(SHIFTMANAGEMENT_FUND_RECEPTION_ACKNOWLEDGEMENT, "ShiftManagement",
					"Shift - Fund Reception & Acknowledgement"));
			permissions.add(new Permission(SHIFTMANAGEMENT_SHIFT_COMPILATION, "ShiftManagement",
					"Shift - Shift Compilation"));
			permissions.add(new Permission(SHIFTMANAGEMENT_ALL_SHIFT_PER_DAY_REPORT, "ShiftManagement",
					"Shift - All Shift Per Day Report"));

			// SETTINGS 9 Sub Modules
			permissions.add(new Permission(SETTINGS_USER_MANAGER, "Settings", "Settings - User Manager"));
			permissions.add(new Permission(SETTINGS_ROLE_MANAGER, "Settings", "Settings - Role Manager"));
			permissions.add(new Permission(SETTINGS_SEED_DATA_SETUP, "Settings", "Settings - Seed Data Setup"));
			permissions.add(new Permission(SETTINGS_GLOBAL_SETTINGS, "Settings", "Settings - Global Settings"));
			permissions.add(new Permission(SETTINGS_LOCATION_SETTINGS, "Settings", "Settings - Location Settings"));
			permissions.add(new Permission(SETTINGS_PASSWORD_RESET, "Settings", "Settings - Password Reset"));
			permissions.add(new Permission(SETTINGS_NHIS_SCHEME_SUSPENSION, "Settings",
					"Settings - Nhis Scheme " + "Suspension"));
			permissions.add(new Permission(SETTINGS_RET_SCHEME_SUSPENSION, "Settings",
					"Settings - Ret Scheme " + "Suspension"));
			permissions.add(
					new Permission(SETTINGS_RIGHT_SURVEILLANCE, "Settings", "Settings - Right Surveillance"));

			// NURSES DESK 7 Sub Modules
			permissions.add(new Permission(NURSES_VITAL_SIGNS_CAPTURE, "Nurses", "Nurses - Vital Signs Capture"));
			permissions.add(new Permission(NURSES_VITAL_SIGNS_TREND, "Nurses", "Nurses - Vital Sign Trend"));
			permissions.add(new Permission(NURSES_NURSING_NOTE, "Nurses", "Nurses - Nursing Note"));
			permissions.add(new Permission(NURSES_NURSING_CARE, "Nurses", "Nurses - Nursing Care"));
			permissions.add(new Permission(NURSES_DRUG_ADMINISTRATION, "Nurses", "Nurses - Drug Administration"));
			permissions.add(new Permission(NURSES_NURSE_PATIENT_EFOLDER, "Nurses", "Nurses - Patient e-Folder"));
			permissions.add(new Permission(NURSES_NURSE_ANTENATAL_BOOKING, "Nurses", "Nurses - Antenatal Booking"));
			permissions.add(new Permission(NURSES_OBSTETRICS_HISTORY, "Nurses", "Nurses - Obstetrics History"));
			permissions.add(new Permission(NURSES_ANC_BOOKING, "Nurses", "Nurses - Anc Booking"));
			permissions.add(new Permission(NURSES_ANC_CARD_NOTE, "Nurses", "Nurses - Anc Card Note"));
			permissions.add(new Permission(NURSES_VISUAL_ACUITY, "Nurses", "Nurses - Visual Acuity"));

			// CLERKING 14
			permissions.add(
					new Permission(DOCTOR_GENERAL_CLERKING_DESK, "Doctor", "Doctor - General Clerking " + "Desk"));
			permissions.add(new Permission(DOCTOR_ANC_CLERKING_DESK, "Doctor", "Doctor - Anc Clerking Desk"));
			permissions.add(new Permission(DOCTOR_DENTAL_CLERKING_DESK, "Doctor", "Doctor - Dental Clerking Desk"));
			permissions.add(new Permission(DOCTOR_PHYSIO_CLERKING_DESK, "Doctor", "Doctor - Physio Clerking Desk"));
			permissions.add(new Permission(DOCTOR_PEDIATRICS_CLERKING_DESK, "Doctor",
					"Doctor - Pediatrics " + "Clerking Desk"));
			permissions.add(new Permission(DOCTOR_DOCTOR_PATIENT_EFOLDER, "Doctor", "Doctor - Patient e-Folder"));
			permissions.add(new Permission(DOCTOR_INPATIENT_CLERKING, "Doctor", "Doctor - Inpatient Clerking"));
			permissions.add(new Permission(DOCTOR_PATIENT_FILE_UPLOAD, "Doctor", "Doctor - Patient File Upload"));
			permissions.add(
					new Permission(DOCTOR_DOCTOR_ANTENATAL_BOOKING, "Doctor", "Doctor - Antenatal Booking"));
			permissions.add(new Permission(DOCTOR_GENERAL_OUTPATIENT_DESK, "Doctor",
					"Doctor - General Outpatient " + "Desk"));
			permissions.add(
					new Permission(DOCTOR_OPHTHALMOLOGY_CLERKING, "Doctor", "Doctor - Ophthalmology " + "Clerking"));
			permissions.add(new Permission(DOCTOR_SURGICAL_OUTPATIENT_CONSULTATION, "Doctor",
					"Doctor - Surgical " + "Outpatient Consultation"));
			permissions.add(new Permission(DOCTOR_HOP_CLINIC_CONSULTATION, "Doctor",
					"Doctor - Hop Clinic " + "Consultation"));
			permissions.add(new Permission(DOCTOR_PSYCHIATRY_CONSULTATION, "Doctor",
					"Doctor - Psychiatry " + "Consultation"));

			// LAB 7
			permissions.add(new Permission(LAB_SPECIMEN_COLLECTION, "LAB", "Lab - Specimen Collection"));
			permissions.add(new Permission(LAB_SPECIMEN_COLLECTION_ACKNOWLEDGEMENT, "LAB",
					"Lab - Specimen " + "Collection Acknowledgement"));
			permissions.add(new Permission(LAB_LAB_REQUEST_TRACKER, "LAB", "Lab - Lab Request Tracker"));
			permissions.add(new Permission(LAB_CHEMICAL_LABORATORY, "LAB", "Lab - Chemical Laboratory"));
			permissions.add(new Permission(LAB_ANATOMICAL_LABORATORY, "LAB", "Lab - Anatomical Laboratory"));
			permissions.add(new Permission(LAB_HAEMATOLOGY, "LAB", "Lab - Haematology"));
			permissions.add(new Permission(LAB_MEDICAL_MICROBIOLOGY, "LAB", "Lab - Medical Microbiology"));
			permissions.add(new Permission(LAB_Billing, "LAB", "Lab - Billing"));
			permissions.add(new Permission(LAB_Parameter_Registration, "LAB", "Lab - Parameter Registration"));
			permissions.add(new Permission(LAB_RePush_Samples, "LAB", "Lab - Re Push Sample"));

			permissions.add(new Permission(LAB_MEDICAL_PARAM_SETUP, "LAB", "Lab - Med Parameter Setup"));
			permissions.add(new Permission(LAB_MEDICAL_RESULT_VIEW, "LAB", "Lab - Med Result View"));
			permissions.add(
					new Permission(LAB_MEDICAL_ORIGINAL_RESULT_VIEW, "LAB", "Lab - Med Original Result View"));
			permissions.add(
					new Permission(LAB_MEDICAL_PROVISIONAL_RESULT_VIEW, "LAB", "Lab - Med Provisional Result View"));
			permissions.add(new Permission(LAB_MEDICAL_PROVISIONAL_RESULT_CONTROL, "LAB",
					"Lab - Med Provisional Result Control"));
			permissions.add(new Permission(LAB_MEDICAL_RESULT_PREPARATION, "LAB", "Lab - Med Result Preparation"));
			permissions.add(
					new Permission(LAB_MEDICAL_RESULT_VERIFICATION, "LAB", "Lab - Med Result Verification"));
			permissions.add(new Permission(LAB_MEDICAL_PATHOLOGIST_VERIFICATION, "LAB",
					"Lab - Med Pathologist Result Verification"));
			// MICROBIOLOGY
			permissions.add(new Permission(LAB_MICROBIOLOGY_MAIN, "LAB", "Lab - Microbiology Main"));
			permissions.add(
					new Permission(LAB_MICROBIOLOGY_PARAM_SETUP, "LAB", "Lab - Microbiology Parameter Setup"));
			permissions.add(new Permission(LAB_MICROBIOLOGY_RESULT_VIEW, "LAB", "Lab - Microbiology Result View"));
			permissions.add(
					new Permission(LAB_MICROBIOLOGY_RESULT_CONTROL, "LAB", "Lab - Microbiology Result Control"));

			permissions.add(
					new Permission(LAB_MICROBIOLOGY_SEROLOGY_MAIN, "LAB", "Lab - Microbiology Serology Main"));
			permissions.add(new Permission(LAB_MICROBIOLOGY_SEROLOGY_RESULT_PREP, "LAB",
					"Lab - Microbiology Serology Result Prep"));
			permissions.add(new Permission(LAB_MICROBIOLOGY_SEROLOGY_RESULT_VERIFY, "LAB",
					"Lab - Microbiology Serology Result Verify"));
			permissions.add(new Permission(LAB_MICROBIOLOGY_SEROLOGY_RESULT_PATH_VERIFY, "LAB",
					"Lab - Microbiology Serology Pathologist Result Verify"));
			permissions.add(new Permission(LAB_MICROBIOLOGY_PARASITOLOGY_MAIN, "LAB",
					"Lab - Microbiology Parasitology Main"));
			permissions.add(new Permission(LAB_MICROBIOLOGY_PARASITOLOGY_RESULT_PREP, "LAB",
					"Lab - Microbiology Parasitology Result Prep"));
			permissions.add(new Permission(LAB_MICROBIOLOGY_PARASITOLOGY_RESULT_VERIFY, "LAB",
					"Lab - MicrobiologyParasitology Result Verify"));
			permissions.add(new Permission(LAB_MICROBIOLOGY_PARASITOLOGY_RESULT_PATH_VERIFY, "LAB",
					"Lab - Microbiology Parasitology Result Pathologist Verify"));

			// RADIOLOGY 5
			permissions.add(new Permission(RADIOLOGY_RADIOLOGY_RESULT_PREPARATION, "Radiology",
					"Radiology - " + "Radiology Result Preparation"));
			permissions.add(new Permission(RADIOLOGY_RADIOLOGY_RESULT_VERIFICATION, "Radiology",
					"Radiology - " + "Radiology Result Verification"));
			permissions.add(new Permission(RADIOLOGY_RADIOLOGY_RESULT_VIEW, "Radiology",
					"Radiology - Radiology " + "Result View"));
			permissions.add(new Permission(RADIOLOGY_RADIOLOGY_PROVISIONAL_RESULT_VIEW, "Radiology",
					"Radiology - " + "Radiology Provisional Result View"));
			permissions.add(
					new Permission(RADIOLOGY_RADIOLOGY_BILLING, "Radiology", "Radiology - Radiology Billing"));
			permissions.add(new Permission(RADIOLOGY_RADIOLOGY_REQUEST_TRACKER, "Radiology",
					"Radiology - Radiology Request Tracker"));
			permissions.add(new Permission(RADIOLOGY_RADIOLOGY_DRAFT, "Radiology", "Radiology - Radiology Draft"));

			// OTHERS 3
			permissions.add(new Permission(OTHER_REQUEST_TRACKER, "Other", "other - Request Tracker"));
			permissions.add(new Permission(OTHER_VENDOR_MANAGEMENT, "Other", "other - Vendor Management"));
			permissions.add(new Permission(OTHER_SERVICE_REGISTER, "Other", "other - Service Register"));
			permissions.add(new Permission(OTHER_DASHBOARD, "Other", "other - Dashboard"));
			permissions.add(new Permission(OTHER_SCHEME_SERVICE_PRICE, "Other", "other - Scheme Service Price"));

			// SETTLEMENT 2
			permissions.add(new Permission(Settlement_Service_Provider_Invoice, "Settlement",
					"settlement - Service " + "Provider Invoice"));
			permissions.add(new Permission(Settlement_Settlement_Advice, "Settlement",
					"settlement - Settlement " + "Advice"));

			// REPORTS
			permissions.add(new Permission(Reports_Registered_Patient, "Report", "Report - Registered_Patient"));
			permissions.add(new Permission(Reports_Appointment_Booking, "Report", "Report - Appointment Booking"));
			permissions.add(new Permission(Reports_Admission_Register, "Report", "Report - Admission Register"));
			permissions.add(new Permission(Reports_All_Shift_Per_Day, "Report", "Report - All Shift PerDay"));
			permissions.add(
					new Permission(Reports_Daily_Cash_Collection, "Report", "Report - Daily Cash Collection"));
			permissions.add(new Permission(Reports_Cancelled_Receipts, "Report", "Report - Cancelled Receipts"));
			permissions.add(
					new Permission(Reports_Payment_Receipt_Report, "Report", "Report - Payment Receipt Report"));
			permissions.add(
					new Permission(Reports_Stock_Balance_Per_Outlet, "Report", "Report - Stock Balance Per Outlet"));
			permissions.add(
					new Permission(Reports_Outlet_Activity_Report, "Report", "Report - Outlet Activity Report"));
			permissions.add(new Permission(Reports_Patient_E_Folder, "Report", "Report - Patient E-Folder"));
			permissions.add(new Permission(Reports_Emr_Patient_Interim_Invoice, "Report",
					"Report - EMR Patient Interim Invoice"));

			permissions.add(new Permission(Reports_Account_Scheme_Consumption_Report, "Report",
					"Report - Account Scheme Consumption Report"));
			permissions.add(new Permission(Reports_Other_Report_Main, "Report", "Report - Other Report Main"));
			permissions.add(new Permission(Reports_Other_Service_Entire_Charge, "Report",
					"Report - Other Service Charge Report"));

			this.seedPermissions = permissionRepository.saveAll(permissions);
			System.out.println("----- Seeding System Permission -----");
		}
	}

	public void seedGlobalSettings() {
		if (globalSettingsRepository.findAll().isEmpty()) {
			List<GlobalSettings> globalSettings = new ArrayList<>();
			globalSettings.add(new GlobalSettings(HOSPITAL_NAME, "ST LUKES TEACHING HOSPITAL", "General"));
			globalSettings.add(new GlobalSettings(PATIENT_NUMBER_PREFIX, "PN", "General"));
			globalSettings.add(new GlobalSettings(PATIENT_NUMBER_SUFFIX, null, "General"));
			// PATIENT_NUMBER_START_POINT get overwritten APP_CODE_START_NUMBER
			globalSettings.add(new GlobalSettings(PATIENT_NUMBER_START_POINT, "100", "General"));
			globalSettings.add(new GlobalSettings(DISABLE_BILLING_INVOICE_POPS, "no", "General"));
			globalSettings.add(new GlobalSettings(ENABLE_SOCIAL_WELFARE_MODULE, "no", "General"));
			globalSettings.add(new GlobalSettings(GENERATE_HOSPITAL_NUMBER_FOR_OLD_PATIENT, "no", "General"));
			globalSettings.add(new GlobalSettings(ENFORCE_REVISIT_BEFORE_BILLING_ACTIONS, "no", "General"));
			globalSettings.add(new GlobalSettings(ENABLE_AUTO_REVISIT, "yes", "General"));

			globalSettings.add(new GlobalSettings(ENABLE_OUT_PATIENT_DEPOSIT, "no", "Account"));
			globalSettings.add(new GlobalSettings(SALES_RECEIPT_COPY_COUNT, "2", "Account"));
			globalSettings.add(new GlobalSettings(DIRECTOR_OF_FINANCE, null, "Account"));
			globalSettings.add(new GlobalSettings(HIDE_BILL_DETAILS_FROM_RECEIPT, "no", "Account"));
			globalSettings.add(new GlobalSettings(ALLOW_PRICE_EDIT_FOR_WALK_P, "no", "Account"));
			globalSettings.add(new GlobalSettings(ENABLE_REGISTRATION_VALIDATION, "no", "Account"));
			globalSettings.add(new GlobalSettings(DEPOSIT_RECEIPT_COUNT, "2", "Account"));
			globalSettings.add(new GlobalSettings(ALWAYS_INCLUDE_PAY_NOW_BILLS, "no", "Account"));
			globalSettings.add(new GlobalSettings(DEPOSIT_ACCOUNT, null, "Account"));

			globalSettings.add(new GlobalSettings(ACTIVATE_STOCK_INVENTORY, "no", "Pharmacy"));
			globalSettings.add(new GlobalSettings(AUTO_UPDATE_SELLING_AFTER_RECEIVE_GOODS, "no", "Pharmacy"));
			globalSettings.add(new GlobalSettings(ENFORCE_PHARMACY_PATIENT_CATEGORY, "no", "Pharmacy"));

			globalSettings.add(new GlobalSettings(ACTIVATE_CLERKING, "no", "Clerking"));
			globalSettings.add(new GlobalSettings(CONSULTANT_EDIT_WINDOW, "1", "Clerking"));
			globalSettings.add(new GlobalSettings(CONSULTATION_FEE_EXPIRATION, "24", "Clerking"));

			globalSettings.add(new GlobalSettings(AUTO_APPLY_APPROVAL_CODE, "no", "Nhis"));

			globalSettings.add(new GlobalSettings(ENFORCE_SPECIMEN_ACK, "no", "Lab"));
			globalSettings.add(new GlobalSettings(ENABLE_SPECIMEN_ACK_DURING_COLLECTION, "no", "Lab"));

			globalSettings.add(new GlobalSettings(ENFORCE_SYSTEM_LOCATION, "no", "AppConfig"));
			globalSettings.add(new GlobalSettings(DEPARTMENT_CODE_PREFIX,
					DEPARTMENT_CODE_DEFAULT_PREFIX, "AppConfig"));
			globalSettings.add(new GlobalSettings(ADD_WARD_CODE_TO_BED_CODE, "no", "AppConfig"));
			globalSettings.add(new GlobalSettings(WARD_CODE_PREFIX, "WARD", "AppConfig"));
			globalSettings.add(new GlobalSettings(BED_CODE_PREFIX, "BED", "AppConfig"));
			globalSettings.add(new GlobalSettings(REVENUE_DEPARTMENT_CODE, "RV-DEPT", "AppConfig"));
			globalSettings.add(new GlobalSettings(SPECIALITY_UNIT_CODE, "SUNIT", "AppConfig"));
			globalSettings.add(new GlobalSettings(PRODUCT_SERVICE_CODE, "PSRV", "AppConfig"));
			globalSettings.add(new GlobalSettings(REVISIT_CODE_PREFIX, "VST", "AppConfig"));
			globalSettings.add(new GlobalSettings(INVOICE_NUMBER_CODE_PREFIX, "INV", "AppConfig"));
			globalSettings.add(new GlobalSettings(APP_CODE_START_NUMBER, "1000", "AppConfig"));
			globalSettings.add(new GlobalSettings(ADMISSION_CODE_PREFIX, "ADM", "AppConfig"));
			globalSettings.add(new GlobalSettings(DEPOSIT_RECEIPT_PREFIX, "DPR", "AppConfig"));
			globalSettings.add(new GlobalSettings(PHARMACY_RECEIPT_PREFIX, "PHR", "AppConfig"));
			globalSettings.add(new GlobalSettings(SERVICE_RECEIPT_PREFIX, "RPN", "AppConfig"));
			//
			globalSettings.add(new GlobalSettings(DRUG_REGISTER_CODE_PREFIX, "DRG", "AppConfig"));
			globalSettings.add(new GlobalSettings(DRUG_ORDER_CODE_PREFIX, "DGO", "AppConfig"));
			globalSettings.add(new GlobalSettings(PHARMACY_RECEIVED_GOODS_CODE_PREFIX, "PRG", "AppConfig"));
			globalSettings.add(new GlobalSettings(DRUG_REQUISITION_CODE_PREFIX, "RQT", "AppConfig"));
			globalSettings.add(new GlobalSettings(DRUG_ISSUANCE_CODE_PREFIX, "ISS", "AppConfig"));
			globalSettings.add(new GlobalSettings(ENABLE_NHIS_SERVICE_PRICE, "no",
					"AppConfig")); // use nhis service price to bill scheme patient
			// clerking
			globalSettings.add(new GlobalSettings(CLERKING_TEMPLATE_CODE_PREFIX, "TEMP", "AppConfig"));
			// cashier
			globalSettings.add(new GlobalSettings(CASHIER_SHIFT_CODE_PREFIX, "SHF", "AppConfig"));
			globalSettings.add(new GlobalSettings(CASHIER_SHIFT_COMPILATION_CODE_PREFIX, "COPL", "AppConfig"));
			// notifications
			globalSettings.add(
					new GlobalSettings(NOTIF_ENABLE_NOTIFICATION_CORE_PREFIX, "no", "NotificationConfig"));
			globalSettings.add(
					new GlobalSettings(NOTIF_ENABLE_EMAIL_NOTIFICATION_PREFIX, "no", "NotificationConfig"));
			globalSettings.add(
					new GlobalSettings(NOTIF_ENABLE_SMS_NOTIFICATION_PREFIX, "no", "NotificationConfig"));
			globalSettings.add(
					new GlobalSettings(NOTIF_ENABLE_WEB_NOTIFICATION_PREFIX, "no", "NotificationConfig"));
			globalSettings.add(
					new GlobalSettings(NOTIF_SET_ACCESS_LEVEL_PREFIX, "Module Level", "NotificationConfig"));
			globalSettingsRepository.saveAll(globalSettings);
			System.out.println("----- Seeding Global Settings Data -----");

		}
	}

	public void seedDepartmentCategory() {
		if (departmentCategoryRepository.findAll().isEmpty()) {
			List<DepartmentCategory> departmentCategories = new ArrayList<>();
			// dont change name property
			// department category names are used as DepartmentCategoryEnum values
			departmentCategories.add(new DepartmentCategory(Clinic.name(), "Clinic Department Category"));
			departmentCategories.add(new DepartmentCategory(Laboratory.name(), "Laboratory Department Category"));
			departmentCategories.add(new DepartmentCategory(Pharmacy.name(), "Pharmacy Department Category"));
			departmentCategories.add(new DepartmentCategory(Radiology.name(), "Radiology Department Category"));
			departmentCategories.add(new DepartmentCategory(Ward.name(), "Ward Department Category"));
			departmentCategories.add(new DepartmentCategory(Cash.name(), "Cash Point Department Category"));
			departmentCategories.add(new DepartmentCategory(Billing.name(), "Billing Point Department Category"));
			departmentCategories.add(new DepartmentCategory(NHIS.name(), "NHIS Department Category"));
			departmentCategories.add(
					new DepartmentCategory(Retainership.name(), "Retainership Department Category"));
			departmentCategories.add(new DepartmentCategory(Others.name(), "Other Department Category"));
			departmentCategoryRepository.saveAll(departmentCategories);
			System.out.println("----- Seeding Department Category Data. -----");

		}

	}

	public void seedDefaultRole() {
		if (this.seedPermissions.size() > 0) {
			Optional<Role> optionalRole = this.roleRepository.findByNameIgnoreCase(HmisConstant.SUPER_ADMIN_ROLE);
			if (!optionalRole.isPresent()) {
				try {
					Role role = new Role();
					role.setName(HmisConstant.SUPER_ADMIN_ROLE);
					role.setDescription("Default System Administrator Role");
					role.setPermissions(new HashSet<>(this.seedPermissions));
					this.seedRole = this.roleRepository.save(role);
					System.out.println("----- Seeding Default Role -----");
				} catch (Exception e) {
					System.out.println("----- Failed To Seed Default Role  -----");
				}

			}
		}

	}

	public void seedDefaultUser() {
		Optional<User> optionalUser = this.userRepository.findByUserNameIgnoreCase(HmisConstant.SUPER_ADMIN_USERNAME);
		if (optionalUser.isEmpty()) {
			try {

				if (this.seedRole != null && this.seedRole.getId() != null) {
					String userName = !ObjectUtils.isEmpty(this.username) ? this.username : HmisConstant.SUPER_ADMIN_USERNAME;
					String password = !ObjectUtils.isEmpty(this.password) ? this.password : "password@1";

					List<Role> roles = new ArrayList<>();
					roles.add(this.seedRole);
					User user = new User();
					user.setUserName(userName);
					user.setPassword(this.passwordEncoder.encode(password ));
					user.setRoles(roles);
					user.setFirstName("User");
					user.setLastName("System");
					user.setEmail("admin@email.com");
					user.setPhoneNumber("09000022200");
					user.setExpiryDate(LocalDate.MAX);
					this.userRepository.save(user);
					System.out.println("----- Seeding Default User -----");
				}
			} catch (Exception e) {
				System.out.println("----- Failed To Seeded Default User -----");
			}

		}
	}

	public void seedRevenueDepartmentType() {
		List<RevenueDepartmentType> revenueDepartmentTypeList = this.revenueDepartmentTypeRepository.findAll();
		if (revenueDepartmentTypeList.size() == 0) {

			RevenueDepartmentType departmentType1 = new RevenueDepartmentType("REVOLVING");
			RevenueDepartmentType departmentType2 = new RevenueDepartmentType("NON-REVOLVING");
			List<RevenueDepartmentType> list = new ArrayList<>();
			list.add(departmentType1);
			list.add(departmentType2);
			this.revenueDepartmentTypeRepository.saveAll(list);
			System.out.println("----- Seeding Revenue Department Types -----");
		}
	}

	public void seedPaymentMethod() {
		List<PaymentMethod> methods = this.paymentMethodRepository.findAll();
		if (methods.size() == 0) {
			List<PaymentMethod> list = new ArrayList<>();
			list.add(new PaymentMethod(PaymentMethodEnum.CASH.title()));
			list.add(new PaymentMethod(PaymentMethodEnum.POS.title()));
			list.add(new PaymentMethod(PaymentMethodEnum.ETF.title()));
			list.add(new PaymentMethod(PaymentMethodEnum.CHEQUE.title()));
			list.add(new PaymentMethod(PaymentMethodEnum.MOBILE_MONEY.title()));
			this.paymentMethodRepository.saveAll(list);
			System.out.println("----- Seeding Payment Methods -----");
		}
	}

	// seed default department && mobile money department
	// note:: after setting up defaults, to enable mobile money payment via app,
	// create a user assigned cash point role (username and password) will be used
	// by mobile app
	// to crate basic authorization in hospital model (username => getEncSecToken,
	// password => getEncSecToken ), to search bill and pay bill via mobile money
	public void seedMobileMoneyLocation() {
		if (!this.departmentRepository.findByName(DEFAULT_DEPARTMENT).isPresent()) {
			int code = HmisCodeDefaults.APP_CODE_START_NUMBER_DEFAULT + 1;
			Department department = new Department();
			department.setCode(DEPARTMENT_CODE_DEFAULT_PREFIX + code);
			department.setDepartmentCategory(this.findCashDepCategory());
			department.setName(DEFAULT_DEPARTMENT);
			department.setIsVisible(false);
			this.departmentRepository.save(department);
		}
		if (!this.departmentRepository.findByName(MOBILE_MONEY_DEPARTMENT).isPresent()) {
			int code2 = HmisCodeDefaults.APP_CODE_START_NUMBER_DEFAULT + 2;
			Department department = new Department();
			department.setCode(DEPARTMENT_CODE_DEFAULT_PREFIX + code2);
			department.setDepartmentCategory(this.findCashDepCategory());
			department.setName(MOBILE_MONEY_DEPARTMENT);
			department.setIsVisible(false);
			this.departmentRepository.save(department);
		}
	}

	private DepartmentCategory findCashDepCategory() {
		Optional<DepartmentCategory> optional = this.departmentCategoryRepository.findByName(Cash.name());
		return optional.orElseGet(
				() -> this.departmentCategoryRepository
						.save(new DepartmentCategory(
								Cash.name(), "Cash Point Department Category")));
	}

}
