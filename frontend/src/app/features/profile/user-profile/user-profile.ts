import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatTabsModule } from '@angular/material/tabs';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';
import { AuthService } from '../../../core/services/auth';
import { User } from '../../../core/models/user';
import { Navbar } from '../../../shared/components/navbar/navbar';

@Component({
  selector: 'app-user-profile',
  imports: [
    CommonModule,
    MatButtonModule,
    MatIconModule,
    MatTabsModule,
    MatCardModule,
    MatChipsModule,
    MatDividerModule,
    Navbar
  ],
  templateUrl: './user-profile.html',
  styleUrl: './user-profile.scss'
})
export class UserProfile implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  authService = inject(AuthService);

  // Signals for reactive state management
  profileUser = signal<User | null>(null);
  isOwnProfile = signal<boolean>(false);
  isFollowing = signal<boolean>(false);

  // Stats signals
  postsCount = signal<number>(0);
  followersCount = signal<number>(0);
  followingCount = signal<number>(0);


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
    this.route.params.subscribe(params => {
      const username = params['username'];
      this.loadUserProfile(username);
    });
  }

  private loadUserProfile(username?: string): void {
    const currentUser = this.authService.currentUser();

    if (!username || username === currentUser?.username) {
      // Show current user's profile
      this.profileUser.set(currentUser);
      this.isOwnProfile.set(true);
    } else {
      // TODO: Load other user's profile from API
      // For now, just show current user
      this.profileUser.set(currentUser);
      this.isOwnProfile.set(false);
    }
  }

  onFollowToggle(): void {
    this.isFollowing.set(!this.isFollowing());
    // TODO: Call API to follow/unfollow user
    if (this.isFollowing()) {
      this.followersCount.set(this.followersCount() + 1);
    } else {
      this.followersCount.set(this.followersCount() - 1);
    }
  }

  onEditProfile(): void {
    this.router.navigate(['/profile/edit'])
  }

  navigateToPost(postId: number): void {
    this.router.navigate(['/posts', postId]);
  }

  navigateToUserProfile(username: string): void {
    this.router.navigate(['/profile', username]);
  }
}