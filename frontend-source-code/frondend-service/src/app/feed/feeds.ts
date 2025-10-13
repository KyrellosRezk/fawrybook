import { ChangeDetectorRef, Component, OnInit, TemplateRef, ViewChild, inject } from '@angular/core';
import { LucideAngularModule, MessageCircleMore, ThumbsDown, ThumbsUp } from 'lucide-angular';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { UserBasicData } from '../payloads/responses/user-basic-data';
import { PaginationRequest } from '../payloads/requests/pagination-request';
import { PostResponse } from '../payloads/responses/post-response';
import { Post } from '../profile/post';
import { User } from '../profile/user';
import { Auth } from '../auth/auth';
import { FilePreviewPipe } from '../../pipes/file-preview-pipe';
import { CreatePostRequest } from '../payloads/requests/create-post-request';
import { CreateMediaEntityResponse } from '../payloads/responses/create-media-entity-response';
import { FileService } from '../profile/file';
import { UploadFilesResponse } from '../payloads/responses/upload-files.response';
import path from 'path';
import { profile } from 'console';
import { catchError, forkJoin, map, of, switchMap } from 'rxjs';
import { Request } from '../profile/request';
import { React } from './react';
import { Comment } from './comment';
import { CommentResponse } from '../payloads/responses/get-comments-response';

@Component({
  selector: 'app-feeds',
  standalone: true,
  templateUrl: './feeds.html',
  styleUrls: ['./feeds.css'],
  imports: [LucideAngularModule, FormsModule, FilePreviewPipe]
})
export class FeedsComponent implements OnInit {
  loading = true;
  expanded = false;
  posts: PostResponse[] = [];
  currentPostPage: number = 0;
  postsPages: number = 1;
  friendSuggestions: SuggestionFriend[]  = [];
  currentSuggesionPage: number = 0;
  suggestionsPages: number = 1;
  size: number = 10;
  signedUser!: UserBasicData;
  selectedPost!: PostResponse;
  newComment: string = '';
  cdr = inject(ChangeDetectorRef);
  @ViewChild('submitComment') submitComment!: TemplateRef<any>;
  readonly comment = MessageCircleMore;
  readonly like = ThumbsUp;
  readonly dislike = ThumbsDown;
  postContent: string = '';
  selectedFiles: File[] = [];
  selectedComments!: CommentResponse[];

  constructor(
    readonly dialog: MatDialog,
    private router: Router,
    private post: Post,
    private react: React,
    private user: User,
    private fileService: FileService,
    private request: Request,
    private auth: Auth,
    private file: FileService,
    private commentService: Comment
  ) {}

  ngOnInit() {
    const userStr = localStorage.getItem('user');
    if (!userStr) {
      alert('No signed-in user');
      this.router.navigate(['/signin']);
      return;
    }

    this.signedUser = JSON.parse(userStr);
    this.loading = true;
    this.fileService.getPathsInFolder('PROFILE', '', this.signedUser.id).subscribe({
    next: (paths: string[]) => {
        if (paths && paths.length > 0) {
          this.signedUser.logoPath = paths[0];
          this.fileService.getFile(paths[0]).subscribe({
            next: (profilePhoto: Blob) => {
              this.signedUser.logoFile = profilePhoto;
              const objectUrl = URL.createObjectURL(profilePhoto);
              this.signedUser.logoPath = objectUrl;
              this.loading = false;
              this.cdr.markForCheck()
            },
            error: err => {
              this.loading = false;
              console.error('Error fetching profile photo file:', err);
            }
          });
        }
      },
      error: err => {
        this.loading = false;
        console.error('Error fetching profile photo paths:', err);
      }
    });

    this.loadPost(0);

    this.loadSuggestionFriends(0);
  }
  
