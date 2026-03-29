import java.util.ArrayList;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

class InsufficientFundsException extends Exception {
    private double amount;

    public InsufficientFundsException(double amount) {
        super("Insufficient funds. Cannot withdraw: " + amount);
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }
}

class AccountNotFoundException extends Exception {
    public AccountNotFoundException(String accNo) {
        super("Account not found: " + accNo);
    }
}

abstract class Account {

    private String accNumber;
    private String holderName;
    protected double balance;
    private ArrayList<String> transactionHistory;

    public Account(String accNumber, String holderName, double balance) {
        this.accNumber = accNumber;
        this.holderName = holderName;
        this.balance = balance;
        this.transactionHistory = new ArrayList<String>();
        addTransaction("Account opened with balance: " + balance);
    }

    public String getAccNumber() { return accNumber; }
    public String getHolderName() { return holderName; }
    public double getBalance() { return balance; }
    public ArrayList<String> getTransactionHistory() { return transactionHistory; }

    protected void addTransaction(String note) {
        transactionHistory.add(note);
    }

    public void deposit(double amount) throws IllegalArgumentException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }
        balance += amount;
        addTransaction("Deposited: " + amount + " | Balance: " + balance);
        System.out.println("Deposit successful. New balance: " + balance);
    }

    public abstract void withdraw(double amount) throws InsufficientFundsException;

    public abstract String getType();

    public void printDetails() {
        System.out.println("---------------------------");
        System.out.println("Account No  : " + accNumber);
        System.out.println("Name        : " + holderName);
        System.out.println("Type        : " + getType());
        System.out.println("Balance     : " + balance);
        System.out.println("---------------------------");
    }

    public void printTransactionHistory() {
        System.out.println("Transaction history for " + accNumber + ":");
        if (transactionHistory.size()  ==0) {
            System.out.println("No transactions yet.");
            return;
        }
        for (int index = 0; index < transactionHistory.size(); index++) {
            System.out.println("  " + (index + 1) + ". " + transactionHistory.get(index));
        }
    }

    public String toFileString() {
        return getType() + "," + accNumber + "," + holderName + "," + balance;
    }
}

class SavingsAccount extends Account {

    private static final double MIN_BALANCE = 500.0;
    private static final double INTEREST_RATE = 0.04;

    public SavingsAccount(String accNumber, String holderName, double balance) {
        super(accNumber, holderName, balance);
    }

    @Override
    public void withdraw(double amount) throws InsufficientFundsException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }

        if (balance - amount < MIN_BALANCE) {
            throw new InsufficientFundsException(amount);
        }
        balance -= amount;
        addTransaction("Withdrawn: " + amount + " | Balance: " + balance);
        System.out.println("Withdrawn successfully. Balance: " + balance);
    }

    public void applyInterest() {
        double interest = balance * INTEREST_RATE;
        balance += interest;
        addTransaction("Interest applied: " + interest + " | Balance: " + balance);
        System.out.println("Interest of " + interest + " added. New balance: " + balance);
    }

    @Override
    public String getType() {
        return "Savings";
    }
}

class CurrentAccount extends Account {

    private double overdraftLimit;

    public CurrentAccount(String accNumber, String holderName, double balance, double overdraftLimit) {
        super(accNumber, holderName, balance);
        this.overdraftLimit = overdraftLimit;
    }

    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    @Override
    public void withdraw(double amount) throws InsufficientFundsException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }

        if (amount > balance + overdraftLimit) {
            throw new InsufficientFundsException(amount);
        }
        balance -= amount;
        addTransaction("Withdrawn: " + amount + " | Balance: " + balance);
        System.out.println("Withdrawn: " + amount + " | Balance: " + balance);
    }

    @Override
    public String getType() {
        return "Current";
    }

    @Override
    public String toFileString() {
        return getType() + "," + getAccNumber() + "," + getHolderName() + "," + balance + "," + overdraftLimit;
    }
}

