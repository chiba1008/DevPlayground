import { HttpClient } from './http'
import type { LoginRequest, LoginResponse, PasskeyLoginRequest, PasskeyLoginResponse, PasskeyRegistrationStartRequest, PasskeyRegistrationStartResponse, UserInfo } from '@/types/auth'

class AuthApi {
    private httpClient: HttpClient

    constructor() {
        this.httpClient = new HttpClient()
    }

    async login(credentials: LoginRequest): Promise<LoginResponse> {
        return this.httpClient.post<LoginResponse>('/login', credentials)
    }

    async passkeyLogin(credentials: PasskeyLoginRequest): Promise<PasskeyLoginResponse> {
        return this.httpClient.post<PasskeyLoginResponse>('/passkey-login', credentials)
    }

    async registerPasskeyStart(
        passkeyRegistrationStartRequest: PasskeyRegistrationStartRequest,
    ): Promise<PasskeyRegistrationStartResponse> {
        return this.httpClient.post<PasskeyRegistrationStartResponse>(
            '/passkey/register-start',
            passkeyRegistrationStartRequest,
        )
    }

    async logout(): Promise<void> {
        await this.httpClient.post('/logout')
    }

    async getCurrentUser(): Promise<UserInfo> {
        return this.httpClient.get<UserInfo>('/current-user')
    }
}

export const authApi = new AuthApi()