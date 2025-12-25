import { Injectable, inject } from '@angular/core';
import { Router } from '@angular/router';

/**
 * Centralized navigation service to avoid duplicated navigation methods
 * across components
 */
@Injectable({
  providedIn: 'root'
})
export class NavigationService {
  private router = inject(Router);

  /**
   * Navigate to a user's profile page
   */
  navigateToProfile(username: string): void {
    this.router.navigate(['/profile', username]);
  }

  /**
   * Navigate to current user's profile
   */
  navigateToOwnProfile(): void {
    this.router.navigate(['/profile']);
  }

  /**
   * Navigate to a specific post
   */
  navigateToPost(postId: number): void {
    this.router.navigate(['/api/posts', postId]);
  }

  /**
   * Navigate to post creation page
   */
  navigateToCreatePost(): void {
    this.router.navigate(['/api/posts/create']);
  }

  /**
   * Navigate to home page
   */
  navigateToHome(): void {
    this.router.navigate(['/home']);
  }

  /**
   * Navigate to admin dashboard
   */
  navigateToAdminDashboard(): void {
    this.router.navigate(['/admin/dashboard']);
  }

  /**
   * Navigate back in browser history
   */
  goBack(): void {
    window.history.back();
  }

  /**
   * Navigate to login page
   */
  navigateToLogin(): void {
    this.router.navigate(['/auth/login']);
  }

  /**
   * Navigate to register page
   */
  navigateToRegister(): void {
    this.router.navigate(['/auth/register']);
  }
}
