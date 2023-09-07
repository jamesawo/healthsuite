import { Component, OnInit } from '@angular/core';
import {DatePayload} from '@app/shared/_payload';

@Component({
  selector: 'app-lab-push-sample',
  templateUrl: './lab-push-sample.component.html',
  styleUrls: ['./lab-push-sample.component.css']
})
export class LabPushSampleComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

  onSearchByChange(searchBy: string) {
    
  }

  onDateSelected(data: DatePayload, start: 'start' | 'end') {

  }

  onSearchRequest() {

  }
}
