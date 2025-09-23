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

<style>
@import '@/styles/components/common.css';
@import '@/styles/components/calendar.css';
</style>


