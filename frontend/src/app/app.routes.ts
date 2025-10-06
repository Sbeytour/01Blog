import { Routes } from '@angular/router';
import { guestGuard } from './core/guards/guest-guard';
import { authGuard } from './core/guards/auth-guard';

export const routes: Routes = [
    {
        path: '',
        redirectTo: '/home',
        pathMatch: 'full'
    },
    {
        path: "auth",
        canActivate: [guestGuard],
        children: [
            {
                path: '',
                redirectTo: 'login',
                pathMatch: 'full'
            },
            {
                path: 'login',
                loadComponent: () => import('./features/auth/login/login').then(m => m.Login)
            },
            {
                path: 'register',
                loadComponent: () => import('./features/auth/register/register').then(m => m.Register)
            }
        ],
    },
    {
        path: "home",
        canActivate: [authGuard],
        loadComponent: () => import('./features/home/home').then(m => m.Home)
    },

    {
        path: "profile",
        canActivate: [authGuard],
        children: [
            {
                path: 'edit',
                loadComponent: () => import('./features/profile/profile-edit/profile-edit').then(m => m.ProfileEdit)
            },
            {
                path: ':username',
                loadComponent: () => import('./features/profile/user-profile/user-profile').then(m => m.UserProfile)
            }
        ]
    },
    {
        path: '**',
        redirectTo: '/auth/login'
    }
];