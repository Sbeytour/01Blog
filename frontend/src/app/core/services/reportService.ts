import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  CreateReportRequest,
  ReportResponse,
  PagedReportResponse
} from '../models/report';

@Injectable({
  providedIn: 'root'
})
export class ReportService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/reports`;

  /**
   * Create a new report
   */
  createReport(request: CreateReportRequest): Observable<ReportResponse> {
    return this.http.post<ReportResponse>(this.apiUrl, request);
  }

  /**
   * Get current user's submitted reports with pagination
   */
  getMyReports(page: number = 0, size: number = 10): Observable<PagedReportResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PagedReportResponse>(`${this.apiUrl}/my-reports`, { params });
  }
}