class Bank {

    private String bankName;
    private ArrayList<Account> accountList;
    private int counter;
    private static final String FILE_NAME = "accounts.txt";

    public Bank(String bankName) {
        this.bankName = bankName;
        this.accountList = new ArrayList<Account>();
        this.counter = 100;
        loadAccountsFromFile();
    }

    private String generateAccNo() {
        counter++;
        return "ACC" + counter;
    }

    public void createSavings(String name, double amount) {
        if (amount < 500) {
            System.out.println("Minimum deposit for savings account is 500.");
            return;
        }
        String accNo = generateAccNo();
        SavingsAccount s = new SavingsAccount(accNo, name, amount);
        accountList.add(s);
        saveAccountsToFile();
        System.out.println("Savings account created. Account no: " + accNo);
    }

    public void createCurrent(String name, double amount, double overdraft) {
        if (amount < 1000) {
            System.out.println("Minimum deposit for current account is 1000.");
            return;
        }
        String accNo = generateAccNo();
        CurrentAccount c = new CurrentAccount(accNo, name, amount, overdraft);
        accountList.add(c);
        saveAccountsToFile();
        System.out.println("Current account created. Account no: " + accNo);
    }

    public Account findAccount(String accNo) throws AccountNotFoundException {
        for (int index = 0; index < accountList.size(); index++) {
            if (accountList.get(index).getAccNumber().equalsIgnoreCase(accNo)) {
                return accountList.get(index);
            }
        }
        throw new AccountNotFoundException(accNo);
    }

    public void showAllAccounts() {
        if (accountList.size()  ==0) {
            System.out.println("No accounts found.");
            return;
        }
        System.out.println("All Accounts:");
        System.out.println("----------------------------------------------------");
        for (int index = 0; index < accountList.size(); index++) {
            Account a = accountList.get(index);
            System.out.println((index + 1) + ". " + a.getAccNumber()
                    + " | " + a.getHolderName()
                    + " | " + a.getType()
                    + " | Balance: " + a.getBalance());
        }
        System.out.println("----------------------------------------------------");
    }

    public void deleteAccount(String accNo) throws AccountNotFoundException {
        Account a = findAccount(accNo);
        accountList.remove(a);
        saveAccountsToFile();
        System.out.println("Account " + accNo + " deleted successfully.");
    }

    public void saveAccountsToFile() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME));
            writer.write("counter=" + counter);
            writer.newLine();
            for (int index = 0; index < accountList.size(); index++) {
                writer.write(accountList.get(index).toFileString());
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving accounts: " + e.getMessage());
        }
    }

    public void loadAccountsFromFile() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME));
            String line = reader.readLine();

            if (line != null && line.startsWith("counter=")) {
                counter = Integer.parseInt(line.substring(8));
            }

            line = reader.readLine();
            while (line != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String type = parts[0];
                    String accNo = parts[1];
                    String name = parts[2];
                    double balance = Double.parseDouble(parts[3]);

                    if (type.equals("Savings")) {
                        accountList.add(new SavingsAccount(accNo, name, balance));
                    } else if (type.equals("Current") && parts.length  ==5) {
                        double overdraft = Double.parseDouble(parts[4]);
                        accountList.add(new CurrentAccount(accNo, name, balance, overdraft));
                    }
                }
                line = reader.readLine();
            }
            reader.close();
            System.out.println("Accounts loaded from file.");

        } catch (IOException e) {
            System.out.println("No saved data found. Starting fresh.");
        }
    }

    public int getTotalAccounts() {
        return accountList.size();
    }
}

public class Main {

    static Scanner sc = new Scanner(System.in);
    static Bank bank = new Bank("MyBank");

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("  Bank Account Management System");
        System.out.println("=================================");

        int choice = -1;

        while (choice != 0) {
            printMenu();

            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                continue;
            }

