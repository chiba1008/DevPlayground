<script setup lang="ts">
import { onMounted } from 'vue'
import { useAuth } from '@/composables/useAuth'
import './styles/global.css'

const { isAuthenticated, user, logout, checkAuthStatus } = useAuth()

onMounted(() => {
  checkAuthStatus()
})

const handleLogout = async () => {
  await logout()
  // logout() handles the redirect now
}
</script>

<template>
  <div id="app">
    <header>
      <div class="header-content">
        <h1>DevPlayground</h1>
        <nav class="main-nav">
          <template v-if="isAuthenticated">
            <RouterLink to="/" class="nav-link">Home</RouterLink>
            <RouterLink to="/users" class="nav-link">User Manager</RouterLink>
            <div class="user-info">
              <span class="welcome-text">Welcome, {{ user?.username }}</span>
              <button @click="handleLogout" class="logout-btn">Logout</button>
            </div>
          </template>
          <template v-else>
            <RouterLink to="/login" class="nav-link">Login</RouterLink>
          </template>
        </nav>
      </div>
    </header>
    <main>
      <RouterView />
    </main>
  </div>
</template>

