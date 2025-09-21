<template>
    <div class="login-manager">
        <section class="login-section">
            <h3>Login</h3>
            <form @submit.prevent="loginApi">
                <div class="form-group">
                    <label for="username">Username:</label>
                    <input id="username" v-model="newUser.username" type="text" required />
                </div>
                <div class="form-group">
                    <label for="email">Email:</label>
                    <input id="email" v-model="newUser.email" type="email" required />
                </div>
                <button type="submit" @click="loginApi" :disabled="loading">Login</button>
            </form>
            <!-- Error Display -->
            <div v-if="error" class="error"><strong>Error:</strong> {{ error }}</div>

            <!-- Loading Indicator -->
            <div v-if="loading" class="loading">Loading...</div>
        </section>
    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { helloApi, type User } from '@/services'
import '../styles/components/HelloManager.css'

const loading = ref(false)
const error = ref('')

const newUser = ref<User>({
    username: '',
    email: '',
})

const loginApi = async () => {
    loading.value = true
    try {
        await helloApi.getHello()
        // TODO: Implement actual login logic
        console.log('Login functionality not implemented yet')
    } catch (err) {
        showError(err instanceof Error ? err.message : 'Failed to connect to API')
    } finally {
        loading.value = false
    }
}

const showError = (message: string) => {
    error.value = message
    setTimeout(() => {
        error.value = ''
    }, 5000)
}
</script>

<style scoped>
.login-section {
    max-width: 400px;
    margin: 0 auto;
    padding: 20px;
    border: 1px solid #ccc;
    border-radius: 8px;
    background-color: #f9f9f9;
}
</style>