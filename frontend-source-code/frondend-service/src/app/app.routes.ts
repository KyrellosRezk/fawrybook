import { Routes } from '@angular/router';
import { Signup } from './auth/signup/signup';
import { VerifyOtp } from './auth/verify-otp/verify-otp';
import { Signin } from './auth/signin/signin';
import { FeedsComponent } from './feed/feeds';
import { Profile } from './profile/profile';

export const routes: Routes = [
  { path: '', redirectTo: '/signin', pathMatch: 'full' },
  { path: 'signup', component: Signup },
  { path: 'verify-otp', component: VerifyOtp },
  { path: 'profile/:id', component: Profile },
  { path: 'signin', component: Signin },
  { path: 'home', component: FeedsComponent },
  { path: '**', redirectTo: '/signin' }
];
