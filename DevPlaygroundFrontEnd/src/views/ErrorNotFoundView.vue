<template>
  <div class="error-container">
    <div class="error-content">
      <div class="error-icon">
        <span class="error-number">404</span>
      </div>
      <div class="error-message">
        <h1>Page Not Found</h1>
        <p>お探しのページが見つかりませんでした。</p>
        <p>URLが正しいかご確認いただくか、以下のボタンからホームページに戻ってください。</p>
      </div>
      <div class="error-actions">
        <RouterLink to="/" class="btn btn-primary">
          ホームページに戻る
        </RouterLink>
        <button @click="goBack" class="btn btn-secondary">
          前のページに戻る
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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
  color: #667eea;
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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