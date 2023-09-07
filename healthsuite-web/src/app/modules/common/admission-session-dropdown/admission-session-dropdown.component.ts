import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {SearchAdmissionPayload} from '@app/shared/_payload/reports/report-erm.payload';

@Component({
  selector: 'app-admission-session-dropdown',
  templateUrl: './admission-session-dropdown.component.html',
  styleUrls: ['./admission-session-dropdown.component.css']
})
export class AdmissionSessionDropdownComponent implements OnInit, OnChanges {
  @Input('collection')
  public collection: SearchAdmissionPayload[] = [];
  @Output('selected')
  public selected: EventEmitter<SearchAdmissionPayload> = new EventEmitter<SearchAdmissionPayload>();

  constructor() {}

  ngOnInit(): void {}

  ngOnChanges(changes: SimpleChanges) {}

  public onSelect(admissionPayload: SearchAdmissionPayload) {
    if (admissionPayload) {
      this.selected.emit(admissionPayload);
    }
  }
}
