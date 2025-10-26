import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { Comment, CreateCommentRequest, PagedCommentResponse } from '../models/comment';

@Injectable({
  providedIn: 'root'
})
export class CommentService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api`;

  // Create a comment on a post
  createComment(postId: number, request: CreateCommentRequest): Observable<Comment> {
    return this.http.post<Comment>(`${this.apiUrl}/posts/${postId}/comments`, request);
  }

  // Get paginated comments for a specific post
  getCommentsByPostId(postId: number, page: number = 0, size: number = 5): Observable<PagedCommentResponse> {
    const url = `${this.apiUrl}/posts/${postId}/comments`;
    const params = { page: page.toString(), size: size.toString() };

    return this.http.get<PagedCommentResponse>(url, { params });
  }

  // Update a comment
  updateComment(commentId: number, request: CreateCommentRequest): Observable<Comment> {
    return this.http.put<Comment>(`${this.apiUrl}/comments/${commentId}`, request);
  }

  // Delete a comment
  deleteComment(commentId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/comments/${commentId}`);
  }
}
