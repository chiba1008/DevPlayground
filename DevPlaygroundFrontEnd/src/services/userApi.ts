import { HttpClient } from './http'

export interface CreateUserRequest {
  username: string
  email: string
  password: string
  role: string
}

export interface UserResponse {
  id: number
  username: string
  email: string
  role: string
  enabled: boolean
}

class UserApi {
  private httpClient: HttpClient

  constructor() {
    this.httpClient = new HttpClient()
  }

  async getAllUsers(): Promise<UserResponse[]> {
    return this.httpClient.get<UserResponse[]>('/admin/users')
  }

  async createUser(userRequest: CreateUserRequest): Promise<UserResponse> {
    return this.httpClient.post<UserResponse>('/admin/users', userRequest)
  }

  async deleteUser(id: number): Promise<void> {
    return this.httpClient.delete<void>(`/admin/users/${id}`)
  }

  async getUserByUsername(username: string): Promise<UserResponse> {
    return this.httpClient.get<UserResponse>(`/admin/users/search?username=${username}`)
  }
}

export const userApi = new UserApi()