import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Auth } from '../auth';
import { ObjectResponse } from '../payloads/responses/object.response';
import { Governorate } from '../governorate';
import { Position } from '../position';

@Component({
  selector: 'app-signup',
  imports: [],
  templateUrl: './signup.html',
  styleUrl: './signup.css'
})
export class Signup implements OnInit {
    
  signupForm: FormGroup;
  governorates: ObjectResponse[] = [];
  positions: ObjectResponse[] = [];
  otpToken?: string; 

  constructor(
    private fb: FormBuilder,
    private auth: Auth,
    private governorate: Governorate,
    private position: Position
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

  onSubmit() {
    if (this.signupForm.invalid) return;

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.authService.signup(this.signupForm.value).subscribe({
      next: (res) => {
        this.successMessage = 'Account created successfully!';
        this.signupForm.reset();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Signup failed. Please try again.';
      },
      complete: () => (this.loading = false)
    });
  }
}
