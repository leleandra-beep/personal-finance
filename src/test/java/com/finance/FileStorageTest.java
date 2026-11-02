package com.finance;

import com.finance.core.model.User;
import com.finance.core.service.FinanceService;
import com.finance.infra.FileStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FileStorageTest {
    @Test void saveAndLoad(@TempDir Path dir) {
        Path file = dir.resolve("w.txt");
        FileStorage fs = new FileStorage(file);

        User u = new User("u1", "p1");
        FinanceService f = new FinanceService();
        f.addIncome(u, "salary", 500);
        f.addExpense(u, "food", 200);
        f.setBudget(u, "food", 1000);

        Map<String, User> map = new HashMap<>();
        map.put("u1", u);
        fs.save(map);

        Map<String, User> loaded = new FileStorage(file).load();
        User l = loaded.get("u1");
        assertNotNull(l);
        assertEquals(500, l.getWallet().totalIncome());
        assertEquals(200, l.getWallet().totalExpense());
        assertEquals(1000, l.getWallet().getBudgets().get("food").getLimit());
    }
    @Test void loadMissingFile(@TempDir Path dir) {
        FileStorage fs = new FileStorage(dir.resolve("missing.txt"));
        assertTrue(fs.load().isEmpty());
    }
}
