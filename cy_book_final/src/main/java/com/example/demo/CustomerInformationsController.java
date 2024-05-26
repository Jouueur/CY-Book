package com.example.demo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Pattern;

public class CustomerInformationsController {

    @FXML private TableView<Borrowing> tab;
    @FXML private TableColumn<Borrowing, String> booksOnLoanCol;
    @FXML private TableColumn<Borrowing, String> previousLoanCol;
    @FXML private TableColumn<Borrowing, String> booksToBeReturnedCol;

    @FXML private Label firstName;
    @FXML private Label lastName;
    @FXML private Label email;

    @FXML private Button editFirstName;
    @FXML private Button editLastName;
    @FXML private Button editEmail;
    @FXML private Button editFirstName1;
    @FXML private Button addLoan;

    @FXML private ListView<Book> listViewOnLoan;
    @FXML private ListView<Book> listViewOverDueBorrowingBook;
    @FXML private ListView<Book> listViewOverDueBorrowingNotReturnedBook;

    private User user;
    private ArrayList<Book> booksOnLoan;
    private ArrayList<Book> overDueBorrowingBook;
    private ArrayList<Book> overDueBorrowingNotReturnedBook;

    /**
     * Sets the first name of the user.
     */
    @FXML
    private void setEditFirstName() {
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/cy_book",
                "root",
                "book")) {

            int userId = user.userConnection(connection);
            user.updateFirstNameById(userId, "zakaria", connection);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Could not update first name", Alert.AlertType.ERROR);
        }
    }

    /**
     * Opens a popup to edit the first name.
     */
    @FXML
    private void editFirstNamePopup() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Edit First Name");
        dialog.setHeaderText("Enter new first name:");
        dialog.setContentText("First Name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newFirstName -> {
            if (isValidName(newFirstName)) {
                try (Connection connection = DriverManager.getConnection(
                        "jdbc:mysql://127.0.0.1:3306/cy_book",
                        "root",
                        "book")) {

                    int userId = user.userConnection(connection);
                    user.updateFirstNameById(userId, newFirstName, connection);
                    firstName.setText(newFirstName);
                    user.setFirstName(newFirstName);

                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Error", "Could not update first name", Alert.AlertType.ERROR);
                }
            } else {
                showAlert("Invalid Input", "First name must not be empty, contain numbers, or special characters.", Alert.AlertType.ERROR);
            }
        });
    }

    /**
     * Opens a popup to edit the last name.
     */
    @FXML
    private void editLastNamePopup() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Edit Last Name");
        dialog.setHeaderText("Enter new last name:");
        dialog.setContentText("Last Name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newLastName -> {
            if (isValidName(newLastName)) {
                try (Connection connection = DriverManager.getConnection(
                        "jdbc:mysql://127.0.0.1:3306/cy_book",
                        "root",
                        "book")) {

                    int userId = user.userConnection(connection);
                    user.updateLastNameById(userId, newLastName, connection);
                    lastName.setText(newLastName);
                    user.setLastName(newLastName);

                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Error", "Could not update last name", Alert.AlertType.ERROR);
                }
            } else {
                showAlert("Invalid Input", "Last name must not be empty, contain numbers, or special characters.", Alert.AlertType.ERROR);
            }
        });
    }

    /**
     * Opens a popup to edit the email.
     */
    @FXML
    private void editEmailPopup() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Edit Email");
        dialog.setHeaderText("Enter new email:");
        dialog.setContentText("Email:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newEmail -> {
            if (isValidEmail(newEmail)) {
                try (Connection connection = DriverManager.getConnection(
                        "jdbc:mysql://127.0.0.1:3306/cy_book",
                        "root",
                        "book")) {

                    int userId = user.userConnection(connection);
                    user.updateEmailById(userId, newEmail, connection);
                    email.setText(newEmail);
                    user.setEmail(newEmail);

                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Error", "Could not update email", Alert.AlertType.ERROR);
                }
            } else {
                showAlert("Invalid Input", "Email must be in a valid format (e.g., user@example.com).", Alert.AlertType.ERROR);
            }
        });
    }

    /**
     * Sets the lists of books for the user.
     */
    private void setUserLists(){
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/cy_book",
                "root",
                "book")) {

            booksOnLoan = user.borrowingStillOutstandingBook(connection);
            listViewOnLoan.getItems().addAll(booksOnLoan);

            overDueBorrowingBook = user.overDueBorrowingBook(connection);
            listViewOverDueBorrowingBook.getItems().addAll(overDueBorrowingBook);

            overDueBorrowingNotReturnedBook = user.overDueBorrowingNotReturnedBook(connection);
            listViewOverDueBorrowingNotReturnedBook.getItems().addAll(overDueBorrowingNotReturnedBook);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Validates a name.
     *
     * @param name The name to validate.
     * @return true if the name is valid, false otherwise.
     */
    private boolean isValidName(String name) {
        return name != null && name.matches("[\\p{L} '-]+");
    }

    /**
     * Validates an email.
     *
     * @param email The email to validate.
     * @return true if the email is valid, false otherwise.
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return email != null && pattern.matcher(email).matches();
    }

    /**
     * Sets the user and initializes the user details and lists.
     *
     * @param user The user to set.
     */
    public void setUser(User user) {
        this.user = user;
        firstName.setText(user.getFirstName());
        lastName.setText(user.getLastName());
        email.setText(user.getEmail());
        setUserLists();
    }

    /**
     * Shows an alert with the specified title, message, and alert type.
     *
     * @param title The title of the alert.
     * @param message The message to display.
     * @param alertType The type of alert.
     */
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Returns the selected book from the listViewOnLoan.
     *
     * @return The selected Book object, or null if no book is selected.
     */
    public Book getSelectedBookOnLoan() {
        return listViewOnLoan.getSelectionModel().getSelectedItem();
    }

    /**
     * Shows details of the selected book and removes it from the loan list.
     */
    @FXML
    private void showSelectedBookDetails() {
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/cy_book",
                "root",
                "book")) {

            Book selectedBook = getSelectedBookOnLoan();
            if (selectedBook != null) {
                switch (user.modifBorrowing(connection, selectedBook.getARK())) {
                    case 0:
                        showAlert("Success", "Book successfully removed", Alert.AlertType.INFORMATION);
                        break;
                    case -1:
                        showAlert("User error", "No users found", Alert.AlertType.WARNING);
                        break;
                    case -2:
                        showAlert("Database error", "Error in SQL request", Alert.AlertType.WARNING);
                        break;
                }
            } else {
                showAlert("No Selection", "No book selected.", Alert.AlertType.WARNING);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the add loan view.
     */
    @FXML
    void addLoanAction(){
        String fxmlFile = "bookloan-view.fxml";
        loadPage2(fxmlFile,user);
    }

    /**
     * Loads the specified FXML file into a new stage.
     *
     * @param fxmlFile The path to the FXML file.
     * @param user The User object to pass to the controller.
     */
    private void loadPage2(String fxmlFile, User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            BookLoanController bookLoanController = loader.getController();
            bookLoanController.setUser(user);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
