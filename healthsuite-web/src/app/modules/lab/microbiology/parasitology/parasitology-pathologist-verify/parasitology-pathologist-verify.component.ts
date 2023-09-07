import { Component, OnInit } from '@angular/core';
import {LabVerificationViewEnum} from '@app/shared/_payload/lab/lab.payload';
import {LabDepartmentResultType} from '@app/shared/_payload/lab/lab-setup.payload';

@Component({
  selector: 'app-parasitology-pathologist-verify',
  templateUrl: './parasitology-pathologist-verify.component.html',
  styleUrls: ['./parasitology-pathologist-verify.component.css']
})
export class ParasitologyPathologistVerifyComponent implements OnInit {
  public pathologist = LabVerificationViewEnum.PATHOLOGIST;
  public microbiologyparasitology = LabDepartmentResultType.MICROBIOLOGY_PARASITOLOGY;

  constructor() { }

  ngOnInit(): void {
  }

}
