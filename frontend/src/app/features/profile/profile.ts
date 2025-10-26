import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';
import { AuthService } from '../../core/services/auth';
import { User } from '../../core/models/user';
import { Navbar } from '../../components/navbar/navbar';
import { ProfileEdit } from '../../components/profile-edit/profile-edit';
import { UserService } from '../../core/services/userService';
import { HttpErrorResponse } from '@angular/common/http';
import { PostService } from '../../core/services/postService';
import { PostCard } from '../../components/post-card/post-card';
import { SubscriptionService } from '../../core/services/subscription';

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
  ],
  templateUrl: './profile.html',
  styleUrl: './profile.scss'
})
export class UserProfile implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  authService = inject(AuthService);
  userService = inject(UserService);
  subscriptionService = inject(SubscriptionService);;
  postService = inject(PostService);

  isLoading = signal(false)
  errorMessage = signal<string | null>(null);

  // Profile data
  profileUser = signal<User | null>(null);
  isOwnProfile = signal(false);
  isFollowing = signal(false);
  isEditing = signal(false);
  isFollowLoading = signal(false);

  // Stats
  postsCount = signal<number | null>(null);
  followersCount = signal(0);
  followingCount = signal(0);

  // Tab data
  posts = signal<any[]>([]);

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
          // Update stats from backend
          this.followersCount.set(response.followersCount || 0);
          this.followingCount.set(response.followingCount || 0);
          this.isFollowing.set(response.isFollowedByCurrentUser || false);
        },
        error: (error: HttpErrorResponse) => {
          console.log("error fetching profile : ", error);
        }
      })
    }
  }

  loadUserPosts(userId: number | undefined) {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    if (userId === undefined) {
      this.isLoading.set(false);
      this.errorMessage.set('User ID is missing');
      return;
    }

    this.postService.getPostsByUser(userId).subscribe({
      next: (posts) => {
        this.posts.set(posts);
        this.postsCount.set(posts.length);
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
      // Load full profile data to get stats
      this.userService.getUserProfile(currentUser.username).subscribe({
        next: (response) => {
          this.profileUser.set(response);
          this.isOwnProfile.set(true);
          this.loadUserPosts(response.id);
          // Update stats from backend
          this.followersCount.set(response.followersCount || 0);
          this.followingCount.set(response.followingCount || 0);
        },
        error: (error: HttpErrorResponse) => {
          console.log("error fetching own profile : ", error);
          // Fallback to cached user data
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
    const wasFollowing = this.isFollowing();

    if (wasFollowing) {
      // Unfollow
      this.subscriptionService.unfollowUser(userId).subscribe({
        next: (response) => {
          this.isFollowing.set(response.isFollowing);
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
          this.isFollowing.set(response.isFollowing);
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

  navigateToPost(postId: number): void {
    this.router.navigate(['/posts', postId]);
  }

  navigateToUserProfile(username: string): void {
    this.router.navigate(['/profile', username]);
  }

  onCancelEdit(): void {
    this.isEditing.set(false);
  }

  onPostDeleted(postId: number): void {
    const updatedPosts = this.posts().filter(post => post.id !== postId);
    this.posts.set(updatedPosts);
    this.postsCount.set(updatedPosts.length);
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
}