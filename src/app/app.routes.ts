import { Routes } from '@angular/router';

export const routes: Routes = [
  {
        path: '',
        redirectTo: 'registration',
        pathMatch: 'full'
    },
    {
        path: 'registration',
        loadComponent: () => import('./registration/registration.component').then(m => m.RegistrationComponent)
    }

];
