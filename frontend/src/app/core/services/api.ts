import { Injectable } from "@angular/core";
import { environment } from "../../../environments/environment";
import { HttpClient } from "@angular/common/http";
import { AuthResponse, LoginRequest, RegisterRequest } from "../models/auth";
import { Observable } from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class ApiService {
    private apiUrl = environment.apiUrl;
    private loginUrl = `${this.apiUrl}/auth/login`
    private registerUrl = `${this.apiUrl}/auth/register`

    constructor(private http: HttpClient) {};

    register(registerData: RegisterRequest): Observable<AuthResponse> {
        return this.http.post<AuthResponse>(`${this.registerUrl}`, registerData)
    }

    login(loginData: LoginRequest): Observable<AuthResponse> {
        return this.http.post<AuthResponse>(`${this.loginUrl}`, loginData);
    }
}