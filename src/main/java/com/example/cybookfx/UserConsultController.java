package com.example.cybookfx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import java.io.IOException;

public class UserConsultController {

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
    public void initialize() {
        rootVBox.heightProperty().addListener((obs, oldVal, newVal) -> {
            double newHeight = newVal.doubleValue();
            topBox.setPrefHeight(newHeight * 0.15); // 15% de la hauteur du VBox parent
            mainBox.setPrefHeight(newHeight * 0.85); // 85% de la hauteur du VBox parent
        });

        rootVBox.widthProperty().addListener((obs, oldVal, newVal) -> {
            double newWidth = newVal.doubleValue();
            leftBox.setPrefWidth(newWidth * 0.3); // 30% de la largeur du VBox parent
            rightBox.setPrefWidth(newWidth * 0.7); // 70% de la largeur du VBox parent
        });

        mainBox.widthProperty().addListener((obs, oldVal, newVal) -> {
            double newWidth = newVal.doubleValue();
            buttonBookLoan.setPrefWidth(newWidth * 0.3); // Ajuster la largeur des boutons en conséquence
            buttonAddUser.setPrefWidth(newWidth * 0.3); // Ajuster la largeur des boutons en conséquence

        });

        // Assurer que le titre prend toute la largeur de topBox
        if (topBoxTitle != null) {
            topBox.widthProperty().addListener((obs, oldVal, newVal) -> {
                double newWidth = newVal.doubleValue();
                topBoxTitle.setPrefWidth(newWidth); // Ajuster la largeur du titre en conséquence
            });
        }
    }

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
