import { Injectable } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError, switchMap, catchError } from 'rxjs';
import { Router } from '@angular/router';
import { Auth } from '../auth';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  private isRefreshing = false;

  // List of URLs that require access token
  private protectedUrls = [
    '/user-management/api/v1/auth/sign-out',
    '/user-management/api/v1/auth/sign-in-with-token',
    '/user-management/api/v1/request/pagination',
    
  ];

  constructor(private auth: Auth, private router: Router) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const isProtected = this.protectedUrls.some(url => req.url.includes(url));

    let authReq = req;
    if (isProtected) {
      const accessToken = localStorage.getItem('accessToken');
      if (accessToken) {
        authReq = req.clone({
          setHeaders: { Authorization: `Bearer ${accessToken}` }
        });
      }
    }

    return next.handle(authReq).pipe(
      catchError(err => {
        if (isProtected && err instanceof HttpErrorResponse && err.status === 401) {
          if (!this.isRefreshing) {
            this.isRefreshing = true;

            return this.auth.refreshToken().pipe(
              switchMap(res => {
                this.isRefreshing = false;
                const newAccessToken = localStorage.getItem('accessToken');
                const retryReq = req.clone({
                  setHeaders: { Authorization: `Bearer ${newAccessToken}` }
                });
                return next.handle(retryReq);
              }),
              catchError(refreshErr => {
                this.isRefreshing = false;
                localStorage.clear();
                this.router.navigate(['/signin']);
                return throwError(() => refreshErr);
              })
            );
          }
        }
        return throwError(() => err);
      })
    );
  }
}