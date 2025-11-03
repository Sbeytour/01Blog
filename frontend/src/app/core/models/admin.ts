import { ReportReason, ReportStatus, ReportedType } from './report';
import { User, UserRole } from './user';

// Admin Statistics
export interface AdminStats {
  totalUsers: number;
  totalPosts: number;
  pendingReports: number;
  mostReportedUser: User | null;
  mostReportedUserReportCount: number;
}

export interface PagedUserResponse {
  content: User[];
  pageable: Pageable;
  totalPages: number;
  totalElements: number;
  last: boolean;
  first: boolean;
  number: number;
  size: number;
  numberOfElements: number;
  empty: boolean;
}

// Report Management
export interface AdminReportDetails {
  id: number;
  reportedType: ReportedType;
  reportedId: number;
  reason: ReportReason;
  description: string;
  status: ReportStatus;
  createdAt: string;
  reporterUsername: string;
  reporterId: number;
  reportedEntityName: string;
  adminNotes?: string;
  resolvedByUsername?: string;
  resolvedAt?: string;
}

export interface PagedReportResponse {
  content: AdminReportDetails[];
  pageable: Pageable;
  totalPages: number;
  totalElements: number;
  last: boolean;
  first: boolean;
  number: number;
  size: number;
  numberOfElements: number;
  empty: boolean;
}

// Common Pagination Interface
export interface Pageable {
  pageNumber: number;
  pageSize: number;
  sort: Sort;
  offset: number;
  paged: boolean;
  unpaged: boolean;
}

export interface Sort {
  empty: boolean;
  sorted: boolean;
  unsorted: boolean;
}

// Request DTOs
export interface BanUserRequest {
  reason: string;
}

export interface UpdateUserRoleRequest {
  role: UserRole;
}

export enum ReportAction {
  NONE = 'NONE',
  BAN_USER = 'BAN_USER',
  DELETE_POST = 'DELETE_POST',
  DELETE_USER = 'DELETE_USER'
}

export interface ResolveReportRequest {
  status: ReportStatus;
  adminNotes: string;
  action: ReportAction;
}

// Label mappings
export const ReportActionLabels: Record<ReportAction, string> = {
  [ReportAction.NONE]: 'No Action',
  [ReportAction.BAN_USER]: 'Ban User',
  [ReportAction.DELETE_POST]: 'Delete Post',
  [ReportAction.DELETE_USER]: 'Delete User Permanently'
};
