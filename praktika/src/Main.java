import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

 class BankAccount {
    // ---- Поля класса ----
    private String ownerName;
    private int balance;
    private LocalDateTime openingDate;
    private boolean blocked;
    private String number; // номер счёта (8 цифр)

    // ---- Конструктор ----
    public BankAccount(String ownerName) {
        this.ownerName = ownerName;
        this.balance = 0;
        this.openingDate = LocalDateTime.now();
        this.blocked = false;
        this.number = generateAccountNumber();
    }

    // Генератор номера (8 цифр)
    private String generateAccountNumber() {
        Random random = new Random();
        int num = random.nextInt(100_000_000);
        return String.format("%08d", num);
    }

    // ---- Основные методы ----
    public boolean deposit(int amount) {
        if (blocked || amount <= 0) return false;
        balance += amount;
        return true;
    }

    public boolean withdraw(int amount) {
        if (blocked || amount <= 0 || balance < amount) return false;
        balance -= amount;
        return true;
    }

    public boolean transfer(BankAccount otherAccount, int amount) {
        if (blocked || amount <= 0 || balance < amount
                || otherAccount == null || otherAccount.isBlocked()) {
            return false;
        }
        balance -= amount;
        otherAccount.balance += amount;
        return true;
    }

    // ---- Геттеры / сеттеры ----
    public String getOwnerName() { return ownerName; }
    public int getBalance() { return balance; }
    public LocalDateTime getOpeningDate() { return openingDate; }
    public boolean isBlocked() { return blocked; }
    public String getNumber() { return number; }
    public void setBlocked(boolean blocked) { this.blocked = blocked; }

    @Override
    public String toString() {
        return String.format("№%s | %s | баланс: %d | %s | заблокирован: %s",
                number, ownerName, balance,
                openingDate.toLocalDate(), blocked ? "да" : "нет");
    }

    // ==================== ИНТЕРАКТИВНЫЙ MAIN ====================
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // Список всех счетов
        ArrayList<BankAccount> accounts = new ArrayList<>();

        // Для удобства сразу создадим пару тестовых счетов
        accounts.add(new BankAccount("Алиса"));
        accounts.add(new BankAccount("Боб"));

        System.out.println("=== БАНКОВСКАЯ СИСТЕМА ===");
        System.out.println("Уже созданы два счёта (Алиса и Боб).");

        boolean exit = false;
        while (!exit) {
            System.out.println("\n--- МЕНЮ ---");
            System.out.println("1. Создать новый счёт");
            System.out.println("2. Пополнить счёт");
            System.out.println("3. Снять со счёта");
            System.out.println("4. Перевести между счетами");
            System.out.println("5. Показать все счета");
            System.out.println("6. Заблокировать / разблокировать счёт");
            System.out.println("0. Выход");
            System.out.print("Ваш выбор: ");

            int choice = readInt(scanner);
            switch (choice) {
                case 1 -> createAccount(scanner, accounts);
                case 2 -> depositMenu(scanner, accounts);
                case 3 -> withdrawMenu(scanner, accounts);
                case 4 -> transferMenu(scanner, accounts);
                case 5 -> printAllAccounts(accounts);
                case 6 -> toggleBlockMenu(scanner, accounts);
                case 0 -> {
                    System.out.println("До свидания!");
                    exit = true;
                }
                default -> System.out.println("Неверный пункт. Попробуйте снова.");
            }
        }
        scanner.close();
    }

    // ---- Вспомогательные методы для работы с консолью ----

    // Безопасное чтение целого числа
    private static int readInt(Scanner scanner) {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Ошибка! Введите целое число: ");
            }
        }
    }

    // Вывод всех счетов с нумерацией
    private static void printAllAccounts(ArrayList<BankAccount> accounts) {
        if (accounts.isEmpty()) {
            System.out.println("Счетов пока нет.");
            return;
        }
        System.out.println("=== СПИСОК СЧЕТОВ ===");
        for (int i = 0; i < accounts.size(); i++) {
            System.out.println((i + 1) + ". " + accounts.get(i));
        }
    }

    // Выбор счёта по номеру из списка (возвращает объект или null)
    private static BankAccount selectAccount(Scanner scanner, ArrayList<BankAccount> accounts, String prompt) {
        if (accounts.isEmpty()) {
            System.out.println("Нет доступных счетов. Сначала создайте счёт (пункт 1).");
            return null;
        }
        printAllAccounts(accounts);
        System.out.print(prompt + " (введите номер): ");
        int index = readInt(scanner) - 1;
        if (index >= 0 && index < accounts.size()) {
            return accounts.get(index);
        } else {
            System.out.println("Неверный номер. Повторите выбор.");
            return null; // вызовем повторно в вызывающем методе
        }
    }

    // Создание нового счёта
    private static void createAccount(Scanner scanner, ArrayList<BankAccount> accounts) {
        System.out.print("Введите имя владельца: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Имя не может быть пустым.");
            return;
        }
        BankAccount newAcc = new BankAccount(name);
        accounts.add(newAcc);
        System.out.println("Счёт успешно создан!");
        System.out.println(newAcc);
    }

    // Пополнение
    private static void depositMenu(Scanner scanner, ArrayList<BankAccount> accounts) {
        BankAccount acc = selectAccount(scanner, accounts, "Выберите счёт для пополнения");
        if (acc == null) return;
        System.out.print("Введите сумму для пополнения: ");
        int amount = readInt(scanner);
        boolean result = acc.deposit(amount);
        System.out.println(result ? "✅ Пополнение выполнено." : "❌ Ошибка! Проверьте сумму или блокировку.");
        System.out.println("Текущее состояние: " + acc);
    }

    // Снятие
    private static void withdrawMenu(Scanner scanner, ArrayList<BankAccount> accounts) {
        BankAccount acc = selectAccount(scanner, accounts, "Выберите счёт для снятия");
        if (acc == null) return;
        System.out.print("Введите сумму для снятия: ");
        int amount = readInt(scanner);
        boolean result = acc.withdraw(amount);
        System.out.println(result ? "✅ Снятие выполнено." : "❌ Ошибка! Недостаточно средств, сумма <= 0 или счёт заблокирован.");
        System.out.println("Текущее состояние: " + acc);
    }

    // Перевод
    private static void transferMenu(Scanner scanner, ArrayList<BankAccount> accounts) {
        if (accounts.size() < 2) {
            System.out.println("Нужно как минимум два счёта для перевода.");
            return;
        }
        BankAccount from = selectAccount(scanner, accounts, "Выберите счёт ОТКУДА переводим");
        if (from == null) return;
        BankAccount to = selectAccount(scanner, accounts, "Выберите счёт КУДА переводим");
        if (to == null) return;
        if (from == to) {
            System.out.println("Нельзя перевести на тот же счёт.");
            return;
        }
        System.out.print("Введите сумму перевода: ");
        int amount = readInt(scanner);
        boolean result = from.transfer(to, amount);
        System.out.println(result ? "✅ Перевод выполнен." : "❌ Ошибка! Проверьте сумму, блокировку или наличие средств.");
        System.out.println("Состояние счетов после операции:");
        printAllAccounts(accounts);
    }

    // Блокировка / разблокировка
    private static void toggleBlockMenu(Scanner scanner, ArrayList<BankAccount> accounts) {
        BankAccount acc = selectAccount(scanner, accounts, "Выберите счёт для изменения блокировки");
        if (acc == null) return;
        System.out.print("Заблокировать (1) или разблокировать (0)? ");
        int action = readInt(scanner);
        if (action == 1) {
            acc.setBlocked(true);
            System.out.println("Счёт заблокирован.");
        } else if (action == 0) {
            acc.setBlocked(false);
            System.out.println("Счёт разблокирован.");
        } else {
            System.out.println("Неверный выбор.");
            return;
        }
        System.out.println("Текущее состояние: " + acc);
    }
}