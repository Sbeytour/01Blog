import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  CreateReportRequest,
  ReportResponse,
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
}
