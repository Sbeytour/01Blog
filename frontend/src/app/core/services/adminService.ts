import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  AdminStats,
  PagedUserResponse,
  BanUserRequest,
  ResolveReportRequest,
  UpdateUserRoleRequest
} from '../models/admin';
import { Post } from '../models/post';
import { ReportDetails, ReportResponse } from '../models/report';
@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/admin`;

  // Dashboard Statistics
  getDashboardStats(): Observable<AdminStats> {
    return this.http.get<AdminStats>(`${this.apiUrl}/stats`);
  }

  // User Management
  getUsers(page: number = 0, size: number = 20): Observable<PagedUserResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PagedUserResponse>(`${this.apiUrl}/users`, { params });
  }

  banUser(userId: number, request: BanUserRequest): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/users/${userId}/ban`, request);
  }

  unbanUser(userId: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/users/${userId}/unban`, null);
  }

  deleteUser(userId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/users/${userId}`);
  }

  updateUserRole(userId: number, role: string): Observable<void> {
    const request: UpdateUserRoleRequest = { role: role as any };
    return this.http.put<void>(`${this.apiUrl}/users/${userId}/role`, request);
  }

  // Report Management
  getAllReports(): Observable<ReportResponse[]> {
    return this.http.get<ReportResponse[]>(`${this.apiUrl}/reports`);
  }

  getReportById(reportId: number): Observable<ReportDetails> {
    return this.http.get<ReportDetails>(`${this.apiUrl}/reports/${reportId}`);
  }

  resolveReport(reportId: number, request: ResolveReportRequest): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/reports/${reportId}/resolve`, request);
  }

  // Post Management
  getPosts(): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/posts`);
  }

  hiddePost(postId: number, reason: string): Observable<void> {
    const request: BanUserRequest = { reason };
    return this.http.put<void>(`${this.apiUrl}/posts/${postId}/hidde`, request);
  }

  unhidePost(postId: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/posts/${postId}/unhidde`, null);
  }

  deletePost(postId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/posts/${postId}`);
  }
}
