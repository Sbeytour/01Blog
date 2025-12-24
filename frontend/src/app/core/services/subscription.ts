import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface FollowResponse {
  isFollowedByCurrentUser: boolean;
  followersCount: number;
  followingCount: number;
}

@Injectable({
  providedIn: 'root'
})
export class SubscriptionService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/users`;

  followUser(userId: number): Observable<FollowResponse> {
    return this.http.post<FollowResponse>(`${this.apiUrl}/${userId}/follow`, {});
  }

  unfollowUser(userId: number): Observable<FollowResponse> {
    return this.http.delete<FollowResponse>(`${this.apiUrl}/${userId}/unfollow`);
  }
}
