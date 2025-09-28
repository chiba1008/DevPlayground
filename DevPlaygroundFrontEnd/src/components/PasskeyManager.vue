<template>
    <div class="hello-manager">
        <h2>Passkey管理</h2>

        <!-- Passkey Registration -->
        <section class="api-test">
            <button @click="registerPasskey" :disabled="loading">Passkey登録</button>
        </section>

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
} from '@/types/auth'
import '@/styles/components/home.css'

const { user } = useAuth()
const userName = computed(() => user.value?.username || '')

const passkeyRegistrationStartResponse = ref<PasskeyRegistrationStartResponse | null>(null)
const loading = ref(false)

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
        // userNameを使ってchallenge等を受け取る
        passkeyRegistrationStartResponse.value = await authApi.registerPasskeyStart(userName.value)

        // 受け取ったchallenge等を使ってCredentialを作成
        // WebAuthn APIを使用してパスキーを生成
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
                    pubKeyCredParams: passkeyRegistrationStartResponse.value.pubKeyCredParams as PublicKeyCredentialParameters[],
                    timeout: 60000,
                    attestation: 'direct',
                },
            })
            .then(async (credential) => {
                if (!credential) {
                    alert('Credential creation was not completed.')
                    return
                }

                const publicKeyCredential = credential as PublicKeyCredential
                const attestationResponse = publicKeyCredential.response as AuthenticatorAttestationResponse
                const clientDataJSON = attestationResponse.clientDataJSON
                const attestationObject = attestationResponse.attestationObject

                const registrationResponse = {
                    id: publicKeyCredential.id,
                    rawId: base64UrlEncode(publicKeyCredential.rawId),
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
                    alert('Passkey registration successful!')
                } catch (err) {
                    alert(err instanceof Error ? err.message : 'Failed to register passkey on server')
                }
            })
            .catch((err) => {
                alert(err instanceof Error ? err.message : 'Failed to create credential')
            })
    } catch (err) {
        alert(err instanceof Error ? err.message : 'Failed to connect to API')
    } finally {
        loading.value = false
    }
}
</script>
