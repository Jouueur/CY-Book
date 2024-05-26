/*package com.example.demo;


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
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import java.io.IOException;
import java.sql.Connection;
import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserSearchController implements Initializable {
    @FXML private TableView<User> userTableView;
    @FXML private TableColumn<User,String> firstNameColumn;
    @FXML private TableColumn<User,String> lastNameColumn;
    @FXML private TableColumn<User,String> emailColumn;
    @FXML private TextField keywordTextField;
    @FXML private Button backButton;
    @FXML private Button addUserButton;
    @FXML private Button selectUserButton;

    ObservableList<User> userObservableList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    DatabaseConnection connectNow = new DatabaseConnection();
    Connection connectDB = connectNow.getDBConnection();
    //sql query
    String userViewQuery = "SELECT name_customer, last_name_customer, email_customer FROM customer";

    try{
        Statement statement = connectDB.createStatement();
        ResultSet queryOutput = statement.executeQuery(userViewQuery);
        while(queryOutput.next()){
            //populate the observable list
            userObservableList.add(new User(queryOutput.getString("name_customer"),queryOutput.getString("last_name_customer"),queryOutput.getString("email_customer")));
        }

        firstNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<User, String>("email"));

        userTableView.setItems(userObservableList);

        FilteredList<User> filteredData = new FilteredList<>(userObservableList, b -> true);

        keywordTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(user -> {
                if (newValue == null || newValue.isEmpty() || newValue.isBlank()) {
                    return true;
                }
                String searchKeywords = newValue.toLowerCase();
                if(user.getFirstName().toLowerCase().contains(searchKeywords)){
                    return true;
                }
                else if(user.getLastName().toLowerCase().contains(searchKeywords)){
                    return true;
                }
                else if(user.getEmail().toLowerCase().contains(searchKeywords)){
                    return true;
                }
                else return false;
            });
        });

        SortedList<User> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(userTableView.comparatorProperty());
        userTableView.setItems(sortedData);
    }
    catch (SQLException e){
        Logger.getLogger(UserSearchController.class.getName()).log(Level.SEVERE, null, e);
        e.printStackTrace();
    }

}

    @FXML
    protected void handleBack() {
        loadPage("menu-view.fxml");
    }
    @FXML
    protected void handleAddUser() {
        loadPage("CreateUserPage.fxml");
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

    private void selectUser() {
        try{
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/cy_book",
                    "root",
                    "book"
            );
            User selectedUser = userTableView.getSelectionModel().getSelectedItem();
            selectedUser.isInDataBase(connection);
            {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("User Selected");
                alert.setHeaderText(null);
                alert.setContentText("Selected user: " + selectedUser.getFirstName() + " " + selectedUser.getLastName() + ", Email: " + selectedUser.getEmail() +"id :" + selectedUser.userConnection(connection));
                alert.showAndWait();
            }
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void handleSelectUserButton() {
        selectUser();
    }

}
*/