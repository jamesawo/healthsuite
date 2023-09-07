import {Component, EventEmitter, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-lab-specimen-color',
  templateUrl: './lab-specimen-color.component.html',
  styleUrls: ['./lab-specimen-color.component.css']
})
export class LabSpecimenColorComponent implements OnInit {
  @Output('selected')
  public selectEmit: EventEmitter<string> = new EventEmitter<string>();
  constructor() {}

  ngOnInit(): void {}

  onSelected(value: string) {
    if (value){
      this.selectEmit.emit(value);
    }
  }
}
