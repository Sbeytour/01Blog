import { Routes } from '@angular/router';

export const routes: Routes = [
    {
        path: "auth",
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

        ]
    }
];
