import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { SignUpRequest } from './payloads/requests/signup-request';
import { OTPResponse } from './payloads/responses/otp.response';
import { VerifyOtpRequest } from './payloads/requests/verify-otp-request';
import { SignInResponse } from './payloads/responses/sign-in-response';
import { SignInRequest } from './payloads/requests/signin-request';
import { response } from 'express';

@Injectable({
  providedIn: 'root'
})

export class Auth {
  private baseUrl = 'http://localhost:8080/user-management/api/v1/auth';
  
  constructor(private http: HttpClient) {}

  signup(payload: SignUpRequest): Observable<OTPResponse> {
    return this.http.post<OTPResponse>(`${this.baseUrl}/sign-up`, payload).pipe(
      tap((response: OTPResponse) => {
        if(response.OTPToken) {
          localStorage.setItem("otpToken", response.OTPToken);
        }
      })
    );
  }

  verifyOtp(payload: VerifyOtpRequest): Observable<SignInResponse> {
    const otpToken = localStorage.getItem('otpToken');
    if (!otpToken) throw new Error('OTP token missing');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${otpToken}`);

    return this.http.post<SignInResponse>(`${this.baseUrl}/verify`, payload,{ headers }).pipe(
      tap((response: SignInResponse) => {
        localStorage.removeItem('otpToken');
        if (response.accessToken && response.refreshToken && response.user) {
          localStorage.setItem('accessToken', response.accessToken);
          localStorage.setItem('refreshToken', response.refreshToken);
          localStorage.setItem('user', JSON.stringify(response.user));
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
    return this.http.post(`${this.baseUrl}/sign-out`, {}).subscribe({
      next: () => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('otpToken');
        localStorage.removeItem('user');
      }
    })
  }


  resetOtp() {
    const otpToken = localStorage.getItem('otpToken');
    if (!otpToken) throw new Error('OTP token missing');

    const headers = new HttpHeaders().set('Authorization', `Bearer ${otpToken}`);
    return this.http.get(`${this.baseUrl}/reset-otp`, {headers});
  }

  signinWithToken(): Observable<SignInResponse> {
  return this.http.post<SignInResponse>(`${this.baseUrl}/sign-in-with-token`, {}).pipe(
    tap((response: SignInResponse) => {
      if (response.accessToken && response.user) { 
        localStorage.setItem('accessToken', response.accessToken);
        console.log(response.user);
        localStorage.setItem('user', JSON.stringify(response.user));
      }
    })
  );
}

  signin(payload: SignInRequest): Observable<SignInResponse> {
    return this.http.post<SignInResponse>(`${this.baseUrl}/sign-in`, payload).pipe(
      tap((response: SignInResponse) => {
        if (response.accessToken && response.refreshToken && response.user) {
          localStorage.setItem('accessToken', response.accessToken);
          localStorage.setItem('refreshToken', response.refreshToken);
          localStorage.setItem('user', JSON.stringify(response.user));
        } else if (response.OTPToken) {
          localStorage.setItem('otpToken', response.OTPToken);
        }
      })
    );
  }
}
