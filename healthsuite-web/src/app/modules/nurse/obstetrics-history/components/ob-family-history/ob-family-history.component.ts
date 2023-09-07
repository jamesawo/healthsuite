import {Component, Input, OnInit} from '@angular/core';
import {ObFamilyHisPayload} from '@app/shared/_payload/nurse/nurse-anc.payload';

@Component({
  selector: 'app-ob-family-history',
  templateUrl: './ob-family-history.component.html',
  styleUrls: ['./ob-family-history.component.css']
})
export class ObFamilyHistoryComponent implements OnInit {
  @Input('props') props: {payload: ObFamilyHisPayload};

  constructor() { }

  ngOnInit(): void {
    if (!this.props.payload) {
      this.props.payload = new ObFamilyHisPayload();
    }
  }

}
