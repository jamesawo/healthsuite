package com.hmis.server.hmis.common.constant;

public class HmisConstant {
	public static final String STATUS = "status";
	public static final String MESSAGE = "message";
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	public static final String DATA = "data";
	public static final String MODULE = "module";
	public static final String MAPPING = "mapping";
	public static final String SAVED_SUCCESSFULLY = "Saved Successfully";
	public static final String SUCCESS_MESSAGE = "Action Successful.";
	public static final String UPDATED_MESSAGE = "Updated Successful.";
	public static final String ERROR_MESSAGE = "Error Occurred.";
	public static final String STATUS_500 = "Internal Server Error";
	public static final String STATUS_502 = "Bad Gateway";
	public static final String STATUS_503 = "Service Unavailable";
	public static final String STATUS_404 = "Not Found";
	public static final String STATUS_405 = "Method Not Allowed";
	public static final String STATUS_403 = "Forbidden";
	public static final String STATUS_401 = "Unauthorized";
	public static final String STATUS_400 = "Bad Request";
	public static final String STATUS_409 = "Resource Conflict, Try Another Name.";
	public static final String STATUS_200 = "OK";
	public static final String STATUS_201 = "Created";
	public static final String STATUS_202 = "Accepted";

	public static final int R_CONFLICT = 409;
	public static final int OK_CODE = 200;
	public static final int SERVER_ERROR = 500;
	public static final int BAD_REQUEST = 400;
	public static final int NOT_FOUND = 400;


	public static final String API_VERSION_1 = "v1";
	public static final String API_PREFIX = "/api/v1/";
	public static final String ACCOUNT_EXPIRED = "User account has expired";
	public static final String LOGGER_IS_ATTEMPTING = " Is attempting to: ";
	public static final String LOGGER_PASSED = " Attempt was successful ";
	public static final String LOGGER_FAILED = " Attempt has failed ";
	public static final String LOGGER_TYPE_ATTEMPT = "attempt";
	public static final String LOGGER_TYPE_FAILED = "failed";
	public static final String LOGGER_TYPE_PASSED = "passed";
	public static final String LOGGER_TYPE_DUPLICATE = "duplicate";
	public static final String ENTITY_EXIST = " Entity Already Exist.";
	public static final String DEBUG = "debug";
	public static final String INFO = "info";
	public static final String SUPER_ADMIN_USERNAME = "superadmin";
	public static final String SUPER_ADMIN_ROLE = "SuperAdmin";
	public static final String SUPPORT_ROLE = "support";
	public static final String PMO_ROLE_PREFIX = "pmo";
	public static final String DEFAULT_DATA_SEED_SUCCESS = "Default Data Seed Successful.";
	public static final String DEFAULT_DATA_SEED_FAILED = "Default Data Seed Failed.";
	public static final String DEFAULT_DATA_SEED_EXIST = "Some Data Already Exist.";
	public static final String WARD_DEP_CATEGORY_NAME = "WARD";
	public static final String DOCTOR_PERMISSION_GROUP = "Doctor";
	public static final String EMPTY_FILE = "EMPTY ROWS";
	public static final String NO_SERVICE_BILL_ITEM_FOUND = "CANNOT PAY BILL WITHOUT SERVICE BILL ITEMS.";
	public static final String NO_DRUG_BILL_ITEM_FOUND = "CANNOT PAY BILL WITHOUT DRUG BILL ITEMS.";
	public static final String INVALID_PAYMENT_TYPE_FOR = "PAYMENT TYPE-FOR IS INVALID. SHOULD BE SERVICE OR DRUG";
	public static final String PROVIDE_WALK_IN_PATIENT = "WalkIn Patient Detail is Required";
	public static final String CANNOT_ACKNOWLEDGE_OPEN_SHIFT = "Cannot Acknowledge Running Cashier Shift";
	public static final String SHIFT_IS_ALREADY_RECONCILED = "CASHIER SHIFT IS ALREADY RECONCILED";
	public static final String ALL_SHIFT_MUST_BE_RECONCILED = "ALL SHIFT MUST BE RECONCILED FIRST.";
	public static final String MOBILE_MONEY_DEPARTMENT = "MOBILE MONEY POINT";
	public static final String MOBILE_MONEY_DEPARTMENT_CODE = "DPMOB";
	public static final String DEFAULT_DEPARTMENT = "DEFAULT";
	public static final String NIL = "NIL";
	public static final String HMO = "HMO NAME: ";
	public static final String REVENUE_DEPARTMENT = "REVENUE DEPARTMENT NAME: ";
	public static final String SERVICE_DEPARTMENT = "SERVICE DEPARTMENT NAME: ";
	public static final String LAB_REPORT_FOOTER_TEXT = "PLEASE NOTE: ANY ALTERATION ON THIS REPORT RENDERS IT INVALID.";
	public static final String LAB_REPORT_NHIS_PRICE_FOOTER = "PLEASE NOTE: DYNAMIC NHIS SERVICE PRICE IS ENABLED. ";

}
