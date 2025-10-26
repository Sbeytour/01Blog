import { Component, EventEmitter, Input, Output, signal, ViewChild, ElementRef, AfterViewInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatFormFieldModule } from '@angular/material/form-field';
import { Comment } from '../../core/models/comment';

@Component({
  selector: 'app-comment-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatMenuModule,
    MatInputModule,
    MatProgressSpinnerModule,
    MatFormFieldModule
  ],
  templateUrl: './comment-list.html',
  styleUrl: './comment-list.scss'
})
export class CommentList implements AfterViewInit, OnDestroy {
  @Input() comments: Comment[] = [];
  @Input() isLoading = false;
  @Input() isLoadingMore = false;
  @Input() hasMore = false;
  @Input() currentUserId?: number;

  @Output() commentDeleted = new EventEmitter<number>();
  @Output() commentEdited = new EventEmitter<{ id: number; content: string }>();
  @Output() loadMore = new EventEmitter<void>();

  @ViewChild('scrollSentinel') scrollSentinel?: ElementRef;

  editingCommentId = signal<number | null>(null);
  editContent = signal('');
  maxLength = 1000;
  private observer?: IntersectionObserver;

  isOwnComment(comment: Comment): boolean {
    return this.currentUserId === comment.user.id;
  }

  startEdit(comment: Comment): void {
    this.editingCommentId.set(comment.id);
    this.editContent.set(comment.content);
  }

  cancelEdit(): void {
    this.editingCommentId.set(null);
    this.editContent.set('');
  }

  saveEdit(commentId: number): void {
    const content = this.editContent().trim();
    if (content && content.length <= this.maxLength) {
      this.commentEdited.emit({ id: commentId, content });
      this.cancelEdit();
    }
  }

  onDelete(commentId: number): void {
    this.commentDeleted.emit(commentId);
  }

  getFullName(comment: Comment): string {
    return `${comment.user.firstName} ${comment.user.lastName}`;
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

  getRemainingChars(): number {
    return this.maxLength - this.editContent().length;
  }

  ngAfterViewInit(): void {
    this.setupIntersectionObserver();
  }

  ngOnDestroy(): void {
    if (this.observer) {
      this.observer.disconnect();
    }
  }

  private setupIntersectionObserver(): void {
    if (!this.scrollSentinel) {
      setTimeout(() => this.setupIntersectionObserver(), 100);
      return;
    }

    this.observer = new IntersectionObserver(
      (entries) => {
        const entry = entries[0];
        if (entry.isIntersecting && this.hasMore && !this.isLoadingMore && !this.isLoading) {
          this.loadMore.emit();
        }
      },
      {
        root: null,
        rootMargin: '200px',
        threshold: 0
      }
    );

    this.observer.observe(this.scrollSentinel.nativeElement);
  }
}
