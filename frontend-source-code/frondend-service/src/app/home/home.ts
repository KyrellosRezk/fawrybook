import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
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
  styleUrls: ['./home.css'],
})
export class Home implements OnInit {
  // --- State
  loadingPosts = false;
  loadingUser = false;

  showCreatePost = false;
  newPostContent = '';
  selectedFiles: File[] = [];
  user?: UserBasicData;

  // Pagination model (same shape)
  postsPage: Page<PostResponse> = {
    content: [
      {
        id: 'post-001',
        content: 'Excited to announce our new product launch next week! 🚀',
        user: {
          id: 'user-101',
          email: 'sara.hassan@example.com',
          firstName: 'Sara',
          middleName: 'Mahmoud',
          lastName: 'Hassan',
          logoPath: 'https://cdn.example.com/uploads/users/sara_hassan.png',
        },
        commentsCount: 12,
        likeCount: 87,
        disLikeCount: 2,
        hasMedia: true,
      },
      {
        id: 'post-002',
        content:
          "Just finished reading 'Atomic Habits' — highly recommended for productivity lovers!",
        user: {
          id: 'user-102',
          email: 'omar.khaled@example.com',
          firstName: 'Omar',
          middleName: '',
          lastName: 'Khaled',
          logoPath: 'https://cdn.example.com/uploads/users/omar_khaled.jpg',
        },
        commentsCount: 5,
        likeCount: 45,
        disLikeCount: 0,
        hasMedia: false,
      },
      {
        id: 'post-003',
        content: 'Beautiful sunset view from my balcony today 🌅',
        user: {
          id: 'user-103',
          email: 'mona.samir@example.com',
          firstName: 'Mona',
          middleName: 'Adel',
          lastName: 'Samir',
          logoPath: 'https://cdn.example.com/uploads/users/mona_samir.png',
        },
        commentsCount: 9,
        likeCount: 120,
        disLikeCount: 3,
        hasMedia: true,
      },
      {
        id: 'post-004',
        content: 'Working on a new Angular project — any suggestions for best UI libraries?',
        user: {
          id: 'user-104',
          email: 'ahmed.tarek@example.com',
          firstName: 'Ahmed',
          middleName: 'Mohamed',
          lastName: 'Tarek',
          logoPath: 'https://cdn.example.com/uploads/users/ahmed_tarek.jpg',
        },
        commentsCount: 15,
        likeCount: 66,
        disLikeCount: 1,
        hasMedia: false,
      },
    ],
    page: 0,
    size: 10,
    totalElements: 4,
    totalPages: 1,
  };

  suggestionsPage: Page<SuggestionFriend> = {
    content: [],
    page: 0,
    size: 5,
    totalElements: 0,
    totalPages: 0,
  };

  // --- DI
  constructor(private auth: Auth, private http: HttpClient, private router: Router) {}
  cdr = inject(ChangeDetectorRef);

  // --- Lifecycle
  ngOnInit() {
    // const userStr = localStorage.getItem('user');
    // if (!userStr) {
    //   this.router.navigate(['/signin']);
    //   return;
    // }
    // this.user = JSON.parse(userStr);
    // this.loadingUser = false;
    // this.loadPosts(0);
    // this.loadSuggestions(0);
  }

  // --- Pagination helpers
  get canPrev(): boolean {
    return this.postsPage.page > 0 && !this.loadingPosts;
  }
  get canNext(): boolean {
    return this.postsPage.page + 1 < this.postsPage.totalPages && !this.loadingPosts;
  }

  prevPage() {
    if (!this.canPrev) return;
    this.loadPosts(this.postsPage.page - 1, false);
  }
  nextPage() {
    if (!this.canNext) return;
    this.loadPosts(this.postsPage.page + 1, false);
  }

  // --- Data loaders (unchanged API)
  loadPosts(page: number, append: boolean = false) {
    if (!this.user || this.loadingPosts) return;
    this.loadingPosts = true;

    const requestBody: PaginationRequest = { page, size: this.postsPage.size };

    this.http
      .post<Page<PostResponse>>(
        'http://localhost:8081/post-management/api/v1/post/pagination',
        requestBody
      )
      .subscribe({
        next: (data) => {
          this.postsPage = {
            content: append ? [...this.postsPage.content, ...data.content] : data.content,
            page: data.page,
            size: data.size,
            totalElements: data.totalElements,
            totalPages: data.totalPages,
          };
          this.loadingPosts = false;
        },
        error: (err) => {
          console.error('Failed to load posts', err);
          this.loadingPosts = false;
        },
      });
  }

  loadSuggestions(page: number) {
    if (!this.user) return;

    const requestBody: PaginationRequest = { page, size: this.suggestionsPage.size };
    this.http
      .post<Page<SuggestionFriend>>(
        'http://localhost:8080/user-management/api/v1/user/suggestions',
        requestBody
      )
      .subscribe({
        next: (data) => (this.suggestionsPage = data),
        error: (err) => console.error('Failed to load suggestions', err),
      });
  }

  // --- Social actions
  follow(suggestion: SuggestionFriend) {
    this.http
      .post(`http://localhost:8080/user-management/api/v1/follow/${suggestion.id}`, {})
      .subscribe({
        next: () => {
          this.suggestionsPage.content = this.suggestionsPage.content.filter(
            (s) => s.id !== suggestion.id
          );
        },
        error: (err) => console.error(err),
      });
  }

  cancel(suggestion: SuggestionFriend) {
    this.http
      .post(`http://localhost:8080/user-management/api/v1/unfollow/${suggestion.id}`, {})
      .subscribe({
        next: () => {
          this.suggestionsPage.content = this.suggestionsPage.content.filter(
            (s) => s.id !== suggestion.id
          );
        },
        error: (err) => console.error(err),
      });
  }

  // --- Create post
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
    this.selectedFiles.forEach((file) => formData.append('media', file));

    this.http.post('http://localhost:8081/post-management/api/v1/post/create', formData).subscribe({
      next: () => {
        this.newPostContent = '';
        this.selectedFiles = [];
        this.showCreatePost = false;
        this.loadPosts(0);
      },
      error: (err) => console.error('Failed to create post', err),
    });
  }

  // --- Inline edit (unchanged behavior)
  editingId: string | null = null;
  editingContent = '';
  saving = false;

  beginEdit(post: PostResponse) {
    this.editingId = post.id;
    this.editingContent = post.content ?? '';
  }
  cancelEdit() {
    this.editingId = null;
    this.editingContent = '';
    this.saving = false;
    this.cdr.markForCheck();
  }
  isValidContent(v: string | null | undefined) {
    const t = (v ?? '').trim();
    return t.length > 0 && t.length <= 1000;
  }
  saveEdit() {
    if (!this.editingId || !this.isValidContent(this.editingContent)) return;

    const id = this.editingId;
    const newContent = this.editingContent.trim();
    const post = this.postsPage.content.find((p) => p.id === id);
    if (!post) return;

    // Optimistic update (keep fake timeout for demo)
    const prev = post.content;
    post.content = newContent;
    this.saving = true;

    // TODO: replace with real PATCH call to your API
    setTimeout(() => {
      // On success -> close
      this.cancelEdit();
      // On error -> revert (example)
      // post.content = prev;
      // this.saving = false;
    }, 2000);
  }

  // --- Misc
  signOut() {
    this.auth.signout();
    this.router.navigate(['/signin']);
  }
  isImage(url: string): boolean {
    return url.match(/\.(jpeg|jpg|gif|png|mp4)$/) != null;
  }
}
