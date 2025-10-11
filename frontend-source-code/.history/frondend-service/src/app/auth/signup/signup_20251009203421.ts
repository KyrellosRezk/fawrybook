import { Component } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Auth } from '../auth';

@Component({
  selector: 'app-signup',
  imports: [],
  templateUrl: './signup.html',
  styleUrl: './signup.css'
})
export class Signup {
    
  signupForm: FormGroup;

  constructor(private fb: FormBuilder, private authService: Auth) {
    this.signupForm = this.fb.group({
      username: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }
}
