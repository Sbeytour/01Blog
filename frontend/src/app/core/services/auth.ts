import { Injectable } from '@angular/core';
import { ApiService } from './api';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
 constructor(
  private apiService: ApiService,
  private router : Router
 ){}
}
