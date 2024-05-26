package com.example.demo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuController {

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
    private Button buttonUserConsult;

    @FXML
    private Button buttonOverdueBook;

    @FXML
    private Button buttonSearchBook;

    @FXML
    private Label topBoxTitle;

    @FXML
    public void initialize() {
        rootVBox.heightProperty().addListener((obs, oldVal, newVal) -> {
            double newHeight = newVal.doubleValue();
            topBox.setPrefHeight(newHeight * 0.15); // 15% of the parent VBox height
            mainBox.setPrefHeight(newHeight * 0.85); // 85% of the parent VBox height
        });

        rootVBox.widthProperty().addListener((obs, oldVal, newVal) -> {
            double newWidth = newVal.doubleValue();
            leftBox.setPrefWidth(newWidth * 0.3); // 30% of the parent VBox width
            rightBox.setPrefWidth(newWidth * 0.7); // 70% of the parent VBox width
        });

        mainBox.widthProperty().addListener((obs, oldVal, newVal) -> {
            double newWidth = newVal.doubleValue();
            buttonUserConsult.setPrefWidth(newWidth * 0.3); // Adjust button widths accordingly
            buttonOverdueBook.setPrefWidth(newWidth * 0.3);
            buttonSearchBook.setPrefWidth(newWidth * 0.3);
        });

        // Ensure the title takes up the full width of topBox
        if (topBoxTitle != null) {
            topBox.widthProperty().addListener((obs, oldVal, newVal) -> {
                double newWidth = newVal.doubleValue();
                topBoxTitle.setPrefWidth(newWidth); // Adjust the title width accordingly
            });
        }
    }

    @FXML
    protected void onOverdueBookButtonClick() {
        loadPage("overduebook-view.fxml");
    }

    @FXML
    protected void onSearchBookButtonClick() {
        loadPage("searchbook-view.fxml");
    }

    @FXML
    protected void onUserConsultButtonClick() {
        loadPage("userconsult-view.fxml");
    }

    private void loadPage(String fxmlFile) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) buttonOverdueBook.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
            stage.setMaximized(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void loadPage2(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            OverdueBookController overdueBookController = loader.getController();
            overdueBookController.initialize();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
