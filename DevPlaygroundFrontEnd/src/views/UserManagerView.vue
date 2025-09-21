<template>
  <div class="user-manager-view">
    <div class="page-header">
      <h1>User Management</h1>
      <p>Create, search, and manage users</p>
      <div class="header-actions">
        <RouterLink to="/users/create" class="btn btn-primary">
          + Create New User
        </RouterLink>
      </div>
    </div>
    <UserManager />
  </div>
</template>

<script setup lang="ts">
import UserManager from '../components/UserManager.vue'
</script>

<style scoped>
.user-manager-view {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 10px;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #007bff;
  position: relative;
}

.header-actions {
  position: absolute;
  top: 20px;
  right: 20px;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  text-decoration: none;
  display: inline-block;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.btn-primary {
  background-color: #27ae60;
  color: white;
}

.btn-primary:hover {
  background-color: #2ecc71;
}

.page-header h1 {
  color: #2c3e50;
  font-size: 2rem;
  margin-bottom: 8px;
}

.page-header p {
  color: #6c757d;
  font-size: 1.1rem;
  margin: 0;
}
</style>