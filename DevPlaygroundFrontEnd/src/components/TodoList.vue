<template>
    <div class="todo-list">
        <div v-if="loading" class="loading">読み込み中...</div>

        <div v-else-if="todos.length === 0" class="no-todos">
            <p>タスクがありません</p>
            <p>新しいタスクを作成してみましょう！</p>
        </div>

        <div v-else class="todos-container">
            <div v-for="todo in todos" :key="todo.id" class="todo-item">
                <div class="todo-header">
                    <h3 class="todo-title">{{ todo.title }}</h3>
                    <span
                        class="status-badge"
                        :class="`status-${todo.status.toLowerCase().replace('_', '-')}`"
                    >
                        {{ getStatusLabel(todo.status) }}
                    </span>
                </div>

                <p class="todo-description">{{ todo.description }}</p>

                <div class="todo-meta">
                    <span v-if="todo.dueDate" class="due-date">
                        期限: {{ formatDate(todo.dueDate) }}
                    </span>
                    <span v-else class="no-due-date">期限なし</span>
                </div>

                <div class="todo-actions">
                    <button
                        @click="deleteTodo(todo.id)"
                        class="btn btn-danger btn-sm"
                        :disabled="loading"
                    >
                        削除
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import type { Todo, TodoStatus } from '@/types/todo'
import { TodoStatusLabels } from '@/types/todo'

interface Props {
    todos: Todo[]
    loading: boolean
}

interface Emits {
    (e: 'todo-deleted', todoId: number): void
}

defineProps<Props>()
const emit = defineEmits<Emits>()

const getStatusLabel = (status: TodoStatus): string => {
    return TodoStatusLabels[status] || status
}

const formatDate = (dateString: string): string => {
    try {
        const date = new Date(dateString)
        return date.toLocaleDateString('ja-JP', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
        })
    } catch {
        return dateString
    }
}

const deleteTodo = (todoId: number) => {
    if (confirm('このタスクを削除しますか？')) {
        emit('todo-deleted', todoId)
    }
}
</script>

<style scoped>
.todo-list {
    width: 100%;
}

.loading {
    text-align: center;
    padding: 40px;
    color: #6c757d;
    font-style: italic;
}

.no-todos {
    text-align: center;
    padding: 40px;
    color: #6c757d;
}

.no-todos p {
    margin-bottom: 10px;
}

.todos-container {
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.todo-item {
    border: 1px solid #e0e0e0;
    border-radius: 8px;
    padding: 20px;
    background: #f9f9f9;
    transition: all 0.3s ease;
}

.todo-item:hover {
    border-color: #667eea;
    box-shadow: 0 2px 8px rgba(102, 126, 234, 0.1);
}

.todo-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
}

.todo-title {
    font-size: 1.2rem;
    font-weight: 600;
    color: #2c3e50;
    margin: 0;
}

/* ステータスバッジはglobal.cssで統一管理 */

.todo-description {
    color: #555;
    margin-bottom: 16px;
    line-height: 1.6;
}

.todo-meta {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    font-size: 14px;
    color: #666;
}

.due-date {
    font-weight: 500;
}

.no-due-date {
    color: #999;
    font-style: italic;
}

.todo-actions {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
}

/* ボタンスタイルはglobal.cssで統一管理 */

@media (max-width: 768px) {
    .todo-item {
        padding: 16px;
    }

    .todo-header {
        flex-direction: column;
        align-items: flex-start;
        gap: 8px;
    }

    .todo-meta {
        flex-direction: column;
        align-items: flex-start;
        gap: 4px;
    }
}
</style>
