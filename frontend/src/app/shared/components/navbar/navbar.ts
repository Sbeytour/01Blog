import { Component, effect, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatBadgeModule } from '@angular/material/badge';
import { MatDividerModule } from '@angular/material/divider';
import { AuthService } from '../../../core/services/auth';

@Component({
  selector: 'app-navbar',
  imports: [
    CommonModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatBadgeModule,
    MatDividerModule
  ],
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss'
})
export class Navbar {
  authService = inject(AuthService);
  router = inject(Router);

  isDarkMode = signal(false);
  onLogout(): void {
    this.authService.logout();
  }

  navigateToProfile(): void {
    console.log(this.authService.currentUser());
    const username = this.authService.currentUser()?.username;
    if (username) {
      this.router.navigate(['/profile', username]);
    }
  }

  navigateToAdminPanel(): void {
    this.router.navigate(['/admin/dashboard']);
  }

  navigateToCreatePost(): void {
    this.router.navigate(['/posts/create']);
  }

  toggleTheme() {
    document.documentElement.classList.toggle('dark-mode');
    this.isDarkMode.set(!this.isDarkMode);
  }
}