  loadSuggestionFriends(page: number) {
    if (page < 0 || (this.suggestionsPages && page >= this.suggestionsPages)) return;

    this.currentSuggesionPage = page;

    const paginationRequest: PaginationRequest = {
      page: this.currentSuggesionPage,
      size: this.size
    };

    this.loading = true;

    this.user.getSuggestions(paginationRequest).subscribe({
      next: (pageResponse: Page<SuggestionFriend>) => {
        this.suggestionsPages = pageResponse.totalPages;
        this.friendSuggestions = pageResponse.content || [];

        if (this.friendSuggestions.length === 0) {
          this.loading = false;
          this.cdr.markForCheck();
          return;
        }

        let loadedCount = 0;

        this.friendSuggestions.forEach(friend => {
          this.fileService.getPathsInFolder('PROFILE', '', friend.id).subscribe({
            next: (paths: string[]) => {
              if (paths && paths.length > 0) {
                this.fileService.getFile(paths[0]).subscribe({
                  next: (file: Blob) => {
                    const objectUrl = URL.createObjectURL(file);
                    friend.logoPath = objectUrl;
                    loadedCount++;
                    if (loadedCount === this.friendSuggestions.length) {
                      this.loading = false;
                      this.cdr.markForCheck();
                    }
                  },
                  error: err => {
                    console.error(`Failed to load file for friend ${friend.id}:`, err);
                    loadedCount++;
                    if (loadedCount === this.friendSuggestions.length) {
                      this.loading = false;
                      this.cdr.markForCheck();
                    }
                  }
                });
              } else {
                loadedCount++;
                if (loadedCount === this.friendSuggestions.length) {
                  this.loading = false;
                  this.cdr.markForCheck();
                }
              }
            },
            error: err => {
              console.error(`Failed to get paths for friend ${friend.id}:`, err);
              loadedCount++;
              if (loadedCount === this.friendSuggestions.length) {
                this.loading = false;
                this.cdr.markForCheck();
              }
            }
          });
        });
      },
      error: err => {
        console.error('Failed to load suggestions:', err);
        this.loading = false;
      }
    });
  }

  loadPost(page: number) {
    if (page < 0 || (this.postsPages && page >= this.postsPages)) return;

    this.currentPostPage = page;

    const paginationRequest: PaginationRequest = {
      page: this.currentPostPage,
      size: this.size
    };

    this.loading = true;

    this.post.getPagination(paginationRequest).subscribe({
      next: (pageResponse: Page<PostResponse>) => {
        this.posts = pageResponse.content || [];
        this.postsPages = pageResponse.totalPages;

        this.posts.forEach(post => {
          post.mediaPaths = [];

          this.fileService.getPathsInFolder('PROFILE', '', post.user.id).subscribe({
            next: (paths: string[]) => {
              if (!paths || paths.length === 0) {
                console.warn(`No profile image found for user ${post.user.id}`);
                return;
              }
              this.fileService.getFile(paths[0]).subscribe({
                next: (file: Blob) => {
                  try {
                    const objectUrl = URL.createObjectURL(file);
                    post.user.logoPath = objectUrl;
                  } catch (err) {
                    console.error(`Failed to create URL for user ${post.user.id} profile image:`, err);
                  } finally {
                    this.cdr.markForCheck();
                  }
                },
                error: (err) => {
                  console.error(`Failed to load profile image file for user ${post.user.id}:`, err);
                }
              });
            },
            error: (err) => {
              console.error(`Failed to fetch profile image paths for user ${post.user.id}:`, err);
            }
          });

          this.fileService.getPathsInFolder('POST', post.id, post.user.id).subscribe({
            next: (paths: string[]) => {
              if (!paths || paths.length === 0) {
                console.warn(`No media found for post ${post.id}`);
                return;
              }

              paths.forEach(path => {
                this.fileService.getFile(path).subscribe({
                  next: (file: Blob) => {
                    try {
                      const objectUrl = URL.createObjectURL(file);
                      post.mediaPaths.push(objectUrl);
                    } catch (err) {
                      console.error(`Failed to create object URL for post ${post.id} file:`, err);
                    } finally {
                      this.cdr.markForCheck();
                    }
                  },
                  error: (err) => {
                    console.error(`Failed to load file for post ${post.id}, path: ${path}`, err);
                  }
                });
              });
            },
            error: (err) => {
              console.error(`Failed to get media paths for post ${post.id}:`, err);
            }
          });
        });

        this.loading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.loading = false;
        console.error('Failed to load posts:', err);
        alert('Failed to load posts. Please try again later.');
      }
    });
  }

  toggleExpand(post: any) {
    post.expanded = !post.expanded;
  }

  openCommentDialog(post: PostResponse) {
    this.selectedPost = post;
    this.newComment = '';

    this.getComments();

    const checkLoaded = () => {
      if (!this.loading) {
        this.dialog.open(this.submitComment, {
        width: '600px',
        panelClass: ['center-dialog', 'custom-scroll-dialog'],
      });
      } else {
        setTimeout(checkLoaded, 50);
      }
    };
    checkLoaded();
  }

