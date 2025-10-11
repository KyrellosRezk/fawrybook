import { Routes } from '@angular/router';
import { Signup } from './auth/signup/signup';
import { VerifyOtp } from './auth/verify-otp/verify-otp';
import { Signin } from './auth/signin/signin';
import { Home } from './home/home';

export const routes: Routes = [
  { path: '', redirectTo: '/signup', pathMatch: 'full' },
  { path: 'signup', component: Signup },
  { path: 'verify-otp', component: VerifyOtp },
  { path: 'signin', component: Signin },
  { path: 'signin', component: Home },
  { path: '**', redirectTo: '/signup' }
];
