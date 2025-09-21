<template>
  <div class="error-container">
    <div class="error-content">
      <div class="error-icon">
        <span class="error-number">500</span>
      </div>
      <div class="error-message">
        <h1>Internal Server Error</h1>
        <p>申し訳ございません。サーバーでエラーが発生しました。</p>
        <p>しばらく時間をおいてから再度お試しください。問題が続く場合は、管理者にお問い合わせください。</p>
      </div>
      <div class="error-actions">
        <button @click="reload" class="btn btn-primary">
          ページを再読み込み
        </button>
        <RouterLink to="/" class="btn btn-secondary">
          ホームページに戻る
        </RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const reload = () => {
  window.location.reload()
}
</script>

<style scoped>
.error-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f39c12 0%, #e67e22 100%);
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
  color: #f39c12;
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
  background: linear-gradient(135deg, #f39c12 0%, #e67e22 100%);
  color: white;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(243, 156, 18, 0.3);
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