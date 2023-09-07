package com.hmis.server.hmis.common.constant;

public class HmisExceptionMessage {

	public static final String REV_DEPARTMENT_TYPE = "Revenue Department Type";
	public static final String MAP_MODEL_TO_DTO_ERROR = "Cannot Map Model To Dto From: ";
	public static final String MAP_DTO_TO_MODEL_ERROR = "Cannot Map Dto To Model From: ";
	public static final String IS_EXIST_CHECK_ERROR = "Cannot Check Is Exist Without: ";
	public static final String ID_NOT_PROVIDED = "No Id Provided For ";
	public static final String NOTHING_FOUND = "No Record Found";
	public static final String REGISTER_NOT_ALLOWED =
			"This route is not allowed for new user registration, use User Manager " + "('/userManager/user/create') to create a new user";
	public static final String INVALID_RECEIPT = "Invalid Receipt";

	public static final String EXCEPTION_NO_GLOBAL_SETTING =
			"Global setting is empty, cannot find matching validation for patient registration";
	public static final String EXCEPTION_NO_RECEIPT =
			"Please provide a valid payment receipt before continuing";
	public static final String EXCEPTION_NO_PATIENT_CATEGORY = "Provide patient category first";
	public static final String EXCEPTION_FAILED = "Failed. Unknown error occurred";
	public static final String EXCEPTION_NHIS_RECORD =
			"Provide scheme or insurance details before continuing";
	public static final String EXCEPTION_DUPLICATE_PN = "Patient Number is already allocated";
	public static final String EXCEPTION_INTERNAL_ERROR = "Something has gone wrong.";
	public static final String EXCEPTION_BAD_REQUEST =
			"Invalid request object, something is missing. Hint: ";


	public static final String EXCEPTION_VALIDATION_FAILED = " Validation Failed! ";
	public static final String DUPLICATE_APPOINTMENT = "Similar appointment already exist!";
	public static final String CONSULTANT_MISMATCH_SPECIALITY =
			"Consultant is not assigned to speciality unit";
	public static final String CONSULTANT_LIMIT_REACHED = "Consultant is fully booked, try again later";
	public static final String PATIENT_IS_REQUIRED = "PATIENT IS REQUIRED";
	public static final String FEATURE_IS_NOT_ENABLED = "THIS FEATURE IS NOT ENABLED";
}
