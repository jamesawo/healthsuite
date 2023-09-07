import { SharedPayload, GenderPayload } from '@app/shared/_payload';
import { DepartmentPayload, PharmacyPatientCategoryPayload } from '@app/modules/settings';
import { NationalityPayload } from '@app/modules/settings/_payload/nationality.payload';

export class SeedSubjectPayload {
    public pCategory?: PharmacyPatientCategoryPayload[];
    public hasData?: boolean;
    public clinic?: DepartmentPayload[];
    public defaultLocation?: DepartmentPayload;
    public maritalS?: SharedPayload[];
    public gender?: GenderPayload[];
    public religion?: SharedPayload[];
    public ethnicG?: SharedPayload[];
    public meansOfIdentification?: SharedPayload[];
    public nationality?: NationalityPayload[];
    public nokRelationship?: SharedPayload[];
    public schemeData?: SharedPayload[];

    constructor() {
        this.pCategory = [];
    }
}
