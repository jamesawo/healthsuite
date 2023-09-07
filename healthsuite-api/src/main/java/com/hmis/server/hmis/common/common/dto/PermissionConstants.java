package com.hmis.server.hmis.common.common.dto;

import org.springframework.stereotype.Component;

@Component( "P" )
public class PermissionConstants {
	public static final String EMR_PATIENT_REGISTRATION = "Access_Patient_Registration";
	public static final String EMR_EDIT_PATIENT_DETAILS = "Access_Edit_Patient_Details";
	public static final String EMR_RECORD_VISIT = "Access_Record_Visit";
	public static final String EMR_PATIENT_FOLDER_MOVEMENT = "Access_Patient_Folder_Movement";
	public static final String EMR_PATIENT_CATEGORY_MANAGEMENT = "Access_Patient_Category_Management";
	public static final String EMR_EDIT_SCHEME_DETAILS = "Access_Edit_Scheme_Details";
	public static final String EMR_PATIENT_ADMISSION = "Access_Patient_Admission";
	public static final String EMR_ADMISSION_DETAILS = "Access_Admission_Details";
	public static final String EMR_WARD_TRANSFER = "Access_Ward_Transfer";
	public static final String EMR_CODING_AND_INDEXING = "Access_Coding_and_Indexing";
	public static final String EMR_APPOINTMENT_BOOKING = "Access_Appointment_Booking";
	public static final String EMR_APPOINTMENT_BOOKING_SETUP = "Access_Appointment_Booking_Setup";
	public static final String EMR_CLINICAL_ATTENDANCE = "Access_Clinical_Attendance";
	public static final String EMR_DEATH_REGISTER = "Access_Death_Register";
	public static final String EMR_PATIENT_DISCHARGE = "Access_Patient_Discharge";
	public static final String EMR_APPROVAL_CODE_OFFICE = "Access_Approval_Code_Office";
	public static final String EMR_BIRTH_REGISTER = "Access_Birth_Register";
	public static final String EMR_PATIENT_SEARCH_INDEX = "Access_Patient_Search_Index";
	public static final String EMR_EDIT_ORGANIZATION_DETAILS = "Access_Edit_Organization_Details";

	public static final String PHARMACY_DRUG_REGISTER = "Access_Drug_Register";
	public static final String PHARMACY_PHARMACY_BILLING = "Access_Pharmacy_Billing";
	public static final String PHARMACY_DRUG_ORDER = "Access_Drug_Order";
	public static final String PHARMACY_RECEIVE_GOODS_FROM_SUPPLIERS = "Access_Receive_Goods_From_Suppliers";
	public static final String PHARMACY_OUTLET_RECONCILIATION = "Access_Outlet_Reconciliation";
	public static final String PHARMACY_STORE_RECONCILIATION = "Access_Store_Reconciliation";
	public static final String PHARMACY_TO_STORE_REQUISITION = "Access_To_Store_Requisition";
	public static final String PHARMACY_TO_OUTLET_REQUISITION = "Access_To_Outlet_Requisition";
	public static final String PHARMACY_STORE_ISSUANCE = "Access_Store_Issuance";
	public static final String PHARMACY_OUTLET_ISSUANCE = "Access_Outlet_Issuance";
	public static final String PHARMACY_DRUG_DISPENSING = "Access_Drug_Dispensing";
	public static final String PHARMACY_BATCH_DRUG_DISPENSING = "Access_Batch_Drug_Dispensing";
	public static final String PHARMACY_DRUG_WITHDRAWALS = "Access_Drug_Withdrawals";
	public static final String PHARMACY_PATIENT_DRUG_RETURN = "Access_Patient_Drug_Return";
	public static final String PHARMACY_DRUG_RETURN_NOTE = "Access_Drug_Return_Note";
	public static final String PHARMACY_PRIVATE_WING_PHARMACY_BILLING = "Access_Private_Wing_Pharmacy_Billing";
	public static final String PHARMACY_EXECUTIVE_WING_PHARMACY_BILLING = "Access_Executive_Wing_Pharmacy_Billing";
	public static final String PHARMACY_PHARMACY_NOTIFICATIONS = "Access_Pharmacy_Notifications";

