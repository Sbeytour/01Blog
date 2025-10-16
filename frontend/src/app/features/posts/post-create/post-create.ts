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
    MatProgressSpinnerModule,
  ],
  templateUrl: './post-create.html',
  styleUrl: './post-create.scss'
})
export class PostCreate {
  private formBuilder = inject(FormBuilder);
  private postService = inject(PostService);

  postForm!: FormGroup;
  selectedFiles = signal<FilePreview[]>([]);
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);

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
    if (currentFiles.length > this.maxToUpload) {
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

      const reader = new FileReader();
      reader.onload = () => {
        const preview: FilePreview = {
          file,
          url: reader.result as string,
          type: file.type.startsWith('video') ? 'video' : 'image'
        };

        this.selectedFiles.set([...currentFiles, preview]);
      };

      reader.readAsDataURL(file);
    });
    
    input.value = '';
  }

}
