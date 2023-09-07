import {Component, Input, OnInit} from '@angular/core';
import {ObHisOfPresentPregPayload} from '@app/shared/_payload/nurse/nurse-anc.payload';

@Component({
  selector: 'app-ob-history-of-present-preg',
  templateUrl: './ob-history-of-present-preg.component.html',
  styleUrls: ['./ob-history-of-present-preg.component.css']
})
export class ObHistoryOfPresentPregComponent implements OnInit {
  @Input('props') props: {payload: ObHisOfPresentPregPayload};

  constructor() { }

  ngOnInit(): void {
    if (!this.props.payload){
      this.props.payload = new ObHisOfPresentPregPayload();
    }
  }

}
