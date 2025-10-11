import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { SignUpRequest } from './payloads/requests/signup-request';
import { OTPResponse } from './payloads/responses/otp.response';
import { VerifyOtpRequest } from './payloads/requests/verify-otp-request';
import { SignInResponse } from './payloads/responses/sign-in-response';
import { SignInRequest } from './payloads/requests/signin-request';

@Injectable({
  providedIn: 'root'
})

export class Auth {
  private baseUrl = 'http://localhost:8080/user-management/api/v1/auth';
  
  constructor(private http: HttpClient) {}

  signup(payload: SignUpRequest): Observable<OTPResponse> {
    return this.http.post<OTPResponse>(`${this.baseUrl}/sign-up`, payload);
  }

  verifyOtp(payload: VerifyOtpRequest): Observable<SignInResponse> {
    const otpToken = localStorage.getItem('otpToken');
    if (!otpToken) throw new Error('OTP token missing');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${otpToken}`);

    return this.http.post<SignInResponse>(`${this.baseUrl}/verify`, payload,{ headers }).pipe(
      tap((response: SignInResponse) => {
        localStorage.removeItem('otpToken');
        if (response.accessToken) {
          localStorage.setItem('accessToken', response.accessToken);
        }
        if (response.refreshToken) {
          localStorage.setItem('refreshToken', response.refreshToken);
        }
      })
    )
  }

   refreshToken(): Observable<SignInResponse> {
    const refreshToken = localStorage.getItem('refreshToken');
    if (!refreshToken) throw new Error('No refresh token');

    const headers = new HttpHeaders().set('Authorization', `Bearer ${refreshToken}`);
    return this.http.post<SignInResponse>(`${this.baseUrl}/refresh-token`, {}, { headers }).pipe(
      tap((response: SignInResponse) => {
        if (response.accessToken) {
          localStorage.setItem('accessToken', response.accessToken);
        }
        if (response.refreshToken) {
          localStorage.setItem('refreshToken', response.refreshToken);
        }
      })
    );
  }

  signout() {
    const accessToken = localStorage.getItem('accessToken');
    if (!accessToken) throw new Error('No access token');

    const headers = new HttpHeaders().set('Authorization', `Bearer ${accessToken}`);
    
    return this.http.post<SignInResponse>(`${this.baseUrl}/sign-in`, payload)
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('otpToken');
  }

  resetOtp(headers: HttpHeaders) {
    return this.http.get(`${this.baseUrl}/reset-otp`, {headers});
  }

  signin(payload: SignInRequest): Observable<SignInResponse> {
    return this.http.post<SignInResponse>(`${this.baseUrl}/sign-in`, payload).pipe(
      tap((response: SignInResponse) => {
        if (response.accessToken) {
          localStorage.setItem('accessToken', response.accessToken);
        }
        if (response.refreshToken) {
          localStorage.setItem('refreshToken', response.refreshToken);
        }
        if (response.OTPToken) {
          localStorage.setItem('otpToken', response.OTPToken);
        }
      })
    );
  }
}
