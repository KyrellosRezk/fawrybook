import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Auth } from '../auth';

@Component({
  selector: 'app-signup',
  imports: [],
  templateUrl: './signup.html',
  styleUrl: './signup.css'
})
export class Signup {
    
  signupForm: FormGroup;

  constructor(private fb: FormBuilder, private auth: Auth) {
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
  }
}