	public static final String BILLING_PATIENT_BILLING = "Access_Patient_Billing";
	public static final String BILLING_PATIENT_SCHEME_BILLING = "Access_Patient_Scheme_Billing";
	public static final String BILLING_BILL_ADJUSTMENT = "Access_Bill_Adjustment";
	public static final String BILLING_PAYMENT_MANAGER = "Access_Payment_Manager";
	public static final String BILLING_PATIENT_DEPOSIT = "Access_Patient_Deposit";
	public static final String BILLING_BILL_WAIVERS = "Access_Bill_Waivers";
	public static final String BILLING_RECEIPT_CANCELLATION = "Access_Receipt_Cancellation";
	public static final String BILLING_HMO_BILLING = "Access_Hmo_Billing";
	public static final String BILLING_RET_ORGANIZATION_BILLING = "Access_Ret_Organization_Billing";
	public static final String BILLING_SCHEME_BILL_ADJUSTMENT = "Access_Scheme_Bill_Adjustment";
	public static final String BILLING_REFUND = "Access_Refund";
	public static final String BILLING_PATIENT_DEPOSIT_TRANSFER = "Access_Patient_Deposit_Transfer";

	public static final String SHIFTMANAGEMENT_SHIFT_MANAGER = "Access_Shift_Manager";
	public static final String SHIFTMANAGEMENT_CASHIER_SHIFT_REPORT = "Access_Cashier_Shift_Report";
	public static final String SHIFTMANAGEMENT_FUND_RECEPTION_ACKNOWLEDGEMENT = "Access_Fund_Reception_Acknowledgement";
	public static final String SHIFTMANAGEMENT_SHIFT_COMPILATION = "Access_Shift_Compilation";
	public static final String SHIFTMANAGEMENT_ALL_SHIFT_PER_DAY_REPORT = "Access_All_Shift_Per_Day_Report";

	public static final String SETTINGS_USER_MANAGER = "Access_User_Manager";
	public static final String SETTINGS_ROLE_MANAGER = "Access_Role_Manager";
	public static final String SETTINGS_SEED_DATA_SETUP = "Access_Seed_Data_Setup";
	public static final String SETTINGS_GLOBAL_SETTINGS = "Access_Global_Settings";
	public static final String SETTINGS_LOCATION_SETTINGS = "Access_Location_Settings";
	public static final String SETTINGS_PASSWORD_RESET = "Access_Password_Reset";
	public static final String SETTINGS_NHIS_SCHEME_SUSPENSION = "Access_Nhis_Scheme_Suspension";
	public static final String SETTINGS_RET_SCHEME_SUSPENSION = "Access_Ret_Scheme_Suspension";
	public static final String SETTINGS_RIGHT_SURVEILLANCE = "Access_Right_Surveillance";

	public static final String NURSES_VITAL_SIGNS_CAPTURE = "Access_Vital_Signs_Capture";
	public static final String NURSES_VITAL_SIGNS_TREND = "Access_Vital_Signs_Trend";
	public static final String NURSES_NURSING_NOTE = "Access_Nursing_Note";
	public static final String NURSES_NURSING_CARE = "Access_Nursing_Care";
	public static final String NURSES_DRUG_ADMINISTRATION = "Access_Drug_Administration";
	public static final String NURSES_NURSE_PATIENT_EFOLDER = "Access_Nurse_Patient_eFolder";
	public static final String NURSES_NURSE_ANTENATAL_BOOKING = "Access_Nurse_Antenatal_Booking";
	public static final String NURSES_OBSTETRICS_HISTORY = "Access_Obstetrics_History";
	public static final String NURSES_ANC_BOOKING = "Access_Anc_Booking";
	public static final String NURSES_ANC_CARD_NOTE = "Access_Anc_Card_Note";
	public static final String NURSES_VISUAL_ACUITY = "Access_Nurse_Visual_Acuity";

