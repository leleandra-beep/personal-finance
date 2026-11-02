package com.finance.core.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Wallet {
    private final List<Transaction> transactions = new ArrayList<>();
    private final Map<String, Budget> budgets = new LinkedHashMap<>();

    public void addTransaction(Transaction t) { transactions.add(t); }
    public List<Transaction> getTransactions() { return transactions; }
    public Map<String, Budget> getBudgets() { return budgets; }

    public void setBudget(String category, double limit) {
        budgets.put(category, new Budget(category, limit));
    }

    public double totalIncome() {
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .mapToDouble(Transaction::getAmount).sum();
    }

    public double totalExpense() {
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .mapToDouble(Transaction::getAmount).sum();
    }

    public double balance() { return totalIncome() - totalExpense(); }

    public double expenseByCategory(String category) {
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE
                        && t.getCategory().equalsIgnoreCase(category))
                .mapToDouble(Transaction::getAmount).sum();
    }

    public double incomeByCategory(String category) {
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME
                        && t.getCategory().equalsIgnoreCase(category))
                .mapToDouble(Transaction::getAmount).sum();
    }
}