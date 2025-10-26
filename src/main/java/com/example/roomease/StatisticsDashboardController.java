package com.example.roomease;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.Month;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StatisticsDashboardController {

    @FXML
    private Button returnButton;

    @FXML
    private Label totalRevenueLabel;

    @FXML
    private ComboBox<String> monthComboBox;

    @FXML
    private ComboBox<Integer> yearComboBox;

    @FXML
    private Label monthlyRevenueLabel;

    @FXML
    private Label totalBookingsLabel;

    @FXML
    private Label monthlyBookingsLabel;

    @FXML
    private ComboBox<Integer> yearComboBoxForMostBookings;

    @FXML
    private Label mostBookingsLabel;

    private final BookingDAO bookingDAO = new BookingDAO();

    @FXML
    public void initialize() {
        loadTotalRevenue();
        loadTotalBookings();

        monthComboBox.setItems(FXCollections.observableArrayList(
                IntStream.rangeClosed(1, 12)
                        .mapToObj(month -> Month.of(month).name())
                        .collect(Collectors.toList())
        ));

        int currentYear = LocalDate.now().getYear();
        yearComboBox.setItems(FXCollections.observableArrayList(
                IntStream.rangeClosed(currentYear - 20, currentYear + 10).boxed().collect(Collectors.toList())
        ));

        yearComboBoxForMostBookings.setItems(FXCollections.observableArrayList(
                IntStream.rangeClosed(currentYear - 20, currentYear + 10).boxed().collect(Collectors.toList())
        ));
    }

    private void loadTotalRevenue() {
        double totalRevenue = bookingDAO.getRevenueForDateRange(null, null);
        totalRevenueLabel.setText(String.format("Total Revenue: $%.2f", totalRevenue));
    }

    private void loadTotalBookings() {
        int totalBookings = bookingDAO.getTotalBookings();
        totalBookingsLabel.setText("Total Bookings: " + totalBookings);
    }

    @FXML
    public void handleCalculateMonthlyRevenue() {
        String selectedMonth = monthComboBox.getValue();
        Integer selectedYear = yearComboBox.getValue();

        if (selectedMonth == null || selectedYear == null) {
            showAlert("Invalid Selection", "Please select a valid month and year.");
            return;
        }

        Month month = Month.valueOf(selectedMonth.toUpperCase());
        LocalDate startDate = LocalDate.of(selectedYear, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        double monthlyRevenue = bookingDAO.getRevenueForDateRange(startDate, endDate);
        monthlyRevenueLabel.setText(String.format("Revenue for %s %d: $%.2f", selectedMonth, selectedYear, monthlyRevenue));
    }

    @FXML
    public void handleShowMonthlyBookings() {
        String selectedMonth = monthComboBox.getValue();
        Integer selectedYear = yearComboBox.getValue();

        if (selectedMonth == null || selectedYear == null) {
            showAlert("Invalid Selection", "Please select a valid month and year.");
            return;
        }

        Month month = Month.valueOf(selectedMonth.toUpperCase());
        int bookings = bookingDAO.getMonthlyBookings(selectedYear, month.getValue());
        monthlyBookingsLabel.setText(String.format("Bookings for %s %d: %d", selectedMonth, selectedYear, bookings));
    }

    @FXML
    public void handleMostBookings() {
        Integer selectedYear = yearComboBoxForMostBookings.getValue();

        if (selectedYear == null) {
            showAlert("Invalid Selection", "Please select a valid year.");
            return;
        }

        String mostBookedMonth = bookingDAO.getMonthWithMostBookings(selectedYear);
        mostBookingsLabel.setText("Most Bookings in: " + mostBookedMonth + " " + selectedYear);
    }

    @FXML
    public void returnToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/roomease/manager_dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) returnButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Unable to return to the Manager Dashboard.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
