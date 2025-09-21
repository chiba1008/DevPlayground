export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  success: boolean
  username: string
}

export interface UserInfo {
  username: string
  authorities: string
  roles: string[]
}

export interface AuthState {
  isAuthenticated: boolean
  user: UserInfo | null
  loading: boolean
}