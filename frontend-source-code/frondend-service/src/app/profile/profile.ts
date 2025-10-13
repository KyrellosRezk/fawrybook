import { ChangeDetectorRef, Component, inject, OnInit, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import {MatIconModule} from '@angular/material/icon';
import { ActivatedRoute, Router } from '@angular/router';
import { ArrowLeft, LucideAngularModule, MessageCircleMore, PlusIcon, ThumbsDown, ThumbsUp } from 'lucide-angular';
import {MatFormFieldModule} from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { TemplateRef } from '@angular/core';
import { Post } from './post';
import { UserBasicData } from '../payloads/responses/user-basic-data';
import { User } from './user';
import { UserProfileDataResponse } from '../payloads/responses/user-profile-data-response';
import { Request } from './request';
import { PostResponse } from '../payloads/responses/post-response';
import { PaginationRequest } from '../payloads/requests/pagination-request';
import { FileService } from './file';



@Component({
  selector: 'app-profile',
  standalone: true,
  templateUrl: './profile.html',
imports: [
  LucideAngularModule,
  FormsModule,
  MatIconModule,
  MatDialogModule,
  MatFormFieldModule,
  MatInputModule,
  MatButtonModule
]
})
export class Profile implements OnInit {

  readonly comment = MessageCircleMore;
  readonly like = ThumbsUp;
  editedContent: string = '';
  readonly dislike = ThumbsDown;
  @ViewChild('editDialogComponent') editDialogComponent: any;
  @ViewChild('friendRequestsDialog', { static: false }) friendRequestsDialog!: TemplateRef<any>;
  readonly plus = PlusIcon;
  readonly leftArrow = ArrowLeft;
  cdr = inject(ChangeDetectorRef);
  loading = true;
  posts: PostResponse[] = [];
  friendRequests: UserBasicData[] = [];
  postsPages: number = 1;
  currentPostPage: number = 0;
  friendRequestsPages: number = 1;
  profileUserId = '';
  signedUser: any;
  profileUser!: UserProfileDataResponse;
  visible = false;
  size: number = 5;
  owner: boolean = false;
  selectedPostId: string = '';


  constructor(
    private router: Router,
    public dialog: MatDialog,
    private route: ActivatedRoute,
    private postService: Post,
    private user: User,
    private fileService: FileService,
    private request: Request
  ) {}

  close(): void {
    this.visible = false;
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { id: null },
    });
  }

  openFriendRequestsDialog() {
    console.log('🟦 Opening friend requests dialog...');

    if (!this.friendRequestsDialog) {
      console.error('Dialog template not found!');
      return;
    }

    this.loading = true;

    this.loadRequests(0);

    const subscription = this.request.getRequests({ page: 0, size: this.size }).subscribe({
      next: (response: Page<UserBasicData>) => {
        this.friendRequests = response.content;
        this.friendRequestsPages = response.totalPages;
        this.loading = false;
        this.cdr.detectChanges();

        this.dialog.open(this.friendRequestsDialog, {
          width: '500px',
          panelClass: 'custom-dialog-container'
        });

        subscription.unsubscribe();
      },
      error: (err) => {
        this.loading = false;
        console.error('Failed to load friend requests:', err);
        alert('Could not load friend requests.');
      }
    });
  }


  acceptRequest(requestId: string) {
    this.request.replyToRequest(requestId, 'APPROVED').subscribe({
      next: () => {
        this.friendRequests = this.friendRequests.filter(req => req.id !== requestId);
        this.cdr.markForCheck()
        console.log(`✅ Friend request accepted successfully`);
      },
      error: (err) => {
        console.error('❌ Failed to accept friend request:', err);
        alert('Something went wrong while accepting the friend request. Please try again later.');
      }
    });
  }

  declineRequest(requestId: string) {
    this.request.replyToRequest(requestId, 'DECLINED').subscribe({
      next: () => {
        this.friendRequests = this.friendRequests.filter(req => req.id !== requestId);
        this.cdr.markForCheck()
        console.log(`Friend request declined successfully`);
      },
      error: (err) => {
        console.error('Failed to decline friend request:', err);
        alert('Something went wrong while declining the friend request. Please try again later.');
      }
    });
  }

  openEditDialog(postId: string) {
    this.selectedPostId = postId;
    const dialogRef = this.dialog.open(this.editDialogComponent, {width: '500px'});
    dialogRef.afterClosed().subscribe(() => {
      console.log('Dialog closed');
    });
  }

  saveEdit(postId: string) {
    if(!this.editedContent || this.editedContent == '') {
      alert.apply("Empty content");
      return
    }
      console.log(postId)
      this.postService.editPost(postId, this.editedContent).subscribe({
      next: () => {
        console.log('Post updated successfully!');
        this.loadPost(this.currentPostPage);
      },
      error: err => {
        console.error('Failed to update post:', err);
      }
    });
    console.log('Post updated:', this.editedContent);
    this.dialog.closeAll();
    this.editedContent == '';
  }

  deletePost(postId: string) {
    console.log('Deleting post:', postId);
    console.log(postId);
    this.postService.deletePost(postId).subscribe({
      next: () => {
        console.log('Post deleted successfully!');
        this.loadPost(this.currentPostPage);
      },
      error: err => {
        console.error('Failed to delete post:', err);
      }
    });
  }

  loadRequests(page: number): void {
    const paginationRequest: PaginationRequest = { 
      page,
      size: this.size 
    };
    this.loading = true;
    let loadedCount: number = 0;
    this.request.getRequests(paginationRequest).subscribe({
      next: (response: Page<UserBasicData>) => {
        this.friendRequests = response.content;
        this.friendRequestsPages = response.totalPages;
        this.friendRequests.forEach(request => {
          this.fileService.getPathsInFolder('PROFILE', '', request.id).subscribe({
            next: (paths: string[]) => {
              if (paths && paths.length > 0) {
                this.fileService.getFile(paths[0]).subscribe({
                  next: (file: Blob) => {
                    const objectUrl = URL.createObjectURL(file);
                    request.logoPath = objectUrl;
                    loadedCount++;
                    if (loadedCount === this.friendRequests.length) {
                      this.loading = false;
                      this.cdr.markForCheck();
                    }
                  },
                  error: err => {
                    console.error(`Failed to load file for friend ${request.id}:`, err);
                    loadedCount++;
                    if (loadedCount === this.friendRequests.length) {
                      this.loading = false;
                      this.cdr.markForCheck();
                    }
                  }
                });
              } else {
                loadedCount++;
                if (loadedCount === this.friendRequests.length) {
                  this.loading = false;
                  this.cdr.markForCheck();
                }
              }
            },
            error: err => {
              console.error(`Failed to get paths for friend ${request.id}:`, err);
              loadedCount++;
              if (loadedCount === this.friendRequests.length) {
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

  goToProfile(profileUserId: string) {
    this.router.navigate(['/profile', profileUserId]);
  }

  getUserProfile(profileUserId: string) {
    this.loading = true;

    this.user.getProfileData(profileUserId).subscribe({
      next: (data: UserProfileDataResponse) => {
        this.profileUser = data;

        this.fileService.getPathsInFolder('PROFILE', '', this.profileUserId).subscribe({
          next: (paths: string[]) => {
            if (paths && paths.length > 0) {
              this.fileService.getFile(paths[0]).subscribe({
                next: (file: Blob) => {
                  const objectUrl = URL.createObjectURL(file);
                  this.profileUser.logoPath = objectUrl;
                  this.loading = false;
                  this.cdr.markForCheck();
                },
                error: (err) => {
                  console.error('❌ Error fetching profile photo file:', err);
                  this.loading = false;
                  this.cdr.markForCheck();
                }
              });
            } else {
              console.warn('⚠️ No profile photo found for user:', this.profileUserId);
              this.loading = false;
              this.cdr.markForCheck();
            }
          },
          error: (err) => {
            console.error('❌ Error fetching profile photo paths:', err);
            this.loading = false;
            this.cdr.markForCheck();
          }
        });
      },
      error: (err) => {
        console.error('❌ Error fetching profile data:', err);
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }

  goBack() {
    this.router.navigate(['/']);
  }
  

  ngOnInit() {
     const userStr = localStorage.getItem('user');
    if (!userStr) {
      alert('No signed-in user');
      this.router.navigate(['/signin']);
      return;
    }

    this.signedUser = JSON.parse(userStr);

    this.profileUserId = this.route.snapshot.paramMap.get('id') || '';
    console.log(`id is ${this.profileUserId}`);

    this.owner = this.profileUserId === this.signedUser.id;

    this.getUserProfile(this.profileUserId);

    this.loadPost(0);
  }
 
  loadPost(page: number) {
    if (page < 0 || (this.postsPages && page >= this.postsPages)) return;

    this.currentPostPage = page;

    const paginationRequest: PaginationRequest = {
      page: this.currentPostPage,
      size: this.size,
      filterUserId: this.profileUserId
    };

    this.loading = true;

    this.postService.getPagination(paginationRequest).subscribe({
      next: (pageResponse: Page<PostResponse>) => {
        this.posts = pageResponse.content || [];
        this.postsPages = pageResponse.totalPages;
        this.posts.forEach(post => post.mediaPaths = []);

        this.posts.forEach(post => {
          this.fileService.getPathsInFolder('POST', post.id, post.user.id).subscribe({
            next: (paths: string[]) => {
              if (!paths || paths.length === 0) return;

              paths.forEach(path => {
                this.fileService.getFile(path).subscribe({
                  next: (file: Blob) => {
                    try {
                      const objectUrl = URL.createObjectURL(file);
                      post.mediaPaths.push(objectUrl);
                    } catch (err) {
                      console.error(`Failed to create object URL for file in post ${post.id}:`, err);
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
      }
    });
  }

  goToFeed(){
    this.router.navigate(['/home'])
  }
}
