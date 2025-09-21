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

<style scoped>
.kanban-board-container {
    width: 100%;
}

.kanban-board {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
    margin-bottom: 20px;
}

.kanban-column {
    background: #f8f9fa;
    border-radius: 12px;
    min-height: 600px;
    display: flex;
    flex-direction: column;
}

.column-header {
    padding: 20px;
    border-radius: 12px 12px 0 0;
    display: flex;
    justify-content: space-between;
    align-items: center;
    color: white;
    font-weight: 600;
}

.header-right {
    display: flex;
    align-items: center;
    gap: 8px;
}

.column-add-btn {
    background: rgba(255, 255, 255, 0.2);
    border: none;
    color: white;
    width: 24px;
    height: 24px;
    border-radius: 50%;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 16px;
    font-weight: bold;
    transition: all 0.2s ease;
}

.column-add-btn:hover {
    background: rgba(255, 255, 255, 0.3);
    transform: scale(1.1);
}

.header-pending {
    background: linear-gradient(135deg, #6c757d 0%, #495057 100%);
}

.header-in-progress {
    background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
}

.header-completed {
    background: linear-gradient(135deg, #28a745 0%, #1e7e34 100%);
}

.header-cancelled {
    background: linear-gradient(135deg, #dc3545 0%, #bd2130 100%);
}

.column-header h3 {
    margin: 0;
    font-size: 1.1rem;
}

.task-count {
    background: rgba(255, 255, 255, 0.2);
    padding: 4px 8px;
    border-radius: 12px;
    font-size: 0.9rem;
}

.column-content {
    flex: 1;
    padding: 20px;
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.kanban-card {
    background: white;
    border: 1px solid #e0e0e0;
    border-radius: 8px;
    padding: 16px;
    cursor: grab;
    transition: all 0.3s ease;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.kanban-card:hover {
    border-color: #667eea;
    box-shadow: 0 4px 8px rgba(102, 126, 234, 0.15);
    transform: translateY(-2px);
}

.kanban-card:active {
    cursor: grabbing;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 12px;
}

.card-title {
    font-size: 1rem;
    font-weight: 600;
    color: #2c3e50;
    margin: 0;
    flex: 1;
    margin-right: 10px;
}

.delete-btn {
    background: none;
    border: none;
    color: #dc3545;
    font-size: 18px;
    font-weight: bold;
    cursor: pointer;
    padding: 0;
    width: 24px;
    height: 24px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s ease;
}

.delete-btn:hover {
    background: #dc3545;
    color: white;
}

.card-description {
    color: #555;
    margin-bottom: 12px;
    font-size: 0.9rem;
    line-height: 1.4;
}

.card-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 0.8rem;
}

.due-date {
    font-weight: 500;
}

.due-normal {
    color: #6c757d;
}

.due-warning {
    color: #fd7e14;
}

.due-soon {
    color: #dc3545;
    font-weight: 600;
}

.overdue {
    color: #dc3545;
    background: rgba(220, 53, 69, 0.1);
    padding: 2px 6px;
    border-radius: 4px;
    font-weight: 600;
}

.no-due-date {
    color: #999;
    font-style: italic;
}

.empty-column {
    text-align: center;
    padding: 40px 20px;
    color: #6c757d;
    font-style: italic;
}

@media (max-width: 1200px) {
    .kanban-board {
        grid-template-columns: repeat(2, 1fr);
    }
}

@media (max-width: 768px) {
    .kanban-board {
        grid-template-columns: 1fr;
        gap: 15px;
    }

    .kanban-column {
        min-height: 400px;
    }
}
</style>
