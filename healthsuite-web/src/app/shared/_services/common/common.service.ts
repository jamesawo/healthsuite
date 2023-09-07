import {ComponentFactoryResolver, Injectable} from '@angular/core';
import { Location } from '@angular/common';
import { DatePayload } from '@app/shared/_payload';
import { DepartmentPayload } from '@app/modules/settings';
import { LocationService } from '@app/shared/_services/settings/location.service';
import { UserPayload } from '@app/modules/settings/_payload/userPayload';
import { AuthService } from '@app/shared/_services/auth/auth.service';
import Swal, { SweetAlertIcon, SweetAlertResult } from 'sweetalert2';
import { HttpClient } from '@angular/common/http';
import { environment } from '@environments/environment';
import {
    DrugRegisterPayload,
    PharmacyBillItem,
    PharmacyLocationTypeEnum,
} from '@app/shared/_payload/pharmacy/pharmacy.payload';
import { GlobalSettingService, ModalPopupService } from '@app/shared/_services';
import { DomSanitizer } from '@angular/platform-browser';
import { ShiftManagerService } from '@app/shared/_services/shift/shift-manager.service';
import { PatientBill, PaymentTypeForEnum } from '@app/shared/_payload/bill-payment/bill.payload';
import { AppStorageService } from '@app/shared/_services/common/app-storage.service';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';
import {LabResultDirective} from '@app/shared/_directives/lab/lab-result.directive';
import {ILabResultComponent} from '@app/shared/_payload/lab/lab-result.payload';

@Injectable({
    providedIn: 'root',
})
export class CommonService {
    basePath: string = environment.clientBaseDir;
    imageList = ['/assets/img/vector/empty_page.svg', '/assets/img/vector/time_ticking.svg'];
    defaultImage = '/assets/img/vector/empty_page.svg';

    private defaultAvatar = 'assets/img/avatar.png';
    public useLocation: boolean;

    constructor(
        private locationService: LocationService,
        private authService: AuthService,
        private http: HttpClient,
        private location: Location,
        private modalService: ModalPopupService,
        private sanitizer: DomSanitizer,
        private shiftService: ShiftManagerService,
        private appStorage: AppStorageService,
        private globalSettingService: GlobalSettingService
    ) {}

    public getImage() {
        const imgPath = this.imageList[Math.floor(Math.random() * this.imageList.length)];
        this.defaultImage = this.basePath + imgPath;
        return this.defaultImage;
    }

    public getCurrentDate(): DatePayload {
        const now = new Date();
        return {
            year: now.getFullYear(),
            month: now.getMonth() + 1,
            day: now.getDate(),
            hour: now.getHours(),
            min: now.getMinutes(),
        };
    }

    public getCurrentLocation(): DepartmentPayload {
        if (this.isEnforceLocation() === true || this.locationService.locationSetting?.id) {
            return this.locationService.locationSetting;
        } else {
            let store = this.appStorage.getStore(HmisConstants.DEFAULT_LOCATION_KEY);
            if (!store) {
                this.locationService.onGetDefaultLocation().toPromise().then(loc => {
                    this.locationService.onSetLocation(loc);
                    store = loc;
                });
            }
            return store;
        }
    }

    public getLocationCategory(): string {
        return this.locationService.onGetLocationCategory();
    }

    public getCurrentUser(): UserPayload {
        const res: UserPayload = new UserPayload();
        const user = this.authService.currentUserValue;
        if (user) {
            res.id = user.userId;
            res.label = `${user.firstName} ${user.lastName}`;
            res.userName = user.username;
        }
        return res;
    }

    public isLocationMatch(location: string): boolean {
        if (this.isEnforceLocation() === true) {
            const locationCat = this.getLocationCategory();
            if (!locationCat) {
                return false;
            }
            // get category id & check against current location
            return locationCat.toLowerCase().includes(location.toLowerCase());
        } else {
            return true;
        }
    }

    public checkLocationAndType(location: string, type: PharmacyLocationTypeEnum): boolean {
        const cLocation = this.getCurrentLocation();
        const isMatch = this.isLocationMatch(location);

        if (type && type === PharmacyLocationTypeEnum.STORE) {
            return isMatch && cLocation.name.toLowerCase().includes('store');
        }
        return isMatch;
    }

    public isPharmacyStoreLocation(locationName: string): boolean {
        const isLocation = this.isLocationMatch(locationName);
        return isLocation && locationName.toLowerCase().includes('store');
    }

    public isPharmacyLocation(location: DepartmentPayload): boolean {
        if (location) {
            return location.departmentCategory.name.toLowerCase().includes('pharmacy');
        }
        return false;
    }

    public isPharmacyStore(locationName: string): boolean {
        return locationName && locationName.toLowerCase().includes('store');
    }

