package com.finance.infra;

import com.finance.core.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class FileStorage implements Storage {
    private final Path path;

    public FileStorage(Path path) { this.path = path; }
    public FileStorage(String path) { this(Path.of(path)); }

    @Override
    public void save(Map<String, User> users) {
        try (BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            for (User u : users.values()) {
                bw.write("USER\t" + u.getLogin() + "\t" + u.getPassword());
                bw.newLine();
                for (Transaction t : u.getWallet().getTransactions()) {
                    bw.write(String.join("\t",
                            "TX", u.getLogin(), t.getType().name(),
                            t.getCategory(), String.valueOf(t.getAmount()),
                            t.getDate().toString()));
                    bw.newLine();
                }
                for (Budget b : u.getWallet().getBudgets().values()) {
                    bw.write("BUDGET\t" + u.getLogin() + "\t" + b.getCategory() + "\t" + b.getLimit());
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Ошибка сохранения данных", e);
        }
    }

    @Override
    public Map<String, User> load() {
        Map<String, User> users = new HashMap<>();
        if (!Files.exists(path)) return users;
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.split("\t");
                switch (p[0]) {
                    case "USER" -> users.put(p[1], new User(p[1], p[2]));
                    case "TX" -> {
                        User u = users.get(p[1]);
                        if (u != null) u.getWallet().addTransaction(new Transaction(
                                TransactionType.valueOf(p[2]), p[3],
                                Double.parseDouble(p[4]), LocalDateTime.parse(p[5])));
                    }
                    case "BUDGET" -> {
                        User u = users.get(p[1]);
                        if (u != null) u.getWallet().setBudget(p[2], Double.parseDouble(p[3]));
                    }
                    default -> { /* неизвестная строка — игнорируем */ }
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Ошибка загрузки данных", e);
        }
        return users;
    }
}
