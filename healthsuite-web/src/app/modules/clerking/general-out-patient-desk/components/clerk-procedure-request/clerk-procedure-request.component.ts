import { Component, OnInit } from '@angular/core';
import {ProductServicePayload} from '@app/shared/_payload';

@Component({
  selector: 'app-clerk-procedure-request',
  templateUrl: './clerk-procedure-request.component.html',
  styleUrls: ['./clerk-procedure-request.component.css']
})
export class ClerkProcedureRequestComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }


  public onServiceSelected(productServicePayload: ProductServicePayload) {}

  isAllCheckBoxChecked(): boolean {
    return false;
  }

  checkAllCheckBox(event: any) {}

  onRemoveAllCheckedBillItems() {}

}
