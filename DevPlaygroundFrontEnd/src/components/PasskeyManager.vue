<template>
    <div class="passkey-manager">
        <h2>Passkey登録</h2>
        <section class="passkey-registration">
            <button @click="registerPasskey" :disabled="loading">Passkey登録</button>
        </section>

        <!-- Error Display -->
        <div v-if="error" class="error"><strong>Error:</strong> {{ error }}</div>

        <!-- Loading Indicator -->
        <div v-if="loading" class="loading">Loading...</div>
    </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { authApi } from '@/services/authApi'
import { useAuth } from '@/composables/useAuth'
import type {
    PasskeyRegistrationStartRequest,
    PasskeyRegistrationStartResponse,
} from '@/types/auth'
import '@/styles/components/home.css'

const { user } = useAuth()
const userName = computed(() => user.value?.username || '')

const passkeyRegistrationStartRequest = ref<PasskeyRegistrationStartRequest>({
    username: '',
})

const passkeyRegistrationStartResponse = ref<PasskeyRegistrationStartResponse | null>(null)
const loading = ref(false)
const error = ref('')

const showError = (message: string) => {
    error.value = message
    setTimeout(() => {
        error.value = ''
    }, 5000)
}

const registerPasskey = async () => {
    loading.value = true
    try {
        passkeyRegistrationStartRequest.value.username = userName.value
        // challenge等を受け取る
        passkeyRegistrationStartResponse.value = await authApi.registerPasskeyStart(
            passkeyRegistrationStartRequest.value,
        )

        navigator.credentials
            .create({
                publicKey: {
                    challenge: Uint8Array.from(
                        atob(passkeyRegistrationStartResponse.value.challenge),
                        (c) => c.charCodeAt(0),
                    ),
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
                    rawId: btoa(String.fromCharCode(...new Uint8Array(credential.rawId))),
                    type: credential.type,
                    clientDataJSON: btoa(String.fromCharCode(...new Uint8Array(clientDataJSON))),
                    attestationObject: btoa(
                        String.fromCharCode(...new Uint8Array(attestationObject)),
                    ),
                }

                // サーバーに登録情報を送信
                try {
                    await authApi.registerPasskeyFinish({
                        username: userName.value,
                        registrationResponse,
                    })
                    alert('Passkey registration successful!')
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
</script>
