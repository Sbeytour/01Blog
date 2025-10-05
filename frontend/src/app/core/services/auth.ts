import { inject, Injectable, signal } from '@angular/core';
import { AuthResponse, LoginRequest, RegisterRequest } from '../models/auth';
import { Observable, tap } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Router } from '@angular/router';
import { User } from '../models/user';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private TOKEN_KEY = 'Token';
  private loginUrl = `${environment.apiUrl}/auth/login`;
  private registerUrl = `${environment.apiUrl}/auth/register`;

  private http = inject(HttpClient);
  private router = inject(Router);

  // Signal to track current user
  currentUser = signal<User | null>(null);

  register(registerData: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.registerUrl}`, registerData).pipe(
      tap((response: AuthResponse) => {
        localStorage.setItem(this.TOKEN_KEY, response.token);
        this.currentUser.set(response.userData);
      })
    );
  }

  login(loginData: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.loginUrl}`, loginData).pipe(
      tap((response: AuthResponse) => {
        localStorage.setItem(this.TOKEN_KEY, response.token);
        this.currentUser.set(response.userData);
      })
    );
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    this.currentUser.set(null);
    this.router.navigate(['/auth/login']);
  }

  isAuthenticated(): boolean {
    const token = localStorage.getItem(this.TOKEN_KEY);
    if (!token) return false;

    return !this.isTokenExpired(token);
  }

  isAdmin(): boolean {
    const user = this.currentUser();
    return user?.role === 'ADMIN';
  }

  public getCurrentUser(): Observable<User> {
    return this.http.get<User>(`${environment.apiUrl}/auth/me`).pipe(tap(
      (user) => {
        this.currentUser.set(user);
      }
    ))
  }

  public isTokenExpired(token: string): boolean {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      if (!payload.exp) return false;

      const expirationDate = new Date(payload.exp * 1000);
      return expirationDate < new Date();
    } catch {
      return true;
    }
  }
}