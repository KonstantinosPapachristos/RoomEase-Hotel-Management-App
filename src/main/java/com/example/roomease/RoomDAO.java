package com.example.roomease;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    public List<Room> getAvailableRoomsForDates(String roomType, LocalDate checkIn, LocalDate checkOut) {
        List<Room> availableRooms = new ArrayList<>();
        String sql = """
            SELECT r.roomNumber, r.roomType, r.isAvailable, r.amenities
            FROM Rooms r
            WHERE r.roomType = ? AND r.isAvailable = true
              AND r.roomNumber NOT IN (
                  SELECT ra.roomNumber FROM RoomAllocation ra
                  WHERE ra.checkInDate < ? AND ra.checkOutDate > ?
              )
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, roomType);
            pstmt.setDate(2, Date.valueOf(checkOut));
            pstmt.setDate(3, Date.valueOf(checkIn));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    availableRooms.add(new Room(
                            rs.getString("roomNumber"),
                            rs.getString("roomType"),
                            rs.getBoolean("isAvailable"),
                            false,
                            rs.getString("amenities"),
                            "No issues",
                            "Available"
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return availableRooms;
    }

    public boolean assignRoomsToBooking(String bookingID, List<String> roomNumbers, LocalDate checkInDate, LocalDate checkOutDate) {
        String sql = "INSERT INTO RoomAllocation (bookingID, roomNumber, checkInDate, checkOutDate) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (String roomNumber : roomNumbers) {
                pstmt.setString(1, bookingID);
                pstmt.setString(2, roomNumber);
                pstmt.setDate(3, Date.valueOf(checkInDate));
                pstmt.setDate(4, Date.valueOf(checkOutDate));
                pstmt.addBatch();
            }

            pstmt.executeBatch(); // Execute the batch insert
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static List<String> getAllocatedRoomsForBooking(String bookingID) {
        List<String> allocatedRooms = new ArrayList<>();
        String sql = "SELECT roomNumber FROM RoomAllocation WHERE bookingID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, bookingID);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    allocatedRooms.add(rs.getString("roomNumber"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allocatedRooms;
    }


    public boolean resetAllRoomsToVacant() {
        String sql = "UPDATE Rooms SET isAvailable = true";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public List<RoomStatus> getAllRoomStatuses() {
        List<RoomStatus> roomStatuses = new ArrayList<>();
        String sql = "SELECT roomNumber, status FROM RoomStatus";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                roomStatuses.add(new RoomStatus(
                        rs.getString("roomNumber"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roomStatuses;
    }

    public boolean updateRoomStatus(String roomNumber, String status) {
        String sql = "UPDATE RoomStatus SET status = ? WHERE roomNumber = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setString(2, roomNumber);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }



}
