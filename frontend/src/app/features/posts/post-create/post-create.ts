import { Component, inject, OnInit, signal } from '@angular/core';
import { Navbar } from '../../../components/navbar/navbar';
import { PostService } from '../../../core/services/postService';
import { MatCardModule } from '@angular/material/card';
import { CommonModule } from '@angular/common';
import { Post } from '../../../core/models/post';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { DialogComponent } from '../../../components/confirmationDialog/dialog';
import { HttpErrorResponse } from '@angular/common/http';
import { PostForm, PostFormData } from '../../../components/post-form/post-form';

@Component({
  selector: 'app-post-create',
  standalone: true,
  imports: [
    Navbar,
    CommonModule,
    MatCardModule,
    PostForm,
  ],
  templateUrl: './post-create.html',
  styleUrl: './post-create.scss'
})
export class PostCreate implements OnInit {
  private router = inject(Router);
  private dialog = inject(MatDialog);
  private postService = inject(PostService);

  isLoading = signal(false);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);

  ngOnInit(): void {
    // No initialization needed for create mode
  }

  handleFormSubmit(formData: PostFormData): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    const formDataToSend = new FormData();
    formDataToSend.append('title', formData.title);
    formDataToSend.append('content', formData.content);

    formData.files.forEach(preview => {
      if (preview.file) {
        formDataToSend.append('files', preview.file);
      }
    });

    this.postService.createPost(formDataToSend).subscribe({
      next: (createdPost) => {
        this.successMessage.set("Post was created successfully");
        this.handleSuccess(createdPost);
      },
      error: (error: HttpErrorResponse) => {
        this.handleError(error);
        this.isLoading.set(false);
      }
    });
  }

  handleFormCancel(): void {
    const dialogRef = this.dialog.open(DialogComponent, {
      data: {
        title: 'Discard Changes?',
        message: 'You have unsaved changes. Are you sure you want to discard them?'
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.router.navigate(['/home']);
      }
    });
  }

  private handleSuccess(createdPost: Post): void {
    this.router.navigate(['/api/posts', createdPost.id]);
  }

  private handleError(error: HttpErrorResponse): void {
    if (error.status === 0) {
      this.errorMessage.set('Network error. Please check your connection.');
    } else if (error.status === 400) {
      this.errorMessage.set(error.error?.message || 'Invalid input. Please check your data.');
    } else if (error.status === 403) {
      this.errorMessage.set('You do not have permission to create this post.');
    } else if (error.status === 500) {
      this.errorMessage.set('Server error. Please try again later.');
    } else {
      this.errorMessage.set(error.error?.message || 'An unexpected error occurred.');
    }
  }
}
