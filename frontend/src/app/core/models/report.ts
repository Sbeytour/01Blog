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
  PENDING,
  RESOLVED,
  DISMISSED
}

export enum ReportedType {
  USER,
  POST
}

// Request DTO
export interface CreateReportRequest {
  reportedType: ReportedType;
  reportedId: number;
  reason: ReportReason;
  description: string;
}

// Response DTOs
export interface ReportResponse {
  id: number;
  reportedType: ReportedType;
  reportedId: number;
  reason: ReportReason;
  description: string;
  status: ReportStatus;
  createdAt: string;
}

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
