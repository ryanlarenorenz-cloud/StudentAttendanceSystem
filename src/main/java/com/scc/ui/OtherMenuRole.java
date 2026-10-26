
package ui;

import java.util.Scanner;

public class OtherMenuRole {

    private final Scanner scanner;

    public OtherMenuRole(Scanner scanner) {
        this.scanner = scanner;
    }

    public void teacherMenu(String username) {
        boolean running = true;

        while (running) {
            System.out.println("\n==================================");
            System.out.println("          TEACHER MENU");
            System.out.println("==================================");
            System.out.println("Welcome, " + username);
            System.out.println("1. Record Attendance");
            System.out.println("2. View Student Information");
            System.out.println("3. View Attendance Reports");
            System.out.println("4. Logout");
            System.out.print("Enter choice: ");

            switch (scanner.nextLine().trim()) {
                case "1" ->
                        System.out.println(
                                "Attendance recording is not implemented yet."
                        );
                case "2" ->
                        System.out.println(
                                "Student information is not implemented yet."
                        );
                case "3" ->
                        System.out.println(
                                "Attendance reports are not implemented yet."
                        );
                case "4" -> running = false;
                default -> System.out.println("Invalid choice.");
            }
        }

        System.out.println("Teacher logged out.");
    }

    public void studentMenu(String username) {
        boolean running = true;

        while (running) {
            System.out.println("\n==================================");
            System.out.println("          STUDENT MENU");
            System.out.println("==================================");
            System.out.println("Welcome, " + username);
            System.out.println("1. View My Attendance");
            System.out.println("2. Logout");
            System.out.print("Enter choice: ");

            switch (scanner.nextLine().trim()) {
                case "1" ->
                        System.out.println(
                                "Attendance history is not implemented yet."
                        );
                case "2" -> running = false;
                default -> System.out.println("Invalid choice.");
            }
        }

        System.out.println("Student logged out.");
    }
}
