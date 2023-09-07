import { Component, ElementRef, OnDestroy, OnInit, Renderer2, ViewChild } from '@angular/core';
import { PatientOtherDetails, PatientPayload } from '@app/shared/_payload/erm/patient.payload';
import { Subscription } from 'rxjs';
import { EmrService } from '@app/shared/_services';

@Component({
    selector: 'app-patient-other-details',
    templateUrl: './patient-other-details.component.html',
    styleUrls: ['./patient-other-details.component.css'],
})
export class PatientOtherDetailsComponent implements OnInit, OnDestroy {
    private subscription: Subscription = new Subscription();
    public otherDetailPayload: PatientOtherDetails = {};
    public patientPayload: PatientPayload;
    @ViewChild('infoElement') infoElement: ElementRef;
    @ViewChild('lifestyleElement') lifestyleElement: ElementRef;

    constructor(private emrService: EmrService, private renderer: Renderer2) {}

    ngOnInit(): void {
        this.subscription.add(
            this.emrService.patientSubject$.subscribe((payload) => {
                this.patientPayload = payload;
            })
        );
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    onTypeChange(event: any) {
        const value = event.target.value;
        const info = this.infoElement.nativeElement;
        const lifestyle = this.lifestyleElement.nativeElement;

        if (value === 'info') {
            this.renderer.addClass(info, 'btn-primary');
            this.renderer.removeClass(lifestyle, 'btn-primary');
        }

        if (value === 'lifestyle') {
            this.renderer.addClass(lifestyle, 'btn-primary');
            this.renderer.removeClass(info, 'btn-primary');
        }
    }
}
