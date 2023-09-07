export class RevenueDepartmentPayload {
    id: number;
    name: string;
    code: string;
    isAttachedToDeposit: boolean;
    revenueDepartmentTypeDto: {
        id: number;
        name: string;
    };
    nameAndCode?: string;
    constructor() {
        this.id = null;
        this.name = '';
        this.nameAndCode = '';
        this.revenueDepartmentTypeDto = {
            id: null,
            name: '',
        };
        this.isAttachedToDeposit = false;
    }
}
