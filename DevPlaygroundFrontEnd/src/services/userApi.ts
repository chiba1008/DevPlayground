import type { User } from '@/types/api'
import { HttpClient } from './http'

export class UserApi {
  private http: HttpClient

  constructor(httpClient: HttpClient) {
    this.http = httpClient
  }

  async getAllUsers(): Promise<User[]> {
    return this.http.get<User[]>('/users/all')
  }

  async getUserByUsername(username: string): Promise<User | null> {
    try {
      return await this.http.get<User>(`/users/by-username?username=${encodeURIComponent(username)}`)
    } catch (error: unknown) {
      if (
        typeof error === 'object' &&
        error !== null &&
        'status' in error &&
        (error as { status?: number }).status === 404
      ) {
        return null
      }
      throw error
    }
  }

  async getUserByEmail(email: string): Promise<User | null> {
    try {
      return await this.http.get<User>(`/users/by-email?email=${encodeURIComponent(email)}`)
    } catch (error: unknown) {
      if (
        typeof error === 'object' &&
        error !== null &&
        'status' in error &&
        (error as { status?: number }).status === 404
      ) {
        return null
      }
      throw error
    }
  }

  async userExistsByUsername(username: string): Promise<boolean> {
    return this.http.get<boolean>(`/users/exists-by-username?username=${encodeURIComponent(username)}`)
  }

  async userExistsByEmail(email: string): Promise<boolean> {
    return this.http.get<boolean>(`/users/exists-by-email?email=${encodeURIComponent(email)}`)
  }

  async saveUser(user: User): Promise<User> {
    return this.http.post<User>('/users/save', user)
  }
}