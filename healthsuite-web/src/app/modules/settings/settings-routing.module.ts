import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { UserManagerComponent } from '@app/modules/settings/user-manager/user-manager.component';
import { RoleManagerComponent } from '@app/modules/settings/role-manager/role-manager.component';
import { SeedDataComponent } from '@app/modules/settings/seed-data/seed-data.component';
import { GlobalSettingsComponent } from '@app/modules/settings/global-settings/global-settings.component';
import { BatchUploadComponent } from '@app/modules/settings/batch-upload/batch-upload.component';
import { LocationSettingsComponent } from '@app/modules/settings/location-settings/location-settings.component';
import { PasswordResetComponent } from '@app/modules/settings/password-reset/password-reset.component';
import { CanActivateGuard } from '@app/shared/_helpers';
import { RightSurveillanceComponent } from '@app/modules/settings/right-surveillance/right-surveillance.component';
import { RetSchemeSuspensionComponent } from '@app/modules/settings/ret-scheme-suspension/ret-scheme-suspension.component';
import { NhisSchemeSuspensionComponent } from '@app/modules/settings/nhis-scheme-suspension/nhis-scheme-suspension.component';

const routes: Routes = [
    {
        path: 'user-management',
        component: UserManagerComponent,
        canActivate: [CanActivateGuard],
        data: {title: 'USER MANAGER'}
    },
    {
        path: 'role-management',
        component: RoleManagerComponent,
        canActivate: [CanActivateGuard],
        data: {title: 'ROLE MANAGER'}
    },

    {
        path: 'seed-data',
        component: SeedDataComponent,
        canActivate: [CanActivateGuard],
        data: {title: 'SEED DATA'}
    },

    {
        path: 'global-settings',
        component: GlobalSettingsComponent,
        canActivate: [CanActivateGuard],
        data: {title: 'GLOBAL SETTINGS'}
    },
    {
        path: 'batch-upload',
        component: BatchUploadComponent,
        canActivate: [CanActivateGuard],
        data: {title: 'BATCH UPLOAD'}
    },
    {
        path: 'location-settings',
        component: LocationSettingsComponent,
        canActivate: [CanActivateGuard],
        data: {title: 'LOCATION SETTING'}
    },
    {
        path: 'password-reset',
        component: PasswordResetComponent,
        canActivate: [CanActivateGuard],
        data: { title: 'PASSWORD RESET' },
    },
    {
        path: 'right-surveillance',
        component: RightSurveillanceComponent,
        canActivate: [CanActivateGuard],
        data: {title: 'USER RIGHT SURVEILLANCE'}
    },
    {
        path: 'ret-scheme-suspension',
        component: RetSchemeSuspensionComponent,
        canActivate: [CanActivateGuard],
        data: {title: 'RET SCHEME SUSPENSION'}
    },
    {
        path: 'nhis-scheme-suspension',
        component: NhisSchemeSuspensionComponent,
        canActivate: [CanActivateGuard],
        data: {title: 'NHIS SCHEME SUSPENSION'}
    },
];

@NgModule({
    declarations: [],
    imports: [CommonModule, RouterModule.forChild(routes)],
})
export class SettingsRoutingModule {}
