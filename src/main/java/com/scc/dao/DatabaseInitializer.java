
package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public void initialize() {
        String usersSql = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                roles TEXT NOT NULL
                    CHECK (roles IN ('ADMIN', 'TEACHER', 'STUDENT')),
                status TEXT NOT NULL DEFAULT 'PENDING'
                    CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED'))
            )
            """;

        String attendanceSql = """
            CREATE TABLE IF NOT EXISTS attendance (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                student_id INTEGER NOT NULL,
                teacher_id INTEGER NOT NULL,
                attendance_date TEXT NOT NULL,
                status TEXT NOT NULL
                    CHECK (status IN ('PRESENT', 'ABSENT', 'LATE')),
                FOREIGN KEY (student_id) REFERENCES users(id),
                FOREIGN KEY (teacher_id) REFERENCES users(id)
            )
            """;

        String logsSql = """
            CREATE TABLE IF NOT EXISTS activity_logs (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER,
                action TEXT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users(id)
            )
            """;

        try (Connection connection = dao.DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute(usersSql);
            statement.execute(attendanceSql);
            statement.execute(logsSql);

            System.out.println("Database initialized successfully.");
            System.out.println("SQLite foreign keys enabled.");

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Database initialization failed: " + e.getMessage(), e
            );
        }
    }
}
