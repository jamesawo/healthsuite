import { Component, OnDestroy, OnInit } from '@angular/core';
import { NgxFileDropEntry, FileSystemFileEntry, FileSystemDirectoryEntry } from 'ngx-file-drop';
import { FileUploadTypeEnum, IModalPopup } from '@app/shared/_payload';
import { FileUploadService } from '@app/shared/_services/others/file-upload.service';
import { Subscription } from 'rxjs';
import { AuthService } from '@app/shared/_services/auth/auth.service';
import { ToastrService } from 'ngx-toastr';
import { HttpEvent, HttpEventType } from '@angular/common/http';

@Component({
    selector: 'app-upload-file',
    templateUrl: './upload-file.component.html',
    styleUrls: ['./upload-file.component.css'],
})
export class UploadFileComponent implements OnInit, OnDestroy, IModalPopup {
    data: { uploadTypeEnum: FileUploadTypeEnum };
    progress = 0;
    uploadRes: any;

    public files: NgxFileDropEntry[] = [];
    public rawFile: File;

    private subscription: Subscription = new Subscription();
    private fileDropped: NgxFileDropEntry;

    constructor(
        private uploadService: FileUploadService,
        private authService: AuthService,
        private toast: ToastrService
    ) {}

    ngOnInit(): void {}

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    public dropped(files: NgxFileDropEntry[]) {
        this.files = files;
        for (const droppedFile of files) {
            // Is it a file?
            if (droppedFile.fileEntry.isFile) {
                if (!this.isFileAllowed(droppedFile.fileEntry.name)) {
                    this.toast.error('Invalid File Type', 'File Not Accepted');
                    return;
                }
                const fileEntry = droppedFile.fileEntry as FileSystemFileEntry;
                fileEntry.file((file: File) => {
                    this.fileDropped = droppedFile;
                    this.rawFile = file;
                });
            } else {
                // It was a directory (empty directories are added, otherwise only files)
                const fileEntry = droppedFile.fileEntry as FileSystemDirectoryEntry;
            }
        }
    }

    public fileOver(event) {}

    public fileLeave(event) {}

    public clearFiles() {
        this.files = [];
        this.rawFile = null;
        this.progress = 0;
        this.uploadRes = null;
    }

    public onUploadFile() {
        const file = this.rawFile;
        const relativePath = this.fileDropped.relativePath;
        this.subscription.add(
            this.uploadService.upload({ file, relativePath }, this.data.uploadTypeEnum).subscribe(
                (event: HttpEvent<any>) => {
                    switch (event.type) {
                        case HttpEventType.Sent:
                            console.log('Request has been made!');
                            break;
                        case HttpEventType.ResponseHeader:
                            console.log('Response header has been received!');
                            break;
                        case HttpEventType.UploadProgress:
                            this.progress = Math.round((event.loaded / event.total) * 100);
                            console.log(`Uploaded! ${this.progress}%`);
                            break;
                        case HttpEventType.Response:
                            if (event.body) {
                                this.toast.success(
                                    'File Uploaded Successfully',
                                    'Upload Successful'
                                );
                            } else {
                                this.toast.error('FAILED TO UPLOAD DATA', 'INVALID/EMPTY ROWS');
                            }
                            this.uploadRes = event.body;
                            setTimeout(() => {
                                this.progress = 0;
                            }, 1500);
                    }
                },
                (error) => {
                    if (error.status === 401) {
                        this.toast.error('Access Token Expired', 'Try Again');
                        this.authService.logout();
                    } else {
                        this.progress = 0;
                        this.toast.error(error.message, 'Uploading Failed, Try Again');
                    }
                }
            )
        );
    }

    protected isFileAllowed(fileName: string) {
        let isFileAllowed = false;
        const allowedFiles = ['.xlsx', '.xls'];
        const regex = /(?:\.([^.]+))?$/;
        const extension = regex.exec(fileName);

        if (undefined !== extension && null !== extension) {
            for (const ext of allowedFiles) {
                if (ext === extension[0]) {
                    isFileAllowed = true;
                }
            }
        }
        return isFileAllowed;
    }
}
