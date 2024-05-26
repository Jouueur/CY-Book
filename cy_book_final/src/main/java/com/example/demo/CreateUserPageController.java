package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CreateUserPageController {

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;
    @FXML
    private Button cancelButton;

    private Stage dialogStage;
    private User user;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    }

    /**
     * Sets the dialog stage.
     *
     * @param dialogStage The dialog stage to set.
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Sets the user for this controller.
     *
     * @param user The user to set.
     */
    public void setUser(User user) {
        this.user = user;
        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        emailField.setText(user.getEmail());
    }

    /**
     * Called when the user clicks ok.
     *
     * @throws SQLException if a database access error occurs.
     */
    @FXML
    private void handleOk() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/cy_book",
                    "root",
                    "book"
            );
            if (isInputValid()) {
                User user = new User(firstNameField.getText(), lastNameField.getText(), emailField.getText());
                user.addToDataBase(connection);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.initOwner(dialogStage);
                alert.setTitle("Success");
                alert.setContentText("User successfully added!");
                alert.showAndWait();
                loadPage("userconsult-view.fxml");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Called when the user clicks cancel.
     */
    @FXML
    protected void handleCancel() {
        loadPage("userconsult-view.fxml");
    }

    /**
     * Loads a new FXML page.
     *
     * @param fxmlFile The FXML file to load.
     */
    private void loadPage(String fxmlFile) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setMaximized(true); // Set the window to maximized
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Validates the user input in the text fields.
     *
     * @return true if the input is valid.
     */
    private boolean isInputValid() {
        String errorMessage = "";

        if (firstNameField.getText() == null || firstNameField.getText().isEmpty()) {
            errorMessage += "Please enter a valid first name.\n";
            firstNameField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
        }
        if (lastNameField.getText() == null || lastNameField.getText().isEmpty()) {
            errorMessage += "Please enter a valid last name.\n";
            lastNameField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
        }
        if (emailField.getText() == null || emailField.getText().isEmpty() || !emailField.getText().matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            errorMessage += "Please enter a valid email.\n";
            emailField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            // Show the error message.
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);
            alert.showAndWait();

            return false;
        }
    }
}
