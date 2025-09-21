<template>
  <div class="hello-manager">
    <h2>API Connection Test</h2>

    <!-- Hello API Test -->
    <section class="api-test">
      <button @click="testHelloApi" :disabled="loading">Test Hello API</button>
      <div v-if="helloResponse" class="response">
        <strong>Response:</strong> {{ helloResponse.message }} ({{ helloResponse.status }})
      </div>
    </section>

    <!-- Error Display -->
    <div v-if="error" class="error"><strong>Error:</strong> {{ error }}</div>

    <!-- Loading Indicator -->
    <div v-if="loading" class="loading">Loading...</div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { helloApi, type HelloResponse } from '@/services'
import '../styles/components/HelloManager.css'

const helloResponse = ref<HelloResponse | null>(null)
const loading = ref(false)
const error = ref('')

const showError = (message: string) => {
  error.value = message
  setTimeout(() => {
    error.value = ''
  }, 5000)
}

const testHelloApi = async () => {
  loading.value = true
  try {
    helloResponse.value = await helloApi.getHello()
  } catch (err) {
    showError(err instanceof Error ? err.message : 'Failed to connect to API')
  } finally {
    loading.value = false
  }
}
</script>
