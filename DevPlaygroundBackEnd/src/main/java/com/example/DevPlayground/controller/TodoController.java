package com.example.DevPlayground.controller;

import com.example.DevPlayground.entity.Todo;
import com.example.DevPlayground.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todo")
public class TodoController {

    private final TodoService todoService;

    @Autowired
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @PostMapping("/create")
    public ResponseEntity<Todo> createTodo(@RequestBody TodoRequest request) {

        // Validate required fields
        if (request.userName() == null || request.userName().trim().isEmpty()) {
            throw new IllegalArgumentException("UserName cannot be null or empty");
        }
        if (request.title() == null || request.title().trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        if (request.description() == null || request.description().trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
        if (request.status() == null || request.status().trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be null or empty");
        }
        
        Todo todoResponse = todoService.createTodo(
                request.userName(),
                request.title(),
                request.description(),
                request.status(),
                request.dueDate()
        );
        return ResponseEntity.ok(todoResponse);
    }

    @GetMapping("/user/{userName}")
    public ResponseEntity<List<Todo>> getTodosByUserName(@PathVariable String userName) {
        List<Todo> todos = todoService.getTodosByUserName(userName);
        return ResponseEntity.ok(todos);
    }

    @DeleteMapping("/delete/{todoId}")
    public ResponseEntity<Void> deleteTodoById(@PathVariable Long todoId) {
        todoService.deleteTodoById(todoId);
        return ResponseEntity.noContent().build();
    }

    public record TodoRequest(String userName, String title, String description, String status, String dueDate) {}
}
