import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { Post } from '../models/post';

@Injectable({
  providedIn: 'root'
})
export class PostService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/post`

  createPost(formData: FormData): Observable<Post> {
    return this.http.post<Post>(this.apiUrl, formData);
  }
}
