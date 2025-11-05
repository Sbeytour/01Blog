import { Component, inject, Inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { FormsModule } from '@angular/forms';
import { ReportResponse, ReportReasonLabels, ReportStatus, ReportedType } from '../../../core/models/report';
import { ReportAction, ReportActionLabels, ResolveReportRequest } from '../../../core/models/admin';
import { AdminService } from '../../../core/services/adminService';

@Component({
  selector: 'app-report-details-modal',
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatChipsModule,
    MatProgressSpinnerModule,
    FormsModule
  ],
  templateUrl: './report-details-modal.html',
  styleUrl: './report-details-modal.scss'
})
export class ReportDetailsModal {
  private adminService = inject(AdminService);
  private dialogRef = inject(MatDialogRef<ReportDetailsModal>);

  report = signal<ReportResponse | null>(null);
  loading = signal<boolean>(true);
  submitting = signal<boolean>(false);
  error = signal<string | null>(null);

  // Form fields
  selectedAction = signal<ReportAction>(ReportAction.NONE);
  adminNotes = signal<string>('');

  // Expose enums and labels to template
  ReportStatus = ReportStatus;
  ReportedType = ReportedType;
  ReportReasonLabels = ReportReasonLabels;
  ReportAction = ReportAction;
  ReportActionLabels = ReportActionLabels;

  constructor(@Inject(MAT_DIALOG_DATA) public data: { reportId: number }) {
    this.loadReportDetails();
  }

  loadReportDetails(): void {
    this.loading.set(true);
    this.error.set(null);

    this.adminService.getReportById(this.data.reportId).subscribe({
      next: (report) => {
        this.report.set(report);
        this.loading.set(false);
        // Pre-fill admin notes if they exist
        if (report.adminNotes) {
          this.adminNotes.set(report.adminNotes);
        }
      },
      error: (error) => {
        console.error('Failed to load report details:', error);
        this.error.set('Failed to load report details. Please try again.');
        this.loading.set(false);
      }
    });
  }

  getAvailableActions(): ReportAction[] {
    const report = this.report();
    if (!report) return [];

    if (report.reportedType === ReportedType.USER) {
      return [
        ReportAction.NONE,
        ReportAction.BAN_USER,
        ReportAction.DELETE_USER
      ];
    } else if (report.reportedType === ReportedType.POST) {
      return [
        ReportAction.NONE,
        ReportAction.DELETE_POST
      ];
    }
    return [ReportAction.NONE];
  }

  getStatusClass(status: ReportStatus): string {
    switch (status) {
      case ReportStatus.PENDING:
        return 'pending-chip';
      case ReportStatus.RESOLVED:
        return 'resolved-chip';
      case ReportStatus.DISMISSED:
        return 'dismissed-chip';
      default:
        return '';
    }
  }

  getTypeClass(type: ReportedType): string {
    return type === ReportedType.USER ? 'user-type-chip' : 'post-type-chip';
  }

  formatDate(dateString?: string): string {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  isPending(): boolean {
    return this.report()?.status === ReportStatus.PENDING;
  }

  resolveReport(): void {
    const report = this.report();
    if (!report) return;

    this.submitting.set(true);
    this.error.set(null);

    const request: ResolveReportRequest = {
      status: ReportStatus.RESOLVED,
      adminNotes: this.adminNotes(),
      action: this.selectedAction()
    };

    this.adminService.resolveReport(report.id, request).subscribe({
      next: () => {
        this.submitting.set(false);
        this.dialogRef.close({ success: true, action: 'resolved' });
      },
      error: (error) => {
        console.error('Failed to resolve report:', error);
        this.error.set('Failed to resolve report. Please try again.');
        this.submitting.set(false);
      }
    });
  }

  dismissReport(): void {
    const report = this.report();
    if (!report) return;

    this.submitting.set(true);
    this.error.set(null);

    const request: ResolveReportRequest = {
      status: ReportStatus.DISMISSED,
      adminNotes: this.adminNotes(),
      action: ReportAction.NONE
    };

    this.adminService.resolveReport(report.id, request).subscribe({
      next: () => {
        this.submitting.set(false);
        this.dialogRef.close({ success: true, action: 'dismissed' });
      },
      error: (error) => {
        console.error('Failed to dismiss report:', error);
        this.error.set('Failed to dismiss report. Please try again.');
        this.submitting.set(false);
      }
    });
  }

  close(): void {
    this.dialogRef.close();
  }
}
