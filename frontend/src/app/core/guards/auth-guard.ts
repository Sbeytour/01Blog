import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth';
import { catchError, map, of } from 'rxjs';

export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const token = localStorage.getItem('Token');

  if (!token) {
    router.navigate(['/auth/login']);
    return false;
  }

  if (authService.isTokenExpired(token)) {
    authService.logout();
    return false;
  }

  if (authService.currentUser()) {
    return true;
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