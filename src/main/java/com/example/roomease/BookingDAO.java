package com.example.roomease;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.time.Month;

public class BookingDAO {

    public boolean addBookingAndAssignRoom(Booking booking) {
        // SQL ερώτημα για εισαγωγή μιας κράτησης στη βάση δεδομένων
        String sql = "INSERT INTO Booking (bookingID, customerName, roomType, bookingType, checkInDate, checkOutDate, isGroup, amountPaid, totalAmount) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Δημιουργία μοναδικού Booking ID
        String bookingID = generateUniqueBookingID();
        if (bookingID == null) {
            // Εάν η δημιουργία του Booking ID αποτύχει, εμφανίζει μήνυμα σφάλματος και επιστρέφει false
            System.err.println("Αποτυχία δημιουργίας μοναδικού Booking ID.");
            return false;
        }
        // Ανάθεση του μοναδικού Booking ID στο αντικείμενο κράτησης
        booking.setBookingID(bookingID);

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                // Εάν η σύνδεση με τη βάση δεδομένων αποτύχει, εμφανίζει μήνυμα σφάλματος και επιστρέφει false
                System.err.println("Αδυναμία σύνδεσης με τη βάση δεδομένων.");
                return false;
            }

            // Απενεργοποίηση του auto-commit για εκτέλεση της λειτουργίας ως συναλλαγή
            conn.setAutoCommit(false);

            // Εισαγωγή της κράτησης στη βάση δεδομένων χωρίς την ανάθεση δωματίου
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                // Εισαγωγή των τιμών για την προετοιμασμένη δήλωση
                pstmt.setString(1, booking.getBookingID()); // Μοναδικό Booking ID
                pstmt.setString(2, booking.getCustomerName()); // Όνομα πελάτη
                pstmt.setString(3, booking.getRoomType()); // Τύπος δωματίου
                pstmt.setString(4, booking.getBookingType()); // Τύπος κράτησης
                pstmt.setDate(5, Date.valueOf(booking.getCheckInDate())); // Ημερομηνία check-in
                pstmt.setDate(6, Date.valueOf(booking.getCheckOutDate())); // Ημερομηνία check-out
                pstmt.setBoolean(7, booking.isGroup()); // Κατάσταση ομαδικής κράτησης
                pstmt.setDouble(8, booking.getAmountPaid()); // Ποσό πληρωμής
                pstmt.setDouble(9, booking.getTotalAmount()); // Συνολικό ποσό

                // Εκτέλεση του ερωτήματος εισαγωγής
                pstmt.executeUpdate();
            }

            // Επιβεβαίωση της συναλλαγής για αποθήκευση των αλλαγών
            conn.commit();
            System.out.println("Η κράτηση προστέθηκε επιτυχώς χωρίς ανάθεση δωματίου.");
            return true;

        } catch (SQLException e) {
            // Εάν προκύψει εξαίρεση κατά τη συναλλαγή, εμφανίζει μήνυμα σφάλματος και επιστρέφει false
            System.err.println("Σφάλμα κατά τη διάρκεια της συναλλαγής: " + e.getMessage());
            return false;
        }
    }




    public List<Booking> getAllBookings() {
        // Δημιουργία λίστας για αποθήκευση όλων των κρατήσεων
        List<Booking> bookings = new ArrayList<>();

        // SQL ερώτημα για ανάκτηση όλων των κρατήσεων από τον πίνακα Booking
        String sql = "SELECT bookingID, customerName, roomType, bookingType, checkInDate, checkOutDate, isGroup, amountPaid, totalAmount FROM Booking";

        try (Connection conn = DatabaseConnection.getConnection(); // Σύνδεση με τη βάση δεδομένων
             Statement stmt = conn.createStatement(); // Δημιουργία δήλωσης SQL
             ResultSet rs = stmt.executeQuery(sql)) { // Εκτέλεση του ερωτήματος και αποθήκευση του αποτελέσματος

            // Επεξεργασία των αποτελεσμάτων
            while (rs.next()) {
                // Δημιουργία ενός αντικειμένου Booking από τα δεδομένα της βάσης και προσθήκη στη λίστα
                bookings.add(new Booking(
                        rs.getString("bookingID"), // ID κράτησης
                        rs.getString("customerName"), // Όνομα πελάτη
                        rs.getString("roomType"), // Τύπος δωματίου
                        rs.getString("bookingType"), // Τύπος κράτησης
                        rs.getDate("checkInDate").toLocalDate(), // Ημερομηνία check-in
                        rs.getDate("checkOutDate").toLocalDate(), // Ημερομηνία check-out
                        rs.getBoolean("isGroup"), // Εάν η κράτηση είναι για ομάδα
                        rs.getDouble("amountPaid"), // Ποσό που πληρώθηκε
                        rs.getDouble("totalAmount") // Συνολικό ποσό κράτησης
                ));
            }
        } catch (SQLException e) {
            // Διαχείριση εξαιρέσεων κατά τη διάρκεια της σύνδεσης ή του ερωτήματος
            e.printStackTrace(); // Εκτύπωση του σφάλματος για εντοπισμό προβλημάτων
        }

        // Επιστροφή της λίστας με όλες τις κρατήσεις
        return bookings;
    }


    public boolean updateBooking(Booking booking) {
        String sql = "UPDATE Booking SET customerName = ?, roomType = ?, bookingType = ?, checkInDate = ?, checkOutDate = ?, isGroup = ?, amountPaid = ?, totalAmount = ? WHERE bookingID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, booking.getCustomerName());
            pstmt.setString(2, booking.getRoomType());
            pstmt.setString(3, booking.getBookingType());
            pstmt.setDate(4, Date.valueOf(booking.getCheckInDate()));
            pstmt.setDate(5, Date.valueOf(booking.getCheckOutDate()));
            pstmt.setBoolean(6, booking.isGroup());
            pstmt.setDouble(7, booking.getAmountPaid());
            pstmt.setDouble(8, booking.getTotalAmount());
            pstmt.setString(9, booking.getBookingID());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteAllBookings() {
        String sql = "DELETE FROM Booking";
        String allocationSql = "DELETE FROM RoomAllocation";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(allocationSql); // Delete all allocations
            stmt.executeUpdate(sql); // Delete all bookings
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Booking> getFilteredBookings(LocalDate startDate, LocalDate endDate, String roomType) {
        List<Booking> bookings = new ArrayList<>();
        String sql = """
            SELECT * FROM Booking WHERE 
            (? IS NULL OR checkInDate >= ?) AND 
            (? IS NULL OR checkOutDate <= ?) AND 
            (? IS NULL OR roomType = ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setObject(1, startDate);
            pstmt.setObject(2, startDate);
            pstmt.setObject(3, endDate);
            pstmt.setObject(4, endDate);
            pstmt.setString(5, roomType);
            pstmt.setString(6, roomType);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(new Booking(
                            rs.getString("bookingID"),
                            rs.getString("customerName"),
                            rs.getString("roomType"),
                            rs.getString("bookingType"),
                            rs.getDate("checkInDate").toLocalDate(),
                            rs.getDate("checkOutDate").toLocalDate(),
                            rs.getBoolean("isGroup"),
                            rs.getDouble("amountPaid"),
                            rs.getDouble("totalAmount")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public boolean deleteBooking(String bookingID) {
        String deleteAllocationsSql = "DELETE FROM RoomAllocation WHERE bookingID = ?";
        String deleteBookingSql = "DELETE FROM Booking WHERE bookingID = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            try (PreparedStatement deleteAllocationsStmt = conn.prepareStatement(deleteAllocationsSql);
                 PreparedStatement deleteBookingStmt = conn.prepareStatement(deleteBookingSql)) {

                // Delete room allocations first
                deleteAllocationsStmt.setString(1, bookingID);
                deleteAllocationsStmt.executeUpdate();

                // Delete the booking
                deleteBookingStmt.setString(1, bookingID);
                int rowsAffected = deleteBookingStmt.executeUpdate();

                conn.commit(); // Commit transaction
                return rowsAffected > 0;

            } catch (SQLException e) {
                conn.rollback(); // Rollback transaction if any error occurs
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    public double getRevenueForDateRange(LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT SUM(totalAmount) AS totalRevenue FROM Booking WHERE 1=1";
        if (startDate != null) sql += " AND checkInDate >= ?";
        if (endDate != null) sql += " AND checkOutDate <= ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int paramIndex = 1;
            if (startDate != null) pstmt.setDate(paramIndex++, Date.valueOf(startDate));
            if (endDate != null) pstmt.setDate(paramIndex, Date.valueOf(endDate));

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("totalRevenue");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0; // Return 0 if no revenue found or error occurs
    }

    public int getTotalBookings() {
        String sql = "SELECT COUNT(*) AS totalBookings FROM Booking";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("totalBookings");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Return 0 if no bookings found or error occurs
    }

    public int getMonthlyBookings(int year, int month) {
        String sql = """
            SELECT COUNT(*) AS monthlyBookings
            FROM Booking
            WHERE YEAR(checkInDate) = ? AND MONTH(checkInDate) = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, year);
            pstmt.setInt(2, month);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("monthlyBookings");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Return 0 if no bookings found or error occurs
    }

    public String getMonthWithMostBookings(int year) {
        String sql = """
            SELECT MONTH(checkInDate) AS month, COUNT(*) AS bookings
            FROM Booking
            WHERE YEAR(checkInDate) = ?
            GROUP BY month
            ORDER BY bookings DESC
            LIMIT 1
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, year);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int month = rs.getInt("month");
                return Month.of(month).name();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "None"; // Return "None" if no bookings found or error occurs
    }


    private String generateUniqueBookingID() {
        String uniqueID;
        do {
            uniqueID = "B" + System.currentTimeMillis(); // Generate ID based on timestamp
            String checkSql = "SELECT 1 FROM Booking WHERE bookingID = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(checkSql)) {

                pstmt.setString(1, uniqueID);
                ResultSet rs = pstmt.executeQuery();
                if (!rs.next()) {
                    break; // ID is unique
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return null; // Return null if an error occurs
            }
        } while (true);
        return uniqueID;
    }


}
