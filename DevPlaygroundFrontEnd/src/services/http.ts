import type { ApiError } from '@/types/api'

const API_BASE_URL = 'http://localhost:8080/api'

export class HttpClient {
  private baseUrl: string

  constructor(baseUrl: string = API_BASE_URL) {
    this.baseUrl = baseUrl
  }

  private async request<T>(
    endpoint: string,
    options: RequestInit = {}
  ): Promise<T> {
    const url = `${this.baseUrl}${endpoint}`

    const config: RequestInit = {
      headers: {
        'Content-Type': 'application/json',
        ...options.headers,
      },
      ...options,
    }

    try {
      const response = await fetch(url, config)

      if (!response.ok) {
        let errorMessage = `HTTP ${response.status}: ${response.statusText}`
        
        try {
          const errorBody = await response.text()
          if (errorBody) {
            try {
              const parsed = JSON.parse(errorBody)
              errorMessage = parsed.message || parsed.error || errorMessage
            } catch {
              // If not JSON, use the text as is
              errorMessage = errorBody.substring(0, 200) // Limit error message length
            }
          }
        } catch {
          // If we can't read the response body, use the default message
        }

        const error: ApiError = {
          message: errorMessage,
          status: response.status,
        }
        throw error
      }

      return await response.json()
    } catch (error) {
      if (error instanceof TypeError) {
        throw {
          message: 'Network error - please check your connection',
          status: 0,
        } as ApiError
      }
      throw error
    }
  }

  async get<T>(endpoint: string): Promise<T> {
    return this.request<T>(endpoint, { method: 'GET' })
  }

  async post<T>(endpoint: string, data?: unknown): Promise<T> {
    return this.request<T>(endpoint, {
      method: 'POST',
      body: data ? JSON.stringify(data) : undefined,
    })
  }

  async put<T>(endpoint: string, data?: unknown): Promise<T> {
    return this.request<T>(endpoint, {
      method: 'PUT',
      body: data ? JSON.stringify(data) : undefined,
    })
  }

  async delete<T>(endpoint: string): Promise<T> {
    return this.request<T>(endpoint, { method: 'DELETE' })
  }
}