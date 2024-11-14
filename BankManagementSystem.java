import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

class Account {
    private String name;
    private String email;
    private String address;
    private String accountType; // Current or Savings
    private int loanCount = 0;
    private ArrayList<String> statement = new ArrayList<>();
    private double balance = 0;
    private long accountNumber;

    public Account(String name, String email, String address, String accountType) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.accountType = accountType;
        this.accountNumber = new Random().nextLong(1000000000L, 9999999999L);
    }

    public String deposit(double amount) {
        if (amount <= 0) {
            return "Invalid deposit amount.";
        }
        balance += amount;
        statement.add("Deposited: " + amount);
        return amount + " deposited successfully.";
    }

    public String withdraw(double amount) {
        if (amount > balance) {
            return "Insufficient balance.";
        } else if (amount <= 0) {
            return "Invalid withdrawal amount.";
        }
        balance -= amount;
        statement.add("Withdrawn: " + amount);
        return amount + " withdrawn successfully.";
    }

    public String checkBalance() {
        return "Your balance is " + balance;
    }

    public ArrayList<String> getTransactionHistory() {
        return statement;
    }

    public String takeLoan(double amount) {
        if (loanCount < 2) {
            balance += amount;
            loanCount++;
            statement.add("Loan Taken: " + amount);
            return "Loan of " + amount + " approved.";
        } else {
            return "Loan limit exceeded.";
        }
    }

    public String transferAmount(double amount, Account receiverAccount) {
        if (amount > balance) {
            return "Insufficient balance.";
        } else if (amount <= 0) {
            return "Invalid transfer amount.";
        }
        receiverAccount.deposit(amount);
        balance -= amount;
        statement.add("Transferred " + amount + " to account " + receiverAccount.getAccountNumber());
        return "Successfully transferred " + amount + ".";
    }

    public long getAccountNumber() {
        return accountNumber;
    }

    @Override
    public String toString() {
        return "Account Name: " + name +
               "\nEmail: " + email +
               "\nAddress: " + address +
               "\nAccount Type: " + accountType +
               "\nBalance: " + balance +
               "\nAccount Number: " + accountNumber +
               "\nLoan Count: " + loanCount +
               "\nStatement: " + statement;
    }
}

class Admin {
    private ArrayList<Account> accounts = new ArrayList<>();
    private double totalBalance = 0;
    private double loanAmount = 0;
    private boolean loanFeature = true;

    public String createAccount(String name, String email, String address, String accountType) {
        Account account = new Account(name, email, address, accountType);
        accounts.add(account);
        return "Account Name: " + name + "\nAccount Number: " + account.getAccountNumber();
    }

    public String deleteAccount(long accountNumber) {
        for (Account account : accounts) {
            if (account.getAccountNumber() == accountNumber) {
                accounts.remove(account);
                return "Account " + accountNumber + " deleted successfully.";
            }
        }
        return "Account not found.";
    }

    public ArrayList<Account> getAllAccounts() {
        return accounts;
    }

    public String getTotalBalance() {
        totalBalance = accounts.stream().mapToDouble(Account::checkBalance).sum();
        return "Total bank balance: " + totalBalance;
    }

    public String getTotalLoan() {
        loanAmount = accounts.stream().mapToInt(Account::getLoanCount).sum();
        return "Total loan amount: " + loanAmount;
    }

    public String disableLoanFeature() {
        loanFeature = false;
        return "Loan feature disabled.";
    }
}

class Bank {
    private String name;
    private String adminPassword = "rakibul263";
    private boolean isAdminAuthenticated = false;
    private Admin adminInstance = new Admin();
    private Scanner scanner = new Scanner(System.in);

    public Bank(String name) {
        this.name = name;
    }

