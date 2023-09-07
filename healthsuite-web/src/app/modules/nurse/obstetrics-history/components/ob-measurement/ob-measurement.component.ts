import {Component, Input, OnInit} from '@angular/core';
import {ObMeasurement} from '@app/shared/_payload/nurse/nurse-anc.payload';

@Component({
  selector: 'app-ob-measurement',
  templateUrl: './ob-measurement.component.html',
  styleUrls: ['./ob-measurement.component.css']
})
export class ObMeasurementComponent implements OnInit {
  @Input('props') props: {payload: ObMeasurement};

  constructor() { }

  ngOnInit(): void {
  }

}
