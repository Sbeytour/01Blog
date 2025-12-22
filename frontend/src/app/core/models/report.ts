// Report Enums
export enum ReportReason {
  SPAM = 'SPAM',
  HARASSMENT = 'HARASSMENT',
  HATE_SPEECH = 'HATE_SPEECH',
  INAPPROPRIATE_CONTENT = 'INAPPROPRIATE_CONTENT',
  VIOLENCE = 'VIOLENCE',
  MISINFORMATION = 'MISINFORMATION',
  IMPERSONATION = 'IMPERSONATION',
  OTHER = 'OTHER'
}

export enum ReportStatus {
  PENDING = 'PENDING',
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
  reportedId: number;
  reason: ReportReason;
  description: string;
}

// Response DTOs
// Report Management
export interface ReportResponse {
  reportedType: ReportedType;
  reason: ReportReason;
  description: string;
  status: ReportStatus;
  reporterUsername: string;
}

//Report Details Response
export interface ReportDetails {
  id: number;
  reportedId: number;
  reporterId: number;
  reportedType: ReportedType;
  reason: ReportReason;
  description: string;
  status: ReportStatus;
  createdAt: string;
  reporterUsername: string;
  reportedName: string;
  reportedStatus?: string;
  adminNotes?: string;
  resolvedByUsername?: string;
  resolvedAt?: string;
}


// export interface ReportResponse {
//   content: ReportDetails[];
//   pageable: Pageable;
//   totalPages: number;
//   totalElements: number;
//   last: boolean;
//   first: boolean;
//   number: number;
//   size: number;
//   numberOfElements: number;
//   empty: boolean;
// }

export const ReportReasonLabels: Record<ReportReason, string> = {
  [ReportReason.SPAM]: 'Spam or misleading content',
  [ReportReason.HARASSMENT]: 'Harassment or bullying',
  [ReportReason.HATE_SPEECH]: 'Hate speech or discrimination',
  [ReportReason.INAPPROPRIATE_CONTENT]: 'Inappropriate or offensive content',
  [ReportReason.VIOLENCE]: 'Violence or threats',
  [ReportReason.MISINFORMATION]: 'Misinformation',
  [ReportReason.IMPERSONATION]: 'Impersonation',
  [ReportReason.OTHER]: 'Other'
};
