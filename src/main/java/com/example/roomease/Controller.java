package com.example.roomease;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Controller {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button exitButton;

    @FXML
    private Label loginMessage;

    /**
     * Handles the Login button action.
     */
    @FXML
    public void handleLoginButtonAction() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (authenticateEmployee(username, password)) {
            loadDashboard("employee_dashboard.fxml", "RoomEase - Employee Dashboard");
        } else if (authenticateManager(username, password)) {
            loadDashboard("manager_dashboard.fxml", "RoomEase - Manager Dashboard");
        } else {
            loginMessage.setText("Invalid username or password.");
        }
    }

    /**
     * Handles the Exit button action to close the application.
     */
    @FXML
    public void handleExitButtonAction() {
        Platform.exit();  // Terminate the application
    }

    /**
     * Dummy authentication logic for an employee.
     */
    private boolean authenticateEmployee(String username, String password) {
        // Placeholder credentials for an employee
        return "employee".equals(username) && "password".equals(password);
    }

    /**
     * Dummy authentication logic for a manager.
     */
    private boolean authenticateManager(String username, String password) {
        // Placeholder credentials for a manager
        return "manager".equals(username) && "password".equals(password);
    }

    /**
     * Loads the specified dashboard.
     *
     * @param fxmlFileName The FXML file for the dashboard.
     * @param title        The title of the window.
     */
    private void loadDashboard(String fxmlFileName, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/roomease/" + fxmlFileName));
            Parent root = loader.load();
            Stage stage = (Stage) loginButton.getScene().getWindow();  // Get the current stage
            stage.setScene(new Scene(root));  // Set new scene to the stage
            stage.setTitle(title);  // Update the title
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            loginMessage.setText("Failed to load the dashboard. Please try again.");
        }
    }
}
