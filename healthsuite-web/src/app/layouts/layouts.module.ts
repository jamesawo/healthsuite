import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IconNavComponent } from './icon-nav/icon-nav.component';

import { MainContentComponent } from './main-content/main-content.component';
import { FooterComponent } from './footer/footer.component';
import { RouterModule } from '@angular/router';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { MenuComponent } from '@app/layouts/menu/menu.component';
import { HeaderComponent } from '@app/layouts/header/header.component';
import { HmisCommonModule } from '@app/modules/common/hmis-common.module';
import { MomentModule } from 'ngx-moment';

@NgModule({
    declarations: [
        IconNavComponent,
        MenuComponent,
        MainContentComponent,
        FooterComponent,
        HeaderComponent,
    ],
    exports: [
        IconNavComponent,
        MenuComponent,
        MainContentComponent,
        FooterComponent,
        HeaderComponent,
    ],
    imports: [CommonModule, RouterModule, NgbTooltipModule, HmisCommonModule, MomentModule],
})
export class LayoutsModule {}
