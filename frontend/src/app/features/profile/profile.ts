import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';
import { MatDialog } from '@angular/material/dialog';
import { AuthService } from '../../core/services/auth';
import { User } from '../../core/models/user';
import { Navbar } from '../../components/navbar/navbar';
import { ProfileEdit } from '../../components/profile-edit/profile-edit';
import { UserService } from '../../core/services/userService';
import { HttpErrorResponse } from '@angular/common/http';
import { PostService } from '../../core/services/postService';
import { PostCard } from '../../components/post-card/post-card';
import { SubscriptionService } from '../../core/services/subscription';
import { ReportDialogComponent } from '../../components/report-dialog/report-dialog';
import { ReportedType } from '../../core/models/report';
import { Post } from '../../core/models/post';
import { InfiniteScroll } from '../../components/infinite-scroll/infinite-scroll';

@Component({
  selector: 'app-profile',
  imports: [
    CommonModule,
    MatButtonModule,
    MatIconModule,
    MatTabsModule,
    MatCardModule,
    MatChipsModule,
    MatDividerModule,
    Navbar,
    ProfileEdit,
    PostCard,
    InfiniteScroll
  ],
  templateUrl: './profile.html',
  styleUrl: './profile.scss'
})
export class UserProfile implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private dialog = inject(MatDialog);
  authService = inject(AuthService);
  userService = inject(UserService);
  subscriptionService = inject(SubscriptionService);;
  postService = inject(PostService);

  isLoading = signal(false)
  errorMessage = signal<string | null>(null);
  isLoadingMore = signal(false);
  hasMore = signal(true);
  currentPage = signal(0);
  pageSize = 10;

  // Profile data
  profileUser = signal<User | null>(null);
  isOwnProfile = signal(false);
  isEditing = signal(false);

  isFollowedByCurrentUser = signal(false);
  isFollowLoading = signal(false);

  // Stats
  postsCount = signal<number>(0);

  followersCount = signal(0);
  followingCount = signal(0);

  // Tab data
  posts = signal<Post[]>([]);

  ngOnInit(): void {
    const currentUser = this.authService.currentUser();

    this.route.paramMap.subscribe(params => {
      const username = params.get('username');
      if (username) {
        this.loadUserProfile(username, currentUser);
      } else {
        this.loadCurrentUserProfile(currentUser);
      }
    });
  }

  loadUserProfile(username: string, currentUser: User | null): void {
    if (currentUser?.username === username) {
      this.router.navigate(['/profile']);
    } else {
      this.userService.getUserProfile(username).subscribe({
        next: (response) => {
          this.profileUser.set(response);
          this.isOwnProfile.set(false);
          this.loadUserPosts(response.id);

          this.followersCount.set(response.followersCount || 0);
          this.followingCount.set(response.followingCount || 0);
          this.isFollowedByCurrentUser.set(response.isFollowedByCurrentUser || false);
        }
      })
    }
  }

  loadUserPosts(userId: number | undefined) {
    this.isLoading.set(true);
    this.errorMessage.set(null);
    this.currentPage.set(0);

    if (userId === undefined) {
      this.isLoading.set(false);
      this.errorMessage.set('User ID is missing');
      return;
    }

    this.postService.getPostsByUser(userId, 0, this.pageSize).subscribe({
      next: (response) => {
        this.posts.set(response.content);
        this.postsCount.set(response.totalElements);
        this.hasMore.set(response.hasMore);
        this.currentPage.set(response.currentPage);
        this.isLoading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error loading posts:', error);
        this.errorMessage.set('Failed to load posts. Please try again later.');
        this.isLoading.set(false);
      }
    })
  }

  loadCurrentUserProfile(currentUser: User | null) {
    if (currentUser) {
      this.userService.getUserProfile(currentUser.username).subscribe({
        next: (response) => {
          this.profileUser.set(response);
          this.isOwnProfile.set(true);
          this.loadUserPosts(response.id);

          this.followersCount.set(response.followersCount || 0);
          this.followingCount.set(response.followingCount || 0);
        },
        error: (error: HttpErrorResponse) => {
          this.profileUser.set(currentUser);
          this.loadUserPosts(currentUser?.id);
          this.isOwnProfile.set(true);
        }
      });
    }
  }

  onEditProfile(): void {
    this.isEditing.set(true);
  }

  onEditComplet(updatedUser: User) {
    this.profileUser.set(updatedUser);
    this.isEditing.set(false);
  }

  onFollowToggle(): void {
    const userId = this.profileUser()?.id;
    if (!userId || this.isFollowLoading()) return;

    this.isFollowLoading.set(true);
    const wasFollowedByCurrentUser = this.isFollowedByCurrentUser();

    if (wasFollowedByCurrentUser) {
      // Unfollow
      this.subscriptionService.unfollowUser(userId).subscribe({
        next: (response) => {
          this.isFollowedByCurrentUser.set(response.isFollowedByCurrentUser);
          this.followersCount.set(response.followersCount);
          this.isFollowLoading.set(false);
        },
        error: (error: HttpErrorResponse) => {
          console.error('Error unfollowing user:', error);
          this.isFollowLoading.set(false);
        }
      });
    } else {
      // Follow
      this.subscriptionService.followUser(userId).subscribe({
        next: (response) => {
          this.isFollowedByCurrentUser.set(response.isFollowedByCurrentUser);
          this.followersCount.set(response.followersCount);
          this.isFollowLoading.set(false);
        },
        error: (error: HttpErrorResponse) => {
          console.error('Error following user:', error);
          this.isFollowLoading.set(false);
        }
      });
    }
  }

  onCancelEdit(): void {
    this.isEditing.set(false);
  }

  onPostDeleted(postId: number): void {
    const updatedPosts = this.posts().filter(post => post.id !== postId);
    this.posts.set(updatedPosts);
    this.postsCount.update(count => Math.max(0, count - 1));
  }

  onPostEdited(editedPost: any): void {
    const updatedPosts = this.posts().map(post =>
      post.id === editedPost.id ? editedPost : post
    );
    this.posts.set(updatedPosts);
  }

  goBack(): void {
    this.router.navigate(['/home']);
  }

  loadMorePosts(): void {
    const userId = this.profileUser()?.id;
    if (!userId || this.isLoadingMore() || !this.hasMore()) return;

    this.isLoadingMore.set(true);
    const nextPage = this.currentPage() + 1;

    this.postService.getPostsByUser(userId, nextPage, this.pageSize).subscribe({
      next: (response) => {
        const currentPosts = this.posts();
        this.posts.set([...currentPosts, ...response.content]);
        this.hasMore.set(response.hasMore);
        this.currentPage.set(response.currentPage);
        this.isLoadingMore.set(false);
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error loading more posts:', error);
        this.isLoadingMore.set(false);
      }
    });
  }

  onReportUser(): void {
    const user = this.profileUser();
    if (!user) return;

    const dialogRef = this.dialog.open(ReportDialogComponent, {
      width: '500px',
      maxWidth: '95vw',
      data: {
        reportedType: ReportedType.USER,
        reportedId: user.id,
        reportedName: `@${user.username}`
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result?.success) {
        // Report submitted successfully - dialog already shows snackbar
      }
    });
  }
}