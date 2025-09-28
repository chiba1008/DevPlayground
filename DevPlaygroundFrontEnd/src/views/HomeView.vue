<template>
    <div class="home">
        <div class="page-header">
            <h1>Welcome to DevPlayground</h1>
            <p>開発とテストのためのプレイグラウンド環境です</p>
        </div>
        <HelloManager />
        <PasskeyManager />
    </div>
</template>

<script setup lang="ts">
import HelloManager from '@/components/HelloManager.vue'
import PasskeyManager from '@/components/PasskeyManager.vue'
</script>
