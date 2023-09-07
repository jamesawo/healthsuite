import { Directive, ViewContainerRef } from '@angular/core';

@Directive({
    selector: '[appModal]',
})
export class ModalPopupDirective {
    constructor(public viewContainerRef: ViewContainerRef) {}
}
