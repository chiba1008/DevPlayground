import { HttpClient } from './http'
import type { Todo, CreateTodoRequest } from '@/types/todo'

class TodoApi {
  private httpClient: HttpClient

  constructor() {
    this.httpClient = new HttpClient()
  }

  async createTodo(todo: CreateTodoRequest): Promise<Todo> {
    return this.httpClient.post<Todo>('/todo/create', todo)
  }

  async getTodosByUserName(userName: string): Promise<Todo[]> {
    return this.httpClient.get<Todo[]>(`/todo/user/${userName}`)
  }

  async deleteTodo(todoId: number): Promise<void> {
    await this.httpClient.delete(`/todo/delete/${todoId}`)
  }

  async updateTodoStatus(todoId: number, status: string): Promise<Todo> {
    return this.httpClient.put<Todo>(`/todo/update/${todoId}/status`, { status })
  }
}

export const todoApi = new TodoApi()