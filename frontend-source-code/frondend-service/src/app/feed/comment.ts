import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CreateMediaEntityResponse } from '../payloads/responses/create-media-entity-response';
import { CreateCommentRequest } from '../payloads/requests/create-comment-request';
import { CommentResponse } from '../payloads/responses/get-comments-response';

@Injectable({
  providedIn: 'root'
})
export class Comment {
  private readonly apiUrl = 'http://localhost:8081/post-management/api/v1/comment';

  constructor(private http: HttpClient) {}

  createComment(content: string, postId: string): Observable<CreateMediaEntityResponse> {
    const payload: CreateCommentRequest = {
      content,
      postId
    }
    return this.http.post<CreateMediaEntityResponse>(this.apiUrl, payload);
  }

  getComments(postId: string): Observable<CommentResponse[]> {
    return this.http.get<CommentResponse[]>(`${this.apiUrl}/${postId}`);
  }
}
