package randomDatabase.src.main;

import java.sql.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        String dburl = "jdbc:mysql://localhost:3306/randomgen";
        String username = "root";
        String password = "admin";


        try (Connection conn = DriverManager.getConnection(dburl, username, password)) {
            createTable(conn); // Create the "names" table if it doesn't exist

            System.out.println("Connected");

            while (true) {
                System.out.println("Choose an option:");
                System.out.println("1. Add names");
                System.out.println("2. Delete names");
                System.out.println("3. Generate random names");
                System.out.println("4. READ DATA");
                System.out.println("0. Exit");

                int option = scanner.nextInt();
                scanner.nextLine();

                switch (option) {
                    case 1:
                        addNames(scanner, conn);
                        break;
                    case 2:
                        deleteNames(scanner, conn);
                        break;
                    case 3:
                        generateRandomNames(scanner, conn);
                        break;
                    case 4:
                        readData(conn);
                        break;
                    case 0:
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid option. Try again.");
                }

                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTable(Connection connection) throws SQLException {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS names (id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(255))";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createTableQuery);
        }
    }

    private static void addNames(Scanner scanner, Connection connection) throws SQLException {
        System.out.println("Enter names (press Enter to stop):");
        String name = scanner.nextLine();

        while (!name.isEmpty()) {
            String insertQuery = "INSERT INTO names (name) VALUES (?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, name);
                preparedStatement.executeUpdate();
            }

            name = scanner.nextLine();
        }

        System.out.println("Names inserted into the database.");
    }

    private static void deleteNames(Scanner scanner, Connection connection) throws SQLException {
        System.out.println("Enter names to delete (press Enter to stop):");
        String name = scanner.nextLine();

        while (!name.isEmpty()) {
            String deleteQuery = "DELETE FROM names WHERE name = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setString(1, name);
                preparedStatement.executeUpdate();
            }

            name = scanner.nextLine();
        }

        System.out.println("Names deleted from the database.");
    }
    private static void readData(Connection conn) throws SQLException {

        String sql = "SELECT * FROM names";
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        int count = 0;

        while (resultSet.next()) {
            String username = resultSet.getString(2);


            String output = "User #%d: %s - %s - %s - %s";
            System.out.println(String.format(username));
        }
    }
    private static void generateRandomNames(Scanner scanner, Connection connection) throws SQLException {
        System.out.print("Enter the number of groups: ");
        int numberOfGroups = scanner.nextInt();

        if (numberOfGroups <= 0) {
            System.out.println("Invalid number of groups. Exiting...");
            return;
        }

        String selectQuery = "SELECT * FROM randomgen.names";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectQuery)) {

            List<String> names = new ArrayList<>();
            while (resultSet.next()) {
                names.add(resultSet.getString(2));
            }

            if (names.isEmpty()) {
                System.out.println("No names in the database. Add names first.");
                return;
            }

            if (names.size() < numberOfGroups) {
                System.out.println("Number of groups exceeds the number of names available.");
                return;
            }

            Random random = new Random();
            List<List<String>> groups = new ArrayList<>();
            int groupSize = names.size() / numberOfGroups;
            int remainingNames = names.size() % numberOfGroups;

            for (int i = 0; i < numberOfGroups; i++) {
                int currentGroupSize = groupSize;
                if (remainingNames > 0) {
                    currentGroupSize++;
                    remainingNames--;
                }

                List<String> group = new ArrayList<>();
                for (int j = 0; j < currentGroupSize; j++) {
                    int randomIndex = random.nextInt(names.size());
                    group.add(names.remove(randomIndex));
                }

                groups.add(group);
            }

            for (int i = 0; i < groups.size(); i++) {
                System.out.println("Group " + (i + 1) + ": " + groups.get(i));
            }
        }
    }
}



