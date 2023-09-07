import { SharedPayload } from '@app/shared/_payload';

export class DepartmentPayload {
    id: number;
    name: string;
    description: string;
    code: string;
    departmentCategory: SharedPayload;

    constructor() {
        this.id = null;
        this.name = '';
        this.code = '';
        this.description = '';

        this.departmentCategory = {
            id: null,
            code: '',
            description: '',
            name: '',
        };
    }
}
