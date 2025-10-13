import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Auth } from '../auth';
import { ObjectResponse } from '../../payloads/responses/object-response';
import { Governorate } from '../governorate';
import { Position } from '../position';
import { SignUpRequest } from '../../payloads/requests/signup-request';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { OTPResponse } from '../../payloads/responses/otp-response';
import { FileService } from '../../profile/file';

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
  profilePhotoFile!: File;
  profilePhotoPreview?: string;

  constructor(
    private fb: FormBuilder,
    private auth: Auth,
    private fileService: FileService,
    private governorate: Governorate,
    private position: Position,
    private router: Router
  ) {}

  ngOnInit(): void {
    localStorage.clear();
    this.signupForm = this.fb.group({
      username: ['', [Validators.required, Validators.maxLength(255)]],
      firstName: ['', [Validators.required, Validators.maxLength(255)]],
      middleName: ['', [Validators.required, Validators.maxLength(255)]],
      lastName: ['', [Validators.required, Validators.maxLength(255)]],
      phoneNumber: ['', [Validators.required, Validators.pattern(/^[0-9]{8,15}$/)]],
      email: ['', [Validators.required, Validators.email]],
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
    if (this.signupForm.invalid) {
      const invalidFields = Object.keys(this.signupForm.controls)
        .filter(key => this.signupForm.get(key)?.invalid);
      console.log('Invalid fields:', invalidFields);
      return;
    }

    const payload: SignUpRequest = this.signupForm.value;

    this.auth.signup(payload).subscribe({
      next: (response: OTPResponse) => {
        alert('Signup successful! OTP token received.');

        if (this.profilePhotoFile) {
          this.fileService.saveProfileImage(this.profilePhotoFile).subscribe({
            next: () => console.log('Profile image uploaded successfully'),
            error: (err) => console.error('Profile image upload failed', err)
          });
        }

        this.router.navigate(['/verify-otp']);
      },
      error: (err) => {
        console.error(err);
        alert('Signup failed!');
      }
    });
  }

    onProfilePhotoSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      this.profilePhotoFile = input.files[0];

      const reader = new FileReader();
      reader.onload = e => this.profilePhotoPreview = e.target?.result as string;
      reader.readAsDataURL(this.profilePhotoFile);
    }
  }

  goToSignin() {
    this.router.navigate(['/signin']);
  }
}
