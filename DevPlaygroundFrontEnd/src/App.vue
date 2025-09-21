<script setup lang="ts">
import UserManager from './components/UserManager.vue'
</script>

<template>
  <div id="app">
    <header>
      <h1>DevPlayground - Frontend ↔ Backend Integration</h1>
    </header>
    <main>
      <UserManager />
    </main>
  </div>
</template>

<style scoped>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}

header {
  text-align: center;
  background-color: #f8f9fa;
  padding: 20px;
  margin-bottom: 20px;
  border-bottom: 1px solid #dee2e6;
}

main {
  padding: 0 20px;
}
</style>
