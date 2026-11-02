package com.finance.core.model;

import java.util.Objects;

public class User {
    private final String login;
    private final String password;
    private final Wallet wallet = new Wallet();

    public User(String login, String password) {
        this.login = Objects.requireNonNull(login);
        this.password = Objects.requireNonNull(password);
    }

    public String getLogin() { return login; }
    public String getPassword() { return password; }
    public Wallet getWallet() { return wallet; }
}