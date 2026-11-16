import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Updates the delay status for flight {@code LX354} in the FlightDB database.
 *
 * <p>The original implementation concatenated user input into the SQL text,
 * allowing that input to change the structure of the statement. This
 * implementation uses a {@link PreparedStatement} with placeholders for the
 * delay status and flight number. Binding those values separately keeps them
 * as data and preserves the structure of the {@code UPDATE} statement.
 */
public class SimpleFlightUpdater {

    /**
     * Reads the MySQL password and the new delay status from the console,
     * executes the update, and prints the number of rows reported by JDBC.
     *
     * <p>The scanner, connection, and prepared statement are closed
     * automatically when their try-with-resources blocks end.
     *
     * @param args command-line arguments, unused
     */
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/FlightDB"
                + "?allowLoadLocalInfile=true&allowPublicKeyRetrieval=true"
                + "&serverTimezone=Asia/Jerusalem&useSSL=false";
        String user = "root";
        String flightNumber = "LX354";

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter MySQL root password: ");
            String password = scanner.nextLine();

            String sql = "UPDATE arrivals SET delay_status = ? WHERE flight_number = ?";

            try (Connection conn = DriverManager.getConnection(url, user, password);
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                System.out.print("Enter new delay status for flight 'LX354': ");
                String userInput = scanner.nextLine();

                stmt.setString(1, userInput);
                stmt.setString(2, flightNumber);

                int rowsAffected = stmt.executeUpdate();
                System.out.println("Rows affected: " + rowsAffected);
            } catch (SQLException e) {
                System.err.println("Database update failed: " + e.getMessage());
            }
        }
    }
}