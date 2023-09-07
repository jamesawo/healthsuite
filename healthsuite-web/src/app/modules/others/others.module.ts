import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ServiceRegisterComponent } from './service-register/service-register.component';
import { OthersRoutingModule } from '@app/modules/others/others-routing.module';
import { HmisCommonModule } from '@app/modules/common/hmis-common.module';
import { NgSelectModule } from '@ng-select/ng-select';
import { FormsModule } from '@angular/forms';
import { RevenueDepartmentSearchComponent } from './revenue-department-search/revenue-department-search.component';
import { ServiceDepartmentSearchComponent } from './service-department-search/service-department-search.component';
import { NgxPaginationModule } from 'ngx-pagination';
import { UploadFileComponent } from './upload-file/upload-file.component';
import { NgxFileDropModule } from 'ngx-file-drop';
import { ModalPopupComponent } from './modal-popup/modal-popup.component';
import { VendorManagementComponent } from './vendor-management/vendor-management.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { SchemeServicePriceComponent } from './scheme-service-price/scheme-service-price.component';

@NgModule({
    declarations: [
        ServiceRegisterComponent,
        RevenueDepartmentSearchComponent,
        ServiceDepartmentSearchComponent,
        UploadFileComponent,
        ModalPopupComponent,
        VendorManagementComponent,
        DashboardComponent,
        SchemeServicePriceComponent,
    ],
    imports: [
        CommonModule,
        OthersRoutingModule,
        HmisCommonModule,
        NgSelectModule,
        FormsModule,
        NgxPaginationModule,
        NgxFileDropModule,
    ],
    exports: [
        VendorManagementComponent,
        ServiceDepartmentSearchComponent,
        RevenueDepartmentSearchComponent,
    ],
})
export class OthersModule {}
