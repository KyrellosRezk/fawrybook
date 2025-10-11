import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class Governorate {
  private baseUrl = 'http://localhost:8080/user-management/api/v1/governorates';

  constructor(private http: HttpClient) {}
  
  getAll(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/all`);
  }
}
