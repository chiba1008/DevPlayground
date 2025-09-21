export interface User {
  id?: number;
  username: string;
  email: string;
}

export interface HelloResponse {
  message: string;
  status: string;
}

export interface ApiError {
  message: string;
  status: number;
}