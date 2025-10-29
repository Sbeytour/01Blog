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
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatMenuModule } from '@angular/material/menu';
import { AdminService } from '../../../core/services/adminService';
import { AdminReportDetails, ReportAction } from '../../../core/models/admin';
import { ReportReasonLabels, ReportStatus } from '../../../core/models/report';
import { DialogComponent } from '../../../components/dialog/dialog';

@Component({
  selector: 'app-reports',
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
    MatTooltipModule,
    MatSelectModule,
    MatFormFieldModule,
    MatMenuModule
  ],
  templateUrl: './reports.html',
  styleUrl: './reports.scss'
})
export class Reports implements OnInit {
  private adminService = inject(AdminService);
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);

  reports = signal<AdminReportDetails[]>([]);
  isLoading = signal(true);
  totalReports = signal(0);
  pageSize = signal(20);
  pageIndex = signal(0);
  statusFilter = signal<string>('');

  reportAction = ReportAction;
  displayedColumns: string[] = ['id', 'type', 'reporter', 'reported', 'reason', 'status', 'date', 'actions'];
  reportReasonLabels = ReportReasonLabels;
  ReportStatus = ReportStatus;

  ngOnInit(): void {
    this.loadReports();
  }

  loadReports(page: number = 0): void {
    this.isLoading.set(true);

    const status = this.statusFilter() || undefined;
    this.adminService.getReports(page, this.pageSize(), status).subscribe({
      next: (response) => {
        this.reports.set(response.content);
        this.totalReports.set(response.totalElements);
        this.pageIndex.set(response.number);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Error loading reports:', error);
        this.showSnackbar('Failed to load reports', 'error');
        this.isLoading.set(false);
      }
    });
  }

  onPageChange(event: PageEvent): void {
    this.pageSize.set(event.pageSize);
    this.loadReports(event.pageIndex);
  }

  onStatusFilterChange(status: string): void {
    this.statusFilter.set(status);
    this.loadReports(0);
  }

  resolveReport(report: AdminReportDetails, action: ReportAction): void {
    let actionMessage = '';
    switch (action) {
      case ReportAction.BAN_USER:
        actionMessage = `ban the user ${report.reportedEntityName}`;
        break;
      case ReportAction.DELETE_POST:
        actionMessage = 'delete the reported post';
        break;
      case ReportAction.DELETE_USER:
        actionMessage = `permanently delete the user ${report.reportedEntityName}`;
        break;
      case ReportAction.NONE:
        actionMessage = 'resolve without taking action';
        break;
    }

    const dialogRef = this.dialog.open(DialogComponent, {
      data: {
        title: 'Resolve Report',
        message: `Are you sure you want to resolve this report and ${actionMessage}?`
      }
    });

    dialogRef.afterClosed().subscribe((confirmed: boolean) => {
      if (confirmed) {
        this.adminService.resolveReport(report.id, {
          status: ReportStatus.RESOLVED,
          adminNotes: `Resolved with action: ${action}`,
          action: action
        }).subscribe({
          next: () => {
            this.showSnackbar('Report resolved successfully', 'success');
            this.loadReports(this.pageIndex());
          },
          error: (error) => {
            console.error('Error resolving report:', error);
            this.showSnackbar(error.error?.message || 'Failed to resolve report', 'error');
          }
        });
      }
    });
  }

  dismissReport(report: AdminReportDetails): void {
    const dialogRef = this.dialog.open(DialogComponent, {
      data: {
        title: 'Dismiss Report',
        message: 'Are you sure you want to dismiss this report? No action will be taken.'
      }
    });

    dialogRef.afterClosed().subscribe((confirmed: boolean) => {
      if (confirmed) {
        this.adminService.resolveReport(report.id, {
          status: ReportStatus.DISMISSED,
          adminNotes: 'Dismissed by admin',
          action: ReportAction.NONE
        }).subscribe({
          next: () => {
            this.showSnackbar('Report dismissed', 'success');
            this.loadReports(this.pageIndex());
          },
          error: (error) => {
            console.error('Error dismissing report:', error);
            this.showSnackbar('Failed to dismiss report', 'error');
          }
        });
      }
    });
  }

  getStatusColor(status: ReportStatus): string {
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

  private showSnackbar(message: string, type: 'success' | 'error'): void {
    this.snackBar.open(message, 'Dismiss', {
      duration: 5000,
      horizontalPosition: 'center',
      verticalPosition: 'top',
      panelClass: type === 'error' ? ['error-snackbar'] : ['success-snackbar']
    });
  }
}
