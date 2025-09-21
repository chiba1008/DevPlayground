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
    max-width: 600px;
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

.modal-body {
    padding: 0 24px;
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.detail-section {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.detail-section label {
    font-weight: 600;
    color: #2c3e50;
    font-size: 14px;
}

.detail-value {
    padding: 12px;
    background: #f8f9fa;
    border-radius: 6px;
    border: 1px solid #e9ecef;
    color: #495057;
}

.detail-value.description {
    white-space: pre-wrap;
    line-height: 1.6;
}

.status-badge {
    padding: 4px 12px;
    border-radius: 16px;
    font-size: 12px;
    font-weight: 500;
    text-transform: uppercase;
    color: white;
    display: inline-block;
}

.status-pending {
    background-color: #6c757d;
}

.status-in-progress {
    background-color: #007bff;
}

.status-completed {
    background-color: #28a745;
}

.status-cancelled {
    background-color: #dc3545;
}

.due-date {
    font-weight: 500;
}

.due-normal {
    color: #6c757d;
}

.due-warning {
    color: #fd7e14;
    font-weight: 600;
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

.status-controls {
    display: flex;
    gap: 12px;
    align-items: center;
}

.status-select {
    flex: 1;
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 6px;
    font-size: 14px;
    background: white;
}

.status-select:focus {
    outline: none;
    border-color: #667eea;
    box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
}

.modal-footer {
    display: flex;
    gap: 12px;
    justify-content: flex-end;
    padding: 20px 24px 24px 24px;
}

.btn {
    padding: 10px 20px;
    border: none;
    border-radius: 6px;
    font-weight: 500;
    font-size: 14px;
    cursor: pointer;
    transition: all 0.3s ease;
    min-width: 100px;
}

.btn-update {
    background-color: #667eea;
    color: white;
}

.btn-update:hover:not(:disabled) {
    background-color: #5a6fd8;
}

.btn-update:disabled {
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

.btn-danger {
    background-color: #dc3545;
    color: white;
}

.btn-danger:hover:not(:disabled) {
    background-color: #c82333;
}

.btn:disabled {
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

    .modal-body {
        padding: 0 20px;
    }

    .modal-footer {
        padding: 20px;
        flex-direction: column;
    }

    .btn {
        width: 100%;
    }

    .status-controls {
        flex-direction: column;
        align-items: stretch;
    }
}
</style>
