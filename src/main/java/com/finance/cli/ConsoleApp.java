package com.finance.cli;

import com.finance.core.exception.NotFoundException;
import com.finance.core.exception.ValidationException;
import com.finance.core.model.User;
import com.finance.core.service.AuthService;
import com.finance.core.service.FinanceService;
import com.finance.core.service.StatisticsService;
import com.finance.infra.FileStorage;
import com.finance.infra.Storage;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ConsoleApp {
    private final AuthService auth = new AuthService();
    private final FinanceService finance = new FinanceService();
    private final StatisticsService stats = new StatisticsService();
    private final Storage storage;
    private final Scanner scanner = new Scanner(System.in);
    private User currentUser;

    public ConsoleApp(Storage storage) { this.storage = storage; }

    public void run() {
        // подгружаем существующих пользователей
        auth.getAllUsers().putAll(storage.load());
        System.out.println("=== Personal Finance CLI ===");
        while (true) {
            if (currentUser == null) {
                if (!authMenu()) { shutdown(); return; }
            } else {
                if (!userMenu()) { shutdown(); return; }
            }
        }
    }

    private boolean authMenu() {
        System.out.println("\n[1] Войти  [2] Регистрация  [3] Выход");
        System.out.print("> ");
        String cmd = scanner.nextLine().trim();
        try {
            switch (cmd) {
                case "1" -> {
                    System.out.print("Логин: ");
                    String l = scanner.nextLine();
                    System.out.print("Пароль: ");
                    String p = scanner.nextLine();
                    currentUser = auth.login(l, p);
                    System.out.println("Добро пожаловать, " + l + "!");
                }
                case "2" -> {
                    System.out.print("Логин: ");
                    String l = scanner.nextLine();
                    System.out.print("Пароль: ");
                    String p = scanner.nextLine();
                    currentUser = auth.register(l, p);
                    System.out.println("Зарегистрирован пользователь " + l);
                }
                case "3" -> { return false; }
                default -> System.out.println("Неизвестная команда");
            }
        } catch (ValidationException | NotFoundException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
        return true;
    }

    private boolean userMenu() {
        printHelp();
        System.out.print("> ");
        String line = scanner.nextLine().trim();
        if (line.isEmpty()) return true;
        String[] parts = line.split("\\s+");
        try {
            switch (parts[0].toLowerCase()) {
                case "help" -> printHelp();
                case "income" -> {
                    require(parts.length >= 3);
                    finance.addIncome(currentUser, parts[1], Double.parseDouble(parts[2]));
                    System.out.println("Доход добавлен.");
                }
                case "expense" -> {
                    require(parts.length >= 3);
                    finance.addExpense(currentUser, parts[1], Double.parseDouble(parts[2]));
                    System.out.println("Расход добавлен.");
                }
                case "budget" -> {
                    require(parts.length >= 3);
                    finance.setBudget(currentUser, parts[1], Double.parseDouble(parts[2]));
                    System.out.println("Бюджет установлен.");
                }
                case "stats" -> stats.printSummary(currentUser.getWallet());
                case "sum-expense" -> {
                    require(parts.length >= 2);
                    List<String> cats = Arrays.asList(parts).subList(1, parts.length);
                    System.out.printf("Сумма расходов: %.2f%n",
                            stats.expenseForCategories(currentUser.getWallet(), cats));
                }
                case "sum-income" -> {
                    require(parts.length >= 2);
                    List<String> cats = Arrays.asList(parts).subList(1, parts.length);
                    System.out.printf("Сумма доходов: %.2f%n",
                            stats.incomeForCategories(currentUser.getWallet(), cats));
                }
                case "transfer" -> {
                    require(parts.length >= 3);
                    finance.transfer(currentUser, parts[1], Double.parseDouble(parts[2]), auth,
                            parts.length >= 4 ? parts[3] : null);
                    System.out.println("Перевод выполнен.");
                }
                case "export" -> {
                    require(parts.length >= 2);
                    storage.save(auth.getAllUsers());
                    System.out.println("Снимок сохранён в " + parts[1]);
                }
                case "logout" -> {
                    storage.save(auth.getAllUsers());
                    currentUser = null;
                    System.out.println("Выход. Данные сохранены.");
                }
                case "exit", "quit" -> { return false; }
                default -> System.out.println("Неизвестная команда. Введите help");
            }
        } catch (ValidationException | NotFoundException e) {
            System.out.println("Ошибка: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: сумма должна быть числом");
        } catch (Exception e) {
            System.out.println("Ошибка выполнения: " + e.getMessage());
        }
        return true;
    }

    private void require(boolean cond) {
        if (!cond) throw new ValidationException("Недостаточно аргументов");
    }

    private void shutdown() {
        storage.save(auth.getAllUsers());
        System.out.println("Данные сохранены. До свидания!");
    }

    private void printHelp() {
        System.out.println("""
            Команды:
              income <категория> <сумма>       добавить доход
              expense <категория> <сумма>      добавить расход
              budget <категория> <лимит>       установить/изменить бюджет
              stats                            сводная статистика
              sum-expense <cat1> [cat2 ...]    сумма расходов по категориям
              sum-income <cat1> [cat2 ...]     сумма доходов по категориям
              transfer <логин> <сумма> [cat]   перевод другому пользователю
              export <файл>                    выгрузить снимок
              logout                           выйти из аккаунта
              exit                             выход из приложения
            """);
    }
}
