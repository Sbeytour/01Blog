import { inject, Injectable } from '@angular/core';
import { AuthResponse, LoginRequest, RegisterRequest } from '../models/auth';
import { Observable, tap } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private TOKEN_KEY = 'Token'
  private loginUrl = `${environment.apiUrl}/auth/login`
  private registerUrl = `${environment.apiUrl}/auth/register`;
  private http = inject(HttpClient);
  private router = inject(Router);

  register(registerData: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.registerUrl}`, registerData).pipe(tap((response: AuthResponse) => {
      localStorage.setItem(this.TOKEN_KEY, response.token)
    }))
  }

  login(loginData: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.loginUrl}`, loginData).pipe(tap((response: AuthResponse) => {
      localStorage.setItem(this.TOKEN_KEY, response.token)
    }))
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    this.router.navigate(['/auth/login'])
  }
}
