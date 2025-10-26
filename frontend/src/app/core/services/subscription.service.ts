import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface FollowResponse {
  isFollowing: boolean;
  followersCount: number;
  followingCount: number;
}

@Injectable({
  providedIn: 'root'
})
export class SubscriptionService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;

  /**
   * Follow a user
   */
  followUser(userId: number): Observable<FollowResponse> {
    return this.http.post<FollowResponse>(`${this.apiUrl}/api/users/${userId}/follow`, {});
  }

  /**
   * Unfollow a user
   */
  unfollowUser(userId: number): Observable<FollowResponse> {
    return this.http.delete<FollowResponse>(`${this.apiUrl}/api/users/${userId}/unfollow`);
  }

  /**
   * Check if current user follows a specific user
   */
  isFollowing(userId: number): Observable<{ isFollowing: boolean }> {
    return this.http.get<{ isFollowing: boolean }>(`${this.apiUrl}/api/users/${userId}/is-following`);
  }
}
