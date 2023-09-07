export interface LoginPayload {
    username: string;
    password: string;
}

export class ChangePasswordPayload {
    oldPassword: string;
    newPassword: string;
    confirmPassword: string;
    userId: number;

    constructor() {
        this.oldPassword = undefined;
        this.newPassword = undefined;
        this.confirmPassword = undefined;
        this.userId = undefined;
    }
}
