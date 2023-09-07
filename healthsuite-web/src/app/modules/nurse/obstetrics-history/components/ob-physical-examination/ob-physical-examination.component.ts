import {Component, Input, OnInit} from '@angular/core';
import {ObPhysicalExamPayload} from '@app/shared/_payload/nurse/nurse-anc.payload';

@Component({
  selector: 'app-ob-physical-examination',
  templateUrl: './ob-physical-examination.component.html',
  styleUrls: ['./ob-physical-examination.component.css']
})
export class ObPhysicalExaminationComponent implements OnInit {
  @Input('props') props: {payload: ObPhysicalExamPayload};

  constructor() { }

  ngOnInit(): void {
    if (!this.props.payload) {
      this.props.payload = new ObPhysicalExamPayload();
    }
  }

}
