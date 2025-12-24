import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { PageEvent } from '@angular/material/paginator';
import { ReportResponse, ReportStatus, ReportedType, ReportDetails } from '../../../core/models/report';
import { AdminService } from '../../../core/services/adminService';
import { ReportDetailsModal } from '../report-details-modal/report-details-modal';
import { TablePagination } from '../../../components/table-pagination/table-pagination';

@Component({
  selector: 'app-reports-list',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatChipsModule,
    MatProgressSpinnerModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    FormsModule,
    TablePagination
  ],
  templateUrl: './reports-list.html',
  styleUrl: './reports-list.scss'
})
export class ReportsList implements OnInit {
  private adminService = inject(AdminService);
  private dialog = inject(MatDialog);

  reports = signal<ReportResponse[]>([]);
  loading = signal<boolean>(false);

  // Pagination
  totalElements = signal<number>(0);
  pageSize = signal<number>(20);
  pageIndex = signal<number>(0);

  displayedColumns: string[] = ['type', 'reason', 'reporter', 'description', 'status', 'actions'];

  ReportStatus = ReportStatus;
  ReportedType = ReportedType;

  ngOnInit(): void {
    this.loadReports();
  }

  loadReports(): void {
    this.loading.set(true);
    this.adminService.getAllReports(this.pageIndex(), this.pageSize()).subscribe({
      next: (response) => {
        this.reports.set(response.content);
        this.totalElements.set(response.totalElements);
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Failed to load reports:', error);
        this.loading.set(false);
      }
    });
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex.set(event.pageIndex);
    this.pageSize.set(event.pageSize);
    this.loadReports();
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

  getTruncatedText(text: string, maxLength: number = 20): string {
    return text.length > maxLength ? text.substring(0, maxLength) + '...' : text;
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  viewDetails(report: ReportDetails): void {
    const dialogRef = this.dialog.open(ReportDetailsModal, {
      width: '700px',
      maxWidth: '90vw',
      maxHeight: '90vh',
      data: { reportId: report.id },
      panelClass: 'report-details-dialog'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result?.success) {
        // Reload reports list after successful resolution
        this.loadReports();
      }
    });
  }
}
