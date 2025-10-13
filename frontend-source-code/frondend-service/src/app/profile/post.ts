import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, forkJoin, map, switchMap } from 'rxjs';
import { PostResponse } from '../payloads/responses/post-response';
import { PaginationRequest } from '../payloads/requests/pagination-request';
import { FileService } from './file';
import { CreatePostRequest } from '../payloads/requests/create-post-request';
import { CreateMediaEntityResponse } from '../payloads/responses/create-media-entity-response';
import { UpdatePostRequest } from '../payloads/requests/update-post-request';

@Injectable({
  providedIn: 'root'
})
export class Post {
  private readonly apiUrl = 'http://localhost:8081/post-management/api/v1/post';

  constructor(private http: HttpClient, private file: FileService) {}

  getPagination(payload: PaginationRequest): Observable<Page<PostResponse>> {
    return this.http.post<Page<PostResponse>>(`${this.apiUrl}/pagination`, payload);
  }


  createPost(payload: CreatePostRequest): Observable<CreateMediaEntityResponse> {
    return this.http.post<CreateMediaEntityResponse>(this.apiUrl, payload);
  }

  deletePost(postId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${postId}`);
  }

  editPost(postId: string, editedContent: string) {
    const payload: UpdatePostRequest = {
      id: postId,
      content: editedContent
    };
    return this.http.post<CreateMediaEntityResponse>(`${this.apiUrl}/update`, payload);
  }
}