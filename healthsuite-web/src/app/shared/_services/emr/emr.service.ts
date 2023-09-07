import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import { environment } from '@environments/environment';
import { BehaviorSubject, Observable } from 'rxjs';
import {
    AppointmentBooking,
    AppointmentBookingSetup, PatientAccountSummary,
    PatientCategoryEnum,
    PatientPayload,
    PatientRevisitPayload,
} from '@app/shared/_payload/erm/patient.payload';
import {HmisSharedDropDown, MessagePayload, ResponsePayload} from '@app/shared/_payload';
import {NationalityPayload} from '@app/modules/settings/_payload/nationality.payload';

@Injectable({
    providedIn: 'root',
})
export class EmrService {
    url: string = environment.apiEndPoint + '/emr';
    patientSubject: BehaviorSubject<PatientPayload>;
    patientSubject$: Observable<PatientPayload>;

    constructor(private http: HttpClient) {
        this.patientSubject = new BehaviorSubject<PatientPayload>(new PatientPayload());
        this.patientSubject$ = this.patientSubject.asObservable();
    }

    public onRegisterPatient(payload: PatientPayload) {
        return this.http.post<PatientPayload>(`${this.url}/patient-registration`, payload, {
            observe: 'response',
        });
    }

    public onUpdatePatient(payload: PatientPayload) {
        return this.http.put(`${this.url}/patient-edit`, payload, { observe: 'response' });
    }

    public patientCategoryEnumToList(): HmisSharedDropDown[] {
        return Object.keys(PatientCategoryEnum).map((key) => ({
            id: PatientCategoryEnum[key],
            name: key,
            value: key,
        }));
    }

    public onRevisitPatient(revisitPayload: PatientRevisitPayload) {
        return this.http.post<ResponsePayload<PatientRevisitPayload>>(
            `${this.url}/patient-revisit`,
            revisitPayload,
            {
                observe: 'response',
            }
        );
    }

    public appointmentBookingSetup(setUpPayload: AppointmentBookingSetup) {
        return this.http.post<AppointmentBookingSetup>(
            `${this.url}/patient-appointment-booking-setup`,
            setUpPayload,
            {
                observe: 'response',
            }
        );
    }

    public patientAppointmentBooking(payload: AppointmentBooking) {
        return this.http.post<AppointmentBooking>(
            `${this.url}/patient-appointment-booking`,
            payload,
            { observe: 'response' }
        );
    }

    public findAllPatientAppointment(param: { patientId: number }) {
        return this.http.get<AppointmentBooking[]>(
            `${this.url}/patient-open-appointments/${param.patientId}`,
            { observe: 'response' }
        );
    }

    public onCancelAppointment(param: { appointmentId: number }) {
        return this.http.put<ResponsePayload<boolean>>(
            `${this.url}/cancel-patient-appointment`,
            param.appointmentId,
            {
                observe: 'response',
            }
        );
    }

    public onFindPatientById(id: number) {
        return this.http.get<PatientPayload>(`${this.url}/find-one/${id}`, { observe: 'response' });
    }

    public onGetPatientRevisitAccountSummary(patientId: number) {
        return this.http.get<PatientAccountSummary>(`${this.url}/patient-account-summary/${patientId}`);
    }

    public getNationalityByLgaId(id: number) {
        return this.http.get<NationalityPayload>(`${this.url}/get-nationality-by-lga/${id}`);
    }

    public onUpdatePatientSchemeDetails(patient: PatientPayload) {
        return this.http.post<MessagePayload>(`${this.url}/update-patient-scheme`, patient);
    }

    public onChangePatientCategory(payload: any) {
        return this.http.post<MessagePayload>(`${this.url}/change-patient-category`, payload );
    }
}
