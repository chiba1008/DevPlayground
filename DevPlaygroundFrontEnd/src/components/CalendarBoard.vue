<template>
    <div class="calendar-board-container">
        <div class="calendar-navigation">
            <button @click="previousMonth" class="nav-btn">‹ 前月</button>
            <h2 class="current-month">{{ formatMonth(currentDate) }}</h2>
            <button @click="nextMonth" class="nav-btn">次月 ›</button>
        </div>

        <div class="calendar-grid">
            <!-- Day headers -->
            <div class="day-header" v-for="day in dayHeaders" :key="day">
                {{ day }}
            </div>

            <!-- Calendar days -->
            <div
                v-for="day in calendarDays"
                :key="`${day.date}-${day.isCurrentMonth}`"
                class="calendar-day"
                :class="{
                    'other-month': !day.isCurrentMonth,
                    today: day.isToday,
                    'has-todos': day.todos.length > 0,
                }"
            >
                <div class="day-number">{{ day.dayNumber }}</div>

                <div class="day-todos">
                    <div
                        v-for="todo in day.todos.slice(0, 3)"
                        :key="todo.id"
                        class="todo-item"
                        :class="`status-${todo.status.toLowerCase().replace('_', '-')}`"
                        @click="openTodoDetail(todo)"
                    >
                        <span class="todo-title">{{ truncateText(todo.title, 15) }}</span>
                    </div>

                    <div v-if="day.todos.length > 3" class="more-todos">
                        +{{ day.todos.length - 3 }} more
                    </div>
                </div>
            </div>
        </div>

        <!-- Legend -->
        <div class="calendar-legend">
            <h3>ステータス</h3>
            <div class="legend-items">
                <div v-for="(label, status) in TodoStatusLabels" :key="status" class="legend-item">
                    <span
                        class="legend-color"
                        :class="`status-${status.toLowerCase().replace('_', '-')}`"
                    ></span>
                    <span class="legend-label">{{ label }}</span>
                </div>
            </div>
        </div>

        <!-- Create Todo Modal -->
        <KanbanCreateModal
            :is-open="showCreateModal"
            :user-name="userName"
            :default-status="'PENDING'"
            @close="closeCreateModal"
            @todo-created="handleTodoCreated"
        />

        <!-- Todo Detail Modal -->
        <TodoDetailModal
            :is-open="showDetailModal"
            :todo="selectedTodo"
            @close="closeDetailModal"
            @todo-updated="handleTodoUpdated"
            @todo-deleted="handleTodoDeleted"
        />
    </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import type { Todo } from '@/types/todo'
import { TodoStatusLabels } from '@/types/todo'
import KanbanCreateModal from '@/components/KanbanCreateModal.vue'
import TodoDetailModal from '@/components/TodoDetailModal.vue'

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

const currentDate = ref(new Date())

// Modal state
const showCreateModal = ref(false)
const showDetailModal = ref(false)
const selectedTodo = ref<Todo | null>(null)

const dayHeaders = ['日', '月', '火', '水', '木', '金', '土']

interface CalendarDay {
    date: Date
    dayNumber: number
    isCurrentMonth: boolean
    isToday: boolean
    todos: Todo[]
}

const formatMonth = (date: Date): string => {
    return date.toLocaleDateString('ja-JP', {
        year: 'numeric',
        month: 'long',
    })
}

const truncateText = (text: string, length: number): string => {
    return text.length > length ? text.substring(0, length) + '...' : text
}

const previousMonth = () => {
    currentDate.value = new Date(
        currentDate.value.getFullYear(),
        currentDate.value.getMonth() - 1,
        1,
    )
}

const nextMonth = () => {
    currentDate.value = new Date(
        currentDate.value.getFullYear(),
        currentDate.value.getMonth() + 1,
        1,
    )
}

const calendarDays = computed((): CalendarDay[] => {
    const year = currentDate.value.getFullYear()
    const month = currentDate.value.getMonth()

    // Get first day of month and last day of month
    const firstDay = new Date(year, month, 1)
    const lastDay = new Date(year, month + 1, 0)

    // Get first day of week (Sunday = 0)
    const startDate = new Date(firstDay)
    startDate.setDate(startDate.getDate() - firstDay.getDay())

    // Get last day of week
    const endDate = new Date(lastDay)
    endDate.setDate(endDate.getDate() + (6 - lastDay.getDay()))

    const days: CalendarDay[] = []
    const today = new Date()
    today.setHours(0, 0, 0, 0)

    for (let date = new Date(startDate); date <= endDate; date.setDate(date.getDate() + 1)) {
        const currentDateCopy = new Date(date)
        const isCurrentMonth = currentDateCopy.getMonth() === month
        const isToday = currentDateCopy.getTime() === today.getTime()

        // Filter todos for this date
        const dayTodos = props.todos.filter((todo) => {
            if (!todo.dueDate) return false
            const todoDate = new Date(todo.dueDate)
            todoDate.setHours(0, 0, 0, 0)
            return todoDate.getTime() === currentDateCopy.getTime()
        })

        days.push({
            date: new Date(currentDateCopy),
            dayNumber: currentDateCopy.getDate(),
            isCurrentMonth,
            isToday,
            todos: dayTodos,
        })
    }

    return days
})