            switch (choice) {
                case 1: createSavingsAccount(); break;
                case 2: createCurrentAccount(); break;
                case 3: depositMoney(); break;
                case 4: withdrawMoney(); break;
                case 5: checkBalance(); break;
                case 6: bank.showAllAccounts(); break;
                case 7: applyInterest(); break;
                case 8: showTransactions(); break;
                case 9: deleteAccount(); break;
                case 0: System.out.println("Goodbye!"); break;
                default: System.out.println("Invalid choice. Try again.");
            }
        }
    }

    static void printMenu() {
        System.out.println("\size--- MENU ---");
        System.out.println("1. Create Savings Account");
        System.out.println("2. Create Current Account");
        System.out.println("3. Deposit");
        System.out.println("4. Withdraw");
        System.out.println("5. Check Balance");
        System.out.println("6. View All Accounts");
        System.out.println("7. Apply Interest (Savings only)");
        System.out.println("8. View Transaction History");
        System.out.println("9. Delete Account");
        System.out.println("0. Exit");
        System.out.print("Enter choice: ");
    }

    static void createSavingsAccount() {
        try {
            System.out.print("Enter name: ");
            String name = sc.nextLine().trim();
            System.out.print("Enter initial deposit: ");
            double amount = Double.parseDouble(sc.nextLine().trim());
            bank.createSavings(name, amount);
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount entered.");
        }
    }

    static void createCurrentAccount() {
        try {
            System.out.print("Enter name: ");
            String name = sc.nextLine().trim();
            System.out.print("Enter initial deposit: ");
            double amount = Double.parseDouble(sc.nextLine().trim());
            System.out.print("Enter overdraft limit: ");
            double od = Double.parseDouble(sc.nextLine().trim());
            bank.createCurrent(name, amount, od);
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount entered.");
        }
    }

    static void depositMoney() {
        try {
            System.out.print("Enter account number: ");
            String accNo = sc.nextLine().trim();
            Account a = bank.findAccount(accNo);
            System.out.print("Enter deposit amount: ");
            double amount = Double.parseDouble(sc.nextLine().trim());
            a.deposit(amount);
            bank.saveAccountsToFile();
        } catch (AccountNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    static void withdrawMoney() {
        try {
            System.out.print("Enter account number: ");
            String accNo = sc.nextLine().trim();
            Account a = bank.findAccount(accNo);
            System.out.print("Enter withdrawal amount: ");
            double amount = Double.parseDouble(sc.nextLine().trim());
            a.withdraw(amount);
            bank.saveAccountsToFile();
        } catch (AccountNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (InsufficientFundsException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    static void checkBalance() {
        try {
            System.out.print("Enter account number: ");
            String accNo = sc.nextLine().trim();
            Account a = bank.findAccount(accNo);
            a.printDetails();
        } catch (AccountNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    static void applyInterest() {
        try {
            System.out.print("Enter account number: ");
            String accNo = sc.nextLine().trim();
            Account a = bank.findAccount(accNo);
            if (a instanceof SavingsAccount) {
                SavingsAccount sa = (SavingsAccount) a;
                sa.applyInterest();
                bank.saveAccountsToFile();
            } else {
                System.out.println("Interest can only be applied to savings accounts.");
            }
        } catch (AccountNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    static void showTransactions() {
        try {
            System.out.print("Enter account number: ");
            String accNo = sc.nextLine().trim();
            Account a = bank.findAccount(accNo);
            a.printTransactionHistory();
        } catch (AccountNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    static void deleteAccount() {
        try {
            System.out.print("Enter account number: ");
            String accNo = sc.nextLine().trim();
            System.out.print("Are you sure? (yes/no): ");
            String confirm = sc.nextLine().trim();
            if (confirm.equalsIgnoreCase("yes")) {
                bank.deleteAccount(accNo);
            } else {
                System.out.println("Deletion cancelled.");
            }
        } catch (AccountNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
