import { Routes } from '@angular/router';
import { guestGuard } from './core/guards/guest-guard';
import { authGuard } from './core/guards/auth-guard';
import { adminGuard } from './core/guards/admin-guard';

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
                path: '',
                loadComponent: () => import('./features/profile/profile').then(m => m.UserProfile)
            },
            {
                path: ':username',
                loadComponent: () => import('./features/profile/profile').then(m => m.UserProfile)
            }
        ]
    },
    {
        path: 'api/posts',
        canActivate: [authGuard],
        children: [
            {
                path: 'create',
                loadComponent: () => import('./features/posts/post-create/post-create').then(m => m.PostCreate)
            },
            {
                path: ':id',
                loadComponent: () => import('./features/posts/post-detail/post-detail').then(m => m.PostDetail)
            }
        ]
    },
    {
        path: 'admin',
        canActivate: [authGuard, adminGuard],
        children: [
            {
                path: '',
                redirectTo: 'dashboard',
                pathMatch: 'full'
            },
            {
                path: 'dashboard',
                loadComponent: () => import('./features/admin/dashboard/dashboard').then(m => m.Dashboard)
            }
        ]
    },
    {
        path: '**',
        redirectTo: '/auth/login'
    }
];