import {Component, Input, OnInit} from '@angular/core';
import {LabResultPrepDto} from '@app/shared/_payload/lab/lab.payload';

@Component({
  selector: 'app-lab-test-user-log',
  templateUrl: './lab-test-user-log.component.html',
  styleUrls: ['./lab-test-user-log.component.css']
})
export class LabTestUserLogComponent implements OnInit {
  @Input('payload')
  payload: LabResultPrepDto;

  constructor() { }

  ngOnInit(): void {
  }

}
