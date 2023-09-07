import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoginComponent } from './login/login.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {NgxSpinnerModule} from 'ngx-spinner';
import {NgbPopoverModule} from '@ng-bootstrap/ng-bootstrap';


@NgModule({
  declarations: [LoginComponent],
    imports: [
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        NgxSpinnerModule,
        NgbPopoverModule
    ],
  exports: [
    LoginComponent
  ]
})
export class AuthModule { }
