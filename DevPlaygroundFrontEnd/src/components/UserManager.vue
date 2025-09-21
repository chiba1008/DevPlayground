<template>
  <div class="user-manager">
    <h2>User Manager</h2>
    <!-- Create User -->
    <section class="create-user">
      <h3>Create User</h3>
      <form @submit.prevent="createUser">
        <div class="form-group">
          <label for="username">Username:</label>
          <input id="username" v-model="newUser.username" type="text" required />
        </div>
        <div class="form-group">
          <label for="email">Email:</label>
          <input id="email" v-model="newUser.email" type="email" required />
        </div>
        <button type="submit" :disabled="loading">Create User</button>
      </form>
    </section>

    <!-- User List -->
    <section class="user-list">
      <h3>All Users</h3>
      <button class="all-users-button" @click="loadUsers" :disabled="loading">Refresh Users</button>
      <div v-if="users.length > 0" class="users">
        <div v-for="user in users" :key="user.id" class="user-item">
          <strong>{{ user.username }}</strong> ({{ user.email }})
        </div>
      </div>
      <div v-else-if="!loading" class="no-users">No users found</div>
    </section>

    <!-- Search User -->
    <section class="search-user">
      <h3>Search User</h3>
      <div class="form-group">
        <label for="searchUsername">Search by Username:</label>
        <input id="searchUsername" v-model="searchUsername" type="text" />
        <button
          class="search-user-button"
          @click="searchUserByUsername"
          :disabled="loading || !searchUsername.trim()"
        >
          Search
        </button>
      </div>
      <div v-if="searchResult" class="search-result">
        <strong>Found:</strong> {{ searchResult.username }} ({{ searchResult.email }})
      </div>
      <div v-else-if="searchUsername && !loading" class="search-result">User not found</div>
    </section>

    <!-- Error Display -->
    <div v-if="error" class="error"><strong>Error:</strong> {{ error }}</div>

    <!-- Loading Indicator -->
    <div v-if="loading" class="loading">Loading...</div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { apiService, type User } from '../services/api'
import '../styles/components/UserManager.css'

const loading = ref(false)
const error = ref('')
const users = ref<User[]>([])
const searchResult = ref<User | null>(null)
const searchUsername = ref('')

const newUser = ref<User>({
  username: '',
  email: '',
})

const showError = (message: string) => {
  error.value = message
  setTimeout(() => {
    error.value = ''
  }, 5000)
}

const loadUsers = async () => {
  loading.value = true
  try {
    users.value = await apiService.getAllUsers()
  } catch (err) {
    showError(err instanceof Error ? err.message : 'Failed to load users')
  } finally {
    loading.value = false
  }
}

const createUser = async () => {
  loading.value = true
  try {
    const createdUser = await apiService.saveUser(newUser.value)
    users.value.push(createdUser)
    newUser.value = { username: '', email: '' }
  } catch (err) {
    showError(err instanceof Error ? err.message : 'Failed to create user')
  } finally {
    loading.value = false
  }
}

const searchUserByUsername = async () => {
  if (!searchUsername.value.trim()) {
    searchResult.value = null
    return
  }

  loading.value = true
  try {
    searchResult.value = await apiService.getUserByUsername(searchUsername.value)
  } catch (err) {
    showError(err instanceof Error ? err.message : 'Failed to search user')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadUsers()
})
</script>
<style scoped>
.all-users-button {
  margin-bottom: 10px;
}

.search-user-button {
  margin-left: 10px;
}
</style>
