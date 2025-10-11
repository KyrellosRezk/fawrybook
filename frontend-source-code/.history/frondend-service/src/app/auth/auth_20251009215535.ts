import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { SignUpRequest } from './payloads/requests/signup-request';
import { OTPResponse } from './payloads/responses/otp.response';
import { VerifyOtpRequest } from './payloads/requests/verify-otp-request';

@Injectable({
  providedIn: 'root'
})

export class Auth {
  private baseUrl = 'http://localhost:8080/user-management/api/v1/auth';
  
  constructor(private http: HttpClient) {}

  signup(payload: SignUpRequest): Observable<OTPResponse> {
    return this.http.post<OTPResponse>(`${this.baseUrl}/sign-up`, payload);
  }

  verifyOtp(verifyOtpRequest: VerifyOtpRequest, headers: ): 

  signin(payload: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/sign-in`, payload);
  }


}
