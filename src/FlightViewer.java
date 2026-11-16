import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class FlightViewer {

    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/FlightDB?allowLoadLocalInfile=true&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jerusalem&useSSL=false";
        String user = "root";

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter MySQL root password: ");
            String password = scanner.nextLine();

            // Establish the connection
            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                // Ask for terminal number and validate input
                String terminal;
                while (true) {
                    System.out.print("Enter a terminal number (2, 3, 4, or 5): ");
                    if (!scanner.hasNextLine()) {
                        return;
                    }
                    terminal = scanner.nextLine().trim();
                    if (terminal.matches("[2-5]")) {
                        break;
                    }
                    System.out.println("Invalid input. Please enter a number between 2 and 5.");
                }

                // Prepare the SQL query
                String sql = "SELECT A.id AS ArrivalID, A.flight_number, FR.from_city, A.scheduled, A.delay_status, A.terminal " +
                        "FROM arrivals A " +
                        "INNER JOIN flightroutes FR ON A.flight_number = FR.flight_number " +
                        "WHERE A.terminal = ? " +
                        "ORDER BY A.scheduled ASC";

                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, terminal);

                    // Execute the query
                    try (ResultSet rs = pstmt.executeQuery()) {
                        // Check if any rows are returned
                        if (!rs.next()) {
                            System.out.println("No flights found for terminal " + terminal);
                        } else {
                            // Print table header
                            System.out.printf("%-10s %-15s %-20s %-15s %-18s %-10s%n",
                                    "ArrivalID", "Flight Number", "Departure City", "Scheduled Time", "Delay Status", "Terminal");

                            // Print each row of the result set
                            do {
                                System.out.printf("%-10d %-15s %-20s %-15s %-18s %-10s%n",
                                        rs.getInt("ArrivalID"), rs.getString("flight_number"),
                                        rs.getString("from_city"), rs.getString("scheduled"),
                                        rs.getString("delay_status"), rs.getString("terminal"));
                            } while (rs.next());
                        }
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
