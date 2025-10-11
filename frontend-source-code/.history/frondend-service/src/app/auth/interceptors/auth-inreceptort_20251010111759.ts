import { Injectable } from "@angular/core";
import { catchError, switchMap, throwError } from "rxjs";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private auth: AuthService, private router: Router) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = localStorage.getItem('accessToken');
    let cloned = req;

    if (token && !req.url.includes('/signin') && !req.url.includes('/verify-otp')) {
      cloned = req.clone({ headers: req.headers.set('Authorization', `Bearer ${token}`) });
    }

    return next.handle(cloned).pipe(
      catchError(err => {
        if (err.status === 401) {
          if (req.url.includes('/verify-otp')) {
            const otpToken = localStorage.getItem('otpToken');
            if (!otpToken) this.router.navigate(['/signin']); // expired
            else alert('Invalid OTP'); // invalid OTP
            return throwError(() => err);
          }

          // Access token expired → refresh
          return this.auth.refreshToken().pipe(
            switchMap(() => {
              const newToken = localStorage.getItem('accessToken')!;
              const retryReq = req.clone({ headers: req.headers.set('Authorization', `Bearer ${newToken}`) });
              return next.handle(retryReq);
            }),
            catchError(() => {
              this.auth.logout();
              this.router.navigate(['/signin']);
              return throwError(() => err);
            })
          );
        }

        return throwError(() => err);
      })
    );
  }
}