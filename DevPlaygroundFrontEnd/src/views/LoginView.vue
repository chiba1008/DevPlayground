<template>
    <div class="login">
        <LoginManager />
    </div>
</template>

<script setup lang="ts">
import LoginManager from '../components/LoginManager.vue'
</script>

<style scoped>
.login {
    max-width: 800px;
    margin: 0 auto;
    padding: 20px;
}

.login-section {
    text-align: center;
    margin-bottom: 40px;
    padding: 30px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    border-radius: 12px;
}

.login-section h1 {
    font-size: 2.5rem;
    margin-bottom: 10px;
    font-weight: 600;
}

.-section p {
    font-size: 1.2rem;
    opacity: 0.9;
}
</style>
