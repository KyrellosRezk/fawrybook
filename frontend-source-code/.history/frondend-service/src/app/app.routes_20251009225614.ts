import { Routes } from '@angular/router';
import { Signup } from './auth/signup/signup';
import { VerifyOtp } from './auth/verify-otp/verify-otp';

export const routes: Routes = [
  { path: '', redirectTo: '/signup', pathMatch: 'full' },
  { path: 'signup', component: Signup },
  { path: 'verify-otp', component: VerifyOtp },
  { path: 'signin', component:}
  { path: '**', redirectTo: '/signup' }
];
