import {Directive, ViewContainerRef} from '@angular/core';

@Directive({
  selector: '[appLabResult]'
})
export class LabResultDirective {

  constructor(public viewContainerRef: ViewContainerRef) {}

}