    public void showMenu() {
        while (true) {
            System.out.println("------------------------------------------");
            System.out.println("|         Welcome to " + name + "        |");
            System.out.println("------------------------------------------");
            System.out.println("|         1. Admin                       |");
            System.out.println("|         2. User                        |");
            System.out.println("|         3. Exit                        |");
            System.out.println("------------------------------------------");

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    adminMenu();
                    break;
                case "2":
                    userMenu();
                    break;
                case "3":
                    System.out.println("Exit");
                    return;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    private void adminMenu() {
        if (!isAdminAuthenticated) {
            System.out.print("Enter Admin Password: ");
            String password = scanner.nextLine();
            if (password.equals(adminPassword)) {
                isAdminAuthenticated = true;
            } else {
                System.out.println("Incorrect password! Returning to the main menu.");
                return;
            }
        }

        while (true) {
            System.out.println("---------------Admin Menu---------------");
            System.out.println("|         1. Create Account            |");
            System.out.println("|         2. Delete Account            |");
            System.out.println("|         3. View All Accounts         |");
            System.out.println("|         4. View Bank Balance         |");
            System.out.println("|         5. Main Menu                 |");
            System.out.println("|         6. Exit                      |");
            System.out.println("----------------------------------------");

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Enter Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter Email: ");
                    String email = scanner.nextLine();
                    System.out.print("Enter Address: ");
                    String address = scanner.nextLine();
                    System.out.print("Enter Account Type: ");
                    String accountType = scanner.nextLine();
                    System.out.println(adminInstance.createAccount(name, email, address, accountType));
                    break;
                case "2":
                    System.out.print("Enter Account Number: ");
                    long accountNumber = Long.parseLong(scanner.nextLine());
                    System.out.println(adminInstance.deleteAccount(accountNumber));
                    break;
                case "3":
                    for (Account account : adminInstance.getAllAccounts()) {
                        System.out.println(account);
                    }
                    break;
                case "4":
                    System.out.println(adminInstance.getTotalBalance());
                    break;
                case "5":
                    return;
                case "6":
                    System.out.println("Exit");
                    return;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    private void userMenu() {
        System.out.print("Enter your account number: ");
        long accountNumber = Long.parseLong(scanner.nextLine());
        Account userAccount = getAccountByNumber(accountNumber);

        if (userAccount == null) {
            System.out.println("Account not found.");
            return;
        }

        while (true) {
            System.out.println("------------------User Menu-------------------");
            System.out.println("|         1. Deposit Money                   |");
            System.out.println("|         2. Withdraw Money                  |");
            System.out.println("|         3. View Balance                    |");
            System.out.println("|         4. Transfer Money                  |");
            System.out.println("|         5. Take Loan                       |");
            System.out.println("|         6. Transaction History             |");
            System.out.println("|         7. Exit                            |");
            System.out.println("----------------------------------------------");

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Enter Amount to Deposit: ");
                    double depositAmount = Double.parseDouble(scanner.nextLine());
                    System.out.println(userAccount.deposit(depositAmount));
                    break;
                case "2":
                    System.out.print("Enter Amount to Withdraw: ");
                    double withdrawAmount = Double.parseDouble(scanner.nextLine());
                    System.out.println(userAccount.withdraw(withdrawAmount));
                    break;
                case "3":
                    System.out.println(userAccount.checkBalance());
                    break;
                case "4":
                    System.out.print("Enter Amount to Transfer: ");
                    double transferAmount = Double.parseDouble(scanner.nextLine());
                    System.out.print("Enter Receiver Account Number: ");
                    long receiverAccountNumber = Long.parseLong(scanner.nextLine());
                    Account receiverAccount = getAccountByNumber(receiverAccountNumber);
                    if (receiverAccount != null) {
                        System.out.println(userAccount.transferAmount(transferAmount, receiverAccount));
                    } else {
                        System.out.println("Receiver account not found.");
                    }
                    break;
                case "5":
                    System.out.print("Enter Amount for Loan: ");
                    double loanAmount = Double.parseDouble(scanner.next)
