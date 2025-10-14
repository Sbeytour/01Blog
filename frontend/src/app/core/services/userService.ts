import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { User } from '../models/user';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}`;

  getUserProfile(username: String): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/profile/${username}`)
  }

  updateProfile(updateData: Partial<User>): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/profile`, updateData);
  }

  updateProfileImg(formData: FormData): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/profile/picture`, formData);
  }

  deletProfilePic(): Observable<User> {
    return this.http.delete<User>(`${this.apiUrl}/profile/picture`);
  }
}