import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
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

  verifyOtp(payload: VerifyOtpRequest, headers: HttpHeaders): Observable<SignInResponse> {
    return this.http.post<SignInResponse>(
      `${this.baseUrl}/verify`,
      payload,
      { headers }
    );
  }

  resetOtp(headers: HttpHeaders): Observable<SignUpRequest> {
    return this.http.get<SignUpRequest>(
      `${this.baseUrl}/reset-otp`,
      {headers}
    );
  }

  signin(payload: SignInRequest): Observable<SignInResponse> {
    return this.http.post<SignInResponse>(`${this.baseUrl}/sign-in`, payload);
  }
}
