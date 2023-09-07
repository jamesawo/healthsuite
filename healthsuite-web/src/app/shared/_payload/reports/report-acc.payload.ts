import {DailyCollectionFilterTypeEnum, DatePayload, ProductServicePayload} from '@app/shared/_payload';
import {DepartmentPayload, RevenueDepartmentPayload} from '@app/modules/settings';
import {DrugRegisterPayload} from '@app/shared/_payload/pharmacy/pharmacy.payload';

export class ReportAccPayload {}

export class ReportColByRevDepPayload {
    startDate: DatePayload;
    endDate: DatePayload;
    revenueDepartment: RevenueDepartmentPayload;
    type: DailyCollectionFilterTypeEnum;
    serviceDepartment: DepartmentPayload;
    service: ProductServicePayload;
    drug: DrugRegisterPayload;

    constructor() {
        this.startDate = undefined;
        this.endDate = undefined;
        this.revenueDepartment = undefined;
        this.type = undefined;
        this.serviceDepartment = undefined;
        this.service = undefined;
        this.drug = undefined;
    }
}
