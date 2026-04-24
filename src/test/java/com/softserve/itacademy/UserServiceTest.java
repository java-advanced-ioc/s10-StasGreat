package com.softserve.itacademy;

import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.UserService;
import com.softserve.itacademy.service.impl.UserServiceImpl;
import org.junit.jupiter.api.*;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;
    private AnnotationConfigApplicationContext context;

    @BeforeEach
    void setUp() {

        context = new AnnotationConfigApplicationContext();
        context.register(UserServiceImpl.class);
        context.refresh();
        userService = context.getBean(UserService.class);
    }

    @AfterEach
    void tearDown() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    void addUser_shouldNormalizeEmail_andEnforceUniqueness() {
        User u1 = user(" John.Doe@Example.COM ");
        User added1 = userService.addUser(u1);
        assertNotNull(added1);
        assertEquals("john.doe@example.com", added1.getEmail());

        User u2 = user("john.doe@example.com");
        User added2 = userService.addUser(u2);
        assertNull(added2, "Duplicate email must be rejected");
    }

    @Test
    void addUser_shouldReturnNull_whenUserOrEmailInvalid() {
        assertNull(userService.addUser(null));
        assertNull(userService.addUser(user(null)));
        assertNull(userService.addUser(user("   ")));
    }
    @Test
    void updateUser_shouldSkipInvalidNames() {
        User base = user("a@ex.com");
        base.setFirstName("Valid");
        userService.addUser(base);

        User updated = user("a@ex.com");
        updated.setFirstName("   "); // пусте ім'я
        userService.updateUser(updated);

        assertEquals("Valid", userService.getAll().getFirst().getFirstName());
    }

    @Test
    void updateUser_shouldReplaceByEmail() {
        User base = user("a@ex.com");
        base.setFirstName("A");
        userService.addUser(base);

        User updated = user("A@EX.com");
        updated.setFirstName("AA");
        User res = userService.updateUser(updated);

        assertNotNull(res);
        assertEquals("AA", res.getFirstName());
        List<User> all = userService.getAll();
        assertEquals(1, all.size());
        assertEquals("AA", all.getFirst().getFirstName());
        assertEquals("a@ex.com", all.getFirst().getEmail());
    }

    @Test
    void updateUser_shouldReturnNull_whenNotFound_orInvalid() {
        assertNull(userService.updateUser(null));
        assertNull(userService.updateUser(user(null)));
        assertNull(userService.updateUser(user("   ")));
        assertNull(userService.updateUser(user("absent@example.com")));
    }

    @Test
    void deleteUser_shouldRemoveByEmail() {
        User u = user("x@y.com");
        userService.addUser(u);
        assertEquals(1, userService.getAll().size());
        userService.deleteUser(user("X@Y.COM"));
        Assertions.assertTrue(userService.getAll().isEmpty());
    }

    @Test
    void deleteUser_shouldNotFail_whenUserAbsent() {

        userService.addUser(user("present@ex.com"));
        assertEquals(1, userService.getAll().size());

        userService.deleteUser(user("absent@ex.com"));

        assertEquals(1, userService.getAll().size());
    }

    @Test
    void getAll_shouldReturnCopy() {
        userService.addUser(user("a@a.com"));
        List<User> all = userService.getAll();
        int sizeBefore = all.size();
        all.clear();
        assertEquals(sizeBefore, userService.getAll().size());
    }

    private static User user(String email) {
        User u = new User();
        u.setFirstName("John");
        u.setLastName("Doe");
        u.setEmail(email);
        return u;
    }
}
