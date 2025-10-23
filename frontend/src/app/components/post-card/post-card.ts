import { Component, EventEmitter, inject, Input, Output, signal } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { PostService } from '../../core/services/postService';
import { LikeService } from '../../core/services/likeService';
import { AuthService } from '../../core/services/auth';
import { Post } from '../../core/models/post';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { MatButtonModule } from '@angular/material/button';
import { DialogComponent } from '../dialog/dialog';
import { PostCreate } from '../../features/posts/post-create/post-create';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-post-card',
  imports: [
    MatCardModule,
    MatIconModule,
    MatMenuModule,
    MatDividerModule,
    MatButtonModule
  ],
  templateUrl: './post-card.html',
  styleUrl: './post-card.scss'
})
export class PostCard {
  @Input({ required: true }) post!: Post;
  @Input() layout: 'default' | 'detail' = 'default';
  @Output() postDeleted = new EventEmitter<number>();
  @Output() postEdited = new EventEmitter<Post>();

  private authService = inject(AuthService);
  private postService = inject(PostService);
  private likeService = inject(LikeService);
  private router = inject(Router);
  private dialog = inject(MatDialog);

  isLiking = signal(false);

  isOwnPost(): boolean {
    const currentUser = this.authService.currentUser();
    return currentUser?.id === this.post.creator.id;
  }

  fullName(): string {
    return `${this.post.creator.firstName} ${this.post.creator.lastName}`
  }

  profilePic(): string | undefined {
    return this.post.creator.profileImgUrl;
  }

  navigateToPost(event: Event): void {
    const target = event.target as HTMLElement;

    if (target.closest('button') || target.closest('a') || target.closest('.header-info')) {
      return
    }

    this.router.navigate(['/api/posts', this.post.id]);
  }

  navigateToProfile(event: Event): void {
    event.stopPropagation();
    this.router.navigate(['/profile', this.post.creator.username]);
  }

  onEdit(event: Event) {
    event.stopPropagation();

    const dialogRef = this.dialog.open(PostCreate, {
      width: '700px',
      maxWidth: '95vw',
      maxHeight: '95vh',
      data: {
        editMode: true,
        postId: this.post.id
      }
    })

    dialogRef.afterClosed().subscribe(result => {
      if (result?.success && result.editedPost) {
        this.postEdited.emit(result.editedPost);
      }
    })
  }

  onDelete(event: Event) {
    event.stopPropagation();

    const dialogRef = this.dialog.open(DialogComponent, {
      data: {
        title: 'Delete Post',
        message: 'Are you sure you want to delete this post?'
      }
    })

    dialogRef.afterClosed().subscribe(confirm => {
      if (confirm) {
        this.postService.deletePost(this.post.id).subscribe({
          next: () => {
            this.postDeleted.emit(this.post.id);
          },
          error: (error) => {
            console.log('Error deleting the post: ', error);
          }
        })
      }
    })
  }

  onLikeToggle(event: Event): void {
    event.stopPropagation();

    if (this.isLiking()) return;

    this.isLiking.set(true);

    this.likeService.toggleLike(this.post.id).subscribe({
      next: (response) => {
        // Update post with new like state and count
        this.post.isLikedByCurrentUser = response.isLiked;
        this.post.likesCount = response.likesCount;
        this.isLiking.set(false);
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error toggling like:', error);
        this.isLiking.set(false);
      }
    });
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
}
