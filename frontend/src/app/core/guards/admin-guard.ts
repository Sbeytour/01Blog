import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth';
import { map, of } from 'rxjs';

export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // If user is already loaded and is admin, allow access
  if (authService.currentUser() && authService.isAdmin()) {
    return true;
  }

  // If user is already loaded but is NOT admin, redirect
  if (authService.currentUser() && !authService.isAdmin()) {
    router.navigate(['/home']);
    return false;
  }

  // If user is not loaded yet (e.g., on refresh), wait for getCurrentUser() to complete
  // The authGuard should have already initiated this call, so we can check again
  return authService.getCurrentUser().pipe(
    map(() => {
      if (authService.isAdmin()) {
        return true;
      }
      router.navigate(['/home']);
      return false;
    })
  );
};
