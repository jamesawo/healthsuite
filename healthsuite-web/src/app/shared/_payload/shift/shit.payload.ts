import { UserPayload } from '@app/modules/settings/_payload/userPayload';
import { DepartmentPayload } from '@app/modules/settings';
import { DatePayload } from '@app/shared/_payload';

export class CashierShitWrapPayload {
    cash: number;
    cheque: number;
    pos: number;
    etf: number;
    total: number;
    mobile: number;
    resultList: CashierShitPayload[];

    constructor() {
        this.cash = 0;
        this.cheque = 0;
        this.pos = 0;
        this.etf = 0;
        this.total = 0;
        this.mobile = 0;
        this.resultList = [];
    }
}

export class CashierShitPayload {
    id: number;
    shiftNumber: string;
    openDate: Date;
    openTime: string;
    receiptCount: number;
    closeTime: string;
    closeDate: Date;
    closedByUser: UserPayload;
    cashier: UserPayload;
    department: DepartmentPayload;
    closeTypeEnum: string;
    isActive: boolean;
    isClosedByCashier: boolean;
    isFundReceived: boolean;
    isShitCompiled: boolean;
    compiledShift: CashierCompiledShiftPayload;
    fundReception: CashierFundReceptionPayload;
    cash: number;
    cheque: number;
    pos: number;
    mobileMoney: number;
    etf: number;
    total: number;
    checked: boolean;

    constructor(id?: number) {
        this.id = id ? id : undefined;
        this.shiftNumber = undefined;
        this.openDate = undefined;
        this.openTime = undefined;
        this.receiptCount = undefined;
        this.closeTime = undefined;
        this.closeDate = undefined;
        this.closedByUser = undefined;
        this.cashier = undefined;
        this.department = undefined;
        this.closeTypeEnum = undefined;
        this.isActive = undefined;
        this.isClosedByCashier = undefined;
        this.isFundReceived = undefined;
        this.isShitCompiled = undefined;
        this.compiledShift = undefined;
        this.fundReception = undefined;
        this.cash = undefined;
        this.cheque = undefined;
        this.pos = undefined;
        this.mobileMoney = undefined;
        this.etf = undefined;
        this.total = undefined;
        this.checked = false;
    }
}

export enum SearchShitByEnum {
    SHIFT_NUMBER = 'SHIFT_NUMBER',
    CASHIER_USERNAME = 'CASHIER_USERNAME',
}

export enum CashierShiftReportDetailTypeEnum {
    SUMMARY = 'SUMMARY',
    DETAILED = 'DETAILED',
}

export enum FundReceptionTypeEnum {
    PENDING = 'PENDING',
    ACKNOWLEDGED = 'ACKNOWLEDGED',
}

export enum ShiftCompileTypeEnum {
    UNCOMPILED= 'UNCOMPILED',
    COMPILED = 'COMPILED'
}

export const searchBy: any[] = [
    { id: 1, name: 'SHIFT NUMBER', value: SearchShitByEnum.SHIFT_NUMBER },
    { id: 1, name: 'CASHIER USERNAME', value: SearchShitByEnum.CASHIER_USERNAME },
];

export class CashierShiftSearchPayload {
    id: number;
    shiftNumber: string;
    username: string;
    openDate: string;
    status: boolean;
    fullName: string;

    constructor() {
        this.id = undefined;
        this.shiftNumber = undefined;
        this.username = undefined;
        this.openDate = undefined;
        this.status = undefined;
        this.fullName = undefined;
    }
}

export class CashierCompiledShiftPayload {
    id: number;
    compiledDate: Date;
    compiledTime: Date;
    compiledBy: UserPayload;
    code: string;
    cashierShifts: CashierShitPayload[];
    location: DepartmentPayload;

    constructor() {
        this.id = undefined;
        this.compiledBy = new UserPayload();
        this.compiledDate = undefined;
        this.compiledTime = undefined;
        this.code = undefined;
        this.cashierShifts = [];
        this.location = new DepartmentPayload();
    }
}

export class CashierFundReceptionPayload {
    id: number;
    date: Date;
    receivedBy: UserPayload;
    time: string;
    shift: CashierShitPayload;
    location: DepartmentPayload;

    constructor() {
        this.id = undefined;
        this.date = undefined;
        this.receivedBy = undefined;
        this.time = undefined;
        this.shift = undefined;
        this.location = undefined;
    }
}

export interface CashierShiftSearch {
    userId: number;
    startDate: DatePayload;
    endDate: DatePayload;
    searchBy: SearchShitByEnum;
    shiftNumber: string;
    reportType?: CashierShiftReportDetailTypeEnum;
    shiftId?: number;
    fundReceptionType?: FundReceptionTypeEnum;
    compileType?: ShiftCompileTypeEnum;
}
