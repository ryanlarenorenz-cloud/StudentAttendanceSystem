import dao.DatabaseInitializer;
import dao.UserDao;
import ui.Authentication;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try {
            DatabaseInitializer initializer = new DatabaseInitializer();
            initializer.initialize();

            UserDao userDao = new UserDao();

            try (Scanner scanner = new Scanner(System.in)) {
                Authentication authentication =
                        new Authentication(userDao, scanner);

                authentication.start();
            }

        } catch (Exception e) {
            System.out.println("Application startup failed.");
            e.printStackTrace();
        }
    }
}
