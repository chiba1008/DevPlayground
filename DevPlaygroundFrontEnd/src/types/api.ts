export interface User {
  id?: number;
  username: string;
  email: string;
}

export interface HelloResponse {
  message: string;
  status: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  success: boolean;
  message: string;
  token?: string;
}

export interface ApiError {
  message: string;
  status: number;
}