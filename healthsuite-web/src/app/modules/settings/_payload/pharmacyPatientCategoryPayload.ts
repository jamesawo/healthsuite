import { SharedPayload } from '@app/shared/_payload';

export class PharmacyPatientCategoryPayload {
    id: number;
    name: string;
    pharmacyPatientCategoryType: SharedPayload;
    paymentMethod: SharedPayload;

    constructor() {
        this.id = null;
        this.name = '';
        this.pharmacyPatientCategoryType = {
            id: null,
            name: '',
        };
        this.paymentMethod = {
            id: null,
            name: '',
        };
    }
}
