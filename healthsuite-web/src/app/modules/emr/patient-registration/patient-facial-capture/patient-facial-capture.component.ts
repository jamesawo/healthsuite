import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { WebcamImage, WebcamInitError, WebcamUtil } from 'ngx-webcam';
import { CommonService } from '@app/shared/_services/common/common.service';

@Component({
    selector: 'app-patient-facial-capture',
    templateUrl: './patient-facial-capture.component.html',
    styleUrls: ['./patient-facial-capture.component.css'],
})
export class PatientFacialCaptureComponent implements OnInit {
    @Output()
    public pictureTaken = new EventEmitter<WebcamImage>();
    // toggle webcam on/off
    public showImage = false;
    public captureData = {
        height: 150,
        width: 200,
    };
    public showWebcam = false;
    public allowCameraSwitch = true;
    public multipleWebcamsAvailable = false;
    public deviceId: string;
    public videoOptions: MediaTrackConstraints = {
        // width: { ideal: 1024}
        // height: {ideal: 576}
    };
    public errors: WebcamInitError[] = [];
    // latest snapshot
    public webcamImage: WebcamImage = null;
    // webcam snapshot trigger
    private trigger: Subject<void> = new Subject<void>();
    // switch to next / previous / specific webcam; true/false: forward/backwards, string: deviceId
    private nextWebcam: Subject<boolean | string> = new Subject<boolean | string>();

    constructor(private commonService: CommonService) {}

    public ngOnInit(): void {
        WebcamUtil.getAvailableVideoInputs().then((mediaDevices: MediaDeviceInfo[]) => {
            this.multipleWebcamsAvailable = mediaDevices && mediaDevices.length > 1;
        });
    }

    public triggerSnapshot(): void {
        this.showImage = true;
        this.trigger.next();
        this.toggleWebcam('yes');
    }

    public toggleWebcam(showImage?: string): void {
        this.showWebcam = !this.showWebcam;
        this.showImage = showImage === 'yes';
    }

    public handleInitError(error: WebcamInitError): void {
        this.errors.push(error);
    }

    public showNextWebcam(directionOrDeviceId: boolean | string): void {
        // true => move forward through devices
        // false => move backwards through devices
        // string => move to device with given deviceId
        this.nextWebcam.next(directionOrDeviceId);
    }

    public handleImage(webcamImage: WebcamImage): void {
        // console.info('received webcam image', webcamImage);
        this.webcamImage = webcamImage;
        this.pictureTaken.emit(webcamImage);
    }

    public base64ToFile(imageBlob: Blob, imageName: string) {
        return new File([imageBlob], imageName, {
            type: 'image/jpeg',
        });
    }

    public cameraWasSwitched(deviceId: string): void {
        // console.log('active device: ' + deviceId);
        this.deviceId = deviceId;
    }

    public get triggerObservable(): Observable<void> {
        return this.trigger.asObservable();
    }

    public get nextWebcamObservable(): Observable<boolean | string> {
        return this.nextWebcam.asObservable();
    }

    public onRestImage() {
        this.webcamImage = null;
        this.showImage = false;
    }

    public onSetDefaultOrPreviousImage(base64: string) {
        this.showImage = true;
        this.showWebcam = false;
        const url: any = this.commonService.onGetBase64AsImageUrl(base64);
        this.webcamImage = new WebcamImage(url, 'image/jpeg', {
            height: this.captureData.height,
            width: this.captureData.width,
            data: null,
        });
    }
}
