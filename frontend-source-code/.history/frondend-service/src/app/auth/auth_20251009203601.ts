import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})

export interface SignUpRequest {
  username: string;
  firstName: string;
  middleName: string;
  lastName: string;
  phoneNumber: string;
  email: string;
  lng: number;
  lat: number;
  password: string;
  governorateId: string;
  positionId: string;
}

export class Auth {
  private baseUrl = 'http://localhost:8080/user-management/api/';
  
  constructor(private http: HttpClient) {}

  signup(payload: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/v1/auth/sign-up`, payload);
  }

  signin(payload: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/v1/auth/sign-in`, payload);
  }
}
