import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class Position {
  
}
@Injectable({
  providedIn: 'root'
})
export class Governorate {
  private baseUrl = 'http://localhost:8080/user-management/api/v1/governorate';

  constructor(private http: HttpClient) {}

  getAll(): Observable<ObjectResponse[]> {
    return this.http.get<ObjectResponse[]>(`${this.baseUrl}`);
  }
}