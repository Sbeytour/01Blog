import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatChipsModule } from '@angular/material/chips';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { MatSelectModule } from '@angular/material/select';
import { AdminService } from '../../../core/services/adminService';
// import { User } from '../../../core/models/admin';
import { User, UserRole } from '../../../core/models/user';

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
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    FormsModule
  ],
  templateUrl: './users-list.html',
  styleUrl: './users-list.scss'
})
export class UsersList implements OnInit {
  private adminService = inject(AdminService);
  private dialog = inject(MatDialog);
  private router = inject(Router)

  users = signal<User[]>([]);
  loading = signal<boolean>(false);

  displayedColumns: string[] = ['user', 'role', 'status', 'reports'];

  // Pagination
  totalElements = signal<number>(0);
  pageSize = signal<number>(20);
  pageIndex = signal<number>(0);

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading.set(true);
    this.adminService.getUsers(this.pageIndex(), this.pageSize()).subscribe({
      next: (response) => {
        this.users.set(response.content);
        this.totalElements.set(response.totalElements);
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Failed to load users:', error);
        this.loading.set(false);
      }
    });
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex.set(event.pageIndex);
    this.pageSize.set(event.pageSize);
    this.loadUsers();
  }

  getFullName(user: User): string {
    return `${user.firstName} ${user.lastName}`.trim();
  }

  getUserAvatar(user: User): string {
    return user.profileImgUrl;
  }

  navigateProfile( user: User) {
    this.router.navigate(['/profile', user.username]);
  }

  openBanDialog(user: User): void {
    const dialogRef = this.dialog.open(BanUserDialog, {
      width: '400px',
      data: { user }
    });

    dialogRef.afterClosed().subscribe((reason: string) => {
      if (reason) {
        this.banUser(user.id, reason);
      }
    });
  }

  banUser(userId: number, reason: string): void {
    this.loading.set(true);
    this.adminService.banUser(userId, reason).subscribe({
      next: () => {
        this.loadUsers();
      },
      error: (error) => {
        console.error('Failed to ban user:', error);
        this.loading.set(false);
      }
    });
  }

  unbanUser(userId: number): void {
    this.loading.set(true);
    this.adminService.unbanUser(userId).subscribe({
      next: () => {
        this.loadUsers();
      },
      error: (error) => {
        console.error('Failed to unban user:', error);
        this.loading.set(false);
      }
    });
  }

  openRoleDialog(user: User): void {
    const dialogRef = this.dialog.open(ChangeRoleDialog, {
      width: '400px',
      data: { user }
    });

    dialogRef.afterClosed().subscribe((newRole: UserRole) => {
      if (newRole) {
        this.updateUserRole(user.id, newRole);
      }
    });
  }

  updateUserRole(userId: number, role: UserRole): void {
    this.loading.set(true);
    this.adminService.updateUserRole(userId, role).subscribe({
      next: () => {
        this.loadUsers();
      },
      error: (error) => {
        console.error('Failed to update user role:', error);
        this.loading.set(false);
      }
    });
  }

  openDeleteDialog(user: User): void {
    const dialogRef = this.dialog.open(DeleteUserDialog, {
      width: '400px',
      data: { user }
    });

    dialogRef.afterClosed().subscribe((confirmed: boolean) => {
      if (confirmed) {
        this.deleteUser(user.id);
      }
    });
  }

  deleteUser(userId: number): void {
    this.loading.set(true);
    this.adminService.deleteUser(userId).subscribe({
      next: () => {
        this.loadUsers();
      },
      error: (error) => {
        console.error('Failed to delete user:', error);
        this.loading.set(false);
      }
    });
  }
}

// Ban User Dialog Component
@Component({
  selector: 'ban-user-dialog',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatButtonModule, FormsModule],
  template: `
    <h2 mat-dialog-title>Ban User</h2>
    <mat-dialog-content>
      <p>You are about to ban <strong>{{ data.user.username }}</strong></p>
      <mat-form-field appearance="outline" style="width: 100%;">
        <mat-label>Reason for ban</mat-label>
        <textarea
          matInput
          [(ngModel)]="reason"
          placeholder="Enter reason (min 10 characters)"
          rows="4"
          required
        ></textarea>
      </mat-form-field>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button [mat-dialog-close]="null">Cancel</button>
      <button mat-raised-button color="warn" [mat-dialog-close]="reason" [disabled]="!isValid()">
        Ban User
      </button>
    </mat-dialog-actions>
  `
})
export class BanUserDialog {
  reason: string = '';

  constructor(@Inject(MAT_DIALOG_DATA) public data: { user: User }) { }

  isValid(): boolean {
    return this.reason.trim().length >= 10;
  }
}

// Change Role Dialog Component
@Component({
  selector: 'change-role-dialog',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatFormFieldModule, MatSelectModule, MatButtonModule, FormsModule],
  template: `
    <h2 mat-dialog-title>Change User Role</h2>
    <mat-dialog-content>
      <p>Change role for <strong>{{ data.user.username }}</strong></p>
      <mat-form-field appearance="outline" style="width: 100%;">
        <mat-label>Role</mat-label>
        <mat-select [(ngModel)]="selectedRole">
          <mat-option [value]="'USER'">User</mat-option>
          <mat-option [value]="'ADMIN'">Admin</mat-option>
        </mat-select>
      </mat-form-field>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button [mat-dialog-close]="null">Cancel</button>
      <button mat-raised-button color="primary" [mat-dialog-close]="selectedRole">
        Update Role
      </button>
    </mat-dialog-actions>
  `
})
export class ChangeRoleDialog {
  selectedRole: UserRole;

  constructor(@Inject(MAT_DIALOG_DATA) public data: { user: User }) {
    this.selectedRole = data.user.role;
  }
}

// Delete User Dialog Component
@Component({
  selector: 'delete-user-dialog',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatButtonModule],
  template: `
    <h2 mat-dialog-title>Delete User</h2>
    <mat-dialog-content>
      <p>Are you sure you want to permanently delete <strong>{{ data.user.username }}</strong>?</p>
      <p style="color: #f44336;">This action cannot be undone!</p>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button [mat-dialog-close]="false">Cancel</button>
      <button mat-raised-button color="warn" [mat-dialog-close]="true">
        Delete
      </button>
    </mat-dialog-actions>
  `
})
export class DeleteUserDialog {
  constructor(@Inject(MAT_DIALOG_DATA) public data: { user: User }) { }
}

import { Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog'; import { Router } from '@angular/router';

