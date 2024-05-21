package com.example.cybookfx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;

public class MenuController {

    @FXML
    private HBox rootHBox;

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
    public void initialize() {
        rootHBox.widthProperty().addListener((obs, oldVal, newVal) -> {
            double newWidth = newVal.doubleValue();
            buttonUserConsult.setPrefWidth(newWidth * 0.3); // 30% de la largeur du HBox
            buttonOverdueBook.setPrefWidth(newWidth * 0.3);
            buttonSearchBook.setPrefWidth(newWidth * 0.3);
        });
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
