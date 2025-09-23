import { HttpClient } from './http'
import type { LoginRequest, LoginResponse, PasskeyLoginRequest, PasskeyLoginResponse, PasskeyRegistrationStartResponse, PasskeyRegistrationFinishRequest, PasskeyRegistrationFinishResponse, PasskeyLoginStartResponse, PasskeyLoginFinishRequest, PasskeyLoginFinishResponse, UserInfo } from '@/types/auth'

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

    async registerPasskeyStart(username: string): Promise<PasskeyRegistrationStartResponse> {
        return this.httpClient.post<PasskeyRegistrationStartResponse>(
            `/auth/passkey/register/start?username=${encodeURIComponent(username)}`,
            {},
        )
    }

    async registerPasskeyFinish(request: PasskeyRegistrationFinishRequest): Promise<PasskeyRegistrationFinishResponse> {
        return this.httpClient.post<PasskeyRegistrationFinishResponse>(
            '/auth/passkey/register/finish',
            request,
        )
    }

    async loginPasskeyStart(username: string): Promise<PasskeyLoginStartResponse> {
        return this.httpClient.post<PasskeyLoginStartResponse>(
            `/auth/passkey/login/start?username=${encodeURIComponent(username)}`,
            {},
        )
    }

    async loginPasskeyFinish(request: PasskeyLoginFinishRequest): Promise<PasskeyLoginFinishResponse> {
        return this.httpClient.post<PasskeyLoginFinishResponse>(
            '/auth/passkey/login/finish',
            request,
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