import { Injectable } from '@angular/core';
import { environment } from '@environments/environment';
import { HttpClient } from '@angular/common/http';
import { NurseNotePayload, ResponsePayload } from '@app/shared/_payload';

@Injectable({ providedIn: 'root' })
export class NurseNoteService {
    url: string = environment.apiEndPoint + '/nurse-note';

    constructor(private http: HttpClient) {}

    public onCreate(payload: NurseNotePayload) {
        return this.http.post<ResponsePayload<string>>(`${this.url}/create`, payload);
    }
}
