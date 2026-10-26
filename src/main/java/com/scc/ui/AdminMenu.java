
package ui;

import dao.UserDao;
import model.User;

import java.util.List;
import java.util.Scanner;

public class AdminMenu {

    private final UserDao userDao;
    private final Scanner scanner;

    public AdminMenu(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void start() {
        boolean running = true;

        while (running) {
            System.out.println("\n=== ADMIN MENU ===");
            System.out.println("1. View All System Users");
            System.out.println("2. View Pending Registrations");
            System.out.println("3. Approve User Account");
            System.out.println("4. Logout");
            System.out.print("Choose: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> printUsers(userDao.getAllUsers());
                case "2" -> printUsers(userDao.getPendingUsers());
                case "3" -> approveAccount();
                case "4" -> running = false;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void approveAccount() {
        List<User> pending = userDao.getPendingUsers();

        if (pending.isEmpty()) {
            System.out.println("No pending registrations.");
            return;
        }

        System.out.println("\n--- PENDING REGISTRATIONS ---");
        printUsers(pending);

        System.out.print("Enter User ID (0 to cancel): ");

        int id;
        try {
            id = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
            return;
        }

        if (id == 0) {
            System.out.println("Approval cancelled.");
            return;
        }

        boolean isPending = pending.stream()
                .anyMatch(user -> user.id() == id);

        if (!isPending) {
            System.out.println("That ID is not in the pending list.");
            return;
        }

        if (userDao.approveUser(id)) {
            System.out.println("User ID " + id + " approved successfully!");
        } else {
            System.out.println("Approval failed. Please try again.");
        }
    }

    private void printUsers(List<User> users) {
        String border =
                "+------+-----------------+--------------+------------+";

        System.out.println(border);
        System.out.printf(
                "| %-5s| %-16s| %-13s| %-11s|%n",
                "ID", "Username", "Role", "Status"
        );
        System.out.println(border);

        for (User user : users) {
            String username = user.username();

            if (username.length() > 16) {
                username = username.substring(0, 13) + "...";
            }

            System.out.printf(
                    "| %-5d| %-16s| %-13s| %-11s|%n",
                    user.id(), username, user.roles(), user.status()
            );
        }

        System.out.println(border);
    }
}
