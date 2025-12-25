import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { FormsModule } from '@angular/forms';
import { UserRole } from '../../core/models/user';

/**
 * Generic confirmation dialog for delete operations
 * Used for deleting users, posts, or any other entities
 */
@Component({
  selector: 'app-delete-confirmation-dialog',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatButtonModule],
  template: `
    <h2 mat-dialog-title>{{ data.title }}</h2>
    <mat-dialog-content>
      <p>{{ data.message }}</p>
      @if (data.entityName) {
        <p><strong>{{ data.entityName }}</strong></p>
      }
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
export class DeleteConfirmationDialog {
  constructor(@Inject(MAT_DIALOG_DATA) public data: DeleteDialogData) { }
}

export interface DeleteDialogData {
  title: string;
  message: string;
  entityName?: string;
}

/**
 * Dialog for banning users with temporary or permanent duration
 */
@Component({
  selector: 'app-ban-user-dialog',
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    FormsModule
  ],
  template: `
    <h2 mat-dialog-title>Ban User</h2>
    <mat-dialog-content>
      <p style="margin-bottom: 16px;">You are about to ban <strong>{{ data.username }}</strong></p>

      <mat-form-field appearance="outline" style="width: 100%; margin-bottom: 16px;">
        <mat-label>Ban Type</mat-label>
        <mat-select [(ngModel)]="permanent">
          <mat-option [value]="false">Temporary Ban</mat-option>
          <mat-option [value]="true">Permanent Ban</mat-option>
        </mat-select>
        <mat-hint>Select whether the ban is temporary or permanent</mat-hint>
      </mat-form-field>

      @if (!permanent) {
      <mat-form-field appearance="outline" style="width: 100%; margin-bottom: 16px;">
        <mat-label>Ban Duration (Days)</mat-label>
        <input
          matInput
          type="number"
          [(ngModel)]="durationDays"
          min="1"
          max="365"
          placeholder="Enter number of days"
          required
        >
        <mat-hint>Duration must be between 1 and 365 days</mat-hint>
      </mat-form-field>
      }

      <mat-form-field appearance="outline" style="width: 100%;">
        <mat-label>Reason for ban</mat-label>
        <textarea
          matInput
          [(ngModel)]="reason"
          placeholder="Enter reason (min 10 characters)"
          rows="4"
          required
        ></textarea>
        <mat-hint align="end">{{ reason.length }}/500</mat-hint>
      </mat-form-field>

      @if (errorMessage) {
      <p style="color: #f44336; font-size: 14px; margin-top: 8px;">{{ errorMessage }}</p>
      }
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button [mat-dialog-close]="null">Cancel</button>
      <button mat-raised-button color="warn" (click)="onBan()" [disabled]="!isValid()">
        Ban User
      </button>
    </mat-dialog-actions>
  `
})
export class BanUserDialog {
  reason: string = '';
  permanent: boolean = false;
  durationDays: number = 7;
  errorMessage: string = '';

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: BanUserDialogData,
    private dialogRef: MatDialogRef<BanUserDialog>
  ) { }

  isValid(): boolean {
    return this.reason.trim().length >= 10;
  }

  onBan(): void {
    // Validate ban duration for temporary bans
    if (!this.permanent) {
      if (!this.durationDays || this.durationDays < 1 || this.durationDays > 365) {
        this.errorMessage = 'Please enter a valid ban duration between 1 and 365 days for temporary bans.';
        return;
      }
    }

    if (!this.isValid()) {
      this.errorMessage = 'Reason must be at least 10 characters long.';
      return;
    }

    this.dialogRef.close({
      reason: this.reason,
      permanent: this.permanent,
      durationDays: this.permanent ? 0 : this.durationDays
    });
  }
}

export interface BanUserDialogData {
  username: string;
}

export interface BanUserDialogResult {
  reason: string;
  permanent: boolean;
  durationDays: number;
}

/**
 * Dialog for hiding posts with a reason
 */
@Component({
  selector: 'app-hide-post-dialog',
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    FormsModule
  ],
  template: `
    <h2 mat-dialog-title>Hide Post</h2>
    <mat-dialog-content>
      <p style="margin-bottom: 16px;">You are about to hide <strong>{{ data.postTitle }}</strong></p>

      <mat-form-field appearance="outline" style="width: 100%;">
        <mat-label>Reason for hiding</mat-label>
        <textarea
          matInput
          [(ngModel)]="reason"
          placeholder="Enter reason (min 10 characters)"
          rows="4"
          required
        ></textarea>
        <mat-hint align="end">{{ reason.length }}/500</mat-hint>
      </mat-form-field>

      @if (errorMessage) {
      <p style="color: #f44336; font-size: 14px; margin-top: 8px;">{{ errorMessage }}</p>
      }
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button [mat-dialog-close]="null">Cancel</button>
      <button mat-raised-button color="warn" (click)="onHide()" [disabled]="!isValid()">
        Hide Post
      </button>
    </mat-dialog-actions>
  `
})
export class HidePostDialog {
  reason: string = '';
  errorMessage: string = '';

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: HidePostDialogData,
    private dialogRef: MatDialogRef<HidePostDialog>
  ) { }

  isValid(): boolean {
    return this.reason.trim().length >= 10;
  }

  onHide(): void {
    if (!this.isValid()) {
      this.errorMessage = 'Reason must be at least 10 characters long.';
      return;
    }

    this.dialogRef.close(this.reason);
  }
}

export interface HidePostDialogData {
  postTitle: string;
}

/**
 * Dialog for changing user roles
 */
@Component({
  selector: 'app-change-role-dialog',
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatSelectModule,
    MatButtonModule,
    FormsModule
  ],
  template: `
    <h2 mat-dialog-title>Change User Role</h2>
    <mat-dialog-content>
      <p>Change role for <strong>{{ data.username }}</strong></p>
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

  constructor(@Inject(MAT_DIALOG_DATA) public data: ChangeRoleDialogData) {
    this.selectedRole = data.currentRole;
  }
}

export interface ChangeRoleDialogData {
  username: string;
  currentRole: UserRole;
}
