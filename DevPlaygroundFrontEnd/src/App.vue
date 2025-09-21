<script setup lang="ts">
import UserManager from './components/UserManager.vue'
import HelloManager from './components/HelloManager.vue'
import './styles/global.css'
</script>

<template>
  <div id="app">
    <header>
      <h1>DevPlayground Manager</h1>
    </header>
    <main>
      <HelloManager />
      <UserManager />
    </main>
  </div>
</template>

