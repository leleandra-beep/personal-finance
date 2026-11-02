package com.finance.core.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Transaction {
    private final TransactionType type;
    private final String category;
    private final double amount;
    private final LocalDateTime date;

    public Transaction(TransactionType type, String category, double amount) {
        this(type, category, amount, LocalDateTime.now());
    }

    public Transaction(TransactionType type, String category, double amount, LocalDateTime date) {
        this.type = Objects.requireNonNull(type, "type");
        this.category = Objects.requireNonNull(category, "category");
        this.amount = amount;
        this.date = Objects.requireNonNull(date, "date");
    }

    public TransactionType getType() { return type; }
    public String getCategory() { return category; }
    public double getAmount() { return amount; }
    public LocalDateTime getDate() { return date; }
}