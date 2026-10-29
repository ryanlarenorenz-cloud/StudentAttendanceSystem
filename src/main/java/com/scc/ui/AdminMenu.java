
package ui;

import dao.UserDao;
import model.User;

import java.util.List;
import java.util.Scanner;

public class AdminMenu {

    private final UserDao userDao;
    private final Scanner scanner;
    private final User currentAdmin;

    // Constructor for the currently logged-in admin
    public AdminMenu(UserDao userDao, Scanner scanner, User currentAdmin) {
        this.userDao = userDao;
        this.scanner = scanner;
        this.currentAdmin = currentAdmin;
    }

    public void start() {
        boolean running = true;

        while (running) {
            System.out.println("\n=== ADMIN MENU ===");
            System.out.println("1. View All System Users");
            System.out.println("2. View Pending Registrations");
            System.out.println("3. Approve User Account");
            System.out.println("4. Update User Role and Status");
            System.out.println("0. Logout");

            int choice = readIntInput("Choose: ");

            switch (choice) {
                case 1 -> printUsers(userDao.getAllUsers());
                case 2 -> printUsers(userDao.getPendingUsers());
                case 3 -> approveAccount();
                case 4 -> updateUser();
                case 0 -> {
                    System.out.println("Logging out...");
                    running = false;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    // INPUT VALIDATION: INTEGER
    private int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println(
                        "Invalid input. Please enter a number.");
            }
        }
    }

    // APPROVE USER ACCOUNT
    private void approveAccount() {
        List<User> pending = userDao.getPendingUsers();

        if (pending.isEmpty()) {
            System.out.println("No pending registrations.");
            return;
        }

        System.out.println("\n--- PENDING REGISTRATIONS ---");
        printUsers(pending);

        int id = readIntInput("Enter User ID (0 to cancel): ");

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
            System.out.println("User ID " + id
                    + " approved successfully!");
        } else {
            System.out.println("Approval failed. Please try again.");
        }
    }

    // UPDATE USER ROLE AND STATUS
    private void updateUser() {
        List<User> users = userDao.getAllUsers();

        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }

        System.out.println("\n--- UPDATE USER ---");
        printUsers(users);

        int targetUserId = readIntInput(
                "Enter User ID to update (0 to cancel): ");

        if (targetUserId == 0) {
            System.out.println("Update cancelled.");
            return;
        }

        // Check if the selected ID exists
        User targetUser = null;

        for (User user : users) {
            if (user.id() == targetUserId) {
                targetUser = user;
                break;
            }
        }

        if (targetUser == null) {
            System.out.println("User ID not found.");
            return;
        }

        // SELF-EDIT RESTRICTION
        if (targetUserId == currentAdmin.id()
                && currentAdmin.status().equalsIgnoreCase("APPROVED")
                && currentAdmin.roles().equalsIgnoreCase("ADMIN")) {

            System.out.println(
                    "WARNING: Modifying the currently active admin account is restricted.");
            return;
        }

        System.out.println("Selected user: "
                + targetUser.username());
        System.out.println("Current role: "
                + targetUser.roles());
        System.out.println("Current status: "
                + targetUser.status());

        String newRole = readValidRole();

        if (newRole == null) {
            System.out.println("Update cancelled.");
            return;
        }

        String newStatus = readValidStatus();

        if (newStatus == null) {
            System.out.println("Update cancelled.");
            return;
        }

        // Confirm and save changes
        System.out.println("\n--- UPDATE SUMMARY ---");
        System.out.println("User ID: " + targetUserId);
        System.out.println("New role: " + newRole);
        System.out.println("New status: " + newStatus);

        if (userDao.updateUserRoleAndStatus(
                targetUserId, newRole, newStatus)) {
            System.out.println("Update completed successfully!");
        } else {
            System.out.println("Update failed.");
        }
    }

    // INPUT VALIDATION: ROLE
    private String readValidRole() {
        while (true) {
            System.out.print(
                    "Enter role (STUDENT/TEACHER/ADMIN, 0 to cancel): ");

            String role = scanner.nextLine()
                    .trim()
                    .toUpperCase();

            if (role.equals("0")) {
                return null;
            }

            if (role.equals("STUDENT")
                    || role.equals("TEACHER")
                    || role.equals("ADMIN")) {
                return role;
            }

            System.out.println(
                    "Invalid role. Enter STUDENT, TEACHER, or ADMIN.");
        }
    }

    // INPUT VALIDATION: STATUS
    private String readValidStatus() {
        while (true) {
            System.out.print(
                    "Enter status (APPROVED/PENDING, 0 to cancel): ");

            String status = scanner.nextLine()
                    .trim()
                    .toUpperCase();

            if (status.equals("0")) {
                return null;
            }

            if (status.equals("APPROVED")
                    || status.equals("PENDING")) {
                return status;
            }

            System.out.println(
                    "Invalid status. Enter APPROVED or PENDING.");
        }
    }

    // DISPLAY USERS TABLE
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
                    user.id(),
                    username,
                    user.roles(),
                    user.status()
            );
        }

        System.out.println(border);
    }
}
