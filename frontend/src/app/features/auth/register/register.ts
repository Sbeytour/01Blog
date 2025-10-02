import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/services/auth';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-register',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    RouterLink
  ],
  templateUrl: './register.html',
  styleUrl: './register.scss'
})
export class Register implements OnInit {
  registerForm!: FormGroup;
  hidePassword = signal(true);
  errorMessage = signal<string | null>(null);

  formbuilder = inject(FormBuilder);
  authService = inject(AuthService);
  router = inject(Router);

  ngOnInit(): void {
    this.intializeForm();
  }

  private intializeForm(): void {
    this.registerForm = this.formbuilder.group({
      username: ['', [
        Validators.required,
        Validators.minLength(4),
        Validators.maxLength(15),
        Validators.pattern(/^[a-zA-Z0-9]+$/) // Only letters and numbers
      ]],
      email: ['', [
        Validators.required,
        Validators.email,
      ]],
      firstName: ['', [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(15),
        Validators.pattern(/^[a-zA-Z\s]+$/) // Only letters and spaces
      ]],
      lastName: ['', [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(15),
        Validators.pattern(/^[a-zA-Z\s]+$/) // Only letters and spaces
      ]],
      password: ['', [
        Validators.required,
        Validators.minLength(8),
        Validators.maxLength(100),
      ]],
      bio: ['', [
        Validators.maxLength(1000)
      ]],
      profileImgUrl: ['', [
        Validators.maxLength(500)
      ]]
    })
  }

  onSubmit(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched;
      return;
    }

    this.authService.register(this.registerForm.value).subscribe({
      next: (response) => {
        console.log("register succeful", response);
        this.router.navigate(['/auth/login'])
      },
      error: (error: HttpErrorResponse) => {
        this.handleError(error);
      }
    })
  }

  togglePasswordVisibility(): void {
    this.hidePassword.set(!this.hidePassword());
  }

  getFieldErrorMsg(fieldName: string) {
    const field = this.registerForm.get(fieldName);

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
      this.errorMessage.set('Network error. Please check your connection and try again.');
    } else if (error.status === 401) {
      this.errorMessage.set(error.error?.message || 'Invalid username/email or password');
    } else if (error.status === 400) {
      this.errorMessage.set(error.error?.message || 'Invalid input. Please check your credentials.');
    } else if (error.status === 500) {
      this.errorMessage.set('Server error. Please try again later.');
    } else {
      this.errorMessage.set(error.error?.message || 'An unexpected error occurred. Please try again.');
    }
  }
}