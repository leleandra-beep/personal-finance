package com.finance.core.service;

import com.finance.core.exception.ValidationException;
import com.finance.core.model.Transaction;
import com.finance.core.model.TransactionType;
import com.finance.core.model.User;
import com.finance.core.model.Wallet;

import java.util.List;

public class FinanceService {

    public void addIncome(User user, String category, double amount) {
        add(user, TransactionType.INCOME, category, amount);
    }

    public void addExpense(User user, String category, double amount) {
        add(user, TransactionType.EXPENSE, category, amount);
        checkExpenseWarnings(user, category);
    }

    public void setBudget(User user, String category, double limit) {
        if (category == null || category.isBlank()) throw new ValidationException("Категория не может быть пустой");
        if (limit < 0) throw new ValidationException("Бюджет не может быть отрицательным");
        user.getWallet().setBudget(category, limit);
    }

    public void transfer(User from, String toLogin, double amount, AuthService auth, String category) {
        if (amount <= 0) throw new ValidationException("Сумма перевода должна быть > 0");
        User to = auth.findByLogin(toLogin);
        if (from == to) throw new ValidationException("Нельзя переводить самому себе");
        if (from.getWallet().balance() < amount)
            throw new ValidationException("Недостаточно средств: баланс " + from.getWallet().balance());
        String cat = (category == null || category.isBlank()) ? "Перевод" : category;
        from.getWallet().addTransaction(new Transaction(TransactionType.EXPENSE, cat + " -> " + toLogin, amount));
        to.getWallet().addTransaction(new Transaction(TransactionType.INCOME, cat + " <- " + from.getLogin(), amount));
    }

    private void add(User user, TransactionType type, String category, double amount) {
        if (category == null || category.isBlank()) throw new ValidationException("Категория не может быть пустой");
        if (amount <= 0) throw new ValidationException("Сумма должна быть положительной");
        user.getWallet().addTransaction(new Transaction(type, category, amount));
    }

    private void checkExpenseWarnings(User user, String category) {
        Wallet w = user.getWallet();
        var budget = w.getBudgets().get(category);
        double spent = w.expenseByCategory(category);
        if (budget != null && budget.getLimit() > 0) {
            double remaining = budget.getLimit() - spent;
            if (remaining < 0) {
                System.out.printf("⚠ ПРЕВЫШЕНИЕ бюджета категории '%s' на %.2f (лимит %.2f, расход %.2f)%n",
                        category, -remaining, budget.getLimit(), spent);
            } else if (spent >= budget.getLimit() * 0.8) {
                System.out.printf("⚠ Использовано %.0f%% бюджета категории '%s' (остаток %.2f)%n",
                        (spent / budget.getLimit()) * 100, category, remaining);
            }
        }
        if (w.totalExpense() > w.totalIncome()) {
            System.out.println("⚠ Внимание: расходы превысили доходы! Баланс: " + w.balance());
        }
    }
}
