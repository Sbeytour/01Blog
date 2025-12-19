import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);

  const token = localStorage.getItem("Token");

  const isAuthEndpoint = req.url.includes('/auth/login') || req.url.includes('/auth/register');

  let authReq = req;
  if (token && !isAuthEndpoint) {
    authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    })
  }

  return next(authReq).pipe(
    catchError((error) => {
      if (error.status === 401 && !isAuthEndpoint) {
        localStorage.removeItem("Token");
        router.navigate(['/auth/login']);
      }

      if (error.status === 403) {
        // User is banned - logout and redirect to login
        localStorage.removeItem("Token");
        router.navigate(['/auth/login'], {
          state: { bannedMessage: error.error?.message || 'Your account has been banned. Please contact support.' }
        });
      }

      return throwError(() => error);
    })
  );
};
