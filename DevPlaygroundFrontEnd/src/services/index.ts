// Export types
export type { User, HelloResponse, ApiError, LoginRequest, LoginResponse } from '@/types/api'

// Export services
export { HttpClient } from './http'
export { HelloApi } from './helloApi'

// Export API instances
export { userApi } from './userApi'
export type { CreateUserRequest, UserResponse } from './userApi'

// Create and export service instances
import { HttpClient } from './http'
import { HelloApi } from './helloApi'

const httpClient = new HttpClient()

export const helloApi = new HelloApi(httpClient)
export { httpClient }