import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Route, Router } from '@angular/router';
import { PostResponse } from './payloads/responses/post-response';
import { PaginationRequest } from './payloads/requests/pagination-request';
import { UserBasicData } from '../auth/payloads/responses/user-basic-data';
import { Auth } from '../auth/auth';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})
@Component({
  selector: 'app-signin',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './signin.html',
  styleUrl: './signin.css'
})
export class Home implements OnInit{
  loadingPosts = false;
  user?: UserBasicData;
  postsPage: Page<PostResponse> = { content: [], page: 0, size: 10, totalElements: 0, totalPages: 0 };
  suggestionsPage: Page<SuggestionFriend> = { content: [], page: 0, size: 5, totalElements: 0, totalPages: 0 };

  constructor(
    private auth: Auth,
    private http: HttpClient,
    private router: Router
  ) {}

  ngOnInit() {
    const userStr = localStorage.getItem('user');
    if(!userStr || userStr == '') {
      this.router.navigate(['/signin']);
    }
    if (userStr) this.user = JSON.parse(userStr);
    this.loadPosts(0);
    this.loadSuggestions(0);
  }

  loadPosts(page: number, append: boolean = false) {
    if (!this.user) return;

    this.loadingPosts = true;

    const requestBody: PaginationRequest = {
      page: page,
      size: this.postsPage.size
    };

    this.http.post<Page<PostResponse>>(
      'http://localhost:8081/post-management/api/v1/post/pagination', 
      requestBody
    ).subscribe({
      next: (data) => {
        if (append) {
          this.postsPage.content = [...this.postsPage.content, ...data.content];
          this.postsPage.page = data.page;
          this.postsPage.totalPages = data.totalPages;
          this.postsPage.totalElements = data.totalElements;
        } else {
          this.postsPage = data;
        }
        this.loadingPosts = false;
      },
      error: (err) => {
        console.error('Failed to load posts', err);
        this.loadingPosts = false;
      }
    });
  }

  loadSuggestions(page: number) {
    if (!this.user) return;

    const requestBody: PaginationRequest = {
      page: page,
      size: this.suggestionsPage.size
    };

    this.http.post<Page<SuggestionFriend>>(
      'http://localhost:8080/user-management/api/v1/user/suggestions', 
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

    follow(suggestion: SuggestionFriend) {
    this.http.post(`user-management/api/v1/follow/${suggestion.email}`, {}).subscribe({
      next: () => {
        alert(`You are now following ${suggestion.firstName}`);
        this.suggestionsPage.content = this.suggestionsPage.content.filter(s => s.email !== suggestion.email);
      },
      error: err => {
        console.error(err);
        alert('Failed to follow user.');
      }
    });
  }

  cancel(suggestion: SuggestionFriend) {
    this.http.post(`user-management/api/v1/unfollow/${suggestion.email}`, {}).subscribe({
      next: () => {
        alert(`Follow request canceled for ${suggestion.firstName}`);
        this.suggestionsPage.content = this.suggestionsPage.content.filter(s => s.email !== suggestion.email);
      },
      error: err => {
        console.error(err);
        alert('Failed to cancel follow request.');
      }
    });
  }

  onPostsScroll(event: any) {
    const target = event.target;
    const threshold = 100;
    const position = target.scrollTop + target.clientHeight;
    const height = target.scrollHeight;

    if (position > height - threshold && !this.loadingPosts && this.postsPage.page + 1 < this.postsPage.totalPages) {
      this.loadPosts(this.postsPage.page + 1, true);
    }
  }

}
