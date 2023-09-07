import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { SeedDataService } from '@app/shared/_services';
import { WardPayload, WardUpdateTypeEnum } from '@app/modules/settings';
import { Subscription } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import { NgxSpinnerService } from 'ngx-spinner';
import { shakeAnimation } from 'angular-animations';
import { PopoverDirective } from 'ngx-bootstrap/popover';
import { HmisConstants } from '@app/shared/_models/constant/hmisConstants';

@Component({
    selector: 'app-seed-bed',
    templateUrl: './seed-bed.component.html',
    styleUrls: ['./seed-bed.component.css'],
    animations: [shakeAnimation()],
})
export class SeedBedComponent implements OnInit, OnDestroy {
    public isSubmitted = false;
    public wardDropDownList: WardPayload[];
    public wardTableList: WardPayload[] = [];
    public wardFormPayload: WardPayload = new WardPayload();
    public subscriptions = new Subscription();
    public shakeBox: boolean = false;
    public numberOfBedsToUpdate: number = 0;
    public add = WardUpdateTypeEnum.ADD_BED;
    public remove = WardUpdateTypeEnum.REMOVE_BED;
    @ViewChild('f') form: any;

    constructor(
        private service: SeedDataService,
        private toast: ToastrService,
        private spinner: NgxSpinnerService
    ) {}

    ngOnInit(): void {
        this.subscriptions.add(
            this.service.getAllWard().subscribe((result) => {
                this.wardDropDownList = result.data;
            })
        );

        this.subscriptions.add(
            this.service.getWardsAndBeds().subscribe((result) => {
                this.wardTableList = result.data;
            })
        );
    }

    ngOnDestroy(): void {
        this.subscriptions.unsubscribe();
    }

    public onCreateBed() {
        this.isSubmitted = true;
        if (!this.isValid(this.wardFormPayload)) {
            this.toast.error('Invalid Form', 'Error, Failed!');
            return;
        }
        if (this.isWardExist(this.wardFormPayload)) {
            this.toast.error('Try updating bed count instead', 'Duplicate Detected!');
            return;
        }
        this.spinner.show().then();
        this.subscriptions.add(
            this.service.onAddBedsToWard(this.wardFormPayload).subscribe(
                (result) => {
                    this.spinner.hide().then();
                    this.toast.success('Created Successfully', 'Success!');
                    result.data.totalBedCount = this.wardFormPayload.totalBedCount;
                    this.addNewWardToWardList(result.data);
                    this.isSubmitted = false;
                },
                (error) => {
                    this.spinner.hide().then();
                    this.isSubmitted = false;
                    this.toast.error('Something Went Wrong', 'Error, Try Again!');
                }
            )
        );
    }

    public onToggleShake() {
        this.numberOfBedsToUpdate = 0;
        this.shakeBox = false;
        setTimeout(() => {
            this.shakeBox = !this.shakeBox;
        }, 500);
    }

    public onUpdateNumberOfBeds(wardId: number, pop: PopoverDirective, type: WardUpdateTypeEnum) {
        pop.hide();
        if (type == this.add) {
            this.onAddBedsToWard(wardId);
        } else if (type == this.remove) {
            this.onSubtractBedsFromWard(wardId);
        }
    }

    protected onAddBedsToWard(ward: number) {
        if (this.numberOfBedsToUpdate < 1) {
            this.toast.error('Enter number of beds', 'Invalid Entry');
            return;
        }
        this.spinner.show().then();
        const wardToUpdate = new WardPayload();
        wardToUpdate.id = ward;
        wardToUpdate.numberOfBedsToUpdate = this.numberOfBedsToUpdate;
        wardToUpdate.type = WardUpdateTypeEnum.ADD_BED;
        this.subscriptions.add(
            this.service.onUpdateWardBedCount(wardToUpdate).subscribe(
                (result) => {
                    this.spinner.hide().then();
                    if (result.status == 200) {
                        if (result.body.data === true) {
                            this.toast.success('Bed Added Successfully', 'Successful');
                            this.wardTableList.find(
                                (value) => value.id == ward
                            ).totalBedCount += this.numberOfBedsToUpdate;
                            this.numberOfBedsToUpdate = 0;
                        } else {
                            this.toast.error(HmisConstants.ERR_TITLE);
                        }
                    }
                },
                (error) => {
                    this.spinner.hide().then();
                    this.toast.error(error.error.message, 'Failed!');
                }
            )
        );
    }

    protected onSubtractBedsFromWard(ward: number) {
        if (this.numberOfBedsToUpdate < 1) {
            this.toast.error('Enter number of beds', 'Invalid Entry');
            return;
        }
        this.spinner.show().then();
        const wardToUpdate = new WardPayload();
        wardToUpdate.id = ward;
        wardToUpdate.numberOfBedsToUpdate = this.numberOfBedsToUpdate;
        wardToUpdate.type = WardUpdateTypeEnum.REMOVE_BED;
        this.subscriptions.add(
            this.service.onUpdateWardBedCount(wardToUpdate).subscribe(
                (result) => {
                    this.spinner.hide().then();
                    if (result.status == 200) {
                        if (result.body.data == true) {
                            this.toast.success('Action Successful', 'Update Successful');
                            // popRemove.hide();
                            this.wardTableList.find(
                                (value) => value.id == ward
                            ).totalBedCount -= this.numberOfBedsToUpdate;
                            this.numberOfBedsToUpdate = 0;
                        } else {
                            this.toast.error(HmisConstants.ERR_TITLE, 'Failed, Try Again');
                        }
                    }
                },
                (error) => {
                    this.spinner.hide().then();
                    this.toast.error(error.error.message, 'Failed!');
                }
            )
        );
    }

    protected isValid(payload: WardPayload): boolean {
        return payload.departmentId && payload.totalBedCount < 101;
    }

    protected isWardExist(ward: WardPayload): boolean {
        const wardPayload = this.wardTableList.find((value) => {
            return value.departmentId == ward.departmentId;
        });
        return !!wardPayload;
    }

    protected addNewWardToWardList(ward: WardPayload): void {
        if (ward) {
            this.wardTableList.push(ward);
            this.form.reset();
        }
    }
}
