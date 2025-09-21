<template>
    <div class="todo-container">
        <div class="todo-header">
            <h1>Todo Manager</h1>
            <p>タスクを管理して生産性を向上させましょう</p>
        </div>

        <div class="todo-content">
            <!-- Create Todo Form -->
            <div class="todo-section">
                <h2>新しいタスクを作成</h2>
                <TodoForm
                    @todo-created="handleTodoCreated"
                    :loading="loading"
                    :user-name="userName"
                />
            </div>

            <!-- Todo List -->
            <div class="todo-section">
                <div class="section-header">
                    <h2>タスク一覧</h2>
                    <button @click="loadTodos" :disabled="loading" class="btn btn-refresh">
                        <span v-if="loading">読み込み中...</span>
                        <span v-else>更新</span>
                    </button>
                </div>

                <TodoList :todos="todos" :loading="loading" @todo-deleted="handleTodoDeleted" />
            </div>
        </div>

        <!-- Error Display -->
        <div v-if="error" class="error-message"><strong>エラー:</strong> {{ error }}</div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useAuth } from '@/composables/useAuth'
import { todoApi } from '@/services/todoApi'
import type { Todo } from '@/types/todo'
import TodoForm from '@/components/TodoForm.vue'
import TodoList from '@/components/TodoList.vue'

const { user, checkAuthStatus } = useAuth()
const userName = computed(() => {
    const name = user.value?.username || ''
    console.log('TodoView userName computed:', name, 'from user:', user.value)
    return name
})

const todos = ref<Todo[]>([])
const loading = ref(false)
const error = ref('')

const showError = (message: string) => {
    error.value = message
    setTimeout(() => {
        error.value = ''
    }, 5000)
}

const loadTodos = async () => {
    if (!userName.value) {
        showError('ユーザー情報が取得できません')
        return
    }

    loading.value = true
    try {
        todos.value = await todoApi.getTodosByUserName(userName.value)
    } catch (err) {
        showError(err instanceof Error ? err.message : 'タスクの読み込みに失敗しました')
    } finally {
        loading.value = false
    }
}

const handleTodoCreated = async () => {
    await loadTodos()
}

const handleTodoDeleted = async (todoId: number) => {
    try {
        loading.value = true
        await todoApi.deleteTodo(todoId)
        await loadTodos()
    } catch (err) {
        showError(err instanceof Error ? err.message : 'タスクの削除に失敗しました')
    } finally {
        loading.value = false
    }
}

onMounted(async () => {
    // Ensure auth status is checked first
    if (!user.value) {
        await checkAuthStatus()
    }
    loadTodos()
})
</script>

<style scoped>
.todo-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
}

.todo-header {
    text-align: center;
    margin-bottom: 40px;
    padding: 40px 20px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    border-radius: 12px;
}

.todo-header h1 {
    font-size: 2.5rem;
    margin-bottom: 10px;
    font-weight: 600;
}

.todo-header p {
    font-size: 1.2rem;
    opacity: 0.9;
}

.todo-content {
    display: grid;
    grid-template-columns: 1fr 2fr;
    gap: 30px;
    margin-bottom: 20px;
}

.todo-section {
    background: white;
    border: 1px solid #ddd;
    border-radius: 12px;
    padding: 30px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.todo-section h2 {
    color: #2c3e50;
    margin-bottom: 20px;
    font-size: 1.5rem;
}

.section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}

.section-header h2 {
    margin: 0;
}

.btn {
    padding: 10px 20px;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    font-weight: 500;
    transition: all 0.3s ease;
}

.btn-refresh {
    background-color: #28a745;
    color: white;
    font-size: 14px;
}

.btn-refresh:hover:not(:disabled) {
    background-color: #218838;
}

.btn-refresh:disabled {
    background-color: #6c757d;
    cursor: not-allowed;
}

.error-message {
    margin-top: 20px;
    padding: 15px;
    background-color: #f8d7da;
    border: 1px solid #f5c6cb;
    border-radius: 8px;
    color: #721c24;
}

.loading-user {
    text-align: center;
    padding: 20px;
    color: #6c757d;
    font-style: italic;
}

@media (max-width: 768px) {
    .todo-container {
        padding: 10px;
    }

    .todo-content {
        grid-template-columns: 1fr;
        gap: 20px;
    }

    .todo-section {
        padding: 20px;
    }

    .todo-header h1 {
        font-size: 2rem;
    }

    .section-header {
        flex-direction: column;
        gap: 10px;
        align-items: stretch;
    }
}
</style>
