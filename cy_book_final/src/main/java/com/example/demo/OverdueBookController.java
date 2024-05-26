package com.example.demo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class OverdueBookController {

    @FXML
    private ListView<Book> bookListView;

    @FXML
    private  ListView<Book> bookListView2;

    private ArrayList<Book> booksMostBorrowed;
    private ArrayList<Book> booksToReturn;

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
    private Button backButton;


    @FXML
    public void initialize() {

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

        if (topBoxTitle != null) {
            topBox.widthProperty().addListener((obs, oldVal, newVal) -> {
                double newWidth = newVal.doubleValue();
                topBoxTitle.setPrefWidth(newWidth);
            });
        }

        setPage();
    }




    private void setPage() {
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/cy_book",
                "root",
                "book")) {

            booksMostBorrowed = Borrowing.getMostBorrowedBooksLast30Days(connection, 5);
            bookListView.getItems().addAll(booksMostBorrowed);

            booksToReturn = Borrowing.displayOverdueBooksISBN(connection);
            bookListView2.getItems().addAll(booksToReturn);

        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    @FXML
    private void handleBackButton() {
        loadPage("menu-view.fxml");
    }


}
