import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AdminService } from '../../../core/services/adminService';
import { AdminStats } from '../../../core/models/admin';
import { Navbar } from '../../../components/navbar/navbar';
import { AuthService } from '../../../core/services/auth';
import { UsersList } from '../users-list/users-list';
import { PostsList } from '../posts-list/posts-list';
import { ReportsList } from '../reports-list/reports-list';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    Navbar,
    ReportsList,
    UsersList,
    PostsList
  ],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class Dashboard implements OnInit {
  private adminService = inject(AdminService);
  private router = inject(Router);
  private authService = inject(AuthService);

  stats = signal<AdminStats | null>(null);
  isLoading = signal(true);
  errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    this.loadStats();
  }

  loadStats(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.adminService.getDashboardStats().subscribe({
      next: (stats) => {
        this.stats.set(stats);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Error loading dashboard stats:', error);
        this.errorMessage.set('Failed to load dashboard statistics');
        this.isLoading.set(false);
      }
    });
  }

  nameUser(): string {
    return `${this.authService.currentUser()?.firstName}`
  }

  activeSection: 'reports' | 'users' | 'posts' | 'mostReported' = 'reports';

  showSection(section: 'reports' | 'users' | 'posts' | 'mostReported') {
    this.activeSection = section;
  }
}
