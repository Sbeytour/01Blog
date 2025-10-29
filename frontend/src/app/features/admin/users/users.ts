import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { AdminService } from '../../../core/services/adminService';
import { UserListItem } from '../../../core/models/admin';
import { DialogComponent } from '../../../components/dialog/dialog';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatPaginatorModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatProgressSpinnerModule,
    MatDialogModule,
    MatSnackBarModule,
    MatTooltipModule
  ],
  templateUrl: './users.html',
  styleUrl: './users.scss'
})
export class Users implements OnInit {
  private adminService = inject(AdminService);
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);

  users = signal<UserListItem[]>([]);
  isLoading = signal(true);
  totalUsers = signal(0);
  pageSize = signal(20);
  pageIndex = signal(0);

  displayedColumns: string[] = ['id', 'username', 'email', 'role', 'banned', 'reportCount', 'actions'];

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(page: number = 0): void {
    this.isLoading.set(true);

    this.adminService.getUsers(page, this.pageSize()).subscribe({
      next: (response) => {
        this.users.set(response.content);
        this.totalUsers.set(response.totalElements);
        this.pageIndex.set(response.number);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Error loading users:', error);
        this.showSnackbar('Failed to load users', 'error');
        this.isLoading.set(false);
      }
    });
  }

  onPageChange(event: PageEvent): void {
    this.pageSize.set(event.pageSize);
    this.loadUsers(event.pageIndex);
  }

  banUser(user: UserListItem): void {
    const dialogRef = this.dialog.open(DialogComponent, {
      data: {
        title: 'Ban User',
        message: `Are you sure you want to ban ${user.username}? This will prevent them from logging in.`
      }
    });

    dialogRef.afterClosed().subscribe((confirmed: boolean) => {
      if (confirmed) {
        this.adminService.banUser(user.id, 'Banned by admin').subscribe({
          next: () => {
            this.showSnackbar(`User ${user.username} has been banned`, 'success');
            this.loadUsers(this.pageIndex());
          },
          error: (error) => {
            console.error('Error banning user:', error);
            this.showSnackbar(error.error?.message || 'Failed to ban user', 'error');
          }
        });
      }
    });
  }

  unbanUser(user: UserListItem): void {
    const dialogRef = this.dialog.open(DialogComponent, {
      data: {
        title: 'Unban User',
        message: `Are you sure you want to unban ${user.username}?`
      }
    });

    dialogRef.afterClosed().subscribe((confirmed: boolean) => {
      if (confirmed) {
        this.adminService.unbanUser(user.id).subscribe({
          next: () => {
            this.showSnackbar(`User ${user.username} has been unbanned`, 'success');
            this.loadUsers(this.pageIndex());
          },
          error: (error) => {
            console.error('Error unbanning user:', error);
            this.showSnackbar('Failed to unban user', 'error');
          }
        });
      }
    });
  }

  deleteUser(user: UserListItem): void {
    const dialogRef = this.dialog.open(DialogComponent, {
      data: {
        title: 'Delete User',
        message: `Are you sure you want to permanently delete ${user.username}? This action cannot be undone and will delete all their posts and data.`
      }
    });

    dialogRef.afterClosed().subscribe((confirmed: boolean) => {
      if (confirmed) {
        this.adminService.deleteUser(user.id).subscribe({
          next: () => {
            this.showSnackbar(`User ${user.username} has been deleted`, 'success');
            this.loadUsers(this.pageIndex());
          },
          error: (error) => {
            console.error('Error deleting user:', error);
            this.showSnackbar(error.error?.message || 'Failed to delete user', 'error');
          }
        });
      }
    });
  }

  toggleRole(user: UserListItem): void {
    const newRole = user.role === 'ADMIN' ? 'USER' : 'ADMIN';
    const dialogRef = this.dialog.open(DialogComponent, {
      data: {
        title: 'Change User Role',
        message: `Are you sure you want to change ${user.username}'s role to ${newRole}?`
      }
    });

    dialogRef.afterClosed().subscribe((confirmed: boolean) => {
      if (confirmed) {
        this.adminService.updateUserRole(user.id, newRole).subscribe({
          next: () => {
            this.showSnackbar(`User role updated to ${newRole}`, 'success');
            this.loadUsers(this.pageIndex());
          },
          error: (error) => {
            console.error('Error updating user role:', error);
            this.showSnackbar(error.error?.message || 'Failed to update user role', 'error');
          }
        });
      }
    });
  }

  private showSnackbar(message: string, type: 'success' | 'error'): void {
    this.snackBar.open(message, 'Dismiss', {
      duration: 5000,
      horizontalPosition: 'center',
      verticalPosition: 'top',
      panelClass: type === 'error' ? ['error-snackbar'] : ['success-snackbar']
    });
  }
}
