import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatChipsModule } from '@angular/material/chips';
import { PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { AdminService } from '../../../core/services/adminService';
import { AuthService } from '../../../core/services/auth';
import { User, UserRole } from '../../../core/models/user';
import { TablePagination } from '../../../components/table-pagination/table-pagination';
import { UserHelpers } from '../../../core/utils/user-helpers';
import {
  BanUserDialog,
  BanUserDialogResult,
  ChangeRoleDialog,
  DeleteConfirmationDialog
} from '../../../components/dialogs/admin-dialogs';

@Component({
  selector: 'app-users-list',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatChipsModule,
    MatProgressSpinnerModule,
    TablePagination
  ],
  templateUrl: './users-list.html',
  styleUrl: './users-list.scss'
})
export class UsersList implements OnInit {
  private adminService = inject(AdminService);
  private dialog = inject(MatDialog);
  private router = inject(Router)
  authService = inject(AuthService);

  users = signal<User[]>([]);
  isLoading = signal<boolean>(false);

  displayedColumns: string[] = ['user', 'role', 'status', 'date', 'reports' , 'actions'];

  // Pagination
  totalElements = signal<number>(0);
  pageSize = signal<number>(20);
  pageIndex = signal<number>(0);

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.isLoading.set(true);
    this.adminService.getUsers(this.pageIndex(), this.pageSize()).subscribe({
      next: (response) => {
        this.users.set(response.content);
        this.totalElements.set(response.totalElements);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Failed to load users:', error);
        this.isLoading.set(false);
      }
    });
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex.set(event.pageIndex);
    this.pageSize.set(event.pageSize);
    this.loadUsers();
  }

  getFullName(user: User): string {
    return UserHelpers.getFullName(user);
  }

  getUserAvatar(user: User): string {
    return user.profileImgUrl;
  }

  navigateProfile(user: User) {
    this.router.navigate(['/profile', user.username]);
  }

  openBanDialog(user: User): void {
    const dialogRef = this.dialog.open(BanUserDialog, {
      width: '500px',
      data: { username: user.username }
    });

    dialogRef.afterClosed().subscribe((result: BanUserDialogResult | null) => {
      if (result) {
        this.banUser(user.id, result.reason, result.permanent, result.durationDays);
      }
    });
  }

  banUser(userId: number, reason: string, permanent: boolean, durationDays: number): void {
    this.isLoading.set(true);
    const banRequest = {
      reason,
      permanent,
      durationDays: permanent ? 0 : durationDays
    };
    this.adminService.banUser(userId, banRequest).subscribe({
      next: () => {
        this.loadUsers();
      },
      error: (error) => {
        console.error('Failed to ban user:', error);
        this.isLoading.set(false);
      }
    });
  }

  unbanUser(userId: number): void {
    this.isLoading.set(true);
    this.adminService.unbanUser(userId).subscribe({
      next: () => {
        this.loadUsers();
      },
      error: (error) => {
        console.error('Failed to unban user:', error);
        this.isLoading.set(false);
      }
    });
  }

  openRoleDialog(user: User): void {
    const dialogRef = this.dialog.open(ChangeRoleDialog, {
      width: '400px',
      data: { username: user.username, currentRole: user.role }
    });

    dialogRef.afterClosed().subscribe((newRole: UserRole) => {
      if (newRole) {
        this.updateUserRole(user.id, newRole);
      }
    });
  }

  updateUserRole(userId: number, role: UserRole): void {
    this.isLoading.set(true);
    this.adminService.updateUserRole(userId, role).subscribe({
      next: () => {
        this.loadUsers();
      },
      error: (error) => {
        console.error('Failed to update user role:', error);
        this.isLoading.set(false);
      }
    });
  }

  openDeleteDialog(user: User): void {
    const dialogRef = this.dialog.open(DeleteConfirmationDialog, {
      width: '400px',
      data: {
        title: 'Delete User',
        message: `Are you sure you want to permanently delete ${user.username}?`,
        entityName: user.username
      }
    });

    dialogRef.afterClosed().subscribe((confirmed: boolean) => {
      if (confirmed) {
        this.deleteUser(user.id);
      }
    });
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  }

  deleteUser(userId: number): void {
    this.isLoading.set(true);
    this.adminService.deleteUser(userId).subscribe({
      next: () => {
        this.loadUsers();
      },
      error: (error) => {
        console.error('Failed to delete user:', error);
        this.isLoading.set(false);
      }
    });
  }
}
