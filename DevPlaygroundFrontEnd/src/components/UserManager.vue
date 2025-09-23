<template>
    <div class="user-manager">
        <h2>User Manager</h2>
        <!-- User List -->
        <section class="user-list">
            <div class="section-header">
                <h3>All Users</h3>
                <button class="refresh-button" @click="loadUsers" :disabled="loading">
                    Refresh Users
                </button>
            </div>

            <div v-if="users.length > 0" class="users-table-container">
                <table class="users-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Username</th>
                            <th>Email</th>
                            <th>Role</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="user in users" :key="user.id">
                            <td>{{ user.id }}</td>
                            <td>
                                <strong>{{ user.username }}</strong>
                            </td>
                            <td>{{ user.email }}</td>
                            <td>
                                <span class="role-badge" :class="`role-${user.role.toLowerCase()}`">
                                    {{ user.role }}
                                </span>
                            </td>
                            <td>
                                <span
                                    class="status-badge"
                                    :class="user.enabled ? 'status-active' : 'status-inactive'"
                                >
                                    {{ user.enabled ? 'Active' : 'Inactive' }}
                                </span>
                            </td>
                            <td>
                                <div class="action-buttons">
                                    <button
                                        class="btn btn-danger btn-sm"
                                        @click="deleteUser(user.id)"
                                        :disabled="loading"
                                    >
                                        Delete
                                    </button>
                                </div>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <div v-else-if="!loading" class="no-users">
                <p>No users found</p>
                <RouterLink to="/users/create" class="btn btn-primary">
                    Create First User
                </RouterLink>
            </div>
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
import { userApi, type UserResponse } from '@/services/userApi'
import '../styles/components/user-manager.css'

const loading = ref(false)
const error = ref('')
const users = ref<UserResponse[]>([])
const searchResult = ref<UserResponse | null>(null)
const searchUsername = ref('')

const showError = (message: string) => {
    error.value = message
    setTimeout(() => {
        error.value = ''
    }, 5000)
}

const loadUsers = async () => {
    loading.value = true
    try {
        users.value = await userApi.getAllUsers()
    } catch (err) {
        showError(err instanceof Error ? err.message : 'Failed to load users')
    } finally {
        loading.value = false
    }
}

const deleteUser = async (userId: number) => {
    if (!confirm('Are you sure you want to delete this user?')) {
        return
    }

    loading.value = true
    try {
        await userApi.deleteUser(userId)
        // Refresh the user list after deletion
        await loadUsers()
    } catch (err) {
        showError(err instanceof Error ? err.message : 'Failed to delete user')
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
        searchResult.value = await userApi.getUserByUsername(searchUsername.value)
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
