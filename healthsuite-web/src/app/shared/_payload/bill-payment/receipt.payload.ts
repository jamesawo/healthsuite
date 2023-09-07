import {UserPayload} from '@app/modules/settings/_payload/userPayload';
import {DepartmentPayload} from '@app/modules/settings';
import {PatientPayload} from '@app/shared/_payload/erm/patient.payload';
import {PatientBill} from '@app/shared/_payload/bill-payment/bill.payload';

export class CancelReceiptPayload {
    paymentReceiptId: number;
    receiptCode: string;
    cancelledBy: UserPayload;
    cancelledFrom: DepartmentPayload;
    comment: string;
    patient: PatientPayload;
    patientBill?: PatientBill;

    constructor() {
        this.paymentReceiptId = undefined;
        this.receiptCode = undefined;
        this.cancelledBy = undefined;
        this.cancelledFrom = undefined;
        this.comment = '';
        this.patient = new PatientPayload();
        this.patientBill = new PatientBill();
    }
}
