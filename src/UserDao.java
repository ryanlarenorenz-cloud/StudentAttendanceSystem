
package dao;

import model.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class UserDao {

    private static final int ITERATIONS = 210000;
    private static final int KEY_LENGTH = 256;

    private String hashPassword(String password) throws Exception {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);

        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(), salt, ITERATIONS, KEY_LENGTH
        );

        byte[] hash = SecretKeyFactory
                .getInstance("PBKDF2WithHmacSHA256")
                .generateSecret(spec).getEncoded();

        spec.clearPassword();

        return ITERATIONS + ":"
                + Base64.getEncoder().encodeToString(salt) + ":"
                + Base64.getEncoder().encodeToString(hash);
    }

    private boolean verifyPassword(
            String password, String storedHash) throws Exception {

        String[] parts = storedHash.split(":");

        if (parts.length != 3) {
            return false;
        }

        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = Base64.getDecoder().decode(parts[1]);
        byte[] expectedHash = Base64.getDecoder().decode(parts[2]);

        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                salt,
                iterations,
                expectedHash.length * 8
        );

        byte[] actualHash = SecretKeyFactory
                .getInstance("PBKDF2WithHmacSHA256")
                .generateSecret(spec).getEncoded();

        spec.clearPassword();

        return MessageDigest.isEqual(expectedHash, actualHash);
    }

    public boolean register(
            String username, String password, String role) {

        if (username == null || username.isBlank()
                || password == null || password.isBlank()) {
            System.out.println("Username and password are required.");
            return false;
        }

        if (!role.equals("TEACHER") && !role.equals("STUDENT")) {
            System.out.println("Invalid role.");
            return false;
        }

        String sql = """
                INSERT INTO users (username, password, roles, status)
                VALUES (?, ?, ?, 'PENDING')
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username.trim());
            ps.setString(2, hashPassword(password));
            ps.setString(3, role);

            ps.executeUpdate();

            System.out.println("Registration successful!");
            System.out.println("Account status: PENDING");
            System.out.println("Wait for administrator approval.");
            return true;

        } catch (SQLException e) {
            if (e.getMessage() != null
                    && e.getMessage().contains("UNIQUE")) {
                System.out.println("Username already exists.");
            } else {
                System.out.println("Registration failed: " + e.getMessage());
            }
            return false;

        } catch (Exception e) {
            System.out.println("Password processing failed.");
            return false;
        }
    }

    public User login(String username, String password) {
        String sql = """
                SELECT id, username, password, roles, status
                FROM users
                WHERE username = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()
                        && verifyPassword(password, rs.getString("password"))) {

                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("roles"),
                            rs.getString("status")
                    );
                }
            }

            System.out.println("Invalid username or password.");

        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
        }

        return null;
    }

    public boolean approveUser(String username) {
        String sql = """
                UPDATE users
                SET status = 'APPROVED'
                WHERE username = ? AND status = 'PENDING'
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username.trim());

            if (ps.executeUpdate() > 0) {
                System.out.println("Account approved successfully.");
                return true;
            }

            System.out.println("Pending account not found.");
            return false;

        } catch (SQLException e) {
            System.out.println("Approval error: " + e.getMessage());
            return false;
        }
    }
}
