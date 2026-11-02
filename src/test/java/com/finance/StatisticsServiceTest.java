package com.finance;

import com.finance.core.exception.ValidationException;
import com.finance.core.model.User;
import com.finance.core.service.FinanceService;
import com.finance.core.service.StatisticsService;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class StatisticsServiceTest {
    @Test void sumByCategories() {
        User u = new User("u", "p");
        FinanceService f = new FinanceService();
        f.addExpense(u, "food", 300);
        f.addExpense(u, "food", 500);
        f.addExpense(u, "fun", 1000);
        StatisticsService s = new StatisticsService();
        assertEquals(1800, s.expenseForCategories(u.getWallet(), List.of("food", "fun")));
    }
    @Test void unknownCategoryThrows() {
        User u = new User("u", "p");
        FinanceService f = new FinanceService();
        f.addExpense(u, "food", 300);
        StatisticsService s = new StatisticsService();
        assertThrows(ValidationException.class,
                () -> s.expenseForCategories(u.getWallet(), List.of("food", "none")));
    }
    @Test void incomeByCategories() {
        User u = new User("u", "p");
        FinanceService f = new FinanceService();
        f.addIncome(u, "salary", 1000);
        f.addIncome(u, "salary", 2000);
        StatisticsService s = new StatisticsService();
        assertEquals(3000, s.incomeForCategories(u.getWallet(), List.of("salary")));
    }
}
