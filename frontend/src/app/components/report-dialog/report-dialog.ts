import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ReportService } from '../../core/services/reportService';
import {
  CreateReportRequest,
  ReportReason,
  ReportReasonLabels,
  ReportedType
} from '../../core/models/report';
import { DialogComponent } from '../dialog/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';

export interface ReportDialogData {
  entityType: ReportedType;
  entityId: number;
  entityName: string; // Post title or username for display
}

@Component({
  selector: 'app-report-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    MatSelectModule,
    MatProgressSpinnerModule,
    MatSnackBarModule
  ],
  templateUrl: './report-dialog.html',
  styleUrl: './report-dialog.scss'
})
export class ReportDialogComponent implements OnInit {
  private formBuilder = inject(FormBuilder);
  private reportService = inject(ReportService);
  private dialogRef = inject(MatDialogRef<ReportDialogComponent>);
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);
  data = inject<ReportDialogData>(MAT_DIALOG_DATA);

  reportForm!: FormGroup;
  isSubmitting = signal(false);
  errorMessage = signal<string | null>(null);

  // Expose enums and labels to template
  reportReasons = Object.values(ReportReason);
  reportReasonLabels = ReportReasonLabels;
  reportedType = ReportedType;

  ngOnInit(): void {
    this.initializeForm();
  }

  private initializeForm(): void {
    this.reportForm = this.formBuilder.group({
      reason: ['', [Validators.required]],
      description: ['', [
        Validators.required,
        Validators.minLength(10),
        Validators.maxLength(1000)
      ]]
    });
  }

  onSubmit(): void {
    if (this.reportForm.invalid) {
      this.reportForm.markAllAsTouched();
      return;
    }

    // Show confirmation dialog first
    this.showConfirmationDialog();
  }

  private showConfirmationDialog(): void {
    const entityLabel = this.data.entityType === ReportedType.USER ? 'user' : 'post';

    const confirmDialogRef = this.dialog.open(DialogComponent, {
      data: {
        title: 'Confirm Report',
        message: `Are you sure you want to report this ${entityLabel}? This action cannot be undone.`
      }
    });

    confirmDialogRef.afterClosed().subscribe((confirmed: boolean) => {
      if (confirmed) {
        this.submitReport();
      }
    });
  }

  private submitReport(): void {
    this.isSubmitting.set(true);
    this.errorMessage.set(null);

    const request: CreateReportRequest = {
      reportedType: this.data.entityType,
      reportedEntityId: this.data.entityId,
      reason: this.reportForm.value.reason,
      description: this.reportForm.value.description
    };

    console.log("report request: ", request)
    this.reportService.createReport(request).subscribe({
      next: (report) => {
        this.snackBar.open(
          'Report submitted successfully. Our team will review it.',
          'Dismiss',
          {
            duration: 5000,
            horizontalPosition: 'center',
            verticalPosition: 'top'
          }
        );
        this.dialogRef.close({ success: true, report });
      },
      error: (error) => {
        this.isSubmitting.set(false);

        // Handle specific error messages
        if (error.error?.message) {
          this.errorMessage.set(error.error.message);
        } else if (error.status === 400) {
          this.errorMessage.set('You have already reported this content. Please wait for admin review.');
        } else {
          this.errorMessage.set('Failed to submit report. Please try again.');
        }
      }
    });
  }

  onCancel(): void {
    this.dialogRef.close({ success: false });
  }

  getFieldErrorMessage(fieldName: string): string {
    const field = this.reportForm.get(fieldName);
    if (!field || !field.errors || !field.touched) return '';

    if (field.errors['required']) return `${fieldName} is required`;
    if (field.errors['minlength']) {
      return `Minimum length: ${field.errors['minlength'].requiredLength} characters`;
    }
    if (field.errors['maxlength']) {
      return `Maximum length: ${field.errors['maxlength'].requiredLength} characters`;
    }

    return 'Invalid input';
  }


  descriptionLength(): number {
    return this.reportForm.get('description')?.value?.length || 0;
  }
}
