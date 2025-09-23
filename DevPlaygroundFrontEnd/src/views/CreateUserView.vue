<template>
  <div class="create-user">
    <div class="page-header">
      <h1>Create New User</h1>
      <p>新しいユーザーをシステムに追加します</p>
    </div>

    <div class="form-container">
      <form @submit.prevent="handleCreateUser" class="create-user-form">
        <div class="form-group">
          <label for="username">Username *</label>
          <input
            id="username"
            v-model="userForm.username"
            type="text"
            required
            placeholder="Enter username"
          />
        </div>

        <div class="form-group">
          <label for="email">Email *</label>
          <input
            id="email"
            v-model="userForm.email"
            type="email"
            required
            placeholder="Enter email address"
          />
        </div>

        <div class="form-group">
          <label for="password">Password *</label>
          <input
            id="password"
            v-model="userForm.password"
            type="password"
            required
            placeholder="Enter password"
            minlength="6"
          />
        </div>

        <div class="form-group">
          <label for="role">Role *</label>
          <select id="role" v-model="userForm.role" required>
            <option value="">Select a role</option>
            <option value="USER">User</option>
            <option value="ADMIN">Admin</option>
          </select>
        </div>

        <div class="form-actions">
          <button type="button" @click="goBack" class="btn btn-secondary">
            Cancel
          </button>
          <button type="submit" :disabled="loading" class="btn btn-primary">
            {{ loading ? 'Creating...' : 'Create User' }}
          </button>
        </div>
      </form>

      <!-- Error Display -->
      <div v-if="error" class="error-message">
        <strong>Error:</strong> {{ error }}
      </div>

      <!-- Success Display -->
      <div v-if="successMessage" class="success-message">
        <strong>Success:</strong> {{ successMessage }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { userApi, type CreateUserRequest } from '@/services/userApi'

const router = useRouter()

const loading = ref(false)
const error = ref('')
const successMessage = ref('')

const userForm = ref<CreateUserRequest>({
  username: '',
  email: '',
  password: '',
  role: ''
})

const handleCreateUser = async () => {
  try {
    loading.value = true
    error.value = ''
    successMessage.value = ''

    const createdUser = await userApi.createUser(userForm.value)
    
    successMessage.value = `User "${createdUser.username}" created successfully!`
    
    // Reset form
    userForm.value = {
      username: '',
      email: '',
      password: '',
      role: ''
    }

    // Redirect after a short delay
    setTimeout(() => {
      router.push({ name: 'users' })
    }, 2000)

  } catch (err: unknown) {
    console.error('Create user error:', err)
    if (typeof err === 'object' && err !== null && 'message' in err) {
      error.value = (err as Error).message
    } else {
      error.value = 'Failed to create user'
    }
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.back()
}
</script>

<style scoped>
/* global.cssのデザインシステムを使用 */
.create-user {
  max-width: 600px;
  margin: 0 auto;
  padding: var(--spacing-lg);
}

.form-container {
  background: var(--bg-white);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius);
  padding: var(--spacing-2xl);
  box-shadow: var(--shadow-md);
}

.create-user-form {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

@media (max-width: 768px) {
  .create-user {
    padding: var(--spacing-xs);
  }
  
  .form-container {
    padding: var(--spacing-lg);
  }
}
</style>