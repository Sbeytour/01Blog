import { Component, EventEmitter, inject, OnInit, Output, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../../core/services/auth';
import { UserService } from '../../../core/services/userService';
import { HttpErrorResponse } from '@angular/common/http';
import { Navbar } from '../../../shared/components/navbar/navbar';
import { MatDividerModule } from '@angular/material/divider';

@Component({
  selector: 'app-profile-edit',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatDividerModule
  ],
  templateUrl: './profile-edit.html',
  styleUrl: './profile-edit.scss'
})
export class ProfileEdit implements OnInit {
  @Output() cancelEdit = new EventEmitter();

  private fb = inject(FormBuilder);
  private router = inject(Router);
  authService = inject(AuthService);
  private userService = inject(UserService);

  profileForm!: FormGroup;

  hidePassword = signal(true);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);
  isLoading = signal(false);
  isUploadingImage = signal(false);

  // Image preview
  selectedFile = signal<File | null>(null);
  imagePreview = signal<string | null>(null);

  ngOnInit(): void {
    this.initializeForm();
    this.loadCurrentUserData();
  }

  private initializeForm(): void {
    this.profileForm = this.fb.group({
      username: ['', [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(15),
        Validators.pattern(/^[a-zA-Z0-9]+$/)
      ]],
      firstName: ['', [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(15),
        Validators.pattern(/^[a-zA-Z\s]+$/)
      ]],
      lastName: ['', [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(15),
        Validators.pattern(/^[a-zA-Z\s]+$/)
      ]],
      email: ['', [
        Validators.required,
        Validators.email
      ]],
      password: ['', [
        Validators.minLength(8),
        Validators.maxLength(30)
      ]],
      bio: ['', [
        Validators.maxLength(1000)
      ]]
    });
  }

  private loadCurrentUserData(): void {
    const user = this.authService.currentUser();
    if (user) {
      this.profileForm.patchValue({
        username: user.username,
        firstName: user.firstName,
        lastName: user.lastName,
        email: user.email,
        bio: user.bio || ''
      });

      if (user.profileImgUrl) {
        this.imagePreview.set(`http://localhost:8080/files${user.profileImgUrl}`);
      }
    }
  }

  onFileSelect(event: Event): void {
    const input = event.target as HTMLInputElement;
    console.log(input);
    console.log(input.files);
    if (input.files && input.files[0]) {
      const file = input.files[0];

      // Validate file size (5MB)
      if (file.size > 5 * 1024 * 1024) {
        this.errorMessage.set('File size must be less than 5MB');
        return;
      }

      // Validate file type
      const validTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif'];
      if (!validTypes.includes(file.type)) {
        this.errorMessage.set('Only JPG, PNG, and Gif images are allowed');
        return;
      }

      this.selectedFile.set(file);

      // Create preview
      const reader = new FileReader();
      reader.onload = (e) => {
        this.imagePreview.set(e.target?.result as string);
      };

      console.log("imagePreview", this.imagePreview)

      reader.readAsDataURL(file);

      this.errorMessage.set(null);
    }
  }

  onUploadImage(): void {
    const file = this.selectedFile();
    if (!file) {
      this.errorMessage.set('Please select an image first');
      return;
    }

    this.isUploadingImage.set(true);
    this.errorMessage.set(null);

    this.userService.updateProfilePicture(file).subscribe({
      next: (response) => {
        this.authService.currentUser.set(response);
        this.successMessage.set('Profile picture updated successfully!');
        this.selectedFile.set(null);
        this.isUploadingImage.set(false);

        setTimeout(() => this.successMessage.set(null), 3000);
      },
      error: (error: HttpErrorResponse) => {
        this.handleError(error);
        this.isUploadingImage.set(false);
      }
    });
  }

  onDeleteImage(): void {
    if (confirm('Are you sure you want to delete your profile picture?')) {
      this.isUploadingImage.set(true);

      this.userService.deleteProfilePicture().subscribe({
        next: (response) => {
          this.authService.currentUser.set(response);
          this.imagePreview.set(null);
          this.selectedFile.set(null);
          this.successMessage.set('Profile picture deleted successfully!');
          this.isUploadingImage.set(false);

          setTimeout(() => this.successMessage.set(null), 3000);
        },
        error: (error: HttpErrorResponse) => {
          this.handleError(error);
          this.isUploadingImage.set(false);
        }
      });
    }
  }

  onSubmit(): void {
    if (this.profileForm.invalid) {
      this.profileForm.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);

    // Create update object (only send changed fields)
    const updateData: any = {};
    const formValue = this.profileForm.value;

    if (formValue.username) updateData.username = formValue.username;
    if (formValue.firstName) updateData.firstName = formValue.firstName;
    if (formValue.lastName) updateData.lastName = formValue.lastName;
    if (formValue.email) updateData.email = formValue.email;
    if (formValue.password) updateData.password = formValue.password;
    if (formValue.bio !== undefined) updateData.bio = formValue.bio;

    this.userService.updateProfile(updateData).subscribe({
      next: (response) => {
        this.authService.currentUser.set(response);
        this.successMessage.set('Profile updated successfully!');
        this.isLoading.set(false);

        // Clear password field
        this.profileForm.patchValue({ password: '' });

        // Redirect to profile after 2 seconds
        setTimeout(() => {
          this.router.navigate(['/profile']);
        }, 2000);
      },
      error: (error: HttpErrorResponse) => {
        this.handleError(error);
        this.isLoading.set(false);
      }
    });
  }

  togglePasswordVisibility(): void {
    this.hidePassword.set(!this.hidePassword());
  }

  onCancel(): void {
    this.cancelEdit.emit();
  }

  getFieldErrorMsg(fieldName: string): string {
    const field = this.profileForm.get(fieldName);

    if (!field || !field.errors || field.untouched) {
      return '';
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
    if (errors['email']) {
      return 'Invalid email format';
    }
    if (errors['pattern']) {
      return `${fieldName} format is invalid`;
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