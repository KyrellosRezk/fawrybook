import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PaginationRequest } from '../payloads/requests/pagination-request';
import { UserBasicData } from '../payloads/responses/user-basic-data';
import { SendRequestRequest } from '../payloads/requests/send-request-request';
import { RequestActionRequest } from '../payloads/requests/request-action-request';

@Injectable({
  providedIn: 'root'
})
export class Request {
  private readonly apiUrl = 'http://localhost:8080/user-management/api/v1/request';

  constructor(private http: HttpClient) {}

  getRequests(pagination: PaginationRequest): Observable<Page<UserBasicData>> {
    return this.http.post<Page<UserBasicData>>(`${this.apiUrl}/pagination`, pagination);
  }

  
  sendFriendRequest(receiverId: string): Observable<void> {
    const payload: SendRequestRequest = {
      receiverId,
      action: 'CREATE'
    };
    return this.http.post<void>(`${this.apiUrl}/action`, payload);
  }

  replyToRequest(senderId: string, action: 'APPROVED' | 'DECLINED'): Observable<void> {
    const payload: RequestActionRequest = {
      senderId,
      action
    };
    return this.http.post<void>(`${this.apiUrl}/reply`, payload);
  }
}