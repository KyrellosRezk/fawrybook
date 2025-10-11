import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Auth } from '../auth';
import { ObjectResponse } from '../payloads/responses/object.response';
import { Governorate } from '../governorate';
import { Position } from '../position';
import { SignUpRequest } from '../payloads/requests/signup-request';
import { OTPResponse } from '../payloads/responses/otp.response';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './signup.html',
  styleUrls: ['./signup.css']
})
export class Signup implements OnInit {
  signupForm!: FormGroup;
  governorates: ObjectResponse[] = [];
  positions: ObjectResponse[] = [];
  otpToken?: string; 

  constructor(
    private fb: FormBuilder,
    private auth: Auth,
    private governorate: Governorate,
    private position: Position,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.signupForm = this.fb.group({
      username: ['', [Validators.required, Validators.maxLength(255)]],
      firstName: ['', [Validators.required, Validators.maxLength(255)]],
      middleName: ['', [Validators.required, Validators.maxLength(255)]],
      lastName: ['', [Validators.required, Validators.maxLength(255)]],
      phoneNumber: ['', [Validators.required, Validators.pattern(/^[0-9]{8,15}$/)]],
      email: ['', [Validators.required, Validators.email]],
      lng: ['', [Validators.required, Validators.min(-180), Validators.max(180)]],
      lat: ['', [Validators.required, Validators.min(-90), Validators.max(90)]],
      password: ['', [
        Validators.required,
        Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#^+=_~])[A-Za-z\d@$!%*?&#^+=_~]{8,}$/)
      ]],
      governorateId: ['', Validators.required],
      positionId: ['', Validators.required]
    });

    this.loadGovernorates();
    this.loadPositions();
  }

    loadGovernorates() {
    this.governorate.getAll().subscribe(data => this.governorates = data);
  }

  loadPositions() {
    this.position.getAll().subscribe(data => this.positions = data);
  }

  onSubmit() {
  if (this.signupForm.invalid.) {
    console.log("Form is invalid", this.signupForm.value);
    return;
  }

    const payload: SignUpRequest = this.signupForm.value;
    this.auth.signup(payload).subscribe({
      next: (response: OTPResponse) => {
        alert('Signup successful! OTP token received.');
        this.router.navigate(['/verify-otp']);
      },
      error: err => {
        console.error(err);
        alert('Signup failed!');
      }
    });
  }


  goToSignin() {
    this.router.navigate(['/signin']);
  }
}
