 
    import java.util.ArrayList;
import java.util.Scanner;
public class atm {
     public static void main(String[] args) {
        Bank bank = new Bank();

        // Adding some initial users for testing
        bank.addUser(new User("user123", "pin123", 300.0));
        bank.addUser(new User("user456", "pin456", 100.0));

        UserInterface userInterface = new UserInterface(bank);
        userInterface.start();
    }
}

class Bank {
    private ArrayList<User> users;

    public Bank() {
        users = new ArrayList<>();
    }

    public User getUser(String userId, String pin) {
        for (User user : users) {
            if (user.getUserId().equals(userId) && user.getPin().equals(pin)) {
                return user;
            }
        }
        return null;
    }

    // ✅ New method: find user by ID (for transfer)
    public User getUserById(String userId) {
        for (User user : users) {
            if (user.getUserId().equals(userId)) {
                return user;
            }
        }
        return null;
    }

    public boolean addUser(User user) {
        for (User existingUser : users) {
            if (existingUser.getUserId().equals(user.getUserId())) {
                return false;
            }
        }
        users.add(user);
        return true;
    }

    public void withdraw(User user, double amount) {
        user.withdraw(amount);
    }

    public void deposit(User user, double amount) {
        user.deposit(amount);
    }

    public void transfer(User fromUser, User toUser, double amount) {
        if (fromUser.getBalance() >= amount) {
            fromUser.withdraw(amount);
            toUser.deposit(amount);
            fromUser.addTransaction("Transfer to " + toUser.getUserId() + ": -$" + amount);
            toUser.addTransaction("Transfer from " + fromUser.getUserId() + ": +$" + amount);
        } else {
            System.out.println("Insufficient balance for transfer.");
        }
    }
}

class User {
    private String userId;
    private String pin;
    private double balance;
    private ArrayList<String> transactions;

    public User(String userId, String pin, double balance) {
        this.userId = userId;
        this.pin = pin;
        this.balance = balance;
        this.transactions = new ArrayList<>();
    }

    public String getUserId() {
        return userId;
    }

    public String getPin() {
        return pin;
    }

    public double getBalance() {
        return balance;
    }

    public ArrayList<String> getTransactions() {
        return transactions;
    }

    public void withdraw(double amount) {
        if (amount <= balance) {
            balance -= amount;
            transactions.add("Withdrawal: -$" + amount);
            System.out.println("Successfully withdrew $" + amount);
        } else {
            System.out.println("Insufficient balance");
        }
    }

    public void deposit(double amount) {
        balance += amount;
        transactions.add("Deposit: $" + amount);
        System.out.println("Successfully deposited $" + amount);
    }

    public void addTransaction(String transaction) {
        transactions.add(transaction);
    }
}

class UserInterface {
    private Bank bank;
    private Scanner scanner;

    public UserInterface(Bank bank) {
        this.bank = bank;
        scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            System.out.println("\n--- Welcome to the ATM system! ---");
            System.out.print("Enter your user ID: ");
            String userId = scanner.nextLine();
            System.out.print("Enter your PIN: ");
            String pin = scanner.nextLine();

            User user = bank.getUser(userId, pin);
            if (user == null) {
                System.out.println("Invalid user ID or PIN. Try again.\n");
                continue;
            }

            while (true) {
                System.out.println("\n--- ATM Menu ---");
                System.out.println("1. Transactions History");
                System.out.println("2. Withdraw");
                System.out.println("3. Deposit");
                System.out.println("4. Transfer");
                System.out.println("5. Check Balance");
                System.out.println("6. Quit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline

                switch (choice) {
                    case 1:
                        displayTransactions(user);
                        break;
                    case 2:
                        withdraw(user);
                        break;
                    case 3:
                        deposit(user);
                        break;
                    case 4:
                        transfer(user);
                        break;
                    case 5:
                        checkBalance(user);
                        break;
                    case 6:
                        System.out.println("Logged out. Thank you!\n");
                        return; // exits to login screen
                    default:
                        System.out.println("Invalid choice");
                }
            }
        }
    }

    private void displayTransactions(User user) {
        System.out.println("\n--- Transactions History ---");
        if (user.getTransactions().isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            for (String transaction : user.getTransactions()) {
                System.out.println(transaction);
            }
        }
    }

    private void withdraw(User user) {
        System.out.print("Enter the amount to withdraw: ");
        double amount = scanner.nextDouble();
        bank.withdraw(user, amount);
    }

    private void deposit(User user) {
        System.out.print("Enter the amount to deposit: ");
        double amount = scanner.nextDouble();
        bank.deposit(user, amount);
    }

    private void transfer(User user) {
        System.out.print("Enter the recipient's user ID: ");
        String recipientUserId = scanner.nextLine();
        User recipientUser = bank.getUserById(recipientUserId); // ✅ fixed

        if (recipientUser == null) {
            System.out.println("Recipient user not found");
            return;
        }

        System.out.print("Enter the amount to transfer: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        bank.transfer(user, recipientUser, amount);
    }

    private void checkBalance(User user) {
        System.out.println("Your current balance is: $" + user.getBalance());
    }
}
