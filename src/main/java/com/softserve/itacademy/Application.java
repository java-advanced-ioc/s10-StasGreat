package com.softserve.itacademy;

import com.softserve.itacademy.model.Priority;
import com.softserve.itacademy.model.Task;
import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.TaskService;
import com.softserve.itacademy.service.ToDoService;
import com.softserve.itacademy.service.UserService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

public class Application {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class);

        // Get beans from Spring context
        UserService userService = context.getBean(UserService.class);
        ToDoService toDoService = context.getBean(ToDoService.class);
        TaskService taskService = context.getBean(TaskService.class);

        System.out.println("=== Spring IoC ToDo Application Demo ===\n");

        // ========== UserService Demo ==========
        System.out.println("--- UserService Demo ---");

        // Create users
        User user1 = new User();
        user1.setEmail("  John.Doe@Example.COM  ");
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setPassword("secret123");

        User added1 = userService.addUser(user1);
        System.out.println("1. Added user: " + added1);
        System.out.println("   Normalized email: " + added1.getEmail());

        User user2 = new User();
        user2.setEmail("jane.smith@example.com");
        user2.setFirstName("Jane");
        user2.setLastName("Smith");
        user2.setPassword("pass456");

        User added2 = userService.addUser(user2);
        System.out.println("2. Added user: " + added2);

        // Try to add duplicate email
        User duplicate = new User();
        duplicate.setEmail("john.doe@example.com");
        duplicate.setFirstName("Duplicate");
        User duplicateResult = userService.addUser(duplicate);
        System.out.println("3. Adding duplicate email (should be null): " + duplicateResult);

        // Update user
        User updateUser = new User();
        updateUser.setEmail("JOHN.DOE@EXAMPLE.COM");
        updateUser.setFirstName("Johnny");
        updateUser.setLastName("Doe Updated");
        User updated = userService.updateUser(updateUser);
        System.out.println("4. Updated user: " + updated);

        // Get all users
        List<User> allUsers = userService.getAll();
        System.out.println("5. Total users: " + allUsers.size());
        allUsers.forEach(u -> System.out.println("   - " + u));

        // ========== ToDoService Demo ==========
        System.out.println("\n--- ToDoService Demo ---");

        // Create ToDo lists for John
        ToDo todo1 = new ToDo();
        todo1.setTitle("  Work Tasks  ");
        ToDo addedTodo1 = toDoService.addTodo(added1, todo1);
        System.out.println("1. Created ToDo for " + added1.getFirstName() + ": " + addedTodo1);

        ToDo todo2 = new ToDo();
        todo2.setTitle("Personal");
        ToDo addedTodo2 = toDoService.addTodo(added1, todo2);
        System.out.println("2. Created ToDo for " + added1.getFirstName() + ": " + addedTodo2);

        // Try to add duplicate title for same owner
        ToDo duplicateTodo = new ToDo();
        duplicateTodo.setTitle("  Work Tasks  ");
        ToDo duplicateTodoResult = toDoService.addTodo(added1, duplicateTodo);
        System.out.println("3. Adding duplicate ToDo title (should be null): " + duplicateTodoResult);

        // Create ToDo for Jane (same title as John's, but different owner - should work)
        ToDo todo3 = new ToDo();
        todo3.setTitle("Work Tasks");
        ToDo addedTodo3 = toDoService.addTodo(added2, todo3);
        System.out.println("4. Created ToDo for " + added2.getFirstName() + " with same title: " + addedTodo3);

        // Get ToDos by user
        List<ToDo> johnTodos = toDoService.getByUser(added1);
        System.out.println("5. John's ToDos (" + johnTodos.size() + "):");
        johnTodos.forEach(t -> System.out.println("   - " + t));

        List<ToDo> janeTodos = toDoService.getByUser(added2);
        System.out.println("6. Jane's ToDos (" + janeTodos.size() + "):");
        janeTodos.forEach(t -> System.out.println("   - " + t));

        // Get specific ToDo by user and title
        ToDo foundTodo = toDoService.getByUserTitle(added1, "Work Tasks");
        System.out.println("7. Found ToDo by title 'Work Tasks': " + foundTodo);

        // Update ToDo
        ToDo updateTodo = new ToDo();
        updateTodo.setOwner(added1);
        updateTodo.setTitle("Personal");
        ToDo updatedTodo = toDoService.updateTodo(updateTodo);
        System.out.println("8. Updated ToDo: " + updatedTodo);

        // ========== TaskService Demo ==========
        System.out.println("\n--- TaskService Demo ---");

        // Add tasks to John's Work ToDo
        Task task1 = new Task("Review pull requests", Priority.HIGH);
        Task addedTask1 = taskService.addTask(task1, addedTodo1);
        System.out.println("1. Added task to 'Work Tasks': " + addedTask1);

        Task task2 = new Task("Update documentation", Priority.MEDIUM);
        Task addedTask2 = taskService.addTask(task2, addedTodo1);
        System.out.println("2. Added task to 'Work Tasks': " + addedTask2);

        Task task3 = new Task("Fix bug #123", Priority.HIGH);
        Task addedTask3 = taskService.addTask(task3, addedTodo1);
        System.out.println("3. Added task to 'Work Tasks': " + addedTask3);

        // Try to add duplicate task name in same ToDo
        Task duplicateTask = new Task("Review pull requests", Priority.LOW);
        Task duplicateTaskResult = taskService.addTask(duplicateTask, addedTodo1);
        System.out.println("4. Adding duplicate task name (should be null): " + duplicateTaskResult);

        // Add task to John's Personal ToDo
        Task task4 = new Task("Buy groceries", Priority.MEDIUM);
        Task addedTask4 = taskService.addTask(task4, addedTodo2);
        System.out.println("5. Added task to 'Personal': " + addedTask4);

        // Same task name in different ToDo (should work)
        Task task5 = new Task("Review pull requests", Priority.LOW);
        Task addedTask5 = taskService.addTask(task5, addedTodo3);
        System.out.println("6. Added same task name to Jane's ToDo: " + addedTask5);

        // Get tasks by ToDo
        List<Task> workTasks = taskService.getByToDo(addedTodo1);
        System.out.println("7. Tasks in John's 'Work Tasks' (" + workTasks.size() + "):");
        workTasks.forEach(t -> System.out.println("   - " + t));

        // Get task by ToDo and name
        Task foundTask = taskService.getByToDoName(addedTodo1, "Fix bug #123");
        System.out.println("8. Found task by name: " + foundTask);

        // Update task priority
        Task updateTask = new Task("Review pull requests", Priority.LOW);
        Task updatedTask = taskService.updateTask(addedTodo1, updateTask);
        System.out.println("9. Updated task priority: " + updatedTask);

        // Get task by user and name (searches across all user's ToDos)
        Task foundByUser = taskService.getByUserName(added1, "Buy groceries");
        System.out.println("10. Found task across all John's ToDos: " + foundByUser);

        // Get all tasks
        List<Task> allTasks = taskService.getAll();
        System.out.println("11. Total tasks in system: " + allTasks.size());

        // Delete task from specific ToDo
        taskService.deleteTask(addedTodo1, "Update documentation");
        List<Task> afterDelete = taskService.getByToDo(addedTodo1);
        System.out.println("12. Tasks in 'Work Tasks' after deletion (" + afterDelete.size() + "):");
        afterDelete.forEach(t -> System.out.println("   - " + t));

        // Delete ToDo
        toDoService.deleteTodo(addedTodo2);
        List<ToDo> afterTodoDelete = toDoService.getByUser(added1);
        System.out.println("13. John's ToDos after deleting 'Personal' (" + afterTodoDelete.size() + "):");
        afterTodoDelete.forEach(t -> System.out.println("   - " + t));

        // Delete user
        userService.deleteUser(added2);
        List<User> finalUsers = userService.getAll();
        System.out.println("14. Users after deleting Jane (" + finalUsers.size() + "):");
        finalUsers.forEach(u -> System.out.println("   - " + u));

        System.out.println("\n=== Demo Complete ===");
        System.out.println("All Spring IoC components working correctly!");

        context.close();
    }

}
