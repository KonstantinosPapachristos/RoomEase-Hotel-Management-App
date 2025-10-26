package com.example.roomease;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

import java.sql.SQLException;
import java.text.BreakIterator;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeDashboardController {

    @FXML
    private TableView<Booking> bookingsTable;
    @FXML
    private TableColumn<Booking, String> bookingIDColumn, customerNameColumn, roomTypeColumn, bookingTypeColumn;
    @FXML
    private TableColumn<Booking, LocalDate> checkInDateColumn, checkOutDateColumn;
    @FXML
    private TableColumn<Booking, Boolean> isGroupColumn;
    @FXML
    private TableColumn<Booking, Double> amountPaidColumn, totalAmountColumn;
    @FXML
    private TableColumn<Booking, String> assignedRoomsColumn;
    @FXML
    private DatePicker startDatePicker, endDatePicker;
    @FXML
    private ComboBox<String> roomTypeFilterComboBox;
    @FXML
    private TableView<RoomStatus> roomStatusTable;
    @FXML
    private TableColumn<RoomStatus, String> roomNumberColumn;
    @FXML
    private TableColumn<RoomStatus, String> statusColumn;
@FXML
    private TextArea roomOccupancySummary;

    private final RoomDAO roomDAO = new RoomDAO();
    private ObservableList<Booking> bookings = FXCollections.observableArrayList();
    private final BookingDAO bookingDAO = new BookingDAO();

    private static final Map<String, Double> roomBasePrices = new HashMap<>();

    static {
        roomBasePrices.put("Single", 100.0);
        roomBasePrices.put("Double", 150.0);
        roomBasePrices.put("Triple", 200.0);
        roomBasePrices.put("Suite", 300.0);
    }

    public void initialize() {
        bookingIDColumn.setCellValueFactory(new PropertyValueFactory<>("bookingID"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        roomTypeColumn.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        bookingTypeColumn.setCellValueFactory(new PropertyValueFactory<>("bookingType"));
        checkInDateColumn.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        checkOutDateColumn.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));
        isGroupColumn.setCellValueFactory(new PropertyValueFactory<>("isGroup"));
        amountPaidColumn.setCellValueFactory(new PropertyValueFactory<>("amountPaid"));
        totalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        roomNumberColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadBookings();
        loadRoomStatuses();
        roomTypeFilterComboBox.setItems(FXCollections.observableArrayList("All", "Single", "Double", "Triple", "Suite"));
        roomTypeFilterComboBox.setValue("All"); // Default to "All"

        assignedRoomsColumn.setCellValueFactory(cellData -> {
            Booking booking = cellData.getValue();
            List<String> roomNumbers = RoomDAO.getAllocatedRoomsForBooking(booking.getBookingID());
            return new SimpleStringProperty(String.join(", ", roomNumbers));
        });
    }


    public void loadRoomOccupancySummary() {
        // Corrected total rooms per room type
        Map<String, Integer> totalRoomsByType = Map.of(
                "Single", 10,
                "Double", 80,
                "Suite", 20,
                "Triple", 5
        );

        // Count occupied rooms grouped by room type
        Map<String, Integer> occupiedRoomsByType = new HashMap<>();
        LocalDate today = LocalDate.now();

        for (Booking booking : bookingDAO.getAllBookings()) {
            if (!booking.getCheckInDate().isAfter(today) && !booking.getCheckOutDate().isBefore(today)) {
                occupiedRoomsByType.merge(booking.getRoomType(), 1, Integer::sum);
            }
        }

        // Build the occupancy summary
        StringBuilder summaryText = new StringBuilder();
        for (Map.Entry<String, Integer> entry : totalRoomsByType.entrySet()) {
            String roomType = entry.getKey();
            int totalRooms = entry.getValue();
            int occupiedRooms = occupiedRoomsByType.getOrDefault(roomType, 0);
            int vacantRooms = totalRooms - occupiedRooms;

            summaryText.append(roomType).append(" Rooms\n")
                    .append("Occupied: ").append(occupiedRooms).append("\n")
                    .append("Vacant: ").append(vacantRooms).append("\n")
                    .append("Total Rooms: ").append(totalRooms).append("\n\n");
        }

        // Display the result in the TextArea
        roomOccupancySummary.setText(summaryText.toString());
    }
    private void loadBookings() {
        List<Booking> bookingList = bookingDAO.getAllBookings();
        bookings = FXCollections.observableArrayList(bookingList);
        bookingsTable.setItems(bookings);
    }

    private void loadRoomStatuses() {
        List<RoomStatus> roomStatuses = roomDAO.getAllRoomStatuses();
        roomStatusTable.setItems(FXCollections.observableArrayList(roomStatuses));
    }

    // Set the status of the selected room to "Cleaning"
    @FXML
    public void setStatusCleaning() {
        updateRoomStatus("Cleaning");
    }

    // Set the status of the selected room to "Maintenance"
    @FXML
    public void setStatusMaintenance() {
        updateRoomStatus("Maintenance");
    }

    // Set the status of the selected room to "Ready"
    @FXML
    public void setStatusReady() {
        updateRoomStatus("Ready");
    }

    // Update the status in the database
    private void updateRoomStatus(String newStatus) {
        RoomStatus selectedRoom = roomStatusTable.getSelectionModel().getSelectedItem();
        if (selectedRoom == null) {
            showAlert("No Selection", "Please select a room to update the status.");
            return;
        }

        boolean isUpdated = roomDAO.updateRoomStatus(selectedRoom.getRoomNumber(), newStatus);
        if (isUpdated) {
            selectedRoom.setStatus(newStatus); // Update the TableView
            roomStatusTable.refresh();
        } else {
            showAlert("Database Error", "Failed to update room status.");
        }
    }
    @FXML
    public void openBookingForm() {
        Dialog<Booking> dialog = new Dialog<>();
        dialog.setTitle("New Booking");

        ButtonType addButtonType = new ButtonType("Add Booking", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        TextField customerNameField = new TextField();
        customerNameField.setPromptText("Customer Name");

        // ComboBox for Room Type
        ComboBox<String> roomTypeComboBox = new ComboBox<>();
        roomTypeComboBox.setPromptText("Room Type");
        roomTypeComboBox.setItems(FXCollections.observableArrayList("Single", "Double", "Triple", "Suite"));

        // TextArea for Amenities
        TextArea amenitiesTextArea = new TextArea("Select a room type to see amenities.");
        amenitiesTextArea.setWrapText(true);
        amenitiesTextArea.setEditable(false);
        amenitiesTextArea.setPrefHeight(60);

        // Predefined amenities map
        Map<String, String> amenitiesMap = new HashMap<>();
        amenitiesMap.put("Single", "Wi-Fi, Flat-Screen TV, Basic Toiletries");
        amenitiesMap.put("Double", "Single amenities + Coffee Maker, Hair Dryer");
        amenitiesMap.put("Triple", "Double amenities + Extra Bedding, Mini-Fridge");
        amenitiesMap.put("Suite", "Triple amenities + Living Area, Full Kitchen");

        // Listener for room type selection
        roomTypeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && amenitiesMap.containsKey(newValue)) {
                amenitiesTextArea.setText(amenitiesMap.get(newValue));
            } else {
                amenitiesTextArea.setText("Select a room type to see amenities.");
            }
        });

        // ComboBox for Booking Type
        ComboBox<String> bookingTypeComboBox = new ComboBox<>();
        bookingTypeComboBox.setPromptText("Booking Type");
        bookingTypeComboBox.setItems(FXCollections.observableArrayList("Prepaid", "Traditional", "Group", "Low Occupancy"));

        DatePicker checkInDatePicker = new DatePicker();
        checkInDatePicker.setPromptText("Check-In Date");

        DatePicker checkOutDatePicker = new DatePicker();
        checkOutDatePicker.setPromptText("Check-Out Date");

        CheckBox isGroupCheckBox = new CheckBox("Group");

        CheckBox eventCheckBox = new CheckBox("Event/Exhibition");

        Label priceLabel = new Label("Total Price: $0.00");
        Label amountPaidLabel = new Label("Amount Paid: $0.00");

        // Listener to update price and amount paid dynamically
        roomTypeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updatePricing(roomTypeComboBox, bookingTypeComboBox, checkInDatePicker, checkOutDatePicker, eventCheckBox, priceLabel, amountPaidLabel));
        bookingTypeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updatePricing(roomTypeComboBox, bookingTypeComboBox, checkInDatePicker, checkOutDatePicker, eventCheckBox, priceLabel, amountPaidLabel));
        checkInDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> updatePricing(roomTypeComboBox, bookingTypeComboBox, checkInDatePicker, checkOutDatePicker, eventCheckBox, priceLabel, amountPaidLabel));
        checkOutDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> updatePricing(roomTypeComboBox, bookingTypeComboBox, checkInDatePicker, checkOutDatePicker, eventCheckBox, priceLabel, amountPaidLabel));
        eventCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> updatePricing(roomTypeComboBox, bookingTypeComboBox, checkInDatePicker, checkOutDatePicker, eventCheckBox, priceLabel, amountPaidLabel));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Customer Name:"), 0, 0);
        grid.add(customerNameField, 1, 0);
        grid.add(new Label("Room Type:"), 0, 1);
        grid.add(roomTypeComboBox, 1, 1);
        grid.add(new Label("Amenities:"), 0, 2);
        grid.add(amenitiesTextArea, 1, 2, 2, 1);
        grid.add(new Label("Booking Type:"), 0, 3);
        grid.add(bookingTypeComboBox, 1, 3);
        grid.add(new Label("Check-In Date:"), 0, 4);
        grid.add(checkInDatePicker, 1, 4);
        grid.add(new Label("Check-Out Date:"), 0, 5);
        grid.add(checkOutDatePicker, 1, 5);
        grid.add(new Label("Group:"), 0, 6);
        grid.add(isGroupCheckBox, 1, 6);
        grid.add(new Label("Event/Exhibition:"), 0, 7);
        grid.add(eventCheckBox, 1, 7);
        grid.add(priceLabel, 0, 8, 2, 1);
        grid.add(amountPaidLabel, 0, 9, 2, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                LocalDate checkInDate = checkInDatePicker.getValue();
                LocalDate checkOutDate = checkOutDatePicker.getValue();

                if (checkInDate == null || checkOutDate == null || checkInDate.isAfter(checkOutDate) || checkInDate.isEqual(checkOutDate)) {
                    showAlert("Invalid Dates", "Ensure valid check-in and check-out dates.");
                    return null;
                }

                double totalPrice = calculatePrice(roomTypeComboBox.getValue(), checkInDate, checkOutDate, eventCheckBox.isSelected());
                double amountPaid = calculateAmountPaid(bookingTypeComboBox.getValue(), totalPrice, checkInDate);

                Booking newBooking = new Booking(
                        "B" + (bookings.size() + 1),
                        customerNameField.getText(),
                        roomTypeComboBox.getValue(),
                        bookingTypeComboBox.getValue(),
                        checkInDate,
                        checkOutDate,
                        isGroupCheckBox.isSelected(),
                        amountPaid,
                        totalPrice
                );

                if (bookingDAO.addBookingAndAssignRoom(newBooking)) {
                    bookings.add(newBooking);

                    // Open room assignment dialog
                    openRoomAssignmentDialog(newBooking);
                } else {
                    showAlert("Database Error", "Failed to save booking to database.");
                }
                return newBooking;
            }
            return null;
        });

        dialog.showAndWait();
        bookingsTable.refresh();
    }


    private void updatePricing(ComboBox<String> roomTypeComboBox, ComboBox<String> bookingTypeComboBox, DatePicker checkInDatePicker, DatePicker checkOutDatePicker, CheckBox eventCheckBox, Label priceLabel, Label amountPaidLabel) {
        String roomType = roomTypeComboBox.getValue();
        String bookingType = bookingTypeComboBox.getValue();
        LocalDate checkInDate = checkInDatePicker.getValue();
        LocalDate checkOutDate = checkOutDatePicker.getValue();

        if (roomType != null && checkInDate != null && checkOutDate != null) {
            double totalPrice = calculatePrice(roomType, checkInDate, checkOutDate, eventCheckBox.isSelected());
            priceLabel.setText("Total Price: $" + String.format("%.2f", totalPrice));

            if (bookingType != null) {
                double amountPaid = calculateAmountPaid(bookingType, totalPrice, checkInDate);
                amountPaidLabel.setText("Amount Paid: $" + String.format("%.2f", amountPaid));
            } else {
                amountPaidLabel.setText("Amount Paid: $0.00");
            }
        }
    }

    private double calculatePrice(String roomType, LocalDate checkIn, LocalDate checkOut, boolean isEvent) {
        double basePrice = roomBasePrices.getOrDefault(roomType, 0.0);
        double nightlyPrice = basePrice;

        if (isSummer(checkIn)) nightlyPrice *= 1.2;
        if (isChristmas(checkIn)) nightlyPrice *= 1.3;
        if (isOrthodoxEaster(checkIn)) nightlyPrice *= 1.25;
        if (isEvent) nightlyPrice *= 1.4;

        long nights = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
        return nightlyPrice * nights;
    }

    private double calculateAmountPaid(String bookingType, double totalPrice, LocalDate checkInDate) {
        if ("Prepaid".equals(bookingType) && LocalDate.now().isBefore(checkInDate.minusDays(90))) {
            return totalPrice * 0.5;
        } else if ("Traditional".equals(bookingType)) {
            return totalPrice * 0.2;
        }
        return 0.0;
    }

    private boolean isSummer(LocalDate date) {
        return !date.isBefore(LocalDate.of(date.getYear(), 6, 1)) && !date.isAfter(LocalDate.of(date.getYear(), 8, 31));
    }

    private boolean isChristmas(LocalDate date) {
        return !date.isBefore(LocalDate.of(date.getYear(), 12, 20)) && !date.isAfter(LocalDate.of(date.getYear() + 1, 1, 5));
    }

    private boolean isOrthodoxEaster(LocalDate date) {
        LocalDate easterSunday = LocalDate.of(date.getYear(), 4, 8); // Approximation
        return !date.isBefore(easterSunday.minusDays(7)) && !date.isAfter(easterSunday.plusDays(7));
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void checkRoomAvailability() {
        System.out.println("Checking room availability...");
    }

    @FXML
    public void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/roomease/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) bookingsTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("RoomEase - Login");
            stage.setMaximized(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void editBooking() {
        Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            showAlert("No Selection", "Please select a booking to edit.");
            return;
        }

        Dialog<Booking> dialog = new Dialog<>();
        dialog.setTitle("Edit Booking");

        ButtonType saveButtonType = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Form fields pre-filled with selected booking data
        TextField customerNameField = new TextField(selectedBooking.getCustomerName());
        ComboBox<String> roomTypeComboBox = new ComboBox<>(FXCollections.observableArrayList("Single", "Double", "Triple", "Suite"));
        roomTypeComboBox.setValue(selectedBooking.getRoomType());
        ComboBox<String> bookingTypeComboBox = new ComboBox<>(FXCollections.observableArrayList("Prepaid", "Traditional", "Group", "Low Occupancy"));
        bookingTypeComboBox.setValue(selectedBooking.getBookingType());
        DatePicker checkInDatePicker = new DatePicker(selectedBooking.getCheckInDate());
        DatePicker checkOutDatePicker = new DatePicker(selectedBooking.getCheckOutDate());
        CheckBox isGroupCheckBox = new CheckBox();
        isGroupCheckBox.setSelected(selectedBooking.isGroup());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(new Label("Customer Name:"), 0, 0);
        grid.add(customerNameField, 1, 0);
        grid.add(new Label("Room Type:"), 0, 1);
        grid.add(roomTypeComboBox, 1, 1);
        grid.add(new Label("Booking Type:"), 0, 2);
        grid.add(bookingTypeComboBox, 1, 2);
        grid.add(new Label("Check-In Date:"), 0, 3);
        grid.add(checkInDatePicker, 1, 3);
        grid.add(new Label("Check-Out Date:"), 0, 4);
        grid.add(checkOutDatePicker, 1, 4);
        grid.add(new Label("Group:"), 0, 5);
        grid.add(isGroupCheckBox, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                selectedBooking.setCustomerName(customerNameField.getText());
                selectedBooking.setRoomType(roomTypeComboBox.getValue());
                selectedBooking.setBookingType(bookingTypeComboBox.getValue());
                selectedBooking.setCheckInDate(checkInDatePicker.getValue());
                selectedBooking.setCheckOutDate(checkOutDatePicker.getValue());
                selectedBooking.setGroup(isGroupCheckBox.isSelected());

                if (bookingDAO.updateBooking(selectedBooking)) {
                    bookingsTable.refresh();
                } else {
                    showAlert("Database Error", "Failed to update booking.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    @FXML
    public void deleteBooking() {
        Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            showAlert("No Selection", "Please select a booking to delete.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Booking");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete the selected booking?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (bookingDAO.deleteBooking(selectedBooking.getBookingID())) {
                    bookings.remove(selectedBooking);
                } else {
                    showAlert("Database Error", "Failed to delete booking.");
                }
            }
        });
    }

    @FXML
    public void deleteAllBookings() {
        // Confirmation dialog
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete All Bookings");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete all bookings? This action cannot be undone.");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Call DAO method to delete all bookings
                if (bookingDAO.deleteAllBookings()) {
                    bookings.clear(); // Clear the local observable list
                    bookingsTable.refresh();
                    showAlert("Success", "All bookings have been deleted.");
                } else {
                    showAlert("Database Error", "Failed to delete all bookings.");
                }
            }
        });
    }



    @FXML
    public void applyFilters() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String roomType = roomTypeFilterComboBox.getValue();

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            showAlert("Invalid Date Range", "Start date cannot be after end date.");
            return;
        }

        List<Booking> filteredBookings = bookingDAO.getFilteredBookings(startDate, endDate, "All".equals(roomType) ? null : roomType);
        bookings.setAll(filteredBookings);
        bookingsTable.refresh();
    }

    @FXML
    public void resetFilters() {
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        roomTypeFilterComboBox.setValue("All");
        loadBookings();
    }

    public void openRoomAssignmentDialog(Booking booking) {
        Dialog<Void> roomDialog = new Dialog<>();
        roomDialog.setTitle("Assign Rooms");
        roomDialog.setHeaderText("Assign rooms for Booking ID: " + booking.getBookingID());

        // Fetch available rooms for the booking
        List<Room> availableRooms = roomDAO.getAvailableRoomsForDates(
                booking.getRoomType(),
                booking.getCheckInDate(),
                booking.getCheckOutDate()
        );

        ListView<String> roomListView = new ListView<>();
        ObservableList<String> roomNumbers = FXCollections.observableArrayList(
                availableRooms.stream().map(Room::getRoomNumber).toList()
        );
        roomListView.setItems(roomNumbers);
        roomListView.getSelectionModel().clearSelection(); // Ensure no room is pre-selected
        roomListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        ButtonType assignButtonType = new ButtonType("Assign Rooms", ButtonBar.ButtonData.OK_DONE);
        roomDialog.getDialogPane().getButtonTypes().addAll(assignButtonType, ButtonType.CANCEL);
        roomDialog.getDialogPane().setContent(new VBox(
                new Label("Available Rooms for the Booking:"),
                roomListView
        ));

        roomDialog.setResultConverter(dialogButton -> {
            if (dialogButton == assignButtonType) {
                List<String> selectedRooms = roomListView.getSelectionModel().getSelectedItems();
                if (!selectedRooms.isEmpty()) {
                    try {
                        // Assign only the rooms explicitly selected by the user
                        roomDAO.assignRoomsToBooking(booking.getBookingID(), selectedRooms, booking.getCheckInDate(), booking.getCheckOutDate());
                        showAlert("Success", "Rooms assigned successfully.");
                    } catch (Exception e) {
                        showAlert("Error", "An unexpected error occurred: " + e.getMessage());
                    }
                } else {
                    showAlert("No Selection", "You must select at least one room.");
                }
            }
            return null;
        });

        roomDialog.showAndWait();
    }

    @FXML
    public void generateReceipt() {
        // Get the selected booking from the table
        Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();

        if (selectedBooking == null) {
            showAlert("No Selection", "Please select a booking to generate a receipt.");
            return;
        }

        // Generate the receipt
        try {
            PDFGenerator.generateReceipt(selectedBooking);
            showAlert("Success", "Receipt generated successfully.");
        } catch (Exception e) {
            showAlert("Error", "Failed to generate receipt: " + e.getMessage());
        }
    }



}
