export interface ResponsePayload<T> {
    message?: string;
    data?: T;
    httpStatusCode?: number;
    httpStatusText?: string;
    date?: Date;
    result?: T;
}

export interface MessagePayload {
    message: string;
}
