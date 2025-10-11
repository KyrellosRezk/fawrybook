import { Component, OnInit } from '@angular/core';
import { UserBasicData } from '../../auth/payloads/responses/user-basic-data';
import { HttpClient } from '@angular/common/http';
import { Route, Router } from '@angular/router';
import { Auth } from '../../auth/auth';
import { Post } from '../post/post';
import { PostResponse } from './payloads/responses/post-response';
import { PaginationRequest } from './payloads/requests/pagination-request';

@Component({
  selector: 'app-home',
  imports: [],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home implements OnInit{
  user?: UserBasicData;
  postsPage: Page<UserBasicData> = { content: [], page: 0, size: 10, totalElements: 0, totalPages: 0 };
  suggestionsPage: Page<SuggestionFriend> = { content: [], page: 0, size: 5, totalElements: 0, totalPages: 0 };

  constructor(
    private auth: Auth,
    private http: HttpClient,
    private router: Router
  ) {}

  ngOnInit() {
    const userStr = localStorage.getItem('user');
    if (userStr) this.user = JSON.parse(userStr);

    this.loadPosts(0);
    this.loadSuggestions(0);
  }

  loadPosts(page: number) {
    if (!this.user) return;

    const requestBody: PaginationRequest = {
      page: page,
      size: this.postsPage.size
    };

    this.http.post<Page<PostResponse>>(
      'http://localhost:8081/post-management/api/v1/post/pagination', 
      requestBody
    ).subscribe({
      next: (data) => {
        this.postsPage = data;
      },
      error: (err) => {
        console.error('Failed to load posts', err);
      }
    });
  }

  loadSuggestions(page: number) {
    if (!this.user) return;

    const requestBody: PaginationRequest = {
      page: page,
      size: this.suggestionsPage.size,
      filterUserId: this.user.idto exclude them
    };

    this.http.post<Page<SuggestionFriend>>(
      'http://localhost:8081/user-management/api/v1/user/suggestions', 
      requestBody
    ).subscribe({
      next: (data) => {
        this.suggestionsPage = data;
      },
      error: (err) => {
        console.error('Failed to load suggestions', err);
      }
    });
  }
}
