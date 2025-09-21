<template>
  <div class="home">
    <div class="welcome-section">
      <h1>Welcome to DevPlayground</h1>
    </div>
    <HelloManager />
  </div>
</template>

<script setup lang="ts">
import HelloManager from '../components/HelloManager.vue'
</script>

<style scoped>
.home {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.welcome-section {
  text-align: center;
  margin-bottom: 40px;
  padding: 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px;
}

.welcome-section h1 {
  font-size: 2.5rem;
  margin-bottom: 10px;
  font-weight: 600;
}

.welcome-section p {
  font-size: 1.2rem;
  opacity: 0.9;
}
</style>