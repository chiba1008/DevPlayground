import { ref, computed } from 'vue'
import { authApi } from '@/services/authApi'
import type { LoginRequest, AuthState } from '@/types/auth'

const state = ref<AuthState>({
    isAuthenticated: false,
    user: null,
    loading: false,
})

export function useAuth() {
    const isAuthenticated = computed(() => state.value.isAuthenticated)
    const user = computed(() => state.value.user)
    const loading = computed(() => state.value.loading)

    const login = async (credentials: LoginRequest) => {
        try {
            state.value.loading = true
            const response = await authApi.login(credentials)

            if (response.success) {
                const userInfo = await authApi.getCurrentUser()
                state.value.isAuthenticated = true
                state.value.user = userInfo
                return { success: true }
            } else {
                return { success: false, error: 'Login failed' }
            }
        } catch (error: unknown) {
            console.error('Login error:', error)
            const errorMessage = error instanceof Error ? error.message : 'Login failed'
            return {
                success: false,
                error: errorMessage,
            }
        } finally {
            state.value.loading = false
        }
    }

    const logout = async () => {
        try {
            state.value.loading = true
            await authApi.logout()

            // Clear local state immediately
            state.value.isAuthenticated = false
            state.value.user = null

            // Force page reload to clear any cached session data
            setTimeout(() => {
                window.location.href = '/login'
            }, 100)
        } catch (logoutError) {
            console.error('Logout error:', logoutError)
            // Still clear local state and redirect even if logout API fails
            state.value.isAuthenticated = false
            state.value.user = null
            window.location.href = '/login'
        } finally {
            state.value.loading = false
        }
    }

    const checkAuthStatus = async () => {
        try {
            state.value.loading = true
            const userInfo = await authApi.getCurrentUser()
            state.value.isAuthenticated = true
            state.value.user = userInfo
        } catch {
            state.value.isAuthenticated = false
            state.value.user = null
        } finally {
            state.value.loading = false
        }
    }

    return {
        // State
        isAuthenticated,
        user,
        loading,

        // Actions
        login,
        logout,
        checkAuthStatus,
    }
}
