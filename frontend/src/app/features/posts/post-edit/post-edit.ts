import { Component, inject, OnInit, signal } from '@angular/core';
import { PostService } from '../../../core/services/postService';
import { MatCardModule } from '@angular/material/card';
import { CommonModule } from '@angular/common';
import { Post } from '../../../core/models/post';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { HttpErrorResponse } from '@angular/common/http';
import { PostForm, PostFormData } from '../../../components/post-form/post-form';
import { MatDialog } from '@angular/material/dialog';
import { DialogComponent } from '../../../components/confirmationDialog/dialog';

export interface PostEditData {
  postId: number;
}

export interface PostEditResult {
  success: boolean;
  editedPost?: Post;
}

@Component({
  selector: 'app-post-edit',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    PostForm,
  ],
  templateUrl: './post-edit.html',
  styleUrl: './post-edit.scss'
})
export class PostEdit implements OnInit {
  private dialogRef = inject(MatDialogRef<PostEdit>);
  private dialogData = inject<PostEditData>(MAT_DIALOG_DATA);
  private dialog = inject(MatDialog);
  private postService = inject(PostService);

  isLoading = signal(false);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);
  postData = signal<Post | undefined>(undefined);

  postId: number;

  constructor() {
    this.postId = this.dialogData.postId;
  }

  ngOnInit(): void {
    this.loadPostData();
  }

  private loadPostData(): void {
    this.isLoading.set(true);
    this.postService.getSinglePost(this.postId).subscribe({
      next: (post: Post) => {
        this.postData.set(post);
        this.isLoading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.handleError(error);
        this.isLoading.set(false);
      }
    });
  }

  handleFormSubmit(formData: PostFormData): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    const formDataToSend = new FormData();
    formDataToSend.append('title', formData.title);
    formDataToSend.append('content', formData.content);

    if (formData.deletedMediaIds && formData.deletedMediaIds.length > 0) {
      formDataToSend.append('deletedMediaIds', JSON.stringify(formData.deletedMediaIds));
    }

    formData.files.forEach(preview => {
      if (preview.file && preview.mediaId === undefined) {
        formDataToSend.append('files', preview.file);
      }
    });

    this.postService.updatePost(this.postId, formDataToSend).subscribe({
      next: (editedPost) => {
        this.successMessage.set("Post was updated successfully");
        this.handleSuccess(editedPost);
      },
      error: (error: HttpErrorResponse) => {
        this.handleError(error);
        this.isLoading.set(false);
      }
    });
  }

  handleFormCancel(): void {
    const confirmDialogRef = this.dialog.open(DialogComponent, {
      data: {
        title: 'Discard Changes?',
        message: 'You have unsaved changes. Are you sure you want to discard them?'
      }
    });
    confirmDialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.dialogRef.close({ success: false });
      }
    });
  }

  private handleSuccess(editedPost: Post): void {
    this.dialogRef.close({ success: true, editedPost: editedPost });
  }

  private handleError(error: HttpErrorResponse): void {
    if (error.status === 0) {
      this.errorMessage.set('Network error. Please check your connection.');
    } else if (error.status === 400) {
      this.errorMessage.set(error.error?.message || 'Invalid input. Please check your data.');
    } else if (error.status === 403) {
      this.errorMessage.set('You do not have permission to edit this post.');
    } else if (error.status === 404) {
      this.errorMessage.set('Post not found.');
    } else if (error.status === 500) {
      this.errorMessage.set('Server error. Please try again later.');
    } else {
      this.errorMessage.set(error.error?.message || 'An unexpected error occurred.');
    }
  }
}
