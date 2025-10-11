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
  otpToken = '';

  constructor(
    private fb: FormBuilder,
    private auth: Auth,
    private router: Router
  ) {}

  ngOnInit() {
    if (typeof window !== 'undefined') {
      // Get OTP token only in browser
      this.otpToken = localStorage.getItem('otpToken') || '';

      if (!this.otpToken) {
        window.alert('OTP token missing. Please sign in again.');
        this.router.navigate(['/signin']);
        return;
      }
    }

    this.otpForm = this.fb.group({
      otp: ['', [Validators.required, Validators.pattern(/^\d{6}$/)]]
    });
  }

  onSubmit() {
    if (this.otpForm.invalid) return;

    const verifyOtpRequest: VerifyOtpRequest = {
      otp: this.otpForm.value.otp
    };

    this.auth.verifyOtp(verifyOtpRequest).subscribe({
      next: () => {
        window.alert('OTP verified successfully!');
        this.router.navigate(['/home']);
      },
      error: err => {
        console.error(err);
        const message = err.error?.message?.toLowerCase() || '';
        console.error
        if (err.error.status === 401 && message.includes('Invalid otp')) {
          alert('Invalid OTP. Try again.');
        } else {
          alert('OTP expired. Please sign in again.');
          localStorage.removeItem("otpToken")
          this.router.navigate(['/signin']);
        }
      }
    });
  }

  resendOtp() {
    this.auth.resetOtp().subscribe({
      next: () => {
        alert('A new OTP has been sent!');
      },
      error: err => {
        alert('Failed to resend OTP. Please sign in again.');
        this.router.navigate(['/signin']);
      }
    });
  }
}