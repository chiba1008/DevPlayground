<template>
  <div v-if="isOpen" class="modal-overlay" @click="closeModal">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h3>新しいタスクを作成</h3>
        <button @click="closeModal" class="close-btn">×</button>
      </div>

      <form @submit.prevent="handleSubmit" class="modal-form">
        <div class="form-group">
          <label for="title">タイトル *</label>
          <input
            id="title"
            v-model="form.title"
            type="text"
            required
            placeholder="タスクのタイトルを入力"
            maxlength="30"
            :disabled="loading"
          />
        </div>

        <div class="form-group">
          <label for="description">説明 *</label>
          <textarea
            id="description"
            v-model="form.description"
            required
            placeholder="タスクの詳細を入力"
            rows="3"
            :disabled="loading"
          ></textarea>
        </div>

        <div class="form-group">
          <label for="status">ステータス *</label>
          <select id="status" v-model="form.status" required :disabled="loading">
            <option value="">ステータスを選択</option>
            <option v-for="(label, status) in TodoStatusLabels" :key="status" :value="status">
              {{ label }}
            </option>
          </select>
        </div>

        <div class="form-group">
          <label for="dueDate">期限日</label>
          <input
            id="dueDate"
            v-model="form.dueDate"
            type="datetime-local"
            placeholder="期限日を選択（任意）"
            :disabled="loading"
          />
        </div>

        <!-- Error Display -->
        <div v-if="error" class="error-message">
          <strong>エラー:</strong> {{ error }}
        </div>

        <!-- Success Display -->
        <div v-if="successMessage" class="success-message">
          <strong>成功:</strong> {{ successMessage }}
        </div>

        <div class="form-actions">
          <button type="button" @click="closeModal" class="btn btn-secondary" :disabled="loading">
            キャンセル
          </button>
          <button type="submit" :disabled="loading || !isFormValid" class="btn btn-primary">
            {{ loading ? '作成中...' : 'タスクを作成' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { todoApi } from '@/services/todoApi'
import { TodoStatusLabels } from '@/types/todo'
import type { CreateTodoRequest } from '@/types/todo'

interface Props {
  isOpen: boolean
  userName: string
  defaultStatus?: string
}

interface Emits {
  (e: 'close'): void
  (e: 'todo-created'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const form = ref<CreateTodoRequest>({
  userName: props.userName || '',
  title: '',
  description: '',
  status: props.defaultStatus || '',
  dueDate: null,
})

// Watch for userName and defaultStatus changes
watch(
  () => [props.userName, props.defaultStatus],
  ([newUserName, newDefaultStatus]) => {
    form.value.userName = newUserName || ''
    if (newDefaultStatus && !form.value.status) {
      form.value.status = newDefaultStatus
    }
  },
  { immediate: true }
)

// Reset form when modal opens
watch(
  () => props.isOpen,
  (isOpen) => {
    if (isOpen) {
      resetForm()
    }
  }
)

const error = ref('')
const successMessage = ref('')
const loading = ref(false)

const isFormValid = computed(() => {
  const userNameValid = form.value.userName && form.value.userName.trim()
  const titleValid = form.value.title && form.value.title.trim()
  const descriptionValid = form.value.description && form.value.description.trim()
  const statusValid = form.value.status
  return userNameValid && titleValid && descriptionValid && statusValid
})

const resetForm = () => {
  form.value = {
    userName: props.userName || '',
    title: '',
    description: '',
    status: props.defaultStatus || '',
    dueDate: null,
  }
  error.value = ''
  successMessage.value = ''
}

const closeModal = () => {
  if (!loading.value) {
    emit('close')
  }
}

const showError = (message: string) => {
  error.value = message
  setTimeout(() => {
    error.value = ''
  }, 5000)
}

const showSuccess = (message: string) => {
  successMessage.value = message
  setTimeout(() => {
    successMessage.value = ''
  }, 2000)
}

const handleSubmit = async () => {
  try {
    loading.value = true
    error.value = ''
    successMessage.value = ''

    // Validate userName
    if (!form.value.userName || form.value.userName.trim() === '') {
      showError('ユーザー情報が取得できません。ページを再読み込みしてください。')
      return
    }

    // Format the request data
    const todoData: CreateTodoRequest = {
      ...form.value,
      dueDate: form.value.dueDate || null,
    }

    console.log('Sending todo data:', todoData)
    await todoApi.createTodo(todoData)

    showSuccess('タスクが正常に作成されました！')
    
    // Wait a bit for success message to show, then close modal and emit event
    setTimeout(() => {
      emit('todo-created')
      emit('close')
    }, 1000)
    
  } catch (err: unknown) {
    console.error('Create todo error:', err)
    if (typeof err === 'object' && err !== null && 'message' in err) {
      showError((err as Error).message)
    } else {
      showError('タスクの作成に失敗しました')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 100%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 24px 0 24px;
  margin-bottom: 20px;
}

.modal-header h3 {
  margin: 0;
  color: #2c3e50;
  font-size: 1.3rem;
  font-weight: 600;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  color: #6c757d;
  cursor: pointer;
  padding: 0;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.close-btn:hover {
  background: #f8f9fa;
  color: #495057;
}

.modal-form {
  padding: 0 24px 24px 24px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-weight: 500;
  color: #2c3e50;
  font-size: 14px;
}

.form-group input,
.form-group textarea,
.form-group select {
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.3s ease;
}

.form-group input:focus,
.form-group textarea:focus,
.form-group select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
}

.form-group input:disabled,
.form-group textarea:disabled,
.form-group select:disabled {
  background-color: #f8f9fa;
  cursor: not-allowed;
}

.form-group textarea {
  resize: vertical;
  min-height: 60px;
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 10px;
}

.btn {
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  font-weight: 500;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  min-width: 100px;
}

.btn-primary {
  background-color: #667eea;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background-color: #5a6fd8;
}

.btn-primary:disabled {
  background-color: #bdc3c7;
  cursor: not-allowed;
}

.btn-secondary {
  background-color: #6c757d;
  color: white;
}

.btn-secondary:hover:not(:disabled) {
  background-color: #5a6268;
}

.btn-secondary:disabled {
  background-color: #bdc3c7;
  cursor: not-allowed;
}

.error-message {
  padding: 12px;
  background-color: #f8d7da;
  border: 1px solid #f5c6cb;
  border-radius: 6px;
  color: #721c24;
  font-size: 14px;
}

.success-message {
  padding: 12px;
  background-color: #d4edda;
  border: 1px solid #c3e6cb;
  border-radius: 6px;
  color: #155724;
  font-size: 14px;
}

@media (max-width: 768px) {
  .modal-overlay {
    padding: 10px;
  }

  .modal-content {
    max-height: 95vh;
  }

  .modal-header {
    padding: 20px 20px 0 20px;
  }

  .modal-form {
    padding: 0 20px 20px 20px;
  }

  .form-actions {
    flex-direction: column;
  }

  .btn {
    width: 100%;
  }
}
</style>