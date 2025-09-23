<template>
    <form @submit.prevent="handleSubmit" class="todo-form">
        <div class="form-group">
            <label for="title">タイトル *</label>
            <input
                id="title"
                v-model="form.title"
                type="text"
                required
                placeholder="タスクのタイトルを入力"
                maxlength="30"
            />
        </div>

        <div class="form-group">
            <label for="description">説明 *</label>
            <textarea
                id="description"
                v-model="form.description"
                required
                placeholder="タスクの詳細を入力"
                rows="4"
            ></textarea>
        </div>

        <div class="form-group">
            <label for="status">ステータス *</label>
            <select id="status" v-model="form.status" required>
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
            />
        </div>

        <div class="form-actions">
            <button type="button" @click="resetForm" class="btn btn-secondary">リセット</button>
            <button type="submit" :disabled="loading || !isFormValid" class="btn btn-primary">
                {{ loading ? '作成中...' : 'タスクを作成' }}
            </button>
        </div>

        <div v-if="!props.userName" class="warning-message">
            <strong>警告:</strong>
            ユーザー情報が取得できていません。ページを再読み込みしてください。
        </div>

        <!-- Error Display -->
        <div v-if="error" class="error-message"><strong>エラー:</strong> {{ error }}</div>

        <!-- Success Display -->
        <div v-if="successMessage" class="success-message">
            <strong>成功:</strong> {{ successMessage }}
        </div>
    </form>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { todoApi } from '@/services/todoApi'
import { TodoStatusLabels } from '@/types/todo'
import type { CreateTodoRequest } from '@/types/todo'

interface Props {
    loading: boolean
    userName: string
}

interface Emits {
    (e: 'todo-created'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const form = ref<CreateTodoRequest>({
    userName: props.userName || '',
    title: '',
    description: '',
    status: '',
    dueDate: null,
})

// Watch for userName changes and update form
watch(
    () => props.userName,
    (newUserName) => {
        form.value.userName = newUserName || ''
    },
    { immediate: true },
)

const error = ref('')
const successMessage = ref('')

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
        status: '',
        dueDate: null,
    }
    error.value = ''
    successMessage.value = ''
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
    }, 3000)
}

const handleSubmit = async () => {
    try {
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
        resetForm()
        emit('todo-created')
    } catch (err: unknown) {
        console.error('Create todo error:', err)
        if (typeof err === 'object' && err !== null && 'message' in err) {
            showError((err as Error).message)
        } else {
            showError('タスクの作成に失敗しました')
        }
    }
}
</script>

<style scoped>
/* global.cssのデザインシステムを使用 */
.todo-form {
    display: flex;
    flex-direction: column;
    gap: var(--spacing-lg);
}
</style>
