package com.example.DevPlayground.service;

import com.example.DevPlayground.entity.Todo;
import com.example.DevPlayground.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class TodoService {

    private final TodoRepository todoRepository;

    @Autowired
    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public Todo createTodo(String userName, String title, String description, String status, String dueDate) {
        LocalDateTime dueDateParsed = null;
        
        if (dueDate != null && !dueDate.trim().isEmpty()) {
            try {
                dueDateParsed = LocalDateTime.parse(dueDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
            } catch (DateTimeParseException e) {
                try {
                    // Fallback to ISO format
                    dueDateParsed = LocalDateTime.parse(dueDate);
                } catch (DateTimeParseException ex) {
                    dueDateParsed = null;
                }
            }
        }

        Todo todo = new Todo(userName, title, description, status, dueDateParsed);
        return todoRepository.save(todo);
    }

    public List<Todo> getTodosByUserName(String userName) {
        return todoRepository.findByUserNameOrderByDueDate(userName);
    }

    public void deleteTodoById(Long todoId) {
        todoRepository.deleteById(todoId);
    }
}