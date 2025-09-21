<template>
    <div class="admin">
        <div class="admin-section">
            <h1>Admin Dashboard</h1>
            <p>This page is only accessible to users with ADMIN role.</p>
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
// import { useAuth } from '@/composables/useAuth'

// const { user } = useAuth()
</script>

<style scoped>
.admin {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
}

.admin-section {
    text-align: center;
    margin-bottom: 40px;
    padding: 30px;
    background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%);
    color: white;
    border-radius: 12px;
}

.admin-section h1 {
    font-size: 2.5rem;
    margin-bottom: 10px;
    font-weight: 600;
}

.admin-section p {
    font-size: 1.2rem;
    opacity: 0.9;
}

.admin-functions {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 20px;
}

.function-card {
    background: white;
    border: 1px solid #ddd;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.function-card h3 {
    color: #2c3e50;
    margin-bottom: 10px;
}

.function-card p {
    color: #666;
    margin-bottom: 15px;
}

.btn {
    padding: 10px 20px;
    border: none;
    border-radius: 4px;
    text-decoration: none;
    display: inline-block;
    cursor: pointer;
    font-weight: 500;
}

.btn-primary {
    background-color: #3498db;
    color: white;
}

.btn-primary:hover {
    background-color: #2980b9;
}

.btn-secondary {
    background-color: #95a5a6;
    color: white;
}

.btn-secondary:disabled {
    opacity: 0.6;
    cursor: not-allowed;
}
</style>
