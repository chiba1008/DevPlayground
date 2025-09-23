<template>
  <div class="user-manager-view">
    <div class="page-header">
      <h1>User Management</h1>
      <p>ユーザーの作成、検索、管理を効率的に行えます</p>
      <div class="header-actions">
        <RouterLink to="/users/create" class="btn btn-ghost">
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
