import { Component, inject, Inject, OnInit, Optional, signal } from '@angular/core';
import { Navbar } from '../../../components/navbar/navbar';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { PostService } from '../../../core/services/postService';
import { MatCardModule } from '@angular/material/card';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { FilePreview, Post } from '../../../core/models/post';
import { ActivatedRoute, Router } from '@angular/router';
import { MAT_DIALOG_DATA, MatDialog, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { DialogComponent } from '../../../components/dialog/dialog';
import { HttpErrorResponse } from '@angular/common/http';

export interface PostEditData {
  editMode: boolean;
  postId: number;
}

@Component({
  selector: 'app-post-create',
  standalone: true,
  imports: [
    Navbar,
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './post-create.html',
  styleUrl: './post-create.scss'
})
export class PostCreate implements OnInit {
  private formBuilder = inject(FormBuilder);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private dialog = inject(MatDialog);
  private postService = inject(PostService);

  //Allow the component to close itself when its opened as a dialog
  private dialogRef = inject(MatDialogRef<PostCreate>, { optional: true });

  //Data to affiche in the card comming from the post card component
  private dialogData = inject<PostEditData | null>(MAT_DIALOG_DATA, { optional: true });

  postForm!: FormGroup;
  selectedFiles = signal<FilePreview[]>([]);
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);

  inEditMode = signal(false);
  isDialog = signal(false); // Track if opened as dialog
  postId?: number;
  originalMediaIds: number[] = [];

  maxToUpload = 5;
  maxFileSize = 50 * 1024 * 1024;

  ngOnInit(): void {
    this.initializeForm();

    //opning the edit mode via dialog
    if (this.dialogData?.postId) {
      this.isDialog.set(true);
      this.inEditMode.set(true);
      this.postId = this.dialogData.postId;
      this.loadPostData(this.postId);
    } else { //opning the edit mode via url
      this.route.paramMap.subscribe(params => {
        const id = params.get('id');
        if (id) {
          this.inEditMode.set(true);
          this.postId = Number(id);
          this.loadPostData(this.postId);
        }
      });
    }
  }

  private initializeForm(): void {
    this.postForm = this.formBuilder.group({
      title: ['', [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(150)
      ]],
      content: ['', [
        Validators.required,
        Validators.minLength(10),
        Validators.maxLength(1000)
      ]]
    });
  }

  loadPostData(postId: number): void {
    this.isLoading.set(true);
    this.postService.getSinglePost(postId).subscribe({
      next: (post: Post) => {
        this.postForm.patchValue({
          title: post.title,
          content: post.content
        });

        this.originalMediaIds = post.media.map(media => media.id);

        const mediaPreview = post.media.map(media => ({
          mediaId: media.id,
          file: null,
          url: media.url,
          type: media.type
        }));

        this.selectedFiles.set(mediaPreview);
        this.isLoading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.handleError(error);
        this.isLoading.set(false);
      }
    });
  }

  onFileSelect(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length) return;

    const currentFiles = this.selectedFiles();
    if (currentFiles.length >= this.maxToUpload) {
      this.errorMessage.set(`You can only upload up to ${this.maxToUpload} files`);
      return;
    }

    Array.from(input.files).forEach(file => {
      if (file.size > this.maxFileSize) {
        this.errorMessage.set('File size must be less than 50MB');
        return;
      }

      const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'video/mp4'];

      if (!allowedTypes.includes(file.type)) {
        this.errorMessage.set('Only images (JPG, PNG, Gif) and videos (MP4) are allowed');
        return;
      }

      const newFiles: FilePreview = {
        mediaId: undefined,
        file,
        url: URL.createObjectURL(file),
        type: file.type.startsWith('video') ? 'VIDEO' : 'IMAGE'
      }

      this.selectedFiles.set([...currentFiles, newFiles]);
    });
  }

  removePicFile(index: number): void {
    const fileToRemove = this.selectedFiles()[index];
    if (fileToRemove.file) {
      URL.revokeObjectURL(fileToRemove.url);
    }
    this.selectedFiles.update(files => files.filter((_, i) => i !== index));
  }

  hasUnsavedChanges(): boolean {
    return this.postForm.dirty || this.selectedFiles().length > 0;
  }

  onCancel(): void {
    // If opened as dialog
    if (this.isDialog() && this.dialogRef) {
      if (this.hasUnsavedChanges()) {
        const confirmDialogRef = this.dialog.open(DialogComponent, {
          data: {
            title: 'Discard Changes?',
            message: 'You have unsaved changes. Are you sure you want to discard them?'
          }
        });
        confirmDialogRef.afterClosed().subscribe(result => {
          if (result) {
            this.dialogRef?.close({ success: false });
          }
        });
      } else {
        this.dialogRef.close({ success: false });
      }
    }
    // If opened as page
    else {
      if (this.hasUnsavedChanges()) {
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
      } else {
        this.router.navigate(['/home']);
      }
    }
  }

  onSubmit(): void {
    if (this.postForm.invalid) {
      this.postForm.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);

    const formData = new FormData();
    formData.append('title', this.postForm.value.title);
    formData.append('content', this.postForm.value.content);

    if (this.inEditMode()) {

      //get media ids from posts in edit mode
      const currentMediaIds = this.selectedFiles().filter(preview => preview.mediaId !== undefined).map(preview => preview.mediaId!);

      //compare the original media ids from the creation with the current post media in edit mode
      const deletedMedia = this.originalMediaIds.length > 0 ? this.delatedMediaIds(currentMediaIds) : [];

      if (deletedMedia.length > 0) {
        formData.append('deletedMediaIds', JSON.stringify(deletedMedia));
      }

      this.selectedFiles().forEach(preview => {
        if (preview.file && preview.mediaId === undefined) {
          formData.append('files', preview.file);
        }
      });

      this.postService.updatePost(this.postId!, formData).subscribe({
        next: (editedPost) => {
          this.successMessage.set("Post was updated successfully");
          this.handleSuccess(editedPost);
        },
        error: (error: HttpErrorResponse) => {
          this.handleError(error);
          this.isLoading.set(false);
        }
      });
    } else {
      this.selectedFiles().forEach(preview => {
        if (preview.file) {
          formData.append('files', preview.file);
        }
      });

      this.postService.createPost(formData).subscribe({
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
  }

  private delatedMediaIds(currentMediaIds: number[]): number[] {
    return this.originalMediaIds.filter(id => !currentMediaIds.includes(id));
  }

  private handleSuccess(editedPost: Post): void {
    if (this.isDialog() && this.dialogRef) {
      // If dialog was confirmed
      this.dialogRef.close({ success: true, editedPost: editedPost });
      this.router.navigate(['/home']);
    } else {
      // If page, navigate to home
      this.router.navigate(['/home']);
    }
  }

  getFieldErrorMsg(fieldName: string): string | undefined {
    const field = this.postForm.get(fieldName);

    if (!field || !field.errors || !field.touched) {
      return undefined;
    }

    const errors = field.errors;

    if (errors['required']) {
      return `${fieldName} is required`;
    }
    if (errors['minlength']) {
      return `${fieldName} must be at least ${errors['minlength'].requiredLength} characters`;
    }
    if (errors['maxlength']) {
      return `${fieldName} cannot exceed ${errors['maxlength'].requiredLength} characters`;
    }

    return 'Invalid input';
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