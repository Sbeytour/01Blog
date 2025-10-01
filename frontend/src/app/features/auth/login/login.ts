import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class Login implements OnInit {
  loginForm !: FormGroup;

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
    console.log('login successfully');
  }
}
