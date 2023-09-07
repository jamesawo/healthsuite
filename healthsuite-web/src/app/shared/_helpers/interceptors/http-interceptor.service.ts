import { Injectable } from '@angular/core';
import { LocalStorageService } from 'ngx-webstorage';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '@app/shared/_services/auth/auth.service';

export const InterceptorSkipHeader = 'X-Skip-Interceptor';

@Injectable({
    providedIn: 'root',
})
export class HttpInterceptorService implements HttpInterceptor {
    constructor(private $localStorage: LocalStorageService, private authService: AuthService) {}
    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        const token = this.$localStorage.retrieve('token');

        if (this.authService.isAuthenticated() && token) {
            const cloned = request.clone({
                headers: request.headers
                    .set('Authorization', token)
                    .set('Accept', 'application/json')
                    .set('Content-Type', 'application/json'),
            });
            return next.handle(cloned);
        } else {
            return next.handle(request);
        }
    }
}