    public isNumberMatch(event): boolean {
        const allowedRegex = /[0-9.]/g;
        if (!event.key.match(allowedRegex)) {
            return false;
        }
        return event.target.value >= 0;
    }

    public stripAndJoin(value: string): string {
        return value.indexOf(' ') >= 0 ? value.split(' ').join('_') : value;
    }

    public flagLocationError() {
        if (this.isEnforceLocation() === true) {
            Swal.fire({
                html: 'CALL SUPPORT FOR HELP',
                title: 'SYSTEM LOCATION IS INVALID',
                allowOutsideClick: false,
                icon: 'error',
                confirmButtonText: 'GO BACK',
                cancelButtonText: 'LOG OUT',
                cancelButtonColor: 'red',
                showCancelButton: true,
                didOpen(popup: HTMLElement) {},
            }).then((result) => {
                if (result.isConfirmed) {
                    this.location.back();
                } else if (result.isDismissed) {
                    this.authService.logout();
                }
            });
        }
    }

    public isEnforceLocation() {
        return this.globalSettingService.isEnforceLocationValue;
    }

    public messageArrayToString(messages: string[]): string {
        let message = '';
        if (messages.length) {
            for (const er of messages) {
                message += `${er} <br>`;
            }
        }
        return message;
    }

    public getDateStamp(): any {
        return Math.floor(Date.now() / 1000);
    }

    public transformToDate(datePayload: DatePayload) {
        return new Date(
            Number(datePayload.year),
            Number(datePayload.month) - 1,
            Number(datePayload.day),
            datePayload.hour,
            datePayload.min
        );
    }

    public calculateBmi(weight: number, height: number) {
        // weight (kg) / [height (m)]2
        if (weight > 0 && height > 0) {
            const hSq = Math.pow(height, 2);
            return weight / hSq;
        }
        return 0;
    }

    public async askAreYouSure(
        question: string,
        title: string,
        icon: SweetAlertIcon
    ): Promise<SweetAlertResult> {
        return await Swal.fire({
            html: `${question}`,
            title: `${title}`,
            allowOutsideClick: false,
            icon,
            confirmButtonText: 'YES',
            cancelButtonText: 'NO',
            showCancelButton: true,
            didOpen(popup: HTMLElement) {},
        });
    }

    public onScroll(top: number, left: number) {
        window.scroll({ top, left, behavior: 'smooth' });
    }

    public isDrugItemDuplicateInList(
        payload: DrugRegisterPayload,
        pharmacyBillItems: PharmacyBillItem[]
    ): boolean {
        let flag = false;
        pharmacyBillItems.forEach((item) => {
            if (item.drugRegister.id === payload.id) {
                flag = true;
            }
        });
        return flag;
    }

    public onRemoveAllCheckDrugItem(isAllChecked: boolean, pharmacyBillList: PharmacyBillItem[]) {
        // todo: refactor this block
        if (isAllChecked === true) {
            for (let i = 0; i < pharmacyBillList.length; i++) {
                pharmacyBillList.splice(i, 1);
            }
        } else {
            for (let i = 0; i < pharmacyBillList.length; i++) {
                if (pharmacyBillList[i].checked) {
                    pharmacyBillList.splice(i, 1);
                }
            }
        }
    }

    public onCloseModal() {
        this.modalService.onCloseModal();
    }

    public onGetBase64AsImageUrl(image: string) {
        if (image) {
            return this.sanitizer.bypassSecurityTrustResourceUrl(`data:image/png;base64, ${image}`);
        } else {
            return this.defaultAvatar;
        }
    }

    public onToggleInputType(input: any, icon: any): any {
        const iconClass: string = icon.className;
        icon.className = iconClass.includes('slash')
            ? 'far fa-eye password-toggle'
            : 'far fa-eye-slash password-toggle';
        input.type = input.type === 'password' ? 'text' : 'password';
    }

    public lookUpCashierShift() {
        const user = this.getCurrentUser().id;
        this.shiftService
            .onLookUpShiftCode(user)
            .toPromise()
            .then((result) => {
                if (result) {
                    this.authService.addShiftNumber(result.shiftNumber);
                }
            });
    }

    public hasBillItems(bill: PatientBill): boolean {
        let result = true;
        const type = bill.billTypeFor;
        const hasServiceBillItems = bill.billItems.length > 0;
        const hasDrugBillItems = bill.pharmacyBillItems.length > 0;
        if (type === PaymentTypeForEnum.DRUG && hasDrugBillItems === false) {
            result = false;
        } else if (type === PaymentTypeForEnum.SERVICE && hasServiceBillItems === false) {
            result = false;
        }
        return result;
    }

    public isValidDatePayload(date: DatePayload) {
        return date?.year && date?.month && date?.day;
    }

    public removeUnderscore(text: string): string {
        if (text && text.length) {
            return text.replace(/_/g, ' ');
        }
        return '';
    }
}
