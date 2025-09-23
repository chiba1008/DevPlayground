<template>
    <div class="kanban-board-container">
        <div class="kanban-board">
            <div v-for="status in statuses" :key="status.key" class="kanban-column">
                <div
                    class="column-header"
                    :class="`header-${status.key.toLowerCase().replace('_', '-')}`"
                >
                    <h3>{{ status.label }}</h3>
                    <div class="header-right">
                        <span class="task-count">{{ getTaskCountByStatus(status.key) }}</span>
                        <button
                            @click="openCreateModalWithStatus(status.key)"
                            class="column-add-btn"
                            title="このステータスでタスクを作成"
                        >
                            ＋
                        </button>
                    </div>
                </div>

                <div
                    class="column-content"
                    @dragover.prevent
                    @drop="handleDrop($event, status.key)"
                >
                    <div
                        v-for="todo in getTodosByStatus(status.key)"
                        :key="todo.id"
                        class="kanban-card"
                        draggable="true"
                        @dragstart="handleDragStart($event, todo)"
                    >
                        <div class="card-header">
                            <h4 class="card-title">{{ todo.title }}</h4>
                            <button @click="deleteTodo(todo.id)" class="delete-btn" title="削除">
                                ×
                            </button>
                        </div>

                        <p class="card-description">{{ todo.description }}</p>

                        <div class="card-footer">
                            <span
                                v-if="todo.dueDate"
                                class="due-date"
                                :class="getDueDateClass(todo.dueDate)"
                            >
                                📅 {{ formatDate(todo.dueDate) }}
                            </span>
                            <span v-else class="no-due-date">期限なし</span>
                        </div>
                    </div>

                    <div v-if="getTodosByStatus(status.key).length === 0" class="empty-column">
                        <p>タスクがありません</p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Create Todo Modal -->
        <KanbanCreateModal
            :is-open="showCreateModal"
            :user-name="userName"
            :default-status="defaultStatus"
            @close="closeCreateModal"
            @todo-created="handleTodoCreated"
        />
    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { todoApi } from '@/services/todoApi'
import type { Todo, TodoStatus } from '@/types/todo'
import { TodoStatusLabels } from '@/types/todo'
import KanbanCreateModal from '@/components/KanbanCreateModal.vue'

interface Props {
    todos: Todo[]
    loading: boolean
    userName: string
}

interface Emits {
    (e: 'todo-created'): void
    (e: 'todo-updated'): void
    (e: 'todo-deleted'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// Modal state
const showCreateModal = ref(false)
const defaultStatus = ref<string>('')

const statuses: { key: TodoStatus; label: string }[] = [
    { key: 'PENDING', label: TodoStatusLabels.PENDING },
    { key: 'IN_PROGRESS', label: TodoStatusLabels.IN_PROGRESS },
    { key: 'COMPLETED', label: TodoStatusLabels.COMPLETED },
    { key: 'CANCELLED', label: TodoStatusLabels.CANCELLED },
]

const getTodosByStatus = (status: TodoStatus): Todo[] => {
    return props.todos.filter((todo) => todo.status === status)
}

const getTaskCountByStatus = (status: TodoStatus): number => {
    return getTodosByStatus(status).length
}

const formatDate = (dateString: string): string => {
    try {
        const date = new Date(dateString)
        return date.toLocaleDateString('ja-JP', {
            month: 'short',
            day: 'numeric',
        })
    } catch {
        return dateString
    }
}

const getDueDateClass = (dueDate: string): string => {
    const now = new Date()
    const due = new Date(dueDate)
    const diffDays = Math.ceil((due.getTime() - now.getTime()) / (1000 * 60 * 60 * 24))

    if (diffDays < 0) return 'overdue'
    if (diffDays <= 1) return 'due-soon'
    if (diffDays <= 3) return 'due-warning'
    return 'due-normal'
}

let draggedTodo: Todo | null = null

const handleDragStart = (event: DragEvent, todo: Todo) => {
    draggedTodo = todo
    if (event.dataTransfer) {
        event.dataTransfer.effectAllowed = 'move'
    }
}

const handleDrop = async (event: DragEvent, newStatus: TodoStatus) => {
    event.preventDefault()

    if (!draggedTodo || draggedTodo.status === newStatus) {
        draggedTodo = null
        return
    }

    try {
        await todoApi.updateTodoStatus(draggedTodo.id, newStatus)
        emit('todo-updated')
    } catch (err) {
        console.error('Failed to update todo status:', err)
    } finally {
        draggedTodo = null
    }
}

const deleteTodo = async (todoId: number) => {
    if (!confirm('このタスクを削除しますか？')) {
        return
    }

    try {
        await todoApi.deleteTodo(todoId)
        emit('todo-deleted')
    } catch (err) {
        console.error('Failed to delete todo:', err)
    }
}

// Modal functions
const openCreateModalWithStatus = (status: TodoStatus) => {
    defaultStatus.value = status
    showCreateModal.value = true
}

const closeCreateModal = () => {
    showCreateModal.value = false
    defaultStatus.value = ''
}

const handleTodoCreated = () => {
    emit('todo-created')
}
</script>

<style>
@import '@/styles/components/common.css';
@import '@/styles/components/kanban.css';
</style>

<style scoped>
/* KanbanBoard固有のスタイルのみ残す */
</style>
