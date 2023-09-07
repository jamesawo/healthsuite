import { Component, OnInit } from '@angular/core';
import {LabVerificationViewEnum} from '@app/shared/_payload/lab/lab.payload';

@Component({
  selector: 'app-med-result-view',
  templateUrl: './med-result-view.component.html',
  styleUrls: ['./med-result-view.component.css']
})
export class MedResultViewComponent implements OnInit {
  public downloadView = LabVerificationViewEnum.DOWNLOAD;

  constructor() { }

  ngOnInit(): void {
  }

}
