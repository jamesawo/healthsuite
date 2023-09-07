import { Component, OnInit } from '@angular/core';
import { GenderPayload, ResponsePayload, ValidationMessage } from '@app/shared/_payload';
import { Subscription } from 'rxjs';
import { SeedDataService } from '@app/shared/_services';
import { WalkInPatientPayload } from '@app/shared/_payload/erm/patient.payload';

@Component({
    selector: 'app-walk-in-patient',
    templateUrl: './walk-in-patient.component.html',
    styleUrls: ['./walk-in-patient.component.css'],
})
export class WalkInPatientComponent implements OnInit {
    public gender: GenderPayload[];
    public payload: WalkInPatientPayload = new WalkInPatientPayload();

    private subscription: Subscription = new Subscription();

    constructor(private seedDataService: SeedDataService) {}

    ngOnInit(): void {
        this.subscription.add(
            this.seedDataService.seedSubject$.subscribe((data) => {
                this.gender = data.gender;
            })
        );
    }

    public onGetWalkInPatientPayload(): WalkInPatientPayload {
        return this.payload;
    }

    public onValidate(payload: WalkInPatientPayload = this.payload): ValidationMessage {
        let res: ValidationMessage = { status: true, message: '' };
        if (!payload.firstName) {
            res.status = false;
            res.message += `Patient First Name is required <br>`;
        }
        if (!payload.lastName) {
            res.status = false;
            res.message += `Patient Last Name is required <br>`;
        }

        if (!payload.otherName) {
            res.status = false;
            res.message += `Patient Other Name is required <br>`;
        }

        if (!payload.phone) {
            res.status = false;
            res.message += `Patient Phone Number is required <br>`;
        }

        if (!payload.age) {
            res.status = false;
            res.message += `Patient Age is required <br>`;
        }

        return res;
    }
}
