export interface User {
  id?: number;
  username: string;
  email: string;
}

export interface HelloResponse {
  message: string;
  timestamp: string;
}

const API_BASE_URL = 'http://localhost:8080/api';

class ApiService {
  private async fetchJson<T>(url: string, options?: RequestInit): Promise<T> {
    const response = await fetch(`${API_BASE_URL}${url}`, {
      headers: {
        'Content-Type': 'application/json',
        ...options?.headers,
      },
      ...options,
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    return response.json();
  }

  // Hello API
  async getHello(): Promise<HelloResponse> {
    return this.fetchJson<HelloResponse>('/hello-world');
  }

  // User APIs
  async getAllUsers(): Promise<User[]> {
    return this.fetchJson<User[]>('/users/all');
  }

  async getUserByUsername(username: string): Promise<User | null> {
    try {
      return await this.fetchJson<User>(`/users/by-username?username=${encodeURIComponent(username)}`);
    } catch (error) {
      if (error instanceof Error && error.message.includes('404')) {
        return null;
      }
      throw error;
    }
  }

  async getUserByEmail(email: string): Promise<User | null> {
    try {
      return await this.fetchJson<User>(`/users/by-email?email=${encodeURIComponent(email)}`);
    } catch (error) {
      if (error instanceof Error && error.message.includes('404')) {
        return null;
      }
      throw error;
    }
  }

  async userExistsByUsername(username: string): Promise<boolean> {
    return this.fetchJson<boolean>(`/users/exists-by-username?username=${encodeURIComponent(username)}`);
  }

  async userExistsByEmail(email: string): Promise<boolean> {
    return this.fetchJson<boolean>(`/users/exists-by-email?email=${encodeURIComponent(email)}`);
  }

  async saveUser(user: User): Promise<User> {
    return this.fetchJson<User>('/users/save', {
      method: 'POST',
      body: JSON.stringify(user),
    });
  }
}

export const apiService = new ApiService();