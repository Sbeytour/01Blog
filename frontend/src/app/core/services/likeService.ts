import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { LikeResponse } from '../models/like';

@Injectable({
  providedIn: 'root'
})
export class LikeService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/post`;

  // Toggle like/unlike on a post
  toggleLike(postId: number): Observable<LikeResponse> {
    return this.http.post<LikeResponse>(`${this.apiUrl}/${postId}/like`, {});
  }
}
