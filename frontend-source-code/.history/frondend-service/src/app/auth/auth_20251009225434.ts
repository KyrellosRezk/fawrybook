import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { SignUpRequest } from './payloads/requests/signup-request';
import { OTPResponse } from './payloads/responses/otp.response';
import { VerifyOtpRequest } from './payloads/requests/verify-otp-request';
import { SignInResponse } from './payloads/responses/sign-in-response';

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

  signin(payload: S): Observable<any> {
    return this.http.post(`${this.baseUrl}/sign-in`, payload);
  }


}
