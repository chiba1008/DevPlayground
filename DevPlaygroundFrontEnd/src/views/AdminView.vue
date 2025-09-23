<template>
    <div class="admin">
        <div class="page-header">
            <h1>Admin Dashboard</h1>
            <p>管理者専用ダッシュボード - システムの管理と設定</p>
        </div>

        <div class="admin-functions">
            <div class="function-card">
                <h3>User Management</h3>
                <p>Manage system users and their roles.</p>
                <RouterLink to="/users" class="btn btn-primary">Go to User Manager</RouterLink>
            </div>

            <div class="function-card">
                <h3>System Settings</h3>
                <p>Configure system-wide settings.</p>
                <button class="btn btn-secondary" disabled>Coming Soon</button>
            </div>

            <div class="function-card">
                <h3>Reports</h3>
                <p>View system reports and analytics.</p>
                <button class="btn btn-secondary" disabled>Coming Soon</button>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import '../styles/components/admin.css'

</script>
