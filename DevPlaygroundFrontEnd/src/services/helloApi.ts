import type { HelloResponse } from '@/types/api'
import { HttpClient } from './http'

export class HelloApi {
  private http: HttpClient

  constructor(httpClient: HttpClient) {
    this.http = httpClient
  }

  async getHello(): Promise<HelloResponse> {
    return this.http.get<HelloResponse>('/hello-world')
  }
}