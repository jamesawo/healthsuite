export class HmisConstants {

    // defaults
    public static readonly DEFAULT_DEPARTMENT_CODE = 'DEPT101';
    public static readonly DEFAULT_DEPARTMENT_NAME = 'DEFAULT';
    public static readonly DEFAULT_LOCATION_KEY = 'DefaultLoc';

    public static readonly ERR_SERVER_ERROR = 'Something is wrong, contact support for help';
    public static readonly ERR_TITLE = 'Action Failed!';
    public static readonly INTERNAL_SERVER_ERROR = 'AN ERROR OCCURRED!';
    public static readonly SUCCESS_RESPONSE = 'Action Successful';
    public static readonly OK_SUCCESS_RESPONSE = 'ACTION COMPLETED';
    public static readonly FAILED_RESPONSE = 'UNABLE TO COMPLETE';
    public static readonly LAST_ACTION_SUCCESS = 'Your Last Action Was Successful';
    public static readonly LAST_ACTION_FAILED = 'Your Last Action Failed, Contact Support! ';
    public static readonly TEMPLATE_FAILED = 'Could Not Apply Template';

    // socket destinations
    public static readonly SOCK_GLOBAL_SETTING_UPDATE = '/topic/global-setting';
    public static readonly SOCK_NOTIFICATION_WEB = '/topic/notification';
    public static readonly SOCK_NURSE_WAITING_LIST = '/topic/nurse-waiting-list';
    public static readonly SOCK_DOCTOR_WAITING_LIST = '/topic/doctor-waiting-list';

    public static readonly PATIENT_ON_ADMISSION =
        'Patient is on Admission! <br> Cannot revisit patient on admission';
    public static readonly PATIENT_IS_ACTIVE = 'Patient is already active.';
    public static readonly VALIDATION_ERR = 'Validation Error!';
    public static readonly PATIENT_IS_REQUIRED = 'Patient is Required First';
    public static readonly RECORD_ADDED = 'RECORD ADDED SUCCESSFULLY';
    public static readonly POSSIBLE_DUPLICATE = 'DUPLICATE ENTRY, SIMILAR TITLE ALREADY EXIST!';
    public static readonly CONNECTION_LOST = 'CONNECTION LOST!';
    public static readonly NETWORK_ERROR = 'ERROR!, NETWORK DISCONNECTED';
    public static readonly UPDATED = 'UPDATED';
    public static readonly NO_BILL_ITEM_FOUND = 'No Items Found In Bill, Recreate Bill';
    public static readonly CANNOT_ADJUST_BILL = 'Cannot Adjust Patient Bill, Admission Session Is Discharged!';
    public static readonly ITEM_QTY_TOO_LOW = 'Item Stock Quantity Is Too Low';
    public static readonly LOW_STOCK_COUNT = 'Low Stock Count';
    public static readonly ERROR = 'ERROR!';
    public static readonly INVALID_RECEIPT = 'Receipt Is Invalid!';
    public static readonly CANNOT_CANCEL_DEPOSIT = 'CANCELLATION OF DEPOSIT NOT ALLOWED HERE';
    public static readonly FEATURE_NOT_ENABLED = 'THIS FEATURE IS NOT ENABLED.';
}
