import { Component, EventEmitter, Input, Output, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { FilePreview, Post } from '../../core/models/post';

export interface PostFormData {
  title: string;
  content: string;
  files: FilePreview[];
  deletedMediaIds?: number[];
}

@Component({
  selector: 'app-post-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './post-form.html',
  styleUrl: './post-form.scss'
})
export class PostForm implements OnInit {
  private formBuilder = inject(FormBuilder);

  @Input() isEditMode = false;
  @Input() isLoading = false;
  @Input() errorMessage: string | null = null;
  @Input() successMessage: string | null = null;
  @Input() initialData?: Post;

  @Output() formSubmit = new EventEmitter<PostFormData>();
  @Output() formCancel = new EventEmitter<void>();

  postForm!: FormGroup;
  selectedFiles = signal<FilePreview[]>([]);
  originalMediaIds: number[] = [];

  maxToUpload = 5;
  maxFileSize = 50 * 1024 * 1024;

  ngOnInit(): void {
    this.initializeForm();

    if (this.initialData) {
      this.loadPostData(this.initialData);
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
        Validators.maxLength(5000)
      ]]
    });
  }

  private loadPostData(post: Post): void {
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
  }

  onFileSelect(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length) return;

    const currentFiles = this.selectedFiles();
    if (currentFiles.length >= this.maxToUpload) {
      return;
    }

    Array.from(input.files).forEach(file => {
      if (file.size > this.maxFileSize) {
        return;
      }

      const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'video/mp4'];

      if (!allowedTypes.includes(file.type)) {
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
    this.formCancel.emit();
  }

  onSubmit(): void {
    if (this.postForm.invalid) {
      this.postForm.markAllAsTouched();
      return;
    }

    const formData: PostFormData = {
      title: this.postForm.value.title,
      content: this.postForm.value.content,
      files: this.selectedFiles()
    };

    if (this.isEditMode) {
      const currentMediaIds = this.selectedFiles()
        .filter(preview => preview.mediaId !== undefined)
        .map(preview => preview.mediaId!);

      const deletedMedia = this.originalMediaIds.length > 0
        ? this.delatedMediaIds(currentMediaIds)
        : [];

      if (deletedMedia.length > 0) {
        formData.deletedMediaIds = deletedMedia;
      }
    }

    this.formSubmit.emit(formData);
  }

  private delatedMediaIds(currentMediaIds: number[]): number[] {
    return this.originalMediaIds.filter(id => !currentMediaIds.includes(id));
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
}
