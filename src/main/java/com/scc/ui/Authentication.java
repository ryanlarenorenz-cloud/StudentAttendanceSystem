
package ui;

import dao.UserDao;
import model.User;

import java.util.Scanner;

public class Authentication {

    private final UserDao userDao;
    private final Scanner scanner;

    public Authentication(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void start() {
        boolean running = true;

        while (running) {
            System.out.println("\n====================================");
            System.out.println("      STUDENT ATTENDANCE SYSTEM");
            System.out.println("====================================");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Select option: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> login();
                case "2" -> register();
                case "3" -> running = false;
                default -> System.out.println("Invalid option.");
            }
        }

        System.out.println("Application closed.");
    }

    private void register() {
        System.out.println("\n========== REGISTRATION ==========");

        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        System.out.println("\nSelect role:");
        System.out.println("1. ADMIN");
        System.out.println("2. TEACHER");
        System.out.println("3. STUDENT");
        System.out.print("Enter role (1-3): ");

        String role = switch (scanner.nextLine().trim()) {
            case "1" -> "ADMIN";
            case "2" -> "TEACHER";
            case "3" -> "STUDENT";
            default -> null;
        };

        if (role == null) {
            System.out.println("Invalid role selection.");
            return;
        }

        boolean registered = userDao.register(username, password, role);

        if (registered) {
            System.out.println("Selected role: " + role);
        }
    }

    private void login() {
        System.out.println("\n============== LOGIN ==============");

        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User user = userDao.login(username, password);

        if (user == null) {
            return;
        }

        System.out.println("Login successful! Welcome, "
                + user.username() + ".");

        routeByRole(user);
    }

    private void routeByRole(User user) {
        switch (user.roles()) {
            case "ADMIN" ->
                    new ui.AdminMenu(userDao, scanner, user).start();

            case "TEACHER" ->
                    teacherMenu(user);

            case "STUDENT" ->
                    studentMenu(user);

            default ->
                    System.out.println("Unknown user role.");
        }
    }

    private void teacherMenu(User user) {
        boolean running = true;

        while (running) {
            System.out.println("\n========== TEACHER MENU ==========");
            System.out.println("Welcome, " + user.username());
            System.out.println("1. Record Attendance");
            System.out.println("2. Logout");
            System.out.print("Select option: ");

            switch (scanner.nextLine().trim()) {
                case "1" ->
                        System.out.println(
                                "Attendance recording is not implemented yet."
                        );
                case "2" -> running = false;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void studentMenu(User user) {
        boolean running = true;

        while (running) {
            System.out.println("\n========== STUDENT MENU ==========");
            System.out.println("Welcome, " + user.username());
            System.out.println("1. View My Attendance");
            System.out.println("2. Logout");
            System.out.print("Select option: ");

            switch (scanner.nextLine().trim()) {
                case "1" ->
                        System.out.println(
                                "Attendance history is not implemented yet."
                        );
                case "2" -> running = false;
                default -> System.out.println("Invalid option.");
            }
        }
    }
}
