import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router} from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';

import { LoginPayload } from '@app/shared/_payload';
import { AuthService } from '@app/shared/_services/auth/auth.service';
import { Subscription } from 'rxjs';
import { environment } from '@environments/environment';
import {HmisConstants} from '@app/shared/_models/constant/hmisConstants';

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.css'],
})
export class LoginComponent implements OnInit, OnDestroy {
    basePath: string = environment.clientBaseDir;
    version: string = environment.version;
    loginForm: FormGroup;
    loginPayload: LoginPayload;
    isLoading = false;
    isSubmitted = false;
    imageList = [
        'assets/img/vector/undraw_doctors_hwty.png',
        'assets/img/vector/undraw_medical_research_qg4d.png',
        'assets/img/vector/undraw_medical_care_movn.png',
        'assets/img/vector/undraw_medicine_b1ol.png',
    ];
    defaultImage = '';
    sub: Subscription;
    redirectURL: string;

    constructor(
        private authService: AuthService,
        private router: Router,
        private spinner: NgxSpinnerService,
        private toast: ToastrService,
        private route: ActivatedRoute
    ) {
        this.loginForm = new FormGroup(
            {
                username: new FormControl('', [Validators.required]),
                password: new FormControl('', Validators.required),
            },
            { updateOn: 'blur' }
        );

        this.loginPayload = {
            username: '',
            password: '',
        };
    }

    ngOnInit() {
        this.getImage();
        const params = this.route.snapshot.queryParams;
        if (params['redirectURL']) {
            this.redirectURL = params['redirectURL'];
        }
    }

    get lf() {
        return this.loginForm.controls;
    }

    getImage() {
        const imgPath = this.imageList[Math.floor(Math.random() * this.imageList.length)];
        this.defaultImage = this.basePath + imgPath;
    }

    onAuthLogin() {
        this.isSubmitted = true;
        if (this.loginForm.invalid) {
            return;
        }
        this.prepPayloadValue(
            this.loginForm.get('username').value,
            this.loginForm.get('password').value
        );
        this.onLoginUser();
    }

    ngOnDestroy(): void {
        this.sub.unsubscribe();
    }

    onLoginUser() {
        this.isLoading = true;
        this.sub = this.authService.login(this.loginPayload).subscribe(
            (result) => {
                if (result.httpStatusCode === 200) {
                    if (this.redirectURL) {
                        this.router
                            .navigateByUrl(this.redirectURL)
                            .catch(() => this.router.navigate(['/']));
                    } else {
                        this.router.navigate(['/']).then();
                    }
                } else {
                    this.toast.error(result.message);
                    this.isLoading = false;
                }
            },
            (error) => {
                this.isLoading = false;
                if (error.status === 0) {
                    this.toast.error(HmisConstants.NETWORK_ERROR, HmisConstants.CONNECTION_LOST);
                } else {
                    this.toast.error(error.error.message, HmisConstants.ERR_TITLE);
                }
            }
        );
    }

    prepPayloadValue(username: string, password: string) {
        this.loginPayload.username = username;
        this.loginPayload.password = password;
    }

    onEnterKeyPressed(username: string, password: string) {
        this.isSubmitted = true;
        this.prepPayloadValue(username, password);
        this.onLoginUser();
    }
}
