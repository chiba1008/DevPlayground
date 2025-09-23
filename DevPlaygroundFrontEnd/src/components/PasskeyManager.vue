<template>
    <div class="passkey-manager">
        <h2>Passkey管理</h2>
        
        <section class="passkey-registration">
            <h3>Passkey登録</h3>
            <button @click="registerPasskey" :disabled="loading">Passkey登録</button>
        </section>

        <section class="passkey-login">
            <h3>Passkeyログイン</h3>
            <div class="login-form">
                <input 
                    v-model="loginUsername" 
                    type="text" 
                    placeholder="ユーザー名を入力" 
                    :disabled="loading"
                />
                <button @click="loginWithPasskey" :disabled="loading || !loginUsername">Passkeyでログイン</button>
            </div>
        </section>

        <!-- Error Display -->
        <div v-if="error" class="error"><strong>Error:</strong> {{ error }}</div>

        <!-- Success Display -->
        <div v-if="success" class="success"><strong>Success:</strong> {{ success }}</div>

        <!-- Loading Indicator -->
        <div v-if="loading" class="loading">Loading...</div>
    </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { authApi } from '@/services/authApi'
import { useAuth } from '@/composables/useAuth'
import type {
    PasskeyRegistrationStartResponse,
    PasskeyLoginStartResponse,
} from '@/types/auth'
import '@/styles/components/home.css'

const { user } = useAuth()
const userName = computed(() => user.value?.username || '')

const passkeyRegistrationStartResponse = ref<PasskeyRegistrationStartResponse | null>(null)
const passkeyLoginStartResponse = ref<PasskeyLoginStartResponse | null>(null)
const loginUsername = ref('')
const loading = ref(false)
const error = ref('')
const success = ref('')

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

const registerPasskey = async () => {
    loading.value = true
    try {
        // challenge等を受け取る
        passkeyRegistrationStartResponse.value = await authApi.registerPasskeyStart(userName.value)

        navigator.credentials
            .create({
                publicKey: {
                    challenge: base64UrlDecode(passkeyRegistrationStartResponse.value.challenge),
                    rp: {
                        id: passkeyRegistrationStartResponse.value.rp.id,
                        name: passkeyRegistrationStartResponse.value.rp.name,
                    },
                    user: {
                        id: Uint8Array.from(userName.value, (c) => c.charCodeAt(0)),
                        name: userName.value,
                        displayName: userName.value,
                    },
                    pubKeyCredParams: passkeyRegistrationStartResponse.value.pubKeyCredParams,
                    timeout: 60000,
                    attestation: 'direct',
                },
            })
            .then(async (credential) => {
                if (!credential) {
                    showError('Credential creation was not completed.')
                    return
                }

                const attestationResponse = credential.response as AuthenticatorAttestationResponse
                const clientDataJSON = attestationResponse.clientDataJSON
                const attestationObject = attestationResponse.attestationObject

                const registrationResponse = {
                    id: credential.id,
                    rawId: base64UrlEncode(credential.rawId),
                    type: credential.type,
                    clientDataJSON: base64UrlEncode(clientDataJSON),
                    attestationObject: base64UrlEncode(attestationObject),
                }

                // サーバーに登録情報を送信
                try {
                    await authApi.registerPasskeyFinish({
                        username: userName.value,
                        registrationResponse,
                    })
                    showSuccess('Passkey registration successful!')
                } catch (err) {
                    showError(
                        err instanceof Error ? err.message : 'Failed to register passkey on server',
                    )
                }
            })
            .catch((err) => {
                showError(err instanceof Error ? err.message : 'Failed to create credentials')
            })
    } catch (err) {
        showError(err instanceof Error ? err.message : 'Failed to connect to API')
    } finally {
        loading.value = false
    }
}

const loginWithPasskey = async () => {
    loading.value = true
    try {
        // ログイン開始 - challengeとallowCredentialsを取得
        passkeyLoginStartResponse.value = await authApi.loginPasskeyStart(loginUsername.value)

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
            username: loginUsername.value,
            authenticationResponse,
        })

        if (loginResult.success) {
            showSuccess('Passkey login successful!')
            // ページをリロードして認証状態を更新
            window.location.reload()
        } else {
            showError(loginResult.message || 'Login failed')
        }
    } catch (err) {
        showError(err instanceof Error ? err.message : 'Failed to login with passkey')
    } finally {
        loading.value = false
    }
}
</script>
