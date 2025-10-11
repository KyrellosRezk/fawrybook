import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { SignInRequest } from '../payloads/requests/signin-request';

@Component({
  selector: 'app-signin',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './signin.html',
  styleUrl: './signin.css'
})
export class Signin implements OnInit{

  signinForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private auth: Auth,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.signinForm = this.fb.group({
      identifier: ['', [Validators.required]],
      password: ['', Validators.required]
    });
  }
  onSubmit() {
    if (this.signinForm.invalid) return;

    const payload: SignInRequest = {
      identifier: this.signinForm.value;
    }

    this.auth.signin(payload).subscribe({
      next: (response: SignInResponse) => {
        // Case 1: Received accessToken & refreshToken → redirect home
        if (response.accessToken && response.refreshToken) {
          localStorage.setItem('accessToken', response.accessToken);
          localStorage.setItem('refreshToken', response.refreshToken);
          this.router.navigate(['/home']);
        }
        // Case 2: Received otpToken → redirect to verify OTP
        else if (response.OTPToken) {
          localStorage.setItem('otpToken', response.OTPToken);
          this.router.navigate(['/verify-otp']);
        } else {
          alert('Unexpected response from server');
        }
      },
      error: err => {
        console.error(err);
        alert('Sign in failed!');
      }
    });
  }
}