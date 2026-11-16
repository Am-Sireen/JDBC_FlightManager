import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class AdvancedFlightDispatcher {

    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/FlightDB?allowLoadLocalInfile=true&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jerusalem&useSSL=false";
        String user = "root";

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter MySQL root password: ");
            String password = scanner.nextLine();

            // Establish the connection
            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                // Define the list of arrival IDs and the new status
                List<Integer> arrivalIds = Arrays.asList(1, 3, 5);
                String newStatus = "Landed - Final";

                // Update multiple delays using a single PreparedStatement
                updateMultipleDelays(conn, arrivalIds, newStatus);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void updateMultipleDelays(Connection conn, List<Integer> arrivalIds, String newStatus) throws SQLException {
        // Construct the SQL statement with placeholders
        String sql = "UPDATE Arrivals SET delay_status = ? WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int totalAffectedRows = 0;

            // Set the new status once outside the loop
            pstmt.setString(1, newStatus);

            // Loop over arrival IDs and set the 'id' parameter, then call addBatch()
            for (Integer id : arrivalIds) {
                pstmt.setInt(2, id);
                pstmt.addBatch();
            }

            // Execute the batch update
            int[] results = pstmt.executeBatch();

            // Compute total affected rows
            for (int result : results) {
                totalAffectedRows += result;
            }

            // Print a clear summary message
            System.out.println("Total rows affected: " + totalAffectedRows);
        }
    }
}