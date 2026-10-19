package ui;

import dao.UserDao;
import model.User;

import java.util.Scanner;

public class Menu {

    private final Scanner scanner = new Scanner(System.in);
    private final UserDao userDao = new UserDao();

    public void start() {
        boolean running = true;

        while (running) {
            System.out.println("\n=== STUDENT ATTENDANCE SYSTEM ===");
            System.out.println("1. Register Account");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> registerAccount();
                case "2" -> loginAccount();
                case "3" -> {
                    System.out.println("Thank you for using the system!");
                    running = false;
                }
                default -> System.out.println("Invalid choice.");
            }
        }

        scanner.close();
    }

    private void registerAccount() {
        System.out.println("\n--- REGISTER ACCOUNT ---");

        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        System.out.println("1. TEACHER");
        System.out.println("2. STUDENT");
        System.out.print("Select role: ");

        String roleChoice = scanner.nextLine().trim();

        String role = switch (roleChoice) {
            case "1" -> "TEACHER";
            case "2" -> "STUDENT";
            default -> null;
        };

        if (role == null) {
            System.out.println("Invalid role.");
            return;
        }

        userDao.register(username, password, role);
    }

    private void loginAccount() {
        System.out.println("\n--- LOGIN ---");

        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        User user = userDao.login(username, password);

        if (user == null) {
            return;
        }

        if (!user.status().equals("APPROVED")) {
            System.out.println("Account status: " + user.status());
            System.out.println("Please wait for approval.");
            return;
        }

        System.out.println("Login successful!");
        System.out.println("Welcome, " + user.username());
        System.out.println("Role: " + user.roles());

        userSession();
    }

    private void userSession() {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("\n--- USER MENU ---");
            System.out.println("1. Logout");
            System.out.println("2. Exit");
            System.out.print("Choose: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    System.out.println("Logged out successfully.");
                    loggedIn = false;
                }
                case "2" -> {
                    System.out.println("Exiting application.");
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }
}
