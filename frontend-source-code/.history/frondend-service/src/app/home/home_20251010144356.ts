import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
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
export class Home implements OnInit {
  loadingPosts = false;
  loadingUser = true;
  showCreatePost = false;
  newPostContent = '';
  selectedFiles: File[] = [];
  user?: UserBasicData;

  postsPage: Page<PostResponse> = { content: [], page: 0, size: 10, totalElements: 0, totalPages: 0 };
  suggestionsPage: Page<SuggestionFriend> = { content: [], page: 0, size: 5, totalElements: 0, totalPages: 0 };

  constructor(private auth: Auth, private http: HttpClient, private router: Router) {}

  ngOnInit() {
    const userStr = localStorage.getItem('user');
    if (!userStr) {
      this.router.navigate(['/signin']);
      return;
    }
    this.user = JSON.parse(userStr);
    this.loadingUser = false;

    this.loadPosts(0);
    this.loadSuggestions(0);
  }

  loadPosts(page: number, append: boolean = false) {
    if (!this.user || this.loadingPosts) return;
    this.loadingPosts = true;

    const requestBody: PaginationRequest = {
      page,
      size: this.postsPage.size,
      filterUserId: this.user.id
    };

    this.http.post<Page<PostResponse>>(
      'http://localhost:8081/post-management/api/v1/post/pagination',
      requestBody
    ).subscribe({
      next: data => {
        this.postsPage = {
          content: append ? [...this.postsPage.content, ...data.content] : data.content,
          page: data.page,
          size: data.size,
          totalElements: data.totalElements,
          totalPages: data.totalPages
        };
        this.loadingPosts = false;
      },
      error: err => {
        console.error('Failed to load posts', err);
        this.loadingPosts = false;
      }
    });
  }

  loadSuggestions(page: number) {
    if (!this.user) return;

    const requestBody: PaginationRequest = { page, size: this.suggestionsPage.size };
    this.http.post<Page<SuggestionFriend>>(
      'http://localhost:8080/user-management/api/v1/user/suggestions',
      requestBody
    ).subscribe({
      next: data => this.suggestionsPage = data,
      error: err => console.error('Failed to load suggestions', err)
    });
  }

  follow(suggestion: SuggestionFriend) {
    this.http.post(`http://localhost:8080/user-management/api/v1/follow/${suggestion.email}`, {})
      .subscribe({
        next: () => {
          this.suggestionsPage.content = this.suggestionsPage.content.filter(s => s.email !== suggestion.email);
        },
        error: err => console.error(err)
      });
  }

  cancel(suggestion: SuggestionFriend) {
    this.http.post(`http://localhost:8080/user-management/api/v1/unfollow/${suggestion.email}`, {})
      .subscribe({
        next: () => {
          this.suggestionsPage.content = this.suggestionsPage.content.filter(s => s.email !== suggestion.email);
        },
        error: err => console.error(err)
      });
  }

  toggleCreatePost() {
    this.showCreatePost = !this.showCreatePost;
  }

  onFileSelected(event: any) {
    this.selectedFiles = Array.from(event.target.files);
  }

  submitPost() {
    if (!this.newPostContent && this.selectedFiles.length === 0) {
      alert('Post cannot be empty');
      return;
    }

    const formData = new FormData();
    formData.append('content', this.newPostContent);
    this.selectedFiles.forEach(file => formData.append('media', file));

    this.http.post('http://localhost:8081/post-management/api/v1/post/create', formData)
      .subscribe({
        next: () => {
          this.newPostContent = '';
          this.selectedFiles = [];
          this.showCreatePost = false;
          this.loadPosts(0); // reload first page
        },
        error: err => console.error('Failed to create post', err)
      });
  }

  onPostsScroll(event: any) {
    const target = event.target;
    const position = target.scrollTop + target.clientHeight;
    const height = target.scrollHeight;

    if (position > height - 100 && !this.loadingPosts && this.postsPage.page + 1 < this.postsPage.totalPages) {
      this.loadPosts(this.postsPage.page + 1, true);
    }
  }

  signOut() {
    this.router.navigate(['/signin']);
  }

  isImage(url: string): boolean {
    return url.match(/\.(jpeg|jpg|gif|png)$/) != null;
  }
}