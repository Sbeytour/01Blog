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
import { PostEdit } from '../../features/posts/post-edit/post-edit';
import { HttpErrorResponse } from '@angular/common/http';
import { ReportDialogComponent } from '../report-dialog/report-dialog';
import { ReportedType } from '../../core/models/report';
import { DateFormatter } from '../../core/utils/date-formatter';
import { StringHelpers } from '../../core/utils/string-helpers';
import { UserHelpers } from '../../core/utils/user-helpers';

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
    return UserHelpers.getFullName(this.post.creator);
  }

  profilePic(): string | undefined {
    return this.post.creator.profileImgUrl;
  }

  getTruncatedText(text: string, maxLength: number = 50): string {
    return StringHelpers.truncate(text, maxLength);
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

    const dialogRef = this.dialog.open(PostEdit, {
      width: '700px',
      maxWidth: '95vw',
      maxHeight: '95vh',
      data: {
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
          }
        })
      }
    })
  }

  onReport(event: Event) {
    event.stopPropagation();

    const dialogRef = this.dialog.open(ReportDialogComponent, {
      width: '700px',
      maxWidth: '95vw',
      maxHeight: '95vh',
      data: {
        reportedType: ReportedType.POST,
        reportedId: this.post.id,
        reportedName: this.post.title
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result?.success) {
        // Report submitted successfully - dialog already shows snackbar
      }
    });
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
    return DateFormatter.formatRelativeTime(dateString);
  }
}
