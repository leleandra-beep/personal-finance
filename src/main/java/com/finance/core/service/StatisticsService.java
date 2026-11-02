package com.finance.core.service;

import com.finance.core.exception.ValidationException;
import com.finance.core.model.Budget;
import com.finance.core.model.Transaction;
import com.finance.core.model.TransactionType;
import com.finance.core.model.Wallet;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class StatisticsService {

    public void printSummary(Wallet w) {
        System.out.printf("Общий доход: %.2f%n", w.totalIncome());
        System.out.printf("Общие расходы: %.2f%n", w.totalExpense());
        System.out.printf("Текущий баланс: %.2f%n", w.balance());
        System.out.println();
        System.out.println("Доходы по категориям:");
        Map<String, Double> inc = new TreeMap<>();
        for (Transaction t : w.getTransactions())
            if (t.getType() == TransactionType.INCOME)
                inc.merge(t.getCategory(), t.getAmount(), Double::sum);
        inc.forEach((k, v) -> System.out.printf("  %s: %.2f%n", k, v));

        System.out.println();
        System.out.println("Расходы по категориям:");
        Map<String, Double> exp = new TreeMap<>();
        for (Transaction t : w.getTransactions())
            if (t.getType() == TransactionType.EXPENSE)
                exp.merge(t.getCategory(), t.getAmount(), Double::sum);
        exp.forEach((k, v) -> System.out.printf("  %s: %.2f%n", k, v));

        System.out.println();
        System.out.println("Бюджет по категориям:");
        for (Budget b : w.getBudgets().values()) {
            double spent = w.expenseByCategory(b.getCategory());
            double rem = b.getLimit() - spent;
            System.out.printf("  %s: %.2f, Оставшийся бюджет: %.2f%n",
                    b.getCategory(), b.getLimit(), rem);
        }
    }

    public double expenseForCategories(Wallet w, List<String> categories) {
        double sum = 0;
        for (String c : categories) {
            boolean found = w.getTransactions().stream()
                    .anyMatch(t -> t.getCategory().equalsIgnoreCase(c));
            if (!found) throw new ValidationException("Категория не найдена: " + c);
            sum += w.expenseByCategory(c);
        }
        return sum;
    }

    public double incomeForCategories(Wallet w, List<String> categories) {
        double sum = 0;
        for (String c : categories) {
            boolean found = w.getTransactions().stream()
                    .anyMatch(t -> t.getCategory().equalsIgnoreCase(c));
            if (!found) throw new ValidationException("Категория не найдена: " + c);
            sum += w.incomeByCategory(c);
        }
        return sum;
    }
}
