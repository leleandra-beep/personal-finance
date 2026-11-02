package com.finance.core.service;

import com.finance.core.exception.NotFoundException;
import com.finance.core.exception.ValidationException;
import com.finance.core.model.User;

import java.util.HashMap;
import java.util.Map;

public class AuthService {
    private final Map<String, User> users = new HashMap<>();

    public User register(String login, String password) {
        validate(login, password);
        if (users.containsKey(login)) {
            throw new ValidationException("Пользователь '" + login + "' уже существует");
        }
        User user = new User(login, password);
        users.put(login, user);
        return user;
    }

    public User login(String login, String password) {
        validate(login, password);
        User user = users.get(login);
        if (user == null) throw new NotFoundException("Пользователь не найден: " + login);
        if (!user.getPassword().equals(password)) throw new ValidationException("Неверный пароль");
        return user;
    }

    public User findByLogin(String login) {
        User u = users.get(login);
        if (u == null) throw new NotFoundException("Пользователь не найден: " + login);
        return u;
    }

    public Map<String, User> getAllUsers() { return users; }

    private void validate(String login, String password) {
        if (login == null || login.isBlank()) throw new ValidationException("Логин не может быть пустым");
        if (password == null || password.isBlank()) throw new ValidationException("Пароль не может быть пустым");
    }
}
