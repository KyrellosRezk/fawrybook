import { Routes } from '@angular/router';
import { Signup } from './auth/signup/signup';

export const routes: Routes = [
  { path: '', redirectTo: '/signup', pathMatch: 'full' },
  { path: 'signup', component: Signup },
    { path: 'verify-otp', component: Ve rifyOtp },
  { path: '**', redirectTo: '/signup' }
];
