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

  // TODO: Update functionality if needed in the future
  // async updateTodo(todoId: number, todo: UpdateTodoRequest): Promise<Todo> {
  //   return this.httpClient.put<Todo>(`/todo/update/${todoId}`, todo)
  // }
}

export const todoApi = new TodoApi()