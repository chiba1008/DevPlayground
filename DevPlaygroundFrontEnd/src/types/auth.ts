export interface LoginRequest {
    username: string
    password: string
}

export interface LoginResponse {
    success: boolean
    username: string
}

export interface PasskeyLoginRequest {
    username: string
}

export interface PasskeyLoginResponse {
    success: boolean
    username: string
}

export interface PasskeyRegistrationStartRequest {
    username: string
}

export interface PasskeyRegistrationStartResponse {
    challenge: string
    rp: {
        id: string
        name: string
    }
    user: {
        id: string
        name: string
        displayName: string
    }
    pubKeyCredParams: Array<{
        type: string
        alg: number
    }>
}

export interface UserInfo {
    username: string
    authorities: string
    roles: string[]
}

export interface AuthState {
    isAuthenticated: boolean
    user: UserInfo | null
    loading: boolean
}
