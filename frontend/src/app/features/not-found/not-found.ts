import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ErrorState, ErrorConfig } from '../../components/error-state/error-state';
import { Navbar } from '../../components/navbar/navbar';
import { AuthService } from '../../core/services/auth';

@Component({
  selector: 'app-not-found',
  standalone: true,
  imports: [CommonModule, ErrorState, Navbar],
  templateUrl: './not-found.html',
  styleUrl: './not-found.scss'
})
export class NotFound {
  authService = inject(AuthService);

  errorConfig: ErrorConfig = {
    type: 'not-found',
    title: '404 - Page Not Found',
    message: 'The page you\'re looking for doesn\'t exist.',
    icon: 'search_off',
    showHomeButton: true,
    showBackButton: true,
    showRetryButton: false
  };
}