	public static final String DOCTOR_GENERAL_CLERKING_DESK = "Access_General_Clerking_Desk";
	public static final String DOCTOR_ANC_CLERKING_DESK = "Access_Anc_Clerking_Desk";
	public static final String DOCTOR_DENTAL_CLERKING_DESK = "Access_Dental_Clerking_Desk";
	public static final String DOCTOR_PHYSIO_CLERKING_DESK = "Access_Physio_Clerking_Desk";
	public static final String DOCTOR_PEDIATRICS_CLERKING_DESK = "Access_Pediatrics_Clerking_Desk";
	public static final String DOCTOR_DOCTOR_PATIENT_EFOLDER = "Access_Doctor_Patient_eFolder";
	public static final String DOCTOR_INPATIENT_CLERKING = "Access_Inpatient_Clerking";
	public static final String DOCTOR_PATIENT_FILE_UPLOAD = "Access_Patient_File_Upload";
	public static final String DOCTOR_DOCTOR_ANTENATAL_BOOKING = "Access_Doctor_Antenatal_Booking";
	public static final String DOCTOR_GENERAL_OUTPATIENT_DESK = "Access_General_Outpatient_Desk";
	public static final String DOCTOR_OPHTHALMOLOGY_CLERKING = "Access_Ophthalmology_Clerking";
	public static final String DOCTOR_SURGICAL_OUTPATIENT_CONSULTATION = "Access_Surgical_Outpatient_Consultation";
	public static final String DOCTOR_HOP_CLINIC_CONSULTATION = "Access_Hop_Clinic_Consultation";
	public static final String DOCTOR_PSYCHIATRY_CONSULTATION = "Access_Psychiatry_Consultation";

	public static final String LAB_SPECIMEN_COLLECTION = "Access_Specimen_Collection";
	public static final String LAB_SPECIMEN_COLLECTION_ACKNOWLEDGEMENT = "Access_Specimen_Collection_Acknowledgement";
	public static final String LAB_LAB_REQUEST_TRACKER = "Access_Lab_Request_Tracker";
	public static final String LAB_CHEMICAL_LABORATORY = "Access_Chemical_Laboratory";
	public static final String LAB_ANATOMICAL_LABORATORY = "Access_Anatomical_Laboratory";
	public static final String LAB_HAEMATOLOGY = "Access_Haematology";
	public static final String LAB_Billing = "Access_Lab_Billing";
	public static final String LAB_Parameter_Registration = "Access_Lab_Parameter_Registration";
	public static final String LAB_RePush_Samples = "Access_Lab_Re_Push_Sample";
	// MEDICAL LABORATORY
	public static final String LAB_MEDICAL_MICROBIOLOGY = "Access_Medical_Microbiology";
	public static final String LAB_MEDICAL_PARAM_SETUP = "Access_Medical_Param_Setup";
	public static final String LAB_MEDICAL_RESULT_VIEW = "Access_Medical_Result_View";
	public static final String LAB_MEDICAL_ORIGINAL_RESULT_VIEW = "Access_Medical_Original_Result_View";
	public static final String LAB_MEDICAL_PROVISIONAL_RESULT_VIEW = "Access_Medical_Provisional_Result";
	public static final String LAB_MEDICAL_PROVISIONAL_RESULT_CONTROL = "Access_Medical_Provisional_Result_Control";
	public static final String LAB_MEDICAL_RESULT_PREPARATION = "Access_Medical_Result_Preparation";
	public static final String LAB_MEDICAL_RESULT_VERIFICATION = "Access_Medical_Result_Verification";
	public static final String LAB_MEDICAL_PATHOLOGIST_VERIFICATION = "'Access_Medical_Pathologist_Verification'";
	// MICROBIOLOGY
	public static final String LAB_MICROBIOLOGY_MAIN = "Access_Microbiology_Main";
	public static final String LAB_MICROBIOLOGY_PARAM_SETUP = "Access_Microbiology_Param_Setup";
	public static final String LAB_MICROBIOLOGY_RESULT_VIEW = "Access_Microbiology_Result_View";
	public static final String LAB_MICROBIOLOGY_RESULT_CONTROL = "Access_Microbiology_Result_Control";
	// MICROBIOLOGY SEROLOGY
	public static final String LAB_MICROBIOLOGY_SEROLOGY_MAIN = "Access_Microbiology_Serology_Main";
	public static final String LAB_MICROBIOLOGY_SEROLOGY_RESULT_PREP = "Access_Microbiology_Serology_Result_Prep";
	public static final String LAB_MICROBIOLOGY_SEROLOGY_RESULT_VERIFY = "Access_Microbiology_Serology_Result_Verify";
	public static final String LAB_MICROBIOLOGY_SEROLOGY_RESULT_PATH_VERIFY = "Access_Microbiology_Serology_Path_Result_Verify";
	// MICROBIOLOGY PARASITOLOGY
	public static final String LAB_MICROBIOLOGY_PARASITOLOGY_MAIN = "Access_Microbiology_Parasitology_Main";
	public static final String LAB_MICROBIOLOGY_PARASITOLOGY_RESULT_PREP = "Access_Microbiology_Parasitology_Result_Prep";
	public static final String LAB_MICROBIOLOGY_PARASITOLOGY_RESULT_VERIFY = "Access_Microbiology_Parasitology_Result_Verify";
	public static final String LAB_MICROBIOLOGY_PARASITOLOGY_RESULT_PATH_VERIFY = "Access_Microbiology_Parasitology_Path_Result_Verify";

