import { DepartmentPayload } from '@app/modules/settings';
import {DatePayload, RolePayload, SharedPayload} from '@app/shared/_payload';

export class UserPayload {
    id?: number;
    lastName?: string;
    firstName?: string;
    otherNames?: string;
    email?: string;
    phone?: string;
    department?: DepartmentPayload;
    role?: RolePayload[];
    userName?: string;
    password?: string;
    confirmPassword?: string;
    expiryDate?: DatePayload;
    accountEnabled?: boolean;
    label?: string;

    constructor() {
        this.id = null;
        this.lastName = null;
        this.otherNames = null;
        this.email = null;
        this.phone = null;
        this.department = new DepartmentPayload();
        this.role = [];
        this.userName = null;
        this.password = null;
        this.expiryDate = null;
        this.accountEnabled = true;
    }
}

export class UserUpdatePayload {
    userId: number;
    role: RolePayload;
    department: DepartmentPayload;
    lastName: string;
    otherNames: string;
    accExpiryDate: DatePayload;
    phoneNumber: string;

        constructor() {
        this.userId = undefined;
        this.role = undefined;
        this.department = undefined;
        this.lastName = undefined;
        this.otherNames = undefined;
        this.accExpiryDate = undefined;
        this.phoneNumber = undefined;
    }
}