  addComment() {
    if (!this.newComment.trim() || !this.selectedPost) return;

    this.loading = true;

    this.commentService.createComment(this.newComment.trim(), this.selectedPost.id).subscribe({
      next: (response: CreateMediaEntityResponse) => {
        const comment: CommentResponse = {
          id: '',
          content: this.newComment.trim(),
          firstName: this.signedUser.firstName,
          middleName: this.signedUser.middleName,
          lastName: this.signedUser.lastName,
          userId: this.signedUser.id,
          userLogoPath: this.signedUser.logoPath
        };

        this.selectedComments.push(comment);
        this.newComment = '';
        this.loading = false;
        this.cdr.markForCheck();
        console.log('Comment added successfully');
      },
      error: (err) => {
        console.error('Failed to add comment:', err);
        this.loading = false;
        this.cdr.markForCheck();
        alert('Failed to add comment. Please try again later.');
      },
      complete: () => {
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }

  getComments() {
    if (!this.selectedPost) return;

    this.loading = true;

    this.commentService.getComments(this.selectedPost.id).subscribe({
      next: (comments: CommentResponse[]) => {
        if (!comments || comments.length === 0) {
          this.selectedComments = [];
          this.loading = false;
          return;
        }

        this.selectedComments = comments;

        this.selectedComments.forEach((comment) => {
          this.file.getPathsInFolder('PROFILE', '', comment.userId).subscribe({
            next: (paths: string[]) => {
              if (!paths || paths.length === 0) {
                return;
              }

              this.file.getFile(paths[0]).subscribe({
                next: (file: Blob) => {
                  const url = URL.createObjectURL(file);
                  comment.userLogoPath = url;
                },
                error: (err) => {
                  console.error(`Error fetching file for user ${comment.userId}:`, err);
                  this.cdr.markForCheck()
                }
              });
            },
            error: (err) => {
              console.error(`Error fetching paths for user ${comment.userId}:`, err);
              this.cdr.markForCheck()
            }
          });
        });
        this.loading = false;
        this.cdr.markForCheck()
      },
      error: (err) => {
        console.error('Error loading comments:', err);
        this.loading = false;
        this.selectedComments = [];
        this.cdr.markForCheck()
      }
    });
  }

  liked(postId: string) {
    let post = this.posts.find(p => p.id === postId);
    if(post && post.liked) return;
    this.react.react(postId, 'LIKE').subscribe({
      next: () => {
        if (post) {
          if(post.disliked) {
            post.disLikeCount = (post.disLikeCount || 0) - 1;
            post.disliked = false;
          }
          post.likeCount = (post.likeCount || 0) + 1;
          post.liked = true;
        }
        console.log(`👍 Post ${postId} liked successfully`);
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error(`Failed to like post ${postId}:`, err);
        alert('Failed to like this post. Please try again later.');
      }
    });
  }

  disliked(postId: string) {
    let post = this.posts.find(p => p.id === postId);
    if(post && post.disliked) return;
    this.react.react(postId, 'DISLIKE').subscribe({
      next: () => {
        if (post) {
          if(post.liked) {
            post.likeCount = (post.likeCount || 0) - 1;
            post.liked = false;
          }
          post.disLikeCount = (post.disLikeCount || 0) + 1;
          post.disliked = true;
        }
        console.log(`Post ${postId} liked successfully`);
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error(`Failed to like post ${postId}:`, err);
        alert('Failed to like this post. Please try again later.');
      }
    });
  }

  signOut() {
    this.auth.signout();
    alert('Signed out!');
    this.router.navigate(['/signin'])   
  }

  addFriend(id: string) {
    this.request.sendFriendRequest(id).subscribe({
      next: () => {
        this.friendSuggestions = this.friendSuggestions.filter(f => f.id !== id);
        console.log(`Sent friend request to user ${id}`);
        this.cdr.markForCheck()
      },
      error: (err) => {
        console.error('Failed to send friend request:', err);
        alert('Could not send friend request. Please try again.');
      }
    });
  }

  onFilesSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.selectedFiles = input.files ? Array.from(input.files) : [];
    console.log('Selected files:', this.selectedFiles);
  }

  createPost(): void {
    console.log('Creating post with content:', this.postContent);
    if(this.postContent == '') {
      alert.apply('empty content');
      return;
    }
    const createPostRequest: CreatePostRequest = {
      content: this.postContent,
      hasMedia: this.selectedFiles.length > 0
    };

    console.log('CreatePostRequest:', createPostRequest);

    this.post.createPost(createPostRequest).subscribe({
      next: (response: CreateMediaEntityResponse) => {
        console.log('Post created successfully:', response);

        if (this.selectedFiles.length > 0) {
          console.log('Uploading attached files...');

          this.file.saveFiles(response.entityName, response.id, this.selectedFiles).subscribe({
            next: (uploadResponse: UploadFilesResponse) => {
              console.log('Files uploaded successfully:', uploadResponse);
              this.postContent = '';
              this.selectedFiles = [];
            },
            error: (err) => {
              console.error('Error uploading files:', err);
            }
          });
        } else {
          this.postContent = '';
        }
      },
      error: (err) => {
        console.error('Error creating post:', err);
      }
    });
  }
  
  goToProfile(userId: string){
      this.router.navigate(['/profile' , userId])
  }
}
