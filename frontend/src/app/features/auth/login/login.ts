import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    RouterLink
  ],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class Login implements OnInit {
  loginForm!: FormGroup;
  hidePassword = true;

  formbuilder = inject(FormBuilder);

  private intializeForm(): void {
    this.loginForm = this.formbuilder.group({
      identifier: ['', [Validators.required,
      Validators.minLength(3),
      Validators.maxLength(50)]],
      password: ['', [
        Validators.required,
        Validators.minLength(6),
        Validators.maxLength(100)
      ]]
    })
  }

  ngOnInit(): void {
    this.intializeForm();
  }

  onSubmit(): void {
    if (this.loginForm.valid) {
      console.log('login successfully', this.loginForm.value);
    }
  }
}