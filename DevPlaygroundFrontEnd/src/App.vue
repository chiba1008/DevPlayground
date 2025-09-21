<script setup lang="ts">
import { onMounted } from 'vue'
import { useAuth } from '@/composables/useAuth'
import './styles/global.css'

const { isAuthenticated, user, logout, checkAuthStatus, hasRole } = useAuth()

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
            <RouterLink to="/todos" class="nav-link">Todo</RouterLink>
            <RouterLink to="/kanban" class="nav-link">カンバン</RouterLink>
            <RouterLink v-if="hasRole('ADMIN')" to="/admin" class="nav-link">Admin</RouterLink>
            <RouterLink v-if="hasRole('ADMIN')" to="/users" class="nav-link">Users</RouterLink>
            <div class="user-info">
              <span class="welcome-text">Welcome, {{ user?.username }} ({{ user?.roles?.join(', ') }})</span>
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

