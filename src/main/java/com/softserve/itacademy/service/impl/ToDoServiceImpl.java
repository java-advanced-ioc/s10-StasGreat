package com.softserve.itacademy.service.impl;

import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.ToDoService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ToDoServiceImpl implements ToDoService {

    private final List<ToDo> todos = new ArrayList<>();

    @Override
    public ToDo addTodo(User user, ToDo todo) {
        if (todo == null || user == null) return null;
        if (todo.getNormalizedTitle() == null) return null;
        if (todos.stream().anyMatch(existing -> existing == todo)) return null;
        if (getByUserTitle(user, todo.getTitle()) != null) return null;

        todo.setOwner(user);
        todos.add(todo);
        user.getMyTodos().add(todo);
        return todo;
    }

    @Override
    public ToDo updateTodo(ToDo todo) {
        if (todo == null || todo.getOwner() == null) return null;
        if (todo.getNormalizedTitle() == null) return null;

        int index = todos.indexOf(todo);
        if (index == -1) return null;

        ToDo existing = todos.get(index);
        existing.setCreatedAt(todo.getCreatedAt());
        existing.setTasks(todo.getTasks());
        return existing;
    }

    @Override
    public void deleteTodo(ToDo todo) {
        if (todo == null || todo.getOwner() == null) return;

        int index = todos.indexOf(todo);
        if (index == -1) return;

        ToDo existing = todos.remove(index);
        existing.getOwner().getMyTodos().remove(existing);
    }

    @Override
    public List<ToDo> getAll() {
        return new ArrayList<>(todos);
    }

    @Override
    public List<ToDo> getByUser(User user) {
        if (user == null) return new ArrayList<>();
        return todos.stream()
                .filter(t -> user.equals(t.getOwner()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public ToDo getByUserTitle(User user, String title) {
        if (user == null || title == null || title.trim().isEmpty()) {
            return null;
        }
        return todos.stream()
                .filter(t -> user.equals(t.getOwner()))
                .filter(t -> title.trim().equals(t.getNormalizedTitle()))
                .findFirst()
                .orElse(null);
    }
}
