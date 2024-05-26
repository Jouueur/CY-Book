package com.example.demo;

import java.net.ConnectException;
import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.InputMismatchException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {


    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("menu-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1500, 700);
        stage.setTitle("Library software");
        stage.setScene(scene);


        stage.setMaximized(true);


        stage.show();
    }

    public static void main(String[] args) {
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/cy_book",
                    "root",
                    "book"
            );
            launch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
