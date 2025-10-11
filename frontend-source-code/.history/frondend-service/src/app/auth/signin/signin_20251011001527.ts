import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { SignInRequest } from '../payloads/requests/signin-request';
import { SignInResponse } from '../payloads/responses/sign-in-response';
import { Auth } from '../auth';
import { Router } from '@angular/router';

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
      identifier: this.signinForm.value.identifier,
      password: this.signinForm.value.password
    }

    this.auth.signin(payload).subscribe({
      conso
      next: (response: SignInResponse) => {
        if (response.accessToken && response.refreshToken) {
          this.router.navigate(['/home']);
        }
        else if (response.OTPToken) {
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

  goToSignup(): void {
    this.router.navigate(['/signup']);
  }
}