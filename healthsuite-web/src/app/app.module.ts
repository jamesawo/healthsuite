import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { NgxWebstorageModule } from 'ngx-webstorage';
import { WebcamModule } from 'ngx-webcam';
import { NgxSpinnerModule } from 'ngx-spinner';
import { ToastrModule } from 'ngx-toastr';
import {
    CanActivateGuard,
    HttpErrorInterceptor,
    HttpInterceptorService,
} from '@app/shared/_helpers';
import { AuthService } from '@app/shared/_services/auth/auth.service';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { SnackbarModule } from 'ngx-snackbar';
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';
import { MomentModule } from 'ngx-moment';
import { NgSelectModule } from '@ng-select/ng-select';
import { SweetAlert2Module } from '@sweetalert2/ngx-sweetalert2';
import { ClipboardModule } from 'ngx-clipboard';
import { NgxFileDropModule } from 'ngx-file-drop';
import { NgxPaginationModule } from 'ngx-pagination';
import { ModalModule } from 'ngx-bootstrap/modal';
import { CoreModule } from '@app/modules/core.module';
import { BsDatepickerModule } from 'ngx-bootstrap/datepicker';
import { TimepickerModule } from 'ngx-bootstrap/timepicker';
import { NumberOnlyPipe } from './shared/_pipes/util/number-only.pipe';
import { BsDropdownModule } from 'ngx-bootstrap/dropdown';
import { ChartsModule } from 'ng2-charts';

@NgModule({
    declarations: [AppComponent, NumberOnlyPipe],
    imports: [
        BrowserModule,
        AppRoutingModule,
        FormsModule,
        ReactiveFormsModule,
        HttpClientModule,
        BrowserAnimationsModule,
        CoreModule,
        NgbModule,
        NgxWebstorageModule.forRoot(),
        WebcamModule,
        NgxSpinnerModule,
        ToastrModule.forRoot({
            closeButton: true,
            tapToDismiss: true,
            positionClass: 'toast-top-center',
            preventDuplicates: true,
            enableHtml: true,
            timeOut: 10000,
            progressBar: true,
        }),
        SnackbarModule.forRoot(),
        NgMultiSelectDropDownModule.forRoot(),
        MomentModule,
        NgSelectModule,
        SweetAlert2Module.forRoot(),
        ClipboardModule,
        NgxFileDropModule,
        NgxPaginationModule,
        ModalModule.forRoot(),
        BsDatepickerModule.forRoot(),
        TimepickerModule.forRoot(),
        BsDropdownModule.forRoot(),
        ChartsModule,
    ],
    providers: [
        { provide: HTTP_INTERCEPTORS, useClass: HttpInterceptorService, multi: true },
        { provide: HTTP_INTERCEPTORS, useClass: HttpErrorInterceptor, multi: true },
        AuthService,
        CanActivateGuard,
    ],
    bootstrap: [AppComponent],
})
export class AppModule {}
