package com.softserve.itacademy;

import com.softserve.itacademy.model.Priority;
import com.softserve.itacademy.model.Task;
import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.ToDoService;
import com.softserve.itacademy.service.impl.ToDoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ToDoServiceTest {

    private ToDoService toDoService;

    @BeforeEach
    void setUp() {
        toDoService = new ToDoServiceImpl();
    }

    @Test
    void addTodo_shouldAttachToOwner_andBeReturnedByGetByUser() {
        // Given
        User user = user("owner@example.com");
        ToDo todo = todo(" Home ");

        // When
        ToDo added = toDoService.addTodo(user, todo);

        // Then
        assertSame(todo, added);
        assertSame(user, added.getOwner());
        assertTrue(user.getMyTodos().contains(todo));
        assertEquals(List.of(todo), toDoService.getByUser(user));
        assertSame(todo, toDoService.getByUserTitle(user, "Home"));
    }

    @Test
    void addTodo_shouldReturnNull_whenInvalid() {
        // Given
        User user = user("owner@example.com");

        // When / Then
        assertNull(toDoService.addTodo(user, null));
        assertNull(toDoService.addTodo(null, todo("Valid")));
        assertNull(toDoService.addTodo(user, todo(null)));
        assertNull(toDoService.addTodo(user, todo("   ")));
        assertTrue(toDoService.getAll().isEmpty());
        assertTrue(user.getMyTodos().isEmpty());
    }

    @Test
    void addTodo_shouldEnforceUniqueTitle_perOwner() {
        // Given
        User owner = user("owner@example.com");
        User sameOwnerByNaturalKey = user(" OWNER@EXAMPLE.COM ");
        User anotherOwner = user("another@example.com");

        ToDo first = todo("Work");
        ToDo duplicate = todo("  Work  ");
        ToDo forAnotherOwner = todo("Work");

        // When / Then
        assertNotNull(toDoService.addTodo(owner, first));
        assertNull(toDoService.addTodo(sameOwnerByNaturalKey, duplicate));
        assertNotNull(toDoService.addTodo(anotherOwner, forAnotherOwner));
        assertEquals(2, toDoService.getAll().size());
        assertEquals(1, toDoService.getByUser(owner).size());
        assertEquals(1, toDoService.getByUser(anotherOwner).size());
    }

    @Test
    void addTodo_shouldRejectReusingSameInstanceForAnotherOwner() {
        // Given
        User firstOwner = user("first@example.com");
        User secondOwner = user("second@example.com");
        ToDo todo = todo("Shared");
        assertNotNull(toDoService.addTodo(firstOwner, todo));

        // When
        ToDo secondAdd = toDoService.addTodo(secondOwner, todo);

        // Then
        assertNull(secondAdd);
        assertSame(firstOwner, todo.getOwner());
        assertEquals(1, toDoService.getAll().size());
        assertEquals(List.of(todo), toDoService.getByUser(firstOwner));
        assertTrue(toDoService.getByUser(secondOwner).isEmpty());
        assertEquals(List.of(todo), firstOwner.getMyTodos());
        assertTrue(secondOwner.getMyTodos().isEmpty());
    }

    @Test
    void updateTodo_shouldReplaceExistingByTitleWithinOwner() {
        // Given
        User user = user("owner@example.com");
        ToDo original = todo("Sprint");
        original.setCreatedAt(LocalDateTime.of(2026, 4, 20, 10, 0));
        original.setTasks(new ArrayList<>(List.of(new Task("Draft", Priority.LOW))));
        toDoService.addTodo(user, original);

        List<Task> updatedTasks = new ArrayList<>(List.of(new Task("Review", Priority.HIGH)));
        ToDo updated = todo("  Sprint  ");
        updated.setOwner(user("OWNER@EXAMPLE.COM"));
        updated.setCreatedAt(LocalDateTime.of(2026, 4, 24, 9, 30));
        updated.setTasks(updatedTasks);

        // When
        ToDo result = toDoService.updateTodo(updated);

        // Then
        assertSame(original, result);
        assertEquals(updated.getCreatedAt(), result.getCreatedAt());
        assertSame(updatedTasks, result.getTasks());
        assertEquals("Sprint", result.getTitle());
        assertSame(user, result.getOwner());
    }

    @Test
    void updateTodo_shouldReturnNull_whenNotFound_orInvalid() {
        // Given
        User user = user("owner@example.com");
        ToDo stored = todo("Existing");
        toDoService.addTodo(user, stored);

        ToDo absent = todo("Absent");
        absent.setOwner(user);

        ToDo invalidWithoutOwner = todo("Existing");
        ToDo invalidWithoutTitle = new ToDo();
        invalidWithoutTitle.setOwner(user);
        ToDo invalidBlankTitle = todo("   ");
        invalidBlankTitle.setOwner(user);

        // When / Then
        assertNull(toDoService.updateTodo(null));
        assertNull(toDoService.updateTodo(absent));
        assertNull(toDoService.updateTodo(invalidWithoutOwner));
        assertNull(toDoService.updateTodo(invalidWithoutTitle));
        assertNull(toDoService.updateTodo(invalidBlankTitle));
    }

    @Test
    void deleteTodo_shouldRemoveFromOwnerList() {
        // Given
        User user = user("owner@example.com");
        ToDo todo = todo("Cleanup");
        toDoService.addTodo(user, todo);

        // When
        assertDoesNotThrow(() -> {
            toDoService.deleteTodo(null);
            ToDo absent = todo("Missing");
            absent.setOwner(user);
            toDoService.deleteTodo(absent);
        });

        ToDo toDelete = todo("  Cleanup ");
        toDelete.setOwner(user("OWNER@EXAMPLE.COM"));
        toDoService.deleteTodo(toDelete);

        // Then
        assertTrue(toDoService.getAll().isEmpty());
        assertTrue(user.getMyTodos().isEmpty());
        assertNull(toDoService.getByUserTitle(user, "Cleanup"));
    }

    @Test
    void getAll_shouldReturnCopy() {
        // Given
        User user = user("owner@example.com");
        ToDo todo = todo("Home");
        toDoService.addTodo(user, todo);

        // When
        List<ToDo> all = toDoService.getAll();
        all.clear();

        // Then
        assertEquals(1, toDoService.getAll().size());
        assertSame(todo, toDoService.getAll().getFirst());
    }

    @Test
    void getByUser_shouldReturnCopy_orEmptyWhenNone() {
        // Given
        User owner = user("owner@example.com");
        User anotherUser = user("another@example.com");
        ToDo first = todo("One");
        ToDo second = todo("Two");
        toDoService.addTodo(owner, first);
        toDoService.addTodo(owner, second);

        // When
        List<ToDo> ownerTodos = toDoService.getByUser(user("OWNER@EXAMPLE.COM"));
        ownerTodos.remove(first);

        // Then
        assertEquals(2, toDoService.getByUser(owner).size());
        assertTrue(toDoService.getByUser(anotherUser).isEmpty());
        assertTrue(toDoService.getByUser(null).isEmpty());
    }

    @Test
    void getByUserTitle_shouldReturnToDo_orNull() {
        // Given
        User owner = user("owner@example.com");
        User anotherUser = user("another@example.com");
        ToDo todo = todo(" Shopping ");
        toDoService.addTodo(owner, todo);

        // When / Then
        assertSame(todo, toDoService.getByUserTitle(user("OWNER@EXAMPLE.COM"), "Shopping"));
        assertSame(todo, toDoService.getByUserTitle(owner, "  Shopping  "));
        assertNull(toDoService.getByUserTitle(anotherUser, "Shopping"));
        assertNull(toDoService.getByUserTitle(owner, "Missing"));
        assertNull(toDoService.getByUserTitle(null, "Shopping"));
        assertNull(toDoService.getByUserTitle(owner, null));
        assertNull(toDoService.getByUserTitle(owner, "   "));
    }

    @Test
    void todo_shouldUseOwnerAndTrimmedTitleAsNaturalKey() {
        // Given
        ToDo first = todo("  Plans ");
        first.setOwner(user("USER@EXAMPLE.COM"));

        ToDo second = todo("Plans");
        second.setOwner(user("user@example.com"));

        // When / Then
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    // Helpers

    private static User user(String email) {
        User user = new User();
        user.setEmail(email);
        return user;
    }

    private static ToDo todo(String title) {
        ToDo todo = new ToDo();
        todo.setTitle(title);
        return todo;
    }
}
