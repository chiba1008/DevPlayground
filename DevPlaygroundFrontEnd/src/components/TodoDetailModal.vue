<template>
    <div v-if="isOpen && todo" class="modal-overlay" @click="closeModal">
        <div class="modal-content" @click.stop>
            <div class="modal-header">
                <h3>タスク詳細</h3>
                <button @click="closeModal" class="close-btn">×</button>
            </div>

            <div class="modal-body">
                <div class="detail-section">
                    <label>タイトル</label>
                    <div class="detail-value">{{ todo.title }}</div>
                </div>

                <div class="detail-section">
                    <label>説明</label>
                    <div class="detail-value description">{{ todo.description }}</div>
                </div>

                <div class="detail-section">
                    <label>ステータス</label>
                    <div class="detail-value">
                        <span
                            class="status-badge"
                            :class="`status-${todo.status.toLowerCase().replace('_', '-')}`"
                        >
                            {{ getStatusLabel(todo.status) }}
                        </span>
                    </div>
                </div>

                <div class="detail-section">
                    <label>期限日</label>
                    <div class="detail-value">
                        <span
                            v-if="todo.dueDate"
                            class="due-date"
                            :class="getDueDateClass(todo.dueDate)"
                        >
                            📅 {{ formatDateTime(todo.dueDate) }}
                        </span>
                        <span v-else class="no-due-date">期限なし</span>
                    </div>
                </div>

                <div class="detail-section">
                    <label>作成日</label>
                    <div class="detail-value">{{ formatDateTime(todo.createdAt) }}</div>
                </div>

                <div class="detail-section">
                    <label>更新日</label>
                    <div class="detail-value">{{ formatDateTime(todo.updatedAt) }}</div>
                </div>

                <!-- Status Update Section -->
                <div class="detail-section">
                    <label>ステータスを変更</label>
                    <div class="status-controls">
                        <select v-model="selectedStatus" class="status-select">
                            <option
                                v-for="(label, status) in TodoStatusLabels"
                                :key="status"
                                :value="status"
                            >
                                {{ label }}
                            </option>
                        </select>
                        <button
                            @click="updateStatus"
                            :disabled="loading || selectedStatus === todo.status"
                            class="btn btn-update"
                        >
                            {{ loading ? '更新中...' : 'ステータス更新' }}
                        </button>
                    </div>
                </div>

                <!-- Error Display -->
                <div v-if="error" class="error-message"><strong>エラー:</strong> {{ error }}</div>

                <!-- Success Display -->
                <div v-if="successMessage" class="success-message">
                    <strong>成功:</strong> {{ successMessage }}
                </div>
            </div>

            <div class="modal-footer">
                <button @click="closeModal" class="btn btn-secondary">閉じる</button>
                <button @click="deleteTodo" :disabled="loading" class="btn btn-danger">
                    {{ loading ? '削除中...' : 'タスクを削除' }}
                </button>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { todoApi } from '@/services/todoApi'
import type { Todo, TodoStatus } from '@/types/todo'
import { TodoStatusLabels } from '@/types/todo'

interface Props {
    isOpen: boolean
    todo: Todo | null
}

interface Emits {
    (e: 'close'): void
    (e: 'todo-updated'): void
    (e: 'todo-deleted'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const loading = ref(false)
const error = ref('')
const successMessage = ref('')
const selectedStatus = ref<TodoStatus>('PENDING')

// Watch for todo changes
watch(
    () => props.todo,
    (newTodo) => {
        if (newTodo) {
            selectedStatus.value = newTodo.status as TodoStatus
        }
    },
    { immediate: true },
)

const getStatusLabel = (status: TodoStatus | string): string => {
    return TodoStatusLabels[status as TodoStatus] || status
}

const formatDateTime = (dateString: string): string => {
    try {
        const date = new Date(dateString)
        return date.toLocaleString('ja-JP', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit',
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

const closeModal = () => {
    if (!loading.value) {
        error.value = ''
        successMessage.value = ''
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
    }, 3000)
}

const updateStatus = async () => {
    if (!props.todo || selectedStatus.value === props.todo.status) {
        return
    }

    try {
        loading.value = true
        error.value = ''
        successMessage.value = ''

        await todoApi.updateTodoStatus(props.todo.id, selectedStatus.value)
        showSuccess('ステータスが正常に更新されました！')

        setTimeout(() => {
            emit('todo-updated')
        }, 1000)
    } catch (err: unknown) {
        console.error('Update todo status error:', err)
        if (typeof err === 'object' && err !== null && 'message' in err) {
            showError((err as Error).message)
        } else {
            showError('ステータスの更新に失敗しました')
        }
    } finally {
        loading.value = false
    }
}

const deleteTodo = async () => {
    if (!props.todo) {
        return
    }

    if (!confirm('このタスクを削除しますか？この操作は元に戻せません。')) {
        return
    }

    try {
        loading.value = true
        error.value = ''
        successMessage.value = ''

        await todoApi.deleteTodo(props.todo.id)
        showSuccess('タスクが正常に削除されました！')

        setTimeout(() => {
            emit('todo-deleted')
            emit('close')
        }, 1000)
    } catch (err: unknown) {
        console.error('Delete todo error:', err)
        if (typeof err === 'object' && err !== null && 'message' in err) {
            showError((err as Error).message)
        } else {
            showError('タスクの削除に失敗しました')
        }
    } finally {
        loading.value = false
    }
}
</script>

<style scoped>
@import '@/styles/components/common.css';
@import '@/styles/components/modal.css';

/* TodoDetailModal固有のスタイルのみ残す */
</style>
