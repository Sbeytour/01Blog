import { Component, inject, signal } from '@angular/core';
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
import { FilePreview } from '../../../core/models/post';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { DialogComponent } from '../../../components/dialog/dialog';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-post-create',
  imports: [Navbar,
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    // MatDialog,
    MatProgressSpinnerModule,
  ],
  templateUrl: './post-create.html',
  styleUrl: './post-create.scss'
})
export class PostCreate {
  private formBuilder = inject(FormBuilder);
  private router = inject(Router);
  private dialog = inject(MatDialog);
  private postService = inject(PostService);


  postForm!: FormGroup;
  selectedFiles = signal<FilePreview[]>([]);
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);

  maxToUpload = 5;
  maxFileSize = 50 * 1024 * 1024;


  ngOnInit(): void {
    this.initializeForm();
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
    })
  }

  onFileSelect(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length) return

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
        file,
        url: URL.createObjectURL(file),
        type: file.type.startsWith('video') ? 'video' : 'image'
      }

      this.selectedFiles.set([...currentFiles, newFiles]);
    });
  }

  removePicFile(index: number) {
    URL.revokeObjectURL(this.selectedFiles()[index].url)
    this.selectedFiles.update(files => files.filter((_, i) => i !== index));
  }

  hasUnsavedChanges() {
    return this.postForm.dirty || this.selectedFiles.length > 0;
  }

  onCancel() {
    if (this.hasUnsavedChanges()) {
      const dialogRef = this.dialog.open(DialogComponent);
      dialogRef.afterClosed().subscribe(result => {
        if (result) {
          this.router.navigate(['/home']);
        }
      })
    } else {
      this.router.navigate(['/home']);
    }
  }

  onSubmit() {
    if (this.postForm.invalid) {
      this.postForm.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);

    const formData = new FormData();
    formData.append('title', this.postForm.value.title);
    formData.append('content', this.postForm.value.content);

    this.selectedFiles().forEach(preview => formData.append('files', preview.file));

    this.postService.createPost(formData).subscribe({
      next: () => {
        this.successMessage.set("Post was created successfuly");
        this.router.navigate(['/home']);
      },
      error: (error: HttpErrorResponse) => {
        this.handleError(error);
        this.isLoading.set(false);
      }
    })
  }

  getFieldErrorMsg(fieldName: string) {
    const field = this.postForm.get(fieldName);

    if (!field || !field.errors || field.untouched) {
      return;
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
    } else if (error.status === 500) {
      this.errorMessage.set('Server error. Please try again later.');
    } else {
      this.errorMessage.set(error.error?.message || 'An unexpected error occurred.');
    }
  }
}