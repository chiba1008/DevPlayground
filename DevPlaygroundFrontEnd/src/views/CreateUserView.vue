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
.create-user {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}

.create-user-section {
  text-align: center;
  margin-bottom: 40px;
  padding: 30px;
  background: linear-gradient(135deg, #27ae60 0%, #2ecc71 100%);
  color: white;
  border-radius: 12px;
}

.create-user-section h1 {
  font-size: 2.5rem;
  margin-bottom: 10px;
  font-weight: 600;
}

.create-user-section p {
  font-size: 1.2rem;
  opacity: 0.9;
}

.form-container {
  background: white;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.create-user-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.form-group label {
  font-weight: 500;
  color: #2c3e50;
}

.form-group input,
.form-group select {
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #3498db;
  box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.2);
}

.form-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  margin-top: 20px;
}

.btn {
  padding: 12px 24px;
  border: none;
  border-radius: 4px;
  font-weight: 500;
  cursor: pointer;
  text-decoration: none;
  display: inline-block;
  transition: background-color 0.3s ease;
}

.btn-primary {
  background-color: #3498db;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background-color: #2980b9;
}

.btn-primary:disabled {
  background-color: #bdc3c7;
  cursor: not-allowed;
}

.btn-secondary {
  background-color: #95a5a6;
  color: white;
}

.btn-secondary:hover {
  background-color: #7f8c8d;
}

.error-message {
  margin-top: 20px;
  padding: 15px;
  background-color: #fdf2f2;
  border: 1px solid #fecaca;
  border-radius: 4px;
  color: #e74c3c;
}

.success-message {
  margin-top: 20px;
  padding: 15px;
  background-color: #f0fdf4;
  border: 1px solid #bbf7d0;
  border-radius: 4px;
  color: #16a085;
}

@media (max-width: 768px) {
  .create-user {
    padding: 10px;
  }
  
  .form-container {
    padding: 20px;
  }
  
  .form-actions {
    flex-direction: column;
  }
}
</style>