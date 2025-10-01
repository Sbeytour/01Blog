import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule],
  templateUrl: './register.html',
  styleUrl: './register.scss'
})
export class Register {
  registerForm !: FormGroup;

  formbuilder = inject(FormBuilder);
  
  ngOnInit(): void {
    this.intializeForm();
  }

  private intializeForm(): void {
    this.registerForm = this.formbuilder.group({
      username: ['', [
        Validators.required,
        Validators.minLength(4),
        Validators.maxLength(15),
        Validators.pattern(/^[a-zA-Z0-9]+$/) // Only letters and numbers
      ]],
      email: ['', [
        Validators.required,
        Validators.email,
      ]],
      firstName: ['', [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(15),
        Validators.pattern(/^[a-zA-Z\s]+$/) // Only letters and spaces
      ]],
      lastName: ['', [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(15),
        Validators.pattern(/^[a-zA-Z\s]+$/) // Only letters and spaces
      ]],
      password: ['', [
        Validators.required,
        Validators.minLength(8),
        Validators.maxLength(100),
      ]],
    })
  }

  onSubmit(): void {
    console.log('login successfully');
  }
}
