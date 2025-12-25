import { Injectable, inject } from '@angular/core';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class NavigationService {
  private router = inject(Router);

  navigateToProfile(username: string): void {
    this.router.navigate(['/profile', username]);
  }

  navigateToOwnProfile(): void {
    this.router.navigate(['/profile']);
  }

  navigateToPost(postId: number): void {
    this.router.navigate(['/api/posts', postId]);
  }

  navigateToCreatePost(): void {
    this.router.navigate(['/api/posts/create']);
  }

  navigateToHome(): void {
    this.router.navigate(['/home']);
  }

  navigateToAdminDashboard(): void {
    this.router.navigate(['/admin/dashboard']);
  }

  goBack(): void {
    window.history.back();
  }

  navigateToLogin(): void {
    this.router.navigate(['/auth/login']);
  }

  navigateToRegister(): void {
    this.router.navigate(['/auth/register']);
  }
}
