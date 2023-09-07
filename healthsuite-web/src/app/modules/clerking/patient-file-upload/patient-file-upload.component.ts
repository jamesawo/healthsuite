import { Component, OnInit } from '@angular/core';
import {PatientPayload} from '@app/shared/_payload/erm/patient.payload';

@Component({
  selector: 'app-patient-file-upload',
  templateUrl: './patient-file-upload.component.html',
  styleUrls: ['./patient-file-upload.component.css']
})
export class PatientFileUploadComponent implements OnInit {
  public patient =  new PatientPayload();
  public payload: any;

  constructor() { }

  ngOnInit(): void {
  }

    onPatientSelected(patientPayload: PatientPayload) {
        
    }
}
