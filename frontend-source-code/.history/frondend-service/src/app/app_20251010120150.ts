import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Auth } from './auth/auth';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('frondend-service');

  constructor(
    private auth: Auth
) {}

  ngOnInit(): void {
  const accessToken = localStorage.getItem('accessToken');
  if (accessToken) {
    this.auth.signinWithToken().subscribe({
      next: res => {
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
