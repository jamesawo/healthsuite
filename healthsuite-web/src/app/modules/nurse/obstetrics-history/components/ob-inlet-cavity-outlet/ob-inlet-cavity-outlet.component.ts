import {Component, Input, OnInit} from '@angular/core';
import {ObInstructionPayload} from '@app/shared/_payload/nurse/nurse-anc.payload';

@Component({
  selector: 'app-ob-inlet-cavity-outlet',
  templateUrl: './ob-inlet-cavity-outlet.component.html',
  styleUrls: ['./ob-inlet-cavity-outlet.component.css']
})
export class ObInletCavityOutletComponent implements OnInit {
  @Input('props') props: {payload: ObInstructionPayload};

  constructor() { }

  ngOnInit(): void {
    if (!this.props.payload) {
      this.props.payload = new ObInstructionPayload();
    }
  }

}
