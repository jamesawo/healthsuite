import { ServiceUsageEnum } from '@app/shared/_payload/others/service-usage.enum';

export class ProductServicePayload {
    id: number;
    name: string;
    code: string;
    description: string;
    costPrice: number;
    unitCost: number;
    regularSellingPrice: number;
    nhisSellingPrice: number;
    discount: number;
    applyDiscount: boolean;
    usedFor: ServiceUsageEnum;
    revenueDepartmentId: number;
    departmentId: number;
    schemeId: number;
    schemePrice: number;

    constructor() {
        this.id = undefined;
        this.name = '';
        this.code = '';
        this.description = '';
        this.usedFor = undefined;
        this.discount = 0.0;
        this.costPrice = 0.0;
        this.nhisSellingPrice = 0.0;
        this.regularSellingPrice = 0.0;
        this.unitCost = 0.0;
        this.schemeId = undefined;
        this.schemePrice = 0.0;
    }
}
