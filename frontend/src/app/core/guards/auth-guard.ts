import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth';
import { catchError, map, of } from 'rxjs';

export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const token = localStorage.getItem('Token');

  if (!authService.isAuthenticated) {
    router.navigate(['/auth/login']);
    return false;
  }

  if (token && authService.isTokenExpired(token)) {
    authService.logout();
    return false;
  }

  return authService.getCurrentUser().pipe(
    map(() => true),
    catchError((error) => {
      console.error('Failed to get current user:', error);
      authService.logout();
      return of(false);
    })
  );
};