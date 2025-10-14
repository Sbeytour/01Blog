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
  ],
  templateUrl: './profile.html',
  styleUrl: './profile.scss'
})
export class UserProfile implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  authService = inject(AuthService);
  userService = inject(UserService)

  // Profile data
  profileUser = signal<User | null>(null);
  isOwnProfile = signal(false);
  isFollowing = signal(false);
  isEditing = signal(false);

  // Stats
  postsCount = signal(12);
  followersCount = signal(245);
  followingCount = signal(183);

  // Tab data
  posts = signal<any[]>([
    { id: 1, title: 'First Post', image: 'https://picsum.photos/300/200?random=1' },
    { id: 2, title: 'Second Post', image: 'https://picsum.photos/300/200?random=2' },
    { id: 3, title: 'Third Post', image: 'https://picsum.photos/300/200?random=3' },
  ]);

  followers = signal<any[]>([
    { id: 1, username: 'user1', profileImg: null },
    { id: 2, username: 'user2', profileImg: null },
  ]);

  following = signal<any[]>([
    { id: 1, username: 'user3', profileImg: null },
    { id: 2, username: 'user4', profileImg: null },
  ]);

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
        }, 
        error: (error : HttpErrorResponse) => {
          console.log("error fetching profile : ", error);
        }
      })
    }
  }

  loadCurrentUserProfile(currentUser: User | null) {
    this.profileUser.set(currentUser);
    this.isOwnProfile.set(true);
  }

  onEditProfile(): void {
    this.isEditing.set(true);
  }

  onEditComplet(updatedUser: User) {
    this.profileUser.set(updatedUser);
    this.isEditing.set(false);
  }

  onFollowToggle(): void {
    this.isFollowing.set(!this.isFollowing());
    // TODO: Call follow/unfollow API
    if (this.isFollowing()) {
      this.followersCount.set(this.followersCount() + 1);
    } else {
      this.followersCount.set(this.followersCount() - 1);
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
}