import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { PatientSearchComponent } from '@app/modules/common/patient-search/patient-search.component';
import {
    DatePayload,
    DateType,
    VitalSignSearchPayload,
    VitalSignSearch,
} from '@app/shared/_payload';
import { Subscription } from 'rxjs';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { CommonService } from '@app/shared/_services/common/common.service';
import { PatientPayload } from '@app/shared/_payload/erm/patient.payload';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import { PatientVitalSignService } from '@app/shared/_services/nurse/patient-vital-sign.service';
import { ChartDataSets } from 'chart.js';

@Component({
    selector: 'app-vital-signs-trend',
    templateUrl: './vital-signs-trend.component.html',
    styleUrls: ['./vital-signs-trend.component.css'],
})
export class VitalSignsTrendComponent implements OnInit, OnDestroy {
    @ViewChild('patientSearchComponent') patientSearchComponent: PatientSearchComponent;
    public payload: VitalSignSearchPayload = new VitalSignSearchPayload();
    public start = DateType.START;
    public end = DateType.END;
    public active = 1;
    public dataList: VitalSignSearch = new VitalSignSearch();

    public chartList: { arr: any[]; labels: string[] }[] = [];

    private subscription: Subscription = new Subscription();

    constructor(
        private spinner: NgxSpinnerService,
        private toast: ToastrService,
        private commonService: CommonService,
        private vitalSignService: PatientVitalSignService
    ) {}

    ngOnInit(): void {}

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public onPatientSelected(patient: PatientPayload) {
        if (patient) {
            this.payload.patient = patient;
        }
    }

    public onDateSelected(datePayload: DatePayload, type: DateType) {
        if (type === this.start) {
            this.payload.startDate = datePayload;
        } else {
            this.payload.endDate = datePayload;
        }
    }

    public onSearchTrends() {
        if (this.onValidate() === false) {
            return;
        }

        this.active = 1;

        this.spinner.show().then();
        this.subscription.add(
            this.vitalSignService.onSearchPatientVitalSing(this.payload).subscribe(
                (res) => {
                    this.spinner.hide().then();
                    if (res) {
                        this.dataList = res;
                        this.dataList.hasData = true;
                        this.onSetChartData(res);
                    }
                },
                (error) => {
                    this.spinner.hide().then();
                    this.toast.error(
                        HmisConstants.FAILED_RESPONSE,
                        HmisConstants.INTERNAL_SERVER_ERROR
                    );
                    console.log(error);
                }
            )
        );
    }

    private onValidate(): boolean {
        if (!this.payload.patient || !this.payload.patient.patientId) {
            this.toast.error('Select Patient', HmisConstants.VALIDATION_ERR);
            return false;
        }

        if (!this.payload.startDate || !this.payload.endDate) {
            this.toast.error('Select Date Range', HmisConstants.VALIDATION_ERR);
            return false;
        }
        return true;
    }

    private onSetChartData(res: VitalSignSearch): void {
        this.chartList.push({
            arr: [{ data: res.pulseValues, label: 'Pulse Rate' }],
            labels: res.dateValues,
        });

        this.chartList.push({
            arr: [{ data: res.respRateValues, label: 'Respiratory Rate' }],
            labels: res.dateValues,
        });
        this.chartList.push({
            arr: [{ data: res.bpSystolicValues, label: 'Blood Pressure (Systolic)' }],
            labels: res.dateValues,
        });
        this.chartList.push({
            arr: [{ data: res.bpDiastolicValues, label: 'Blood Pressure (Diastolic)' }],
            labels: res.dateValues,
        });
        this.chartList.push({
            arr: [{ data: res.tempValues, label: 'Temperature' }],
            labels: res.dateValues,
        });
    }
}
