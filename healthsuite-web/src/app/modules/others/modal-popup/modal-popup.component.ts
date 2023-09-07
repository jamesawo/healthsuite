import {
    Component,
    ComponentFactoryResolver,
    ElementRef,
    OnInit,
    ViewChild,
} from '@angular/core';
import { BsModalRef } from 'ngx-bootstrap/modal';
import { ModalPopupDirective } from '@app/shared/_directives/modal/modal-popup.directive';
import { IModalPopup } from '@app/shared/_payload';

@Component({
    selector: 'app-modal-popup',
    templateUrl: './modal-popup.component.html',
    styleUrls: ['./modal-popup.component.css'],
})
export class ModalPopupComponent implements OnInit, IModalPopup {
    component: any;
    data: any;
    title: string;
    @ViewChild(ModalPopupDirective, { static: true }) adHost: ModalPopupDirective;
    @ViewChild('closeButton') closeButton: ElementRef;

    constructor(
        public bsModalRef: BsModalRef,
        private componentFactoryResolver: ComponentFactoryResolver
    ) {}

    ngOnInit(): void {
        if (!this.title) {
            this.title = '';
        }

        if (this.component) {
            this.loadComponent();
        }
    }

    public onCloseButtonClick(): void {
        this.closeButton.nativeElement.click();
    }

    private loadComponent() {
        const componentFactory = this.componentFactoryResolver.resolveComponentFactory<IModalPopup>(
            this.component
        );

        const viewContainerRef = this.adHost.viewContainerRef;
        viewContainerRef.clear();

        const componentRef = viewContainerRef.createComponent<IModalPopup>(componentFactory);
        if (this.data) { componentRef.instance.data = this.data; }
    }
}
