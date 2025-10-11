import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private baseUrl = 'http://localhost:8080/user-management/api/';
  signup(payload: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/signup`, payload);
  }

  signin(payload: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/signin`, payload);
  }
}
