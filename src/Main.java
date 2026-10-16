
import dao.DatabaseConnection;
import ui.Menu;

public class Main {

    public static void main(String[] args) {
        DatabaseConnection.initializeDatabase();

        Menu menu = new Menu();
        menu.start();
    }
}
gg