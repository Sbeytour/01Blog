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
import { Comment } from '../../../core/models/comment';
import { PostService } from '../../../core/services/postService';
import { CommentService } from '../../../core/services/commentService';
import { AuthService } from '../../../core/services/auth';
import { Navbar } from '../../../components/navbar/navbar';
import { MatDialog } from '@angular/material/dialog';
import { DialogComponent } from '../../../components/dialog/dialog';
import { MatMenuModule } from '@angular/material/menu';
import { PostCreate } from '../post-create/post-create';
import { MatInputModule } from '@angular/material/input';
import { CommentInput } from '../../../components/comment-input/comment-input';
import { CommentList } from '../../../components/comment-list/comment-list';

@Component({
  selector: 'app-post-detail',
  standalone: true,
  imports: [
    CommonModule,
    Navbar,
    MatMenuModule,
    MatInputModule,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatDividerModule,
    CommentInput,
    CommentList
  ],
  templateUrl: './post-detail.html',
  styleUrl: './post-detail.scss'
})
export class PostDetail implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private postService = inject(PostService);
  private commentService = inject(CommentService);
  private authService = inject(AuthService);
  private dialog = inject(MatDialog);

  post = signal<Post | null>(null);
  postId?: number;
  isLoading = signal(true);
  errorMessage = signal<string | null>(null);

  // Comment-related signals
  comments = signal<Comment[]>([]);
  isLoadingComments = signal(false);
  isSubmittingComment = signal(false);

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
        if (post.comments) {
          this.comments.set(post.comments);
        } else {
          this.loadComments(postId);
        }
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error loading post:', error);
        this.handleError(error);
        this.isLoading.set(false);
      }
    });
  }

  private loadComments(postId: number): void {
    this.isLoadingComments.set(true);

    this.commentService.getCommentsByPostId(postId).subscribe({
      next: (comments) => {
        this.comments.set(comments);
        this.isLoadingComments.set(false);
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error loading comments:', error);
        this.isLoadingComments.set(false);
      }
    });
  }

  onCommentSubmit(content: string): void {
    if (!this.postId) return;

    this.isSubmittingComment.set(true);

    this.commentService.createComment(this.postId, { content }).subscribe({
      next: (newComment) => {
        // Add new comment to the beginning of the list
        this.comments.update(comments => [newComment, ...comments]);

        // Update comment count in post
        if (this.post()) {
          const currentPost = this.post()!;
          this.post.set({
            ...currentPost,
            commentsCount: currentPost.commentsCount + 1
          });
        }

        this.isSubmittingComment.set(false);
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error creating comment:', error);
        this.isSubmittingComment.set(false);
      }
    });
  }

  onCommentEdit(comment: { id: number; content: string }): void {
    this.commentService.updateComment(comment.id, { content: comment.content }).subscribe({
      next: (updatedComment) => {
        // Update the comment in the list
        this.comments.update(comments =>
          comments.map(coment => coment.id === updatedComment.id ? updatedComment : coment)
        );
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error updating comment:', error);
      }
    });
  }

  onCommentDelete(commentId: number): void {
    const dialogRef = this.dialog.open(DialogComponent, {
      data: {
        title: 'Delete Comment',
        message: 'Are you sure you want to delete this comment?'
      }
    });

    dialogRef.afterClosed().subscribe(confirm => {
      if (confirm) {
        this.commentService.deleteComment(commentId).subscribe({
          next: () => {
            // Remove comment from the list
            this.comments.update(comments =>
              comments.filter(c => c.id !== commentId)
            );

            // Update comment count in post
            if (this.post()) {
              const currentPost = this.post()!;
              this.post.set({
                ...currentPost,
                commentsCount: Math.max(0, currentPost.commentsCount - 1)
              });
            }
          },
          error: (error: HttpErrorResponse) => {
            console.error('Error deleting comment:', error);
          }
        });
      }
    });
  }

  getCurrentUserId(): number | undefined {
    return this.authService.currentUser()?.id;
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


  onEdit(event: Event) {
    event.stopPropagation();

    const dialogRef = this.dialog.open(PostCreate, {
      width: '700px',
      maxWidth: '95vw',
      maxHeight: '95vh',
      data: {
        editMode: true,
        postId: this.post()!.id
      }
    })

    dialogRef.afterClosed().subscribe(result => {
      console.log('Dialog closed with:', result);
      if (result?.success && result.editedPost) {
        console.log('Reloading post...');
        this.loadPost(result.editedPost.id);
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
        this.postService.deletePost(this.post()!.id).subscribe({
          next: () => {
            this.router.navigate(['home']);
          },
          error: (error) => {
            this.handleError(error);
            console.log('Error deleting the post: ', error);
          }
        })
      }
    })
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