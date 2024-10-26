//package src;


import java.sql.*;

public class IsolationExample {
    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:h2:~/test", "sa", "");
            conn.setAutoCommit(false); // Transaction start

            // Begin Transaction 1
            new Thread(() -> executeTransaction(conn, 1, 2, 1000, 1)).start();

            // Wait to simulate delay for concurrency
            Thread.sleep(2000);

            // Begin Transaction 2
            new Thread(() -> executeTransaction(conn, 1, 2, 1500, 2)).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void executeTransaction(Connection conn, int fromAccount, int toAccount, int amount, int tran) {
        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            // Retrieve and display balances
            System.out.println("Balances Before Transaction" +tran+":");
            displayAccountBalance(conn);

            // Transfer amount
            PreparedStatement stmt = conn.prepareStatement("SELECT balance FROM accounts WHERE id = ?");
            stmt.setInt(1, fromAccount);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int balance = rs.getInt("balance");

            if (balance - amount < 1000) {
                System.out.println("Cannot maintain minimum balance of $1000 after transaction");
                conn.rollback();
                return;
            }

            // Debit fromAccount
            stmt = conn.prepareStatement("UPDATE accounts SET balance = balance - ? WHERE id = ?");
            stmt.setInt(1, amount);
            stmt.setInt(2, fromAccount);
            stmt.executeUpdate();

            // Delay to simulate concurrent transaction behavior
            Thread.sleep(5000);

            // Credit toAccount
            stmt = conn.prepareStatement("UPDATE accounts SET balance = balance + ? WHERE id = ?");
            stmt.setInt(1, amount);
            stmt.setInt(2, toAccount);
            stmt.executeUpdate();

            conn.commit();
            System.out.println("Transaction"+tran+" committed successfully");
            displayAccountBalance(conn);

        } catch (Exception e) {
            try {
                conn.rollback();
                System.out.println("Transaction rolled back due to error");
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        }
    }

    public static void displayAccountBalance(Connection conn) {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM accounts");
            while (rs.next()) {
                System.out.println("Account " + rs.getInt("id") + ": $" + rs.getInt("balance"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

