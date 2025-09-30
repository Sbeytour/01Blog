import { User } from "./user";

export interface AuthResponse {
    token: string;
    userData: User;
}

export interface LoginRequest {
    identifier: string;
    password: string;
}

export interface RegisterRequest {
    username: string;
    firstName: string;
    lastName: string;
    email: string;
    password: string;
    bio?: string;
    profileImgUrl?: string;
}

export interface AuthState {
    isAuthenticated: boolean;
    user: User | null;
    token: string | null;
}
