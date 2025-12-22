import { ReportStatus } from './report';
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
  durationDays?: number;
  permanent?: boolean;
}

export interface UpdateUserRoleRequest {
  role: UserRole;
}

export enum ReportAction {
  NONE = 'NONE',
  BAN_USER = 'BAN_USER',
  HIDE_POST = 'HIDE_POST',
  DELETE_POST = 'DELETE_POST',
  DELETE_USER = 'DELETE_USER'
}

export interface ResolveReportRequest {
  status: ReportStatus;
  adminNotes: string;
  action: ReportAction;
  banDurationDays?: number;
  banPermanent?: boolean;
}

// Label mappings
export const ReportActionLabels: Record<ReportAction, string> = {
  [ReportAction.NONE]: 'No Action',
  [ReportAction.BAN_USER]: 'Ban User',
  [ReportAction.HIDE_POST]: 'Hide Post',
  [ReportAction.DELETE_POST]: 'Delete Post',
  [ReportAction.DELETE_USER]: 'Delete User Permanently'
};
