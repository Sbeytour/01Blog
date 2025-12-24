import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatBadgeModule } from '@angular/material/badge';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../core/services/auth';
import { NotificationService } from '../../core/services/notificationService';
import { Notification } from '../../core/models/notification';
import { SearchBarComponent } from '../searchBar/searchBar';

@Component({
  selector: 'app-navbar',
  imports: [
    CommonModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatBadgeModule,
    MatDividerModule,
    MatProgressSpinnerModule,
    RouterLink,
    SearchBarComponent
  ],
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss'
})
export class Navbar implements OnInit {
  authService = inject(AuthService);
  router = inject(Router);
  notificationService = inject(NotificationService);

  isDarkMode = signal(false);

  ngOnInit(): void {
    this.notificationService.loadNotifications();
  }

  handleNotificationClick(notification: Notification): void {
    // Mark as read
    this.notificationService.markAsRead(notification.id).subscribe({
      next: () => {
        // Navigate to the related post if it exists
        if (notification.relatedPostId) {
          this.router.navigate(['/api/posts', notification.relatedPostId]);
        }
      },
      error: (error) => {
        console.error('Failed to mark notification as read:', error);
      }
    });
  }

  markAllAsRead(): void {
    this.notificationService.markAllAsRead().subscribe({
      error: (error) => {
        console.error('Failed to mark all notifications as read:', error);
      }
    });
  }

  onLogout(): void {
    this.authService.logout();
  }

  navigateToProfile(): void {
    const username = this.authService.currentUser()?.username;
    if (username) {
      this.router.navigate(['/profile']);
    }
  }

  navigateToAdminPanel(): void {
    this.router.navigate(['/admin/dashboard']);
  }

  navigateToCreatePost(): void {
    this.router.navigate(['/api/posts/create']);
  }

  toggleTheme() {
    document.documentElement.classList.toggle('dark-mode');
    this.isDarkMode.set(!this.isDarkMode);
  }
}