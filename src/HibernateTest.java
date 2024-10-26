

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class HibernateTest {
    public static void main(String[] args) {
        // Database URL, username, and password
//        String jdbcUrl = "jdbc:h2:tcp://localhost/~/test";
        String jdbcUrl = "jdbc:h2:~/test"; // Use embedded mode

        String username = "sa";
        String password = "";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            // Connect to H2 database
            conn = DriverManager.getConnection(jdbcUrl, username, password);
            conn.setAutoCommit(false); // Begin transaction

            // Create a new order
            Order order = new Order();
            order.setCustomer_id(1);
            order.setProduct_id(101);
            order.setQuantity(3);

            // Insert order into database
            String sql = "INSERT INTO orders (customer_id, product_id, quantity) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, order.getCustomer_id());
            stmt.setInt(2, order.getProduct_id());
            stmt.setInt(3, order.getQuantity());
            stmt.executeUpdate();

            // Commit transaction
            conn.commit();
            System.out.println("Order saved successfully");

        } catch (SQLException e) {
            // Roll back transaction on error
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            // Clean up resources
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}

