import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { Router } from '@angular/router';

export type ErrorType = 'not-found' | 'forbidden' | 'server-error' | 'network-error' | 'generic';

export interface ErrorConfig {
  type: ErrorType;
  title?: string;
  message?: string;
  icon?: string;
  entityName?: string; // e.g., username, post title
  showHomeButton?: boolean;
  showBackButton?: boolean;
  showRetryButton?: boolean;
}

@Component({
  selector: 'app-error-state',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule
  ],
  templateUrl: './error-state.html',
  styleUrl: './error-state.scss'
})
export class ErrorState {
  @Input() config!: ErrorConfig;
  @Output() retry = new EventEmitter<void>();

  constructor(private router: Router) { }

  get errorTitle(): string {
    if (this.config.title) return this.config.title;

    switch (this.config.type) {
      case 'not-found':
        return this.getNotFoundTitle();
      case 'forbidden':
        return 'Access Denied';
      case 'server-error':
        return 'Server Error';
      case 'network-error':
        return 'Connection Error';
      default:
        return 'Something Went Wrong';
    }
  }

  get errorMessage(): string {
    if (this.config.message) return this.config.message;

    switch (this.config.type) {
      case 'not-found':
        return this.getNotFoundMessage();
      case 'forbidden':
        return 'You don\'t have permission to access this resource.';
      case 'server-error':
        return 'We\'re experiencing technical difficulties. Please try again later.';
      case 'network-error':
        return 'Unable to connect to the server. Please check your internet connection.';
      default:
        return 'An unexpected error occurred. Please try again.';
    }
  }

  get errorIcon(): string {
    if (this.config.icon) return this.config.icon;

    switch (this.config.type) {
      case 'not-found':
        return this.config.entityName ? 'person_off' : 'search_off';
      case 'forbidden':
        return 'block';
      case 'server-error':
        return 'error_outline';
      case 'network-error':
        return 'wifi_off';
      default:
        return 'error';
    }
  }

  get showHomeButton(): boolean {
    return this.config.showHomeButton !== false; // default true
  }

  get showBackButton(): boolean {
    return this.config.showBackButton !== false; // default true
  }

  get showRetryButton(): boolean {
    return this.config.showRetryButton === true; // default false
  }

  private getNotFoundTitle(): string {
    if (this.config.entityName) {
      return 'Not Found';
    }
    return 'Page Not Found';
  }

  private getNotFoundMessage(): string {
    if (this.config.entityName) {
      return `The resource "${this.config.entityName}" could not be found. It may have been deleted or the link might be incorrect.`;
    }
    return 'The page you\'re looking for doesn\'t exist.';
  }

  goToHome(): void {
    this.router.navigate(['/home']);
  }

  goBack(): void {
    window.history.back();
  }

  onRetry(): void {
    this.retry.emit();
  }
}
