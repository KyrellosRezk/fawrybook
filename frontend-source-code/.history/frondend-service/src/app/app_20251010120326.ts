import { Component, signal } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { Auth } from './auth/auth';
import { SignInResponse } from './auth/payloads/responses/sign-in-response';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('frondend-service');

  constructor(
    private auth: Auth,
    private router: Router
  ) {}

  ngOnInit(): void {
  const accessToken = localStorage.getItem('accessToken');
  if (accessToken) {
    this.auth.signinWithToken().subscribe({
      next: (response: SignInResponse) => {
        console.log('Session restored');
      },
      error: err => {
        console.log('Session invalid, sign in again');
        localStorage.clear();
        this.router.navigate(['/signin']);
      }
    });
  }
}

}
