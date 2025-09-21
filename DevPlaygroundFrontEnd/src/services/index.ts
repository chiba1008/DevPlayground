// Export types
export type { User, HelloResponse, ApiError, LoginRequest, LoginResponse } from '@/types/api'

// Export services
export { HttpClient } from './http'
export { UserApi } from './userApi'
export { HelloApi } from './helloApi'

// Create and export service instances
import { HttpClient } from './http'
import { UserApi } from './userApi'
import { HelloApi } from './helloApi'

const httpClient = new HttpClient()

export const userApi = new UserApi(httpClient)
export const helloApi = new HelloApi(httpClient)
export { httpClient }