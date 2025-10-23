import { Component, EventEmitter, Input, Output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';

@Component({
  selector: 'app-comment-input',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule
  ],
  templateUrl: './comment-input.html',
  styleUrl: './comment-input.scss'
})
export class CommentInput {
  @Input() postId!: number;
  @Input() isLoading = false;
  @Output() commentSubmitted = new EventEmitter<string>();

  commentContent = signal('');
  maxLength = 1000;

  onSubmit(): void {
    const content = this.commentContent().trim();
    if (content && content.length <= this.maxLength && !this.isLoading) {
      this.commentSubmitted.emit(content);
      this.commentContent.set('');
    }
  }

  isSubmitDisabled(): boolean {
    const content = this.commentContent().trim();
    return !content || content.length > this.maxLength || this.isLoading;
  }

  getRemainingChars(): number {
    return this.maxLength - this.commentContent().length;
  }

  onKeyPress(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.onSubmit();
    }
  }
}
