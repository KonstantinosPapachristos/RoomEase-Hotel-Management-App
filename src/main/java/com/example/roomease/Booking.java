package com.example.roomease;

import javafx.beans.property.BooleanProperty;

import java.time.LocalDate;

public class Booking {
    private String bookingID;
    private String customerName;
    private String roomType;
    private String bookingType;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private boolean isGroup;
    private double amountPaid;
    private double totalAmount;


    // Constructor with all fields
    public Booking(String bookingID, String customerName, String roomType, String bookingType, LocalDate checkInDate, LocalDate checkOutDate, boolean isGroup, double amountPaid, double totalAmount) {
        this.bookingID = bookingID;
        this.customerName = customerName;
        this.roomType = roomType;
        this.bookingType = bookingType;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.isGroup = isGroup;
        this.amountPaid = amountPaid;
        this.totalAmount = totalAmount;
    }

    // Getters and Setters
    public String getBookingID() { return bookingID; }
    public String getCustomerName() { return customerName; }
    public String getRoomType() { return roomType; }
    public String getBookingType() { return bookingType; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public boolean isGroup() { return isGroup; }
    public double getAmountPaid() { return amountPaid; }
    public double getTotalAmount() { return totalAmount; }

    public void setBookingID(String bookingID) { this.bookingID = bookingID; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    public void setBookingType(String bookingType) { this.bookingType = bookingType; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }
    public void setGroup(boolean isGroup) { this.isGroup = isGroup; }
    public void setAmountPaid(double amountPaid) { this.amountPaid = amountPaid; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    // Override toString for debugging
    @Override
    public String toString() {
        return "Booking{" +
                "bookingID='" + bookingID + '\'' +
                ", customerName='" + customerName + '\'' +
                ", roomType='" + roomType + '\'' +
                ", bookingType='" + bookingType + '\'' +
                ", checkInDate=" + checkInDate +
                ", checkOutDate=" + checkOutDate +
                ", isGroup=" + isGroup +
                ", amountPaid=" + amountPaid +
                ", totalAmount=" + totalAmount +
                '}';
    }
}
