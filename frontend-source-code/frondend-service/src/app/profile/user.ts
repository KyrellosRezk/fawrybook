import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { UserProfileDataResponse } from '../payloads/responses/user-profile-data-response';
import { Observable } from 'rxjs';
import { PaginationRequest } from '../payloads/requests/pagination-request';
import { SendRequestRequest } from '../payloads/requests/send-request-request';

@Injectable({
  providedIn: 'root'
})
export class User {
  
  private readonly apiUrl = 'http://localhost:8080/user-management/api/v1/user';

  constructor(private http: HttpClient) {}

  getProfileData(profileId: string): Observable<UserProfileDataResponse> {
    return this.http.get<UserProfileDataResponse>(`${this.apiUrl}/id`, {
      params: { id: profileId }
    });
  }

  getSuggestions(payload: PaginationRequest): Observable<Page<SuggestionFriend>> {
    return this.http.post<Page<SuggestionFriend>>(`${this.apiUrl}/suggestions`, payload);
  }
}
