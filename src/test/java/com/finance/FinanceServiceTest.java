package com.finance;

import com.finance.core.exception.ValidationException;
import com.finance.core.model.User;
import com.finance.core.service.AuthService;
import com.finance.core.service.FinanceService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FinanceServiceTest {
    @Test void incomeAndExpense() {
        User u = new User("u", "p");
        FinanceService f = new FinanceService();
        f.addIncome(u, "salary", 1000);
        f.addExpense(u, "food", 300);
        assertEquals(1000, u.getWallet().totalIncome());
        assertEquals(300, u.getWallet().totalExpense());
        assertEquals(700, u.getWallet().balance());
    }
    @Test void negativeAmount() {
        User u = new User("u", "p");
        FinanceService f = new FinanceService();
        assertThrows(ValidationException.class, () -> f.addExpense(u, "food", -1));
    }
    @Test void budgetSet() {
        User u = new User("u", "p");
        FinanceService f = new FinanceService();
        f.setBudget(u, "food", 4000);
        assertEquals(4000, u.getWallet().getBudgets().get("food").getLimit());
    }
    @Test void transfer() {
        AuthService auth = new AuthService();
        User a = auth.register("a", "p");
        User b = auth.register("b", "p");
        FinanceService f = new FinanceService();
        f.addIncome(a, "salary", 1000);
        f.transfer(a, "b", 400, auth, null);
        assertEquals(600, a.getWallet().balance());
        assertEquals(400, b.getWallet().balance());
    }
    @Test void transferTooMuch() {
        AuthService auth = new AuthService();
        User a = auth.register("a", "p");
        auth.register("b", "p");
        FinanceService f = new FinanceService();
        assertThrows(ValidationException.class, () -> f.transfer(a, "b", 100, auth, null));
    }
    @Test void emptyCategory() {
        User u = new User("u", "p");
        FinanceService f = new FinanceService();
        assertThrows(ValidationException.class, () -> f.addIncome(u, "", 10));
    }
}