// Modal functions
const closeCreateModal = () => {
    showCreateModal.value = false
}

const handleTodoCreated = () => {
    emit('todo-created')
}

const openTodoDetail = (todo: Todo) => {
    selectedTodo.value = todo
    showDetailModal.value = true
}

const closeDetailModal = () => {
    showDetailModal.value = false
    selectedTodo.value = null
}

const handleTodoUpdated = () => {
    emit('todo-updated')
}

const handleTodoDeleted = () => {
    emit('todo-deleted')
}
</script>

<style scoped>
.calendar-board-container {
    width: 100%;
}

.calendar-navigation {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30px;
    padding: 0 20px;
}

.nav-btn {
    background: #667eea;
    color: white;
    border: none;
    padding: 10px 20px;
    border-radius: 6px;
    cursor: pointer;
    font-size: 16px;
    font-weight: 500;
    transition: all 0.3s ease;
}

.nav-btn:hover {
    background: #5a6fd8;
    transform: translateY(-2px);
}

.current-month {
    font-size: 1.5rem;
    font-weight: 600;
    color: #2c3e50;
    margin: 0;
}

.calendar-grid {
    display: grid;
    grid-template-columns: repeat(7, 1fr);
    gap: 1px;
    background: #e0e0e0;
    border-radius: 8px;
    overflow: hidden;
    margin-bottom: 30px;
}

.day-header {
    background: #f8f9fa;
    padding: 15px 8px;
    text-align: center;
    font-weight: 600;
    color: #495057;
    font-size: 14px;
}

.calendar-day {
    background: white;
    min-height: 120px;
    padding: 8px;
    display: flex;
    flex-direction: column;
    cursor: pointer;
    transition: all 0.2s ease;
}

.calendar-day:hover {
    background: #f8f9fa;
}

.calendar-day.other-month {
    background: #f8f9fa;
    color: #6c757d;
}

.calendar-day.today {
    background: #e3f2fd;
    border: 2px solid #2196f3;
}

.calendar-day.has-todos {
    background: #fff8e1;
}

.day-number {
    font-weight: 600;
    font-size: 14px;
    margin-bottom: 4px;
    color: #2c3e50;
}

.other-month .day-number {
    color: #adb5bd;
}

.day-todos {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 2px;
}

.todo-item {
    padding: 2px 6px;
    border-radius: 4px;
    font-size: 11px;
    cursor: pointer;
    transition: all 0.2s ease;
    border-left: 3px solid;
}

.todo-item:hover {
    opacity: 0.8;
    transform: translateX(2px);
}

.todo-title {
    font-weight: 500;
    color: white;
}

.status-pending {
    background-color: #6c757d;
    border-left-color: #495057;
}

.status-in-progress {
    background-color: #007bff;
    border-left-color: #0056b3;
}

.status-completed {
    background-color: #28a745;
    border-left-color: #1e7e34;
}

.status-cancelled {
    background-color: #dc3545;
    border-left-color: #bd2130;
}

.more-todos {
    font-size: 10px;
    color: #6c757d;
    font-style: italic;
    padding: 2px 6px;
}

.calendar-legend {
    background: white;
    border: 1px solid #e0e0e0;
    border-radius: 8px;
    padding: 20px;
    margin-bottom: 20px;
}

.calendar-legend h3 {
    margin: 0 0 15px 0;
    color: #2c3e50;
    font-size: 1.1rem;
}

.legend-items {
    display: flex;
    flex-wrap: wrap;
    gap: 15px;
}

.legend-item {
    display: flex;
    align-items: center;
    gap: 8px;
}

.legend-color {
    width: 16px;
    height: 16px;
    border-radius: 4px;
    border-left: 3px solid;
}

.legend-label {
    font-size: 14px;
    color: #495057;
}

@media (max-width: 768px) {
    .calendar-grid {
        gap: 0;
    }

    .calendar-day {
        min-height: 80px;
        padding: 4px;
    }

    .day-header {
        padding: 10px 4px;
        font-size: 12px;
    }

    .todo-item {
        font-size: 10px;
        padding: 1px 4px;
    }

    .day-number {
        font-size: 12px;
    }

    .current-month {
        font-size: 1.2rem;
    }

    .legend-items {
        flex-direction: column;
        gap: 10px;
    }
}
</style>
