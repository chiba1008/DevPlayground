<template>
    <div class="login-manager">
        <div v-if="!isAuthenticated" class="login-section">
            <h3>Login</h3>
            
            <!-- Traditional Login -->
            <form @submit.prevent="performLogin">
                <div class="form-group">
                    <label for="username">Username:</label>
                    <input id="username" v-model="loginRequest.username" type="text" required />
                </div>
                <div class="form-group">
                    <label for="password">Password:</label>
                    <input id="password" v-model="loginRequest.password" type="password" required />
                </div>
                <button type="submit" :disabled="loading">Login</button>
            </form>
            <div class="passkey-login-section">
                <h4>Passkeyでログイン</h4>
                <div class="form-group">
                    <label for="passkey-username">Username:</label>
                    <input 
                        id="passkey-username" 
                        v-model="passkeyUsername" 
                        type="text" 
                        placeholder="ユーザー名を入力"
                        :disabled="loading"
                    />
                </div>
                <button 
                    @click="loginWithPasskey" 
                    :disabled="loading || !passkeyUsername"
                    class="passkey-button"
                >
                    🔐 Passkeyでログイン
                </button>
            </div>

            <!-- Error Display -->
            <div v-if="error" class="error"><strong>Error:</strong> {{ error }}</div>

            <!-- Success Display -->
            <div v-if="success" class="success"><strong>Success:</strong> {{ success }}</div>

            <!-- Loading Indicator -->
            <div v-if="loading" class="loading">Loading...</div>
        </div>

        <div v-else class="user-section">
            <h3>Welcome, {{ user?.username }}!</h3>
            <p>Authorities: {{ user?.authorities }}</p>
            <button @click="performLogout" :disabled="loading">Logout</button>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuth } from '@/composables/useAuth'
import { authApi } from '@/services/authApi'
import type { LoginRequest, PasskeyLoginStartResponse } from '@/types/auth'
import '../styles/components/home.css'

const { isAuthenticated, user, loading, login, logout, checkAuthStatus } = useAuth()
const router = useRouter()

const error = ref('')
const success = ref('')
const passkeyUsername = ref('')
const passkeyLoginStartResponse = ref<PasskeyLoginStartResponse | null>(null)

const loginRequest = ref<LoginRequest>({
    username: '',
    password: '',
})

onMounted(() => {
    checkAuthStatus()
})

const performLogin = async () => {
    const result = await login(loginRequest.value)

    if (!result.success) {
        showError(result.error || 'Login failed')
    } else {
        loginRequest.value.username = ''
        loginRequest.value.password = ''
        router.push({ name: 'home' })
    }
}

const performLogout = async () => {
    await logout()
    router.push({ name: 'login' })
}

const showError = (message: string) => {
    error.value = message
    success.value = ''
    setTimeout(() => {
        error.value = ''
    }, 5000)
}

const showSuccess = (message: string) => {
    success.value = message
    error.value = ''
    setTimeout(() => {
        success.value = ''
    }, 5000)
}

const base64UrlDecode = (str: string): Uint8Array => {
    const base64 = str.replace(/-/g, '+').replace(/_/g, '/')
    const padding = '='.repeat((4 - (base64.length % 4)) % 4)
    const decoded = atob(base64 + padding)
    return Uint8Array.from(decoded, c => c.charCodeAt(0))
}

const base64UrlEncode = (buffer: ArrayBuffer): string => {
    const bytes = new Uint8Array(buffer)
    const base64 = btoa(String.fromCharCode(...bytes))
    return base64.replace(/\+/g, '-').replace(/\//g, '_').replace(/=/g, '')
}

const loginWithPasskey = async () => {
    try {
        // ログイン開始 - challengeとallowCredentialsを取得
        passkeyLoginStartResponse.value = await authApi.loginPasskeyStart(passkeyUsername.value)

        // WebAuthn認証の実行
        const credential = await navigator.credentials.get({
            publicKey: {
                challenge: base64UrlDecode(passkeyLoginStartResponse.value.challenge),
                allowCredentials: passkeyLoginStartResponse.value.allowCredentials.map(cred => ({
                    type: 'public-key' as const,
                    id: base64UrlDecode(cred.id),
                })),
                timeout: 60000,
            },
        })

        if (!credential) {
            showError('Authentication was not completed.')
            return
        }

        const assertionResponse = credential.response as AuthenticatorAssertionResponse
        const clientDataJSON = assertionResponse.clientDataJSON
        const authenticatorData = assertionResponse.authenticatorData
        const signature = assertionResponse.signature

        const authenticationResponse = {
            id: credential.id,
            rawId: base64UrlEncode(credential.rawId),
            type: credential.type,
            clientDataJSON: base64UrlEncode(clientDataJSON),
            authenticatorData: base64UrlEncode(authenticatorData),
            signature: base64UrlEncode(signature),
        }

        // サーバーでログインを完了
        const loginResult = await authApi.loginPasskeyFinish({
            username: passkeyUsername.value,
            authenticationResponse,
        })

        if (loginResult.success) {
            showSuccess('Passkey login successful!')
            // 認証状態を更新
            await checkAuthStatus()
            // ホームページにリダイレクト
            router.push({ name: 'home' })
        } else {
            showError(loginResult.message || 'Login failed')
        }
    } catch (err) {
        showError(err instanceof Error ? err.message : 'Failed to login with passkey')
    }
}
</script>

<style scoped>
.login-section,
.user-section {
    max-width: 400px;
    margin: 0 auto;
    padding: 20px;
    border: 1px solid #ccc;
    border-radius: 8px;
    background-color: #f9f9f9;
}

.user-section {
    text-align: center;
}

.user-section h3 {
    color: #2c3e50;
    margin-bottom: 10px;
}

.user-section p {
    color: #666;
    margin-bottom: 15px;
}

.login-divider {
    text-align: center;
    margin: 20px 0;
    position: relative;
}

.login-divider::before {
    content: '';
    position: absolute;
    top: 50%;
    left: 0;
    right: 0;
    height: 1px;
    background-color: #ddd;
}

.login-divider span {
    background-color: #f9f9f9;
    padding: 0 15px;
    color: #666;
    font-size: 14px;
}

.passkey-login-section {
    margin-top: 20px;
}

.passkey-login-section h4 {
    color: #2c3e50;
    margin-bottom: 15px;
    font-size: 16px;
}

.passkey-button {
    width: 100%;
    background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%);
    color: white;
    border: none;
    padding: 12px;
    border-radius: 6px;
    font-size: 16px;
    cursor: pointer;
    transition: all 0.3s ease;
}

.passkey-button:hover:not(:disabled) {
    background: linear-gradient(135deg, #45a049 0%, #3d8b40 100%);
    transform: translateY(-1px);
    box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}

.passkey-button:disabled {
    background: #ccc;
    cursor: not-allowed;
    transform: none;
    box-shadow: none;
}

/* エラー、ローディング、ボタンスタイルはglobal.cssで統一管理 */
</style>
