import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Auth } from '../auth';
import { HttpHeaders } from '@angular/common/http';
import { VerifyOtpRequest } from '../payloads/requests/verify-otp-request';
import { OTPResponse } from '../payloads/responses/otp.response';

@Component({
  selector: 'app-verify-otp',
  standalone: true,
  templateUrl: './verify-otp.html',
  styleUrls: ['./verify-otp.css'],
  imports: [CommonModule, ReactiveFormsModule]
})
export class VerifyOtp {
  otpForm!: FormGroup;
  otpToken!: string;

  constructor(
    private fb: FormBuilder,
    private auth: Auth,
    private router: Router
  ) {}

  ngOnInit() {
    if(localStorage)
    this.otpToken = localStorage.getItem('otpToken') || '';
    if (!this.otpToken) {
      alert('OTP token missing. Please sign in again.');
      this.router.navigate(['/signin']);
      return;
    }

    this.otpForm = this.fb.group({
      otp: ['', [Validators.required, Validators.pattern(/^\d{6}$/)]]
    });
  }

  onSubmit() {
    if (this.otpForm.invalid) return;

    const headers = new HttpHeaders().set('Authorization', `Bearer ${this.otpToken}`);
    const verifyOtpRequest: VerifyOtpRequest = {
      otp: this.otpForm.value.otp
    }

    this.auth.verifyOtp(verifyOtpRequest, headers).subscribe({
      next: () => {
        alert('OTP verified successfully!');
        localStorage.removeItem('otpToken');
        this.router.navigate(['/home']);
      },
      error: err => {
        console.error(err);
        alert('Invalid OTP!');
      }
    });
  }

  resendOtp() {
    if (!this.otpToken) return;

    const headers = new HttpHeaders().set('Authorization', `Bearer ${this.otpToken}`);
    this.auth.resetOtp(headers).subscribe({
      next: (response: OTPResponse) => {
        this.otpToken = response.OTPToken;
        localStorage.setItem('otpToken', this.otpToken);
        alert('A new OTP has been sent!');
      },
      error: err => {
        console.error(err);
        alert('Failed to resend OTP.');
      }
    });
  }
}