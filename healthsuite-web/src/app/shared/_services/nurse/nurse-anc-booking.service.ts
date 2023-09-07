import { Injectable } from '@angular/core';
import { environment } from '@environments/environment';
import { HttpClient } from '@angular/common/http';
import { AntenatalBookingPayload, NurseNotePayload, ResponsePayload } from '@app/shared/_payload';

@Injectable({ providedIn: 'root' })
export class NurseAncBookingService {
    url: string = environment.apiEndPoint + '/antenatal-booking';

    constructor(private http: HttpClient) {}

    public onCreateBooking(payload: AntenatalBookingPayload) {
        return this.http.post<ResponsePayload<string>>(`${this.url}/create`, payload);
    }
}
