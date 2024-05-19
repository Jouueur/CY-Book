package com.example.cybookfx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import java.io.IOException;

public class UserConsultController {

    @FXML
    private Button backButton;

    @FXML
    public void handleBackButton() {
        loadPage("menu-view.fxml");
    }

    @FXML
    protected void openBookLoan() {
        loadPage("bookloan-view.fxml");
    }

    @FXML
    protected void openAddUser() {
        loadPage("createuserpage-view.fxml");
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
