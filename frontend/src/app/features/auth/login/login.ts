import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-login',
  imports: [
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    RouterLink
  ],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class Login implements OnInit {
  loginForm!: FormGroup;
  hidePassword = signal(true);
  errorMessage = signal<string | null>(null);


  formbuilder = inject(FormBuilder);
  authService = inject(AuthService);
  router = inject(Router);

  private intializeForm(): void {
    this.loginForm = this.formbuilder.group({
      identifier: ['', [Validators.required,
      Validators.minLength(3),
      Validators.maxLength(50)]],
      password: ['', [
        Validators.required,
        Validators.minLength(6),
        Validators.maxLength(100)
      ]]
    })
  }

  ngOnInit(): void {
    this.intializeForm();
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.errorMessage.set(null);

    this.authService.login(this.loginForm.value).subscribe({
      next: (response) => {
        console.log("login succeful", response);
        this.router.navigate(['/home']);
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
    const field = this.loginForm.get(fieldName);

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