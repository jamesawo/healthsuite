import { Injectable } from '@angular/core';
import {
    CanActivate,
    ActivatedRouteSnapshot,
    RouterStateSnapshot,
    UrlTree,
    Router,
} from '@angular/router';
import { AuthService } from '@app/shared/_services/auth/auth.service';
import { LocalStorageService } from 'ngx-webstorage';
import { StorageService } from 'ngx-webstorage/lib/core/interfaces/storageService';

@Injectable({
    providedIn: 'root',
})
export class CanActivateGuard implements CanActivate {
    constructor(private router: Router, private authService: AuthService) {}

    canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
        // todo create a new guard called isAuthenticated and move this logic in there
        // this guard should check if a user has permission to access the route
        const currentUser = this.authService.isAuthenticated();
        if (currentUser) {
            return true;
        }
        this.router.navigate(['/login'], { queryParams: { redirectURL: state.url } }).then();
        // this.router.navigate(['/login']);
        return false;
    }
}
