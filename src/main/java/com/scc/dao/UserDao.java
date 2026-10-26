
package dao;

import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    // REGISTER USER
    public boolean register(String username, String password, String role) {

        if (username.isBlank() || password.isBlank()) {
            System.out.println("Username and password are required.");
            return false;
        }

        if (!role.equals("ADMIN")
                && !role.equals("TEACHER")
                && !role.equals("STUDENT")) {
            System.out.println("Invalid role.");
            return false;
        }

        String checkSql = "SELECT id FROM users WHERE username = ?";
        String sql = """
                INSERT INTO users (username, password, roles, status)
                VALUES (?, ?, ?, 'PENDING')
                """;

        try (Connection conn = dao.DatabaseConnection.getConnection()) {

            try (PreparedStatement check = conn.prepareStatement(checkSql)) {
                check.setString(1, username);

                try (ResultSet rs = check.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("Username already exists.");
                        return false;
                    }
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setString(3, role);

                ps.executeUpdate();

                System.out.println("Registration successful!");
                System.out.println("Role: " + role);
                System.out.println("Status: PENDING");
                return true;
            }

        } catch (SQLException e) {
            System.out.println("Registration failed: " + e.getMessage());
            return false;
        }
    }

    // LOGIN USER
    public User login(String username, String password) {

        String sql = """
                SELECT id, username, password, roles, status
                FROM users
                WHERE username = ?
                """;

        try (Connection conn = dao.DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    String savedPassword = rs.getString("password");
                    String status = rs.getString("status");

                    if (!savedPassword.equals(password)) {
                        System.out.println("Incorrect password.");
                        return null;
                    }

                    if (!status.equalsIgnoreCase("APPROVED")) {
                        System.out.println("Account status: " + status);
                        System.out.println("Your account needs approval.");
                        return null;
                    }

                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            savedPassword,
                            rs.getString("roles"),
                            status
                    );
                }

                System.out.println("Username not found.");
            }

        } catch (SQLException e) {
            System.out.println("Login failed: " + e.getMessage());
        }

        return null;
    }

    // GET ALL USERS
    public List<User> getAllUsers() {

        List<User> users = new ArrayList<>();

        String sql = """
                SELECT id, username, password, roles, status
                FROM users
                ORDER BY id
                """;

        try (Connection conn = dao.DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("roles"),
                        rs.getString("status")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving users: " + e.getMessage());
        }

        return users;
    }

    // GET PENDING USERS
    public List<User> getPendingUsers() {

        List<User> users = new ArrayList<>();

        String sql = """
                SELECT id, username, password, roles, status
                FROM users
                WHERE status = 'PENDING'
                ORDER BY id
                """;

        try (Connection conn = dao.DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("roles"),
                        rs.getString("status")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving pending users: " + e.getMessage());
        }

        return users;
    }

    // APPROVE USER
    public boolean approveUser(int userId) {

        String sql = """
                UPDATE users
                SET status = 'APPROVED'
                WHERE id = ? AND status = 'PENDING'
                """;

        try (Connection conn = dao.DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            if (ps.executeUpdate() > 0) {
                System.out.println("User approved successfully!");
                return true;
            }

            System.out.println("User not found or already processed.");

        } catch (SQLException e) {
            System.out.println("Approval failed: " + e.getMessage());
        }

        return false;
    }
}
