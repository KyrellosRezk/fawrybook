import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CreateReactRequest } from '../payloads/requests/create-react-request';

@Injectable({
  providedIn: 'root'
})
export class React {
  private readonly url = 'http://localhost:8081/post-management/api/v1/react';

  constructor(private http: HttpClient) {}

  react(postId: string, type: 'LIKE' | 'DISLIKE'): Observable<void> {
    const payload: CreateReactRequest = { postId, type };
    return this.http.post<void>(this.url, payload);
  }
}
