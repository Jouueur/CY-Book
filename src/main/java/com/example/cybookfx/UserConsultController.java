package com.example.cybookfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserConsultController implements Initializable {

    @FXML
    private Button backButton;

    @FXML
    private Button buttonBookLoan;

    @FXML
    private Button buttonAddUser;

    @FXML
    private VBox rootVBox;

    @FXML
    private HBox topBox;

    @FXML
    private HBox mainBox;

    @FXML
    private VBox leftBox;

    @FXML
    private VBox rightBox;

    @FXML
    private Label topBoxTitle;

    @FXML
    private TableView<User> userTableView;
    @FXML
    private TableColumn<User, String> firstNameColumn;
    @FXML
    private TableColumn<User, String> lastNameColumn;
    @FXML
    private TableColumn<User, String> emailColumn;
    @FXML
    private TextField keywordTextField;
    @FXML
    private Button selectUserButton;

    private ObservableList<User> userObservableList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureLayoutBindings();
        loadUserData();
        setupTableView();
        setupSearchFunctionality();
    }

    private void configureLayoutBindings() {
        rootVBox.heightProperty().addListener((obs, oldVal, newVal) -> {
            double newHeight = newVal.doubleValue();
            topBox.setPrefHeight(newHeight * 0.15);
            mainBox.setPrefHeight(newHeight * 0.85);
        });

        rootVBox.widthProperty().addListener((obs, oldVal, newVal) -> {
            double newWidth = newVal.doubleValue();
            leftBox.setPrefWidth(newWidth * 0.3);
            rightBox.setPrefWidth(newWidth * 0.7);
        });

        mainBox.widthProperty().addListener((obs, oldVal, newVal) -> {
            double newWidth = newVal.doubleValue();
            buttonBookLoan.setPrefWidth(newWidth * 0.3);
            buttonAddUser.setPrefWidth(newWidth * 0.3);
        });

        topBox.widthProperty().addListener((obs, oldVal, newVal) -> {
            double newWidth = newVal.doubleValue();
            topBoxTitle.setPrefWidth(newWidth);
        });
    }

    private void loadUserData() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getDBConnection();
        String userViewQuery = "SELECT name_customer, last_name_customer, email_customer FROM customer";

        try (Statement statement = connectDB.createStatement(); ResultSet queryOutput = statement.executeQuery(userViewQuery)) {
            while (queryOutput.next()) {
                userObservableList.add(new User(
                        queryOutput.getString("name_customer"),
                        queryOutput.getString("last_name_customer"),
                        queryOutput.getString("email_customer")));
            }
        } catch (SQLException e) {
            Logger.getLogger(UserConsultController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void setupTableView() {
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        userTableView.setItems(userObservableList);
    }

    private void setupSearchFunctionality() {
        FilteredList<User> filteredData = new FilteredList<>(userObservableList, b -> true);

        keywordTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(user -> {
                if (newValue == null || newValue.isEmpty() || newValue.isBlank()) {
                    return true;
                }
                String searchKeywords = newValue.toLowerCase();
                return user.getFirstName().toLowerCase().contains(searchKeywords) ||
                        user.getLastName().toLowerCase().contains(searchKeywords) ||
                        user.getEmail().toLowerCase().contains(searchKeywords);
            });
        });

        SortedList<User> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(userTableView.comparatorProperty());
        userTableView.setItems(sortedData);
    }

    @FXML
    private void handleBackButton() {
        loadPage("menu-view.fxml");
    }

    @FXML
    protected void openBookLoan() {
        loadPage("bookloan-view.fxml");
    }

    @FXML
    protected void handleAddUser() {
        loadPage("CreateUserPage.fxml");
    }

    @FXML
    private void selectUser() {
        User selectedUser = userTableView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            try (Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3307/cy_book",
                    "root",
                    "book")) {
                selectedUser.isInDataBase(connection);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("User Selected");
                alert.setHeaderText(null);
                alert.setContentText("Selected user: " + selectedUser.getFirstName() + " " + selectedUser.getLastName() + ", Email: " + selectedUser.getEmail() + " id: " + selectedUser.userConnection(connection));
                alert.showAndWait();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    protected void handleSelectUserButton() {
        selectUser();
    }

    private void loadPage(String fxmlFile) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
