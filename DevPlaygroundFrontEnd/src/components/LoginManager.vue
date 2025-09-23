<template>
    <div class="login-manager">
        <div v-if="!isAuthenticated" class="login-section">
            <h3>Login</h3>
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
            <!-- Error Display -->
            <div v-if="error" class="error"><strong>Error:</strong> {{ error }}</div>

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
import type { LoginRequest } from '@/types/auth'
import '../styles/components/home.css'

const { isAuthenticated, user, loading, login, logout, checkAuthStatus } = useAuth()
const router = useRouter()

const error = ref('')

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
    setTimeout(() => {
        error.value = ''
    }, 5000)
}
</script>

<style scoped>
.login-section, .user-section {
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

/* エラー、ローディング、ボタンスタイルはglobal.cssで統一管理 */
</style>
