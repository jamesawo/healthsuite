import { Injectable } from '@angular/core';
import {
    HttpErrorResponse,
    HttpEvent,
    HttpHandler,
    HttpInterceptor,
    HttpRequest,
} from '@angular/common/http';
import { AuthService } from '@app/shared/_services/auth/auth.service';

import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable({
    providedIn: 'root',
})
@Injectable()
export class HttpErrorInterceptor implements HttpInterceptor {
    constructor(private authService: AuthService) {}

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return next.handle(request).pipe(
            catchError((error: HttpErrorResponse) => {
                let errorMsg = '';
                if (error.error instanceof ErrorEvent) {
                    console.log('client side error');
                    errorMsg = `Error: ${error.error.message}`;
                } else {
                    console.log('server side error');
                    errorMsg = `Error Code: ${error.error.status},  Message: ${error.error.message}`;
                }
                console.log(errorMsg);
                if (error.status === 401) {
                    this.authService.logout();
                    location.reload(true);
                }
                return throwError(error);
            })
        );
    }
}
