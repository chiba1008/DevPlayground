<template>
  <div class="user-manager">
    <h2>User Manager</h2>
    
    <!-- Hello API Test -->
    <section class="api-test">
      <h3>API Connection Test</h3>
      <button @click="testHelloApi" :disabled="loading">Test Hello API</button>
      <div v-if="helloResponse" class="response">
        <strong>Response:</strong> {{ helloResponse.message }} ({{ helloResponse.timestamp }})
      </div>
    </section>

    <!-- Create User -->
    <section class="create-user">
      <h3>Create User</h3>
      <form @submit.prevent="createUser">
        <div class="form-group">
          <label for="username">Username:</label>
          <input
            id="username"
            v-model="newUser.username"
            type="text"
            required
          />
        </div>
        <div class="form-group">
          <label for="email">Email:</label>
          <input
            id="email"
            v-model="newUser.email"
            type="email"
            required
          />
        </div>
        <button type="submit" :disabled="loading">Create User</button>
      </form>
    </section>

    <!-- User List -->
    <section class="user-list">
      <h3>All Users</h3>
      <button @click="loadUsers" :disabled="loading">Refresh Users</button>
      <div v-if="users.length > 0" class="users">
        <div v-for="user in users" :key="user.id" class="user-item">
          <strong>{{ user.username }}</strong> ({{ user.email }})
        </div>
      </div>
      <div v-else-if="!loading" class="no-users">
        No users found
      </div>
    </section>

    <!-- Search User -->
    <section class="search-user">
      <h3>Search User</h3>
      <div class="form-group">
        <label for="searchUsername">Search by Username:</label>
        <input
          id="searchUsername"
          v-model="searchUsername"
          type="text"
          @input="searchUserByUsername"
        />
      </div>
      <div v-if="searchResult" class="search-result">
        <strong>Found:</strong> {{ searchResult.username }} ({{ searchResult.email }})
      </div>
      <div v-else-if="searchUsername && !loading" class="search-result">
        User not found
      </div>
    </section>

    <!-- Error Display -->
    <div v-if="error" class="error">
      <strong>Error:</strong> {{ error }}
    </div>

    <!-- Loading Indicator -->
    <div v-if="loading" class="loading">
      Loading...
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { apiService, type User, type HelloResponse } from '../services/api'

const loading = ref(false)
const error = ref('')
const users = ref<User[]>([])
const helloResponse = ref<HelloResponse | null>(null)
const searchResult = ref<User | null>(null)
const searchUsername = ref('')

const newUser = ref<User>({
  username: '',
  email: ''
})

const showError = (message: string) => {
  error.value = message
  setTimeout(() => {
    error.value = ''
  }, 5000)
}

const testHelloApi = async () => {
  loading.value = true
  try {
    helloResponse.value = await apiService.getHello()
  } catch (err) {
    showError(err instanceof Error ? err.message : 'Failed to connect to API')
  } finally {
    loading.value = false
  }
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
.user-manager {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

section {
  margin-bottom: 30px;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

h2, h3 {
  color: #333;
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

input {
  width: 100%;
  max-width: 300px;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

button {
  background-color: #007bff;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-right: 10px;
}

button:hover:not(:disabled) {
  background-color: #0056b3;
}

button:disabled {
  background-color: #6c757d;
  cursor: not-allowed;
}

.user-item {
  padding: 10px;
  border: 1px solid #eee;
  border-radius: 4px;
  margin-bottom: 10px;
  background-color: #f8f9fa;
}

.response, .search-result {
  margin-top: 10px;
  padding: 10px;
  background-color: #e9ecef;
  border-radius: 4px;
}

.error {
  color: #dc3545;
  background-color: #f8d7da;
  border: 1px solid #f5c6cb;
  padding: 10px;
  border-radius: 4px;
  margin-top: 10px;
}

.loading {
  text-align: center;
  padding: 20px;
  font-style: italic;
  color: #6c757d;
}

.no-users {
  text-align: center;
  color: #6c757d;
  font-style: italic;
}
</style>