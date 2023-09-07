import { Component, OnInit } from '@angular/core';
import {LabVerificationViewEnum} from '@app/shared/_payload/lab/lab.payload';

@Component({
  selector: 'app-med-pathologist-verification',
  templateUrl: './med-pathologist-verification.component.html',
  styleUrls: ['./med-pathologist-verification.component.css']
})
export class MedPathologistVerificationComponent implements OnInit {
  public pathologist = LabVerificationViewEnum.PATHOLOGIST;

  constructor() { }

  ngOnInit(): void {
  }

}
