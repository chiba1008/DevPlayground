<template>
  <div class="error-container">
    <div class="error-content">
      <div class="error-icon">
        <span class="error-number">{{ errorCode }}</span>
      </div>
      <div class="error-message">
        <h1>{{ title }}</h1>
        <p>{{ message }}</p>
        <p v-if="description">{{ description }}</p>
      </div>
      <div class="error-actions">
        <button v-if="showReload" @click="reload" class="btn btn-primary">
          ページを再読み込み
        </button>
        <RouterLink v-if="showHome" to="/" class="btn btn-primary">
          ホームページに戻る
        </RouterLink>
        <RouterLink v-if="showLogin" to="/login" class="btn btn-secondary">
          ログインページ
        </RouterLink>
        <button v-if="showBack" @click="goBack" class="btn btn-secondary">
          前のページに戻る
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { computed } from 'vue'

interface Props {
  errorCode: string | number
  title: string
  message: string
  description?: string
  type?: 'info' | 'warning' | 'error' | 'success'
  showReload?: boolean
  showHome?: boolean
  showLogin?: boolean
  showBack?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  type: 'error',
  showReload: false,
  showHome: true,
  showLogin: false,
  showBack: true
})

const router = useRouter()

const backgroundGradient = computed(() => {
  switch (props.type) {
    case 'info':
      return 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
    case 'warning':
      return 'linear-gradient(135deg, #f39c12 0%, #e67e22 100%)'
    case 'error':
      return 'linear-gradient(135deg, #e74c3c 0%, #c0392b 100%)'
    case 'success':
      return 'linear-gradient(135deg, #27ae60 0%, #2ecc71 100%)'
    default:
      return 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
  }
})

const errorColor = computed(() => {
  switch (props.type) {
    case 'info':
      return '#667eea'
    case 'warning':
      return '#f39c12'
    case 'error':
      return '#e74c3c'
    case 'success':
      return '#27ae60'
    default:
      return '#667eea'
  }
})

const reload = () => {
  window.location.reload()
}

const goBack = () => {
  if (window.history.length > 1) {
    router.back()
  } else {
    router.push('/')
  }
}
</script>

<style scoped>
.error-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: v-bind(backgroundGradient);
  padding: 20px;
}

.error-content {
  text-align: center;
  background: white;
  border-radius: 16px;
  padding: 60px 40px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
  max-width: 600px;
  width: 100%;
}

.error-icon {
  margin-bottom: 30px;
}

.error-number {
  font-size: 8rem;
  font-weight: 900;
  color: v-bind(errorColor);
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
  line-height: 1;
}

.error-message h1 {
  font-size: 2.5rem;
  color: #2c3e50;
  margin-bottom: 20px;
  font-weight: 600;
}

.error-message p {
  font-size: 1.1rem;
  color: #7f8c8d;
  margin-bottom: 15px;
  line-height: 1.6;
}

.error-actions {
  margin-top: 40px;
  display: flex;
  gap: 20px;
  justify-content: center;
  flex-wrap: wrap;
}

.btn {
  padding: 14px 28px;
  border: none;
  border-radius: 8px;
  font-weight: 500;
  font-size: 16px;
  cursor: pointer;
  text-decoration: none;
  display: inline-block;
  transition: all 0.3s ease;
  min-width: 160px;
}

.btn-primary {
  background: v-bind(backgroundGradient);
  color: white;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}

.btn-secondary {
  background-color: #95a5a6;
  color: white;
}

.btn-secondary:hover {
  background-color: #7f8c8d;
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(149, 165, 166, 0.3);
}

@media (max-width: 768px) {
  .error-content {
    padding: 40px 20px;
  }
  
  .error-number {
    font-size: 6rem;
  }
  
  .error-message h1 {
    font-size: 2rem;
  }
  
  .error-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .btn {
    width: 100%;
    max-width: 300px;
  }
}
</style>