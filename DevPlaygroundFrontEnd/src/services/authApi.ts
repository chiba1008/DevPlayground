import { HttpClient } from './http'
import type { LoginRequest, LoginResponse, UserInfo } from '@/types/auth'

class AuthApi {
  private httpClient: HttpClient

  constructor() {
    this.httpClient = new HttpClient()
  }

  async login(credentials: LoginRequest): Promise<LoginResponse> {
    return this.httpClient.post<LoginResponse>('/login', credentials)
  }

  async logout(): Promise<void> {
    await this.httpClient.post('/logout-manual')
  }

  async getCurrentUser(): Promise<UserInfo> {
    return this.httpClient.get<UserInfo>('/current-user')
  }
}

export const authApi = new AuthApi()