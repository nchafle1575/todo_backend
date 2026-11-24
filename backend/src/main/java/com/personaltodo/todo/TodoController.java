package com.personaltodo.todo;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TodoController {
    private final TodoRepository repository;

    public TodoController(TodoRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/health")
    public java.util.Map<String, String> health() {
        return java.util.Map.of("status", "UP", "service", "personal-todo-api");
    }

    @GetMapping("/todos")
    public List<Todo> getAll() {
        return repository.findAllByOrderByCreatedAtDesc();
    }

    @PostMapping("/todos")
    @ResponseStatus(HttpStatus.CREATED)
    public Todo create(@Valid @RequestBody Todo todo) {
        todo.setId(null);
        return repository.save(todo);
    }

    @PutMapping("/todos/{id}")
    public Todo update(@PathVariable Long id, @Valid @RequestBody Todo input) {
        Todo todo = findTodo(id);
        todo.setTitle(input.getTitle());
        todo.setDescription(input.getDescription());
        todo.setCategory(input.getCategory());
        todo.setPriority(input.getPriority());
        todo.setDueDate(input.getDueDate());
        todo.setCompleted(input.isCompleted());
        return repository.save(todo);
    }

    @PatchMapping("/todos/{id}/toggle")
    public Todo toggle(@PathVariable Long id) {
        Todo todo = findTodo(id);
        todo.setCompleted(!todo.isCompleted());
        return repository.save(todo);
    }

    @DeleteMapping("/todos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        repository.delete(findTodo(id));
    }

    private Todo findTodo(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo not found"));
    }
}