	public static final String RADIOLOGY_RADIOLOGY_RESULT_PREPARATION = "Access_Radiology_Result_Preparation";
	public static final String RADIOLOGY_RADIOLOGY_RESULT_VERIFICATION = "Access_Radiology_Result_Verification";
	public static final String RADIOLOGY_RADIOLOGY_RESULT_VIEW = "Access_Radiology_Result_View";
	public static final String RADIOLOGY_RADIOLOGY_PROVISIONAL_RESULT_VIEW = "Access_Radiology_Provisional_Result_View";
	public static final String RADIOLOGY_RADIOLOGY_BILLING = "Access_Radiology_Billing";
	public static final String RADIOLOGY_RADIOLOGY_REQUEST_TRACKER = "Access_Radiology_Request_Tracker";
	public static final String RADIOLOGY_RADIOLOGY_DRAFT = "Access_Radiology_Draft";

	public static final String OTHER_REQUEST_TRACKER = "Access_Request_Tracker";
	public static final String OTHER_VENDOR_MANAGEMENT = "Access_Vendor_Management";
	public static final String OTHER_SERVICE_REGISTER = "Access_Service_Register";
	public static final String OTHER_DASHBOARD = "Access_Other_Dashboard";
	public static final String OTHER_SCHEME_SERVICE_PRICE = "Access_Other_Scheme_Service_Price";

	public static final String Settlement_Service_Provider_Invoice = "Access_Service_Provider_Invoice";
	public static final String Settlement_Settlement_Advice = "Access_Settlement_Advice";

	public static final String Reports_Registered_Patient = "Access_Reports_Registered_Patient";
	public static final String Reports_Appointment_Booking = "Access_Reports_Appointment_Booking";
	public static final String Reports_Admission_Register = "Access_Reports_Admission_Register";
	public static final String Reports_All_Shift_Per_Day = "Access_Reports_All_Shift_Per_Day";
	public static final String Reports_Daily_Cash_Collection = "Access_Reports_Daily_Cash_Collection";
	public static final String Reports_Cancelled_Receipts = "Access_Reports_Cancelled_Receipts";
	public static final String Reports_Payment_Receipt_Report = "Access_Reports_Payment_Receipt_Report";
	public static final String Reports_Stock_Balance_Per_Outlet = "Access_Reports_Stock_Balance_Per_Outlet";
	public static final String Reports_Outlet_Activity_Report = "Access_Reports_Outlet_Activity_Report";
	public static final String Reports_Patient_E_Folder = "Access_Reports_Patient_E_Folder";
	public static final String Reports_Emr_Patient_Interim_Invoice = "Access_Reports_Emr_Patient_Interim_Invoice";
	public static final String Reports_Other_Report_Main = "Access_Reports_Other_Service_Main";
	public static final String Reports_Other_Service_Entire_Charge = "Access_Reports_Other_Service_Entire_Charge";

	public static final String Reports_Account_Scheme_Consumption_Report = "Access_Reports_Scheme_Consumption_Report";

}
