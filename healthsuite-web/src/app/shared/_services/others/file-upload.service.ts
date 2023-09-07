import { Injectable } from '@angular/core';
import { HttpBackend, HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { environment } from '@environments/environment';
import { LocalStorageService } from 'ngx-webstorage';
import { Observable, throwError } from 'rxjs';
import { FileUploadTypeEnum } from '@app/shared/_payload';

@Injectable({
    providedIn: 'root',
})
export class FileUploadService {
    url = environment.apiEndPoint + '/file';
    private httpClient: HttpClient;

    constructor(handler: HttpBackend, private $localStorage: LocalStorageService) {
        this.httpClient = new HttpClient(handler);
    }

    public upload(data: { file: File; relativePath: string }, type: FileUploadTypeEnum) {
        const token = this.$localStorage.retrieve('token');
        const { file, relativePath } = data;
        const formData = new FormData();
        formData.append('file', file);

        const headers = new HttpHeaders().set('Authorization', token);
        let endpoint = '';
        if (type) {
            if (type == FileUploadTypeEnum.SERVICE) {
                endpoint = `${this.url}/upload/services`;
            } else if (type == FileUploadTypeEnum.DRUG) {
                endpoint = `${this.url}/upload/drugs`;
            }
        } else {
            throwError('type of FileUploadTypeEnum is required');
            return;
        }

        return this.httpClient
            .post<any>(`${endpoint}`, formData, {
                headers: headers,
                reportProgress: true,
                observe: 'events',
            })
            .pipe(catchError(this.errorMgmt));
        // .pipe(
        //     map((event) => {
        //         switch (event.type) {
        //             case HttpEventType.UploadProgress:
        //                 const progress = Math.round((100 * event.loaded) / event.total);
        //                 return { status: 'progress', message: progress };
        //
        //             case HttpEventType.Response:
        //                 return event.body;
        //             default:
        //                 return `Unhandled event: ${event.type}`;
        //         }
        //     })
        // );
    }

    errorMgmt(error: HttpErrorResponse) {
        let errorMessage = '';
        if (error.error instanceof ErrorEvent) {
            // Get client-side error
            errorMessage = error.error.message;
        } else {
            // Get server-side error
            errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
        }
        console.log(errorMessage);
        return throwError(errorMessage);
    }

    public onDownloadFile(file: string | undefined): Observable<Blob> {
        const token = this.$localStorage.retrieve('token');
        const headers = new HttpHeaders().set('Authorization', token);
        return this.httpClient.get(`${this.url}/download/${file}`, {
            responseType: 'blob',
            headers,
        });
    }
}
