import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDividerModule } from '@angular/material/divider';
import { HttpErrorResponse } from '@angular/common/http';
import { Post } from '../../../core/models/post';
import { PostService } from '../../../core/services/postService';
import { AuthService } from '../../../core/services/auth';
import { Navbar } from '../../../components/navbar/navbar';

@Component({
  selector: 'app-post-detail',
  standalone: true,
  imports: [
    CommonModule,
    Navbar,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatDividerModule
  ],
  templateUrl: './post-detail.html',
  styleUrl: './post-detail.scss'
})
export class PostDetail implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private postService = inject(PostService);
  private authService = inject(AuthService);

  post = signal<Post | null>(null);
  postId?: number;
  isLoading = signal(true);
  errorMessage = signal<string | null>(null);

  isOwnPost(): boolean {
    const currentUser = this.authService.currentUser();
    return currentUser?.id === this.post()?.creator.id;
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.postId = Number(id);
        this.loadPost(this.postId);
      } else {
        this.router.navigate(['/home']);
      }
    });
  }

  private loadPost(postId: number): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.postService.getSinglePost(postId).subscribe({
      next: (post) => {
        this.post.set(post);
        this.isLoading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error loading post:', error);
        this.handleError(error);
        this.isLoading.set(false);
      }
    });
  }

  fullName(): string {
    return `${this.post()?.creator.firstName} ${this.post()?.creator.lastName}`
  }

  profilePic(): string | undefined {
    return this.post()?.creator.profileImgUrl;
  }

  navigateToProfile(event: Event): void {
    const target = event.target as HTMLElement;

    if (target.closest('button') || target.closest('a')) {
      return
    }
    
    this.router.navigate(['/profile', this.post()?.creator.username]);
  }

  goBack(): void {
    this.router.navigate(['/home']);
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    const now = new Date();
    const diffInMs = now.getTime() - date.getTime();
    const diffInMinutes = Math.floor(diffInMs / 60000);
    const diffInHours = Math.floor(diffInMs / 3600000);
    const diffInDays = Math.floor(diffInMs / 86400000);

    if (diffInMinutes < 1) return 'Just now';
    if (diffInMinutes < 60) return `${diffInMinutes}m ago`;
    if (diffInHours < 24) return `${diffInHours}h ago`;
    if (diffInDays < 7) return `${diffInDays}d ago`;

    return date.toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      year: date.getFullYear() !== now.getFullYear() ? 'numeric' : undefined
    });
  }

  private handleError(error: HttpErrorResponse): void {
    if (error.status === 404) {
      this.errorMessage.set('Post not found.');
    } else if (error.status === 403) {
      this.errorMessage.set('You do not have permission to view this post.');
    } else if (error.status === 0) {
      this.errorMessage.set('Network error. Please check your connection.');
    } else {
      this.errorMessage.set('Failed to load post. Please try again later.');
    }
  }
}