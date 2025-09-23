<template>
    <div class="todo-container">
        <div class="page-header">
            <h1>Todo Manager</h1>
            <p>タスクの作成、管理、進捗追跡を効率的に行えます</p>
            <!-- View Tabs -->
            <div class="header-tabs">
                <button
                    @click="setActiveView('list')"
                    :class="['tab-btn', { active: activeView === 'list' }]"
                >
                    リスト表示
                </button>
                <button
                    @click="setActiveView('kanban')"
                    :class="['tab-btn', { active: activeView === 'kanban' }]"
                >
                    カンバン
                </button>
                <button
                    @click="setActiveView('calendar')"
                    :class="['tab-btn', { active: activeView === 'calendar' }]"
                >
                    カレンダー
                </button>
            </div>
        </div>

        <!-- List View -->
        <div v-if="activeView === 'list'" class="todo-content">
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

        <!-- Kanban View -->
        <KanbanBoard
            v-if="activeView === 'kanban'"
            :todos="todos"
            :loading="loading"
            :user-name="userName"
            @todo-created="handleTodoCreated"
            @todo-updated="handleTodoCreated"
            @todo-deleted="handleTodoCreated"
        />

        <!-- Calendar View -->
        <CalendarBoard
            v-if="activeView === 'calendar'"
            :todos="todos"
            :loading="loading"
            :user-name="userName"
            @todo-created="handleTodoCreated"
            @todo-updated="handleTodoCreated"
            @todo-deleted="handleTodoCreated"
        />

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
import KanbanBoard from '@/components/KanbanBoard.vue'
import CalendarBoard from '@/components/CalendarBoard.vue'

const { user, checkAuthStatus } = useAuth()
const userName = computed(() => {
    const name = user.value?.username || ''
    console.log('TodoView userName computed:', name, 'from user:', user.value)
    return name
})

const todos = ref<Todo[]>([])
const loading = ref(false)
const error = ref('')
const activeView = ref<'list' | 'kanban' | 'calendar'>('list')

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

const setActiveView = (view: 'list' | 'kanban' | 'calendar') => {
    activeView.value = view
}

onMounted(async () => {
    // Ensure auth status is checked first
    if (!user.value) {
        await checkAuthStatus()
    }
    loadTodos()
})
</script>

<style>
@import '@/styles/components/common.css';
@import '@/styles/components/todo.css';
</style>
