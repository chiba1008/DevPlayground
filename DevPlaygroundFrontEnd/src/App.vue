<script setup lang="ts">
import './styles/global.css'
</script>

<template>
  <div id="app">
    <header>
      <div class="header-content">
        <h1>DevPlayground</h1>
        <nav class="main-nav">
          <RouterLink to="/" class="nav-link">Home</RouterLink>
          <RouterLink to="/users" class="nav-link">User Manager</RouterLink>
        </nav>
      </div>
    </header>
    <main>
      <RouterView />
    </main>
  </div>
</template>

