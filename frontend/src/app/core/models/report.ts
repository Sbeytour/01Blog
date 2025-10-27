// Report Enums
export enum ReportReason {
  SPAM = 'SPAM',
  HARASSMENT = 'HARASSMENT',
  HATE_SPEECH = 'HATE_SPEECH',
  INAPPROPRIATE_CONTENT = 'INAPPROPRIATE_CONTENT',
  VIOLENCE = 'VIOLENCE',
  MISINFORMATION = 'MISINFORMATION',
  IMPERSONATION = 'IMPERSONATION',
  COPYRIGHT_VIOLATION = 'COPYRIGHT_VIOLATION',
  OTHER = 'OTHER'
}

export enum ReportStatus {
  PENDING = 'PENDING',
  UNDER_REVIEW = 'UNDER_REVIEW',
  RESOLVED = 'RESOLVED',
  DISMISSED = 'DISMISSED'
}

export enum ReportedType {
  USER = 'USER',
  POST = 'POST'
}

// Request DTO
export interface CreateReportRequest {
  reportedType: ReportedType;
  reportedEntityId: number;
  reason: ReportReason;
  description: string;
}

// Response DTOs
export interface ReportResponse {
  id: number;
  reportedType: ReportedType;
  reportedEntityId: number;
  reason: ReportReason;
  description: string;
  status: ReportStatus;
  createdAt: string;
}

export interface AdminReportResponse extends ReportResponse {
  updatedAt: string;
  resolvedAt?: string;
  reporterId: number;
  reporterUsername: string;
  reporterEmail: string;
  resolvedById?: number;
  resolvedByUsername?: string;
  adminNotes?: string;
  reportedEntityTitle?: string;
  reportedUsername?: string;
}

// Paginated Response
export interface PagedReportResponse {
  content: ReportResponse[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

// Helper for displaying reason labels
export const ReportReasonLabels: Record<ReportReason, string> = {
  [ReportReason.SPAM]: 'Spam or misleading content',
  [ReportReason.HARASSMENT]: 'Harassment or bullying',
  [ReportReason.HATE_SPEECH]: 'Hate speech or discrimination',
  [ReportReason.INAPPROPRIATE_CONTENT]: 'Inappropriate or offensive content',
  [ReportReason.VIOLENCE]: 'Violence or threats',
  [ReportReason.MISINFORMATION]: 'Misinformation',
  [ReportReason.IMPERSONATION]: 'Impersonation',
  [ReportReason.COPYRIGHT_VIOLATION]: 'Copyright violation',
  [ReportReason.OTHER]: 'Other'
};
