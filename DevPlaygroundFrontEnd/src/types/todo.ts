export type TodoStatus = 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED'

export interface Todo {
  id: number
  userName: string
  title: string
  description: string
  status: TodoStatus
  dueDate: string | null
  createdAt: string
  updatedAt: string
}

export interface CreateTodoRequest {
  userName: string
  title: string
  description: string
  status: TodoStatus | string
  dueDate: string | null
}

export const TodoStatusLabels: Record<TodoStatus, string> = {
  PENDING: '未着手',
  IN_PROGRESS: '進行中',
  COMPLETED: '完了',
  CANCELLED: 'キャンセル'
}