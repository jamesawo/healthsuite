import { Component, OnInit } from '@angular/core';
import {PatientPayload} from '@app/shared/_payload/erm/patient.payload';
import {DatePayload, DateType} from '@app/shared/_payload';

@Component({
  selector: 'app-appointment-booking-report',
  templateUrl: './appointment-booking-report.component.html',
  styleUrls: ['./appointment-booking-report.component.css']
})
export class AppointmentBookingReportComponent implements OnInit {

  start = DateType.START;
  end = DateType.END;

  constructor() { }

  ngOnInit(): void {
  }

  onPatientSelected(patientPayload: PatientPayload) {
    console.log(patientPayload);
  }

  onDateSelected(datePayload: DatePayload, end: DateType) {

  }

  onSearchReport() {

  }
}
