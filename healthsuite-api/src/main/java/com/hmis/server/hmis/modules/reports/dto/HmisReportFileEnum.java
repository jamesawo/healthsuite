package com.hmis.server.hmis.modules.reports.dto;

import java.io.InputStream;

import com.hmis.server.hmis.common.common.service.HmisUtilService;

public enum HmisReportFileEnum {
	EMR_PATIENT_REGISTER_REPORT {
		final String path = "/reports/emr/patient_register_report";

		@Override
		public String filePath() {
			return SRC + path;
		}

		@Override
		public InputStream absoluteFilePath(HmisUtilService util) {
			return util.getAbsoluteFilePath(path + EXT);
			/*
			 * if ( util.isDevOrProdProfile() ) {
			 * return SRC + path + EXT;
			 * }
			 * else {
			 * return util.getAbsoluteFilePath( path + EXT );
			 * }
			 */
		}
	},
	EMR_PATIENT_DISCHARGE_GATE_PASS {
		final String path = "/reports/emr/patient_discharge_gate_pass";

		@Override
		public String filePath() {
			return SRC + path;
		}

		@Override
		public InputStream absoluteFilePath(HmisUtilService util) {
			return util.getAbsoluteFilePath(path + EXT);
			/*
			 * if ( util.isDevOrProdProfile() ) {
			 * return SRC + path + EXT;
			 * }
			 * else {
			 * return util.getAbsoluteFilePath( path + EXT );
			 * }
			 */
		}
	},
	EMR_PATIENT_INTERIM_INVOICE {
		final String path = "/reports/emr/patient_interim_invoice";

		@Override
		public String filePath() {
			return SRC + path;
		}

		@Override
		public InputStream absoluteFilePath(HmisUtilService util) {
			return util.getAbsoluteFilePath(path + EXT);
			/*
			 * if ( util.isDevOrProdProfile() ) {
			 * return SRC + path + EXT;
			 * }
			 * else {
			 * return util.getAbsoluteFilePath( path + EXT );
			 * }
			 */
		}
	},
	ACC_DAILY_CASH_COLLECTION {
		final String path = "/reports/account/daily_cash_collection";

		@Override
		public String filePath() {
			return SRC + path;
		}

		@Override
		public InputStream absoluteFilePath(HmisUtilService util) {
			return util.getAbsoluteFilePath(path + EXT);
			/*
			 * if ( util.isDevOrProdProfile() ) {
			 * return SRC + path + EXT;
			 * }
			 * else {
			 * return util.getAbsoluteFilePath( path + EXT );
			 * }
			 */
		}
	},
	SHIFT_CASHIER_REPORT_SUMMARY {
		final String path = "/reports/shift/cashier_shift_summarized";

		@Override
		public String filePath() {
			return SRC + path;
		}

		@Override
		public InputStream absoluteFilePath(HmisUtilService util) {
			return util.getAbsoluteFilePath(path + EXT);
			/*
			 * if ( util.isDevOrProdProfile() ) {
			 * return SRC + path + EXT;
			 * }
			 * else {
			 * return util.getAbsoluteFilePath( path + EXT );
			 * }
			 */
		}
	},
	SHIFT_CASHIER_REPORT_DETAILED {
		final String path = "/reports/shift/cashier_shift_detailed";

		@Override
		public String filePath() {
			return SRC + path;
		}

		@Override
		public InputStream absoluteFilePath(HmisUtilService util) {
			return util.getAbsoluteFilePath(path + EXT);
			/*
			 * if ( util.isDevOrProdProfile() ) {
			 * return SRC + path + EXT;
			 * }
			 * else {
			 * return util.getAbsoluteFilePath( path + EXT );
			 * }
			 */
		}
	},
	SHIFT_CASHIER_ACKNOWLEDGEMENT {
		final String path = "/reports/shift/cashier_shift_acknowledgement";

		@Override
		public String filePath() {
			return SRC + path;
		}

		@Override
		public InputStream absoluteFilePath(HmisUtilService util) {
			return util.getAbsoluteFilePath(path + EXT);
			/*
			 * if ( util.isDevOrProdProfile() ) {
			 * return SRC + path + EXT;
			 * }
			 * else {
			 * return util.getAbsoluteFilePath( path + EXT );
			 * }
			 */
		}
	},
	SHIFT_CASHIER_COMPILATION {
		final String path = "/reports/shift/cashier_shift_compilation";

		@Override
		public String filePath() {
			return SRC + path;
		}

		@Override
		public InputStream absoluteFilePath(HmisUtilService util) {
			return util.getAbsoluteFilePath(path + EXT);
			/*
			 * if ( util.isDevOrProdProfile() ) {
			 * return SRC + path + EXT;
			 * }
			 * else {
			 * return util.getAbsoluteFilePath( path + EXT );
			 * }
			 */
		}
	},
	SHIFT_CASHIER_ALL_SHIFT_PER_DAY {
		final String path = "/reports/shift/cashier_all_shift_per_day";

		@Override
		public String filePath() {
			return SRC + path;
		}

		@Override
		public InputStream absoluteFilePath(HmisUtilService util) {
			return util.getAbsoluteFilePath(path + EXT);
			/*
			 * if ( util.isDevOrProdProfile() ) {
			 * return SRC + path + EXT;
			 * }
			 * else {
			 * return util.getAbsoluteFilePath( path + EXT );
			 * }
			 */
		}
	},
	// clerking reports
	CLERK_DRUG_ADMINISTRATION {
		final String path = "/reports/clerk/clerk_drug_administration";

		@Override
		public String filePath() {
			return SRC + path;
		}

		@Override
		public InputStream absoluteFilePath(HmisUtilService util) {
			return util.getAbsoluteFilePath(path + EXT);
			/*
			 * if ( util.isDevOrProdProfile() ) {
			 * return SRC + path + EXT;
			 * }
			 * else {
			 * return util.getAbsoluteFilePath( path + EXT );
			 * }
			 */
		}
	},
	CLERK_DRUG_PRESCRIPTION {
		final String path = "/reports/clerk/clerk_drug_prescription";

		@Override
		public String filePath() {
			return SRC + path;
		}

		@Override
		public InputStream absoluteFilePath(HmisUtilService util) {
			return util.getAbsoluteFilePath(path + EXT);
			/*
			 * if ( util.isDevOrProdProfile() ) {
			 * return SRC + path + EXT;
			 * }
			 * else {
			 * return util.getAbsoluteFilePath( path + EXT );
			 * }
			 */
		}
	},
	CLERK_OBSTETRIC_HISTORY {
		final String path = "/reports/clerk/clerk_obstetrics_history";

		@Override
		public String filePath() {
			return SRC + path;
		}

		@Override
		public InputStream absoluteFilePath(HmisUtilService util) {
			return util.getAbsoluteFilePath(path + EXT);
			/*
			 * if ( util.isDevOrProdProfile() ) {
			 * return SRC + path + EXT;
			 * }
			 * else {
			 * return util.getAbsoluteFilePath( path + EXT );
			 * }
			 */
		}
	},
	CLERK_GENERAL_CONSULTATION {
		final String path = "/reports/clerk/clerk_general_consultation";

		@Override
		public String filePath() {
			return SRC + path;
		}

		@Override
		public InputStream absoluteFilePath(HmisUtilService util) {
			return util.getAbsoluteFilePath(path + EXT);
			/*
			 * if ( util.isDevOrProdProfile() ) {
			 * return SRC + path + EXT;
			 * }
			 * else {
			 * return util.getAbsoluteFilePath( path + EXT );
			 * }
			 */
		}
	},
	CLERK_GENERAL_OUT_PATIENT_CONSULTATION {
		final String path = "/reports/clerk/clerk_general_outpatient_consultation";

		@Override
		public String filePath() {
			return SRC + path;
		}

		@Override
		public InputStream absoluteFilePath(HmisUtilService util) {
			return util.getAbsoluteFilePath(path + EXT);
			/*
			 * if ( util.isDevOrProdProfile() ) {
			 * return SRC + path + EXT;
			 * }
			 * else {
			 * return util.getAbsoluteFilePath( path + EXT );
			 * }
			 */
		}
	},
	CLERK_LAB_REQUEST {
		final String path = "/reports/clerk/clerk_lab_request";

		@Override
		public String filePath() {
			return SRC + path;
		}

		@Override
		public InputStream absoluteFilePath(HmisUtilService util) {
			return util.getAbsoluteFilePath(path + EXT);
			/*
			 * if ( util.isDevOrProdProfile() ) {
			 * return SRC + path + EXT;
			 * }
			 * else {
			 * return util.getAbsoluteFilePath( path + EXT );
			 * }
			 */
		}
	},
	CLERK_LAB_RESULT {
		final String path = "/reports/clerk/clerk_lab_result";

		@Override
		public String filePath() {
			return SRC + path;
		}

		@Override
		public InputStream absoluteFilePath(HmisUtilService util) {
			return util.getAbsoluteFilePath(path + EXT);
			/*
			 * if ( util.isDevOrProdProfile() ) {
			 * return SRC + path + EXT;
			 * }
			 * else {
			 * return util.getAbsoluteFilePath( path + EXT );
			 * }
			 */
		}
	},
	CLERK_LAB_MICROBIOLOGY_RESULT {
		final String path = "/reports/clerk/clerk_lab_microbiology_result";

		@Override
		public String filePath() {
			return SRC + path;
		}

		@Override
		public InputStream absoluteFilePath(HmisUtilService util) {
			return util.getAbsoluteFilePath(path + EXT);
			/*
			 * if ( util.isDevOrProdProfile() ) {
			 * return SRC + path + EXT;
			 * }
			 * else {
			 * return util.getAbsoluteFilePath( path + EXT );
			 * }
			 */
		}
	},
	CLERK_PATIENT_NOTES {
		final String path = "/reports/clerk/clerk_notes";

		@Override
		public String filePath() {
			return SRC + path;
		}

		@Override
		public InputStream absoluteFilePath(HmisUtilService util) {
			return util.getAbsoluteFilePath(path + EXT);
			/*
			 * if ( util.isDevOrProdProfile() ) {
			 * return SRC + path + EXT;
			 * }
			 * else {
			 * return util.getAbsoluteFilePath( path + EXT );
			 * }
			 */
		}
	},
	CLERK_PATIENT_E_FOLDER {
		final String path = "/reports/clerk/clerk_patient_e_folder";

		@Override
		public String filePath() {
			return SRC + path;
		}

		@Override
		public InputStream absoluteFilePath(HmisUtilService util) {
			return util.getAbsoluteFilePath(path + EXT);
			/*
			 * if ( util.isDevOrProdProfile() ) {
			 * return SRC + path + EXT;
			 * }
			 * else {
			 * return util.getAbsoluteFilePath( path + EXT );
			 * }
			 */
		}
	},
	CLERK_RADIOLOGY_REQUEST {
		final String path = "/reports/clerk/clerk_radiology_request";

		@Override
		public String filePath() {
			return SRC + path;
		}

		@Override
		public InputStream absoluteFilePath(HmisUtilService util) {
			return util.getAbsoluteFilePath(path + EXT);
			/*
			 * if ( util.isDevOrProdProfile() ) {
			 * return SRC + path + EXT;
			 * }
			 * else {
			 * return util.getAbsoluteFilePath( path + EXT );
			 * }
			 */
		}
	},
	CLERK_RADIOLOGY_RESULT {
		final String path = "/reports/clerk/clerk_radiology_result";

		@Override
		public String filePath() {
			return SRC + path;
		}

		@Override
		public InputStream absoluteFilePath(HmisUtilService util) {
			return util.getAbsoluteFilePath(path + EXT);
			/*
			 * if ( util.isDevOrProdProfile() ) {
			 * return SRC + path + EXT;
			 * }
			 * else {
			 * return util.getAbsoluteFilePath( path + EXT );
			 * }
			 */
		}
	},
	CLERK_CLINIC_TRANSFER {
		final String path = "/reports/clerk/clerk_clinic_transfer";

		@Override
		public String filePath() {
			return SRC + path;
		}

		@Override
		public InputStream absoluteFilePath(HmisUtilService util) {
			return util.getAbsoluteFilePath(path + EXT);
			/*
			 * if ( util.isDevOrProdProfile() ) {
			 * return SRC + path + EXT;
			 * }
			 * else {
			 * return util.getAbsoluteFilePath( path + EXT );
			 * }
			 */
		}
	},
	CLERK_WARD_TRANSFER {
		final String path = "/reports/clerk/clerk_ward_transfer";

		@Override
		public String filePath() {
			return SRC + path;
		}

		@Override
		public InputStream absoluteFilePath(HmisUtilService util) {
			return util.getAbsoluteFilePath(path + EXT);
			/*
			 * if ( util.isDevOrProdProfile() ) {
			 * return SRC + path + EXT;
			 * }
			 * else {
			 * return util.getAbsoluteFilePath( path + EXT );
			 * }
			 */
		}
	},
	CLERK_VITAL_SIGN {
		final String path = "/reports/clerk/clerk_vital_sign";

		@Override
		public String filePath() {
			return SRC + path;
		}

		@Override
		public InputStream absoluteFilePath(HmisUtilService util) {
			return util.getAbsoluteFilePath(path + EXT);
			/*
			 * if ( util.isDevOrProdProfile() ) {
			 * return SRC + path + EXT;
			 * }
			 * else {
			 * return util.getAbsoluteFilePath( path + EXT );
			 * }
			 */
		}
	},
	// nurse
	NURSE_PATIENT_FLUID_BALANCE {
		final String path = "/reports/nurse/nurse_patient_fluid_balance";

		@Override
		public String filePath() {
			return SRC + path;
		}

		@Override
		public InputStream absoluteFilePath(HmisUtilService util) {
			return util.getAbsoluteFilePath(path + EXT);
			/*
			 * if ( util.isDevOrProdProfile() ) {
			 * return SRC + path + EXT;
			 * }
			 * else {
			 * return util.getAbsoluteFilePath( path + EXT );
			 * }
			 */
		}
	},
	// other reports
	SERVICE_CHARGE_LIST {
		final String path = "/reports/other/service_charge_list_report";

		@Override
		public String filePath() {
			return SRC + path;
		}

		@Override
		public InputStream absoluteFilePath(HmisUtilService util) {
			return util.getAbsoluteFilePath(path + EXT);
			/*
			 * if ( util.isDevOrProdProfile() ) {
			 * return SRC + path + EXT;
			 * }
			 * else {
			 * return util.getAbsoluteFilePath( path + EXT );
			 * }
			 */
		}
	},
	SERVICE_CHARGE_LIST_SUB {
		final String path = "/reports/other/service_charge_list_sub_report";

		@Override
		public String filePath() {
			return SRC + path;
		}

		@Override
		public InputStream absoluteFilePath(HmisUtilService util) {
			return util.getAbsoluteFilePath(path + EXT);
			/*
			 * if ( util.isDevOrProdProfile() ) {
			 * return SRC + path + EXT;
			 * }
			 * else {
			 * return util.getAbsoluteFilePath( path + EXT );
			 * }
			 */
		}
	},
	// without grouping
	ACC_SCHEME_CONSUMPTION_SUMMARY {
		final String path = "/reports/account/scheme_consumption_report/scheme_consumption_summary";

		@Override
		public String filePath() {
			return SRC + path;
		}

		@Override
		public InputStream absoluteFilePath(HmisUtilService util) {
			return util.getAbsoluteFilePath(path + EXT);
			/*
			 * if ( util.isDevOrProdProfile() ) {
			 * return SRC + path + EXT;
			 * }
			 * else {
			 * return util.getAbsoluteFilePath( path + EXT );
			 * }
			 */
		}
	},
	// main report with grouping
	ACC_SCHEME_CONSUMPTION_SUMMARY_MAIN {
		final String path = "/reports/account/scheme_consumption_report/summary_group_parent";

		@Override
		public String filePath() {
			return SRC + path;
		}

		@Override
		public InputStream absoluteFilePath(HmisUtilService util) {
			return util.getAbsoluteFilePath(path + EXT);
			/*
			 * if ( util.isDevOrProdProfile() ) {
			 * return SRC + path + EXT;
			 * }
			 * else {
			 * return util.getAbsoluteFilePath( path + EXT );
			 * }
			 */
		}
	};

	public abstract String filePath();

	// todo:: refactor, remove HmisUtilService param, use static reference in
	// override
	public abstract InputStream absoluteFilePath(HmisUtilService util);

	public static final String SRC = "src/main/resources";
	public static final String EXT = ".jrxml";
}
