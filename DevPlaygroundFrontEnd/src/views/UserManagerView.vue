<template>
  <div class="user-manager-view">
    <div class="page-header">
      <h1>User Management</h1>
      <p>Create, search, and manage users</p>
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