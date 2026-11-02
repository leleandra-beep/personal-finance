package com.finance;

import com.finance.core.exception.NotFoundException;
import com.finance.core.exception.ValidationException;
import com.finance.core.service.AuthService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {
    @Test void registerAndLogin() {
        AuthService a = new AuthService();
        a.register("alice", "pwd");
        assertEquals("alice", a.login("alice", "pwd").getLogin());
    }
    @Test void duplicateUser() {
        AuthService a = new AuthService();
        a.register("bob", "p");
        assertThrows(ValidationException.class, () -> a.register("bob", "p"));
    }
    @Test void wrongPassword() {
        AuthService a = new AuthService();
        a.register("c", "p");
        assertThrows(ValidationException.class, () -> a.login("c", "x"));
    }
    @Test void unknownUser() {
        AuthService a = new AuthService();
        assertThrows(NotFoundException.class, () -> a.login("z", "p"));
    }
    @Test void emptyLogin() {
        AuthService a = new AuthService();
        assertThrows(ValidationException.class, () -> a.register("", "p"));
    }
}
