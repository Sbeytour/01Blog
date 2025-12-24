import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { Post } from '../models/post';

export interface PagedPostResponse {
  content: Post[];
  currentPage: number;
  totalPages: number;
  totalElements: number;
  hasMore: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class PostService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/post`

  //Create Post
  createPost(formData: FormData): Observable<Post> {
    return this.http.post<Post>(this.apiUrl, formData);
  }
  // //Get all Posts
  // getAllPosts(): Observable<Post[]> {
  //   return this.http.get<Post[]>(this.apiUrl);
  // }

  //Get all Posts with pagination (for infinite scroll)
  getAllPosts(page: number = 0, size: number = 10): Observable<PagedPostResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PagedPostResponse>(`${this.apiUrl}`, { params });
  }

  //Get Posts Created by a specific user
  getPostsByUser(userId: number): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/user/${userId}`);
  }

  //Get a single post
  getSinglePost(postId: number): Observable<Post> {
    return this.http.get<Post>(`${this.apiUrl}/${postId}`);
  }

  //Update a Post
  updatePost(postId: number, formData: FormData): Observable<Post> {
    return this.http.put<Post>(`${this.apiUrl}/${postId}`, formData);
  }

  //Delete a Post
  deletePost(postId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${postId}`);
  }


}
