package com.example.demo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.IOException;
import java.util.ArrayList;

public class SearchBookController {


    @FXML
    private GridPane basePane;

    @FXML
    private TextField titleSearchBar;
    @FXML
    private TextField firstNameSearchBar;
    @FXML
    private TextField lastNameSearchBar;
    @FXML
    private TextField isbnSearchBar;

    @FXML
    private TextField arkSearchBar;
    @FXML
    private VBox baseVbox;

    @FXML
    private Button backButton;

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
    private int startIndex;

    @FXML
    private void handleBackButton() {
        loadPage("menu-view.fxml");
    }


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


        // Assurer que le titre prend toute la largeur de topBox
        if (topBoxTitle != null) {
            topBox.widthProperty().addListener((obs, oldVal, newVal) -> {
                double newWidth = newVal.doubleValue();
                topBoxTitle.setPrefWidth(newWidth); // Ajuster la largeur du titre en conséquence
            });
        }
    }


    private VBox bookDisplayer(Book book) {
        VBox bookDisplayer = new VBox();
        bookDisplayer.setMaxWidth(600);
        bookDisplayer.setSpacing(10);
        bookDisplayer.setStyle("-fx-border-radius: 5px; -fx-border-color: #e0e0e0;");

        Label title = new Label(book.getTitle());
        Label author = new Label(book.getAuthorFirstName() + " " + book.getAuthorLastName());
        Label edition = new Label(book.getEdition());
        bookDisplayer.getChildren().addAll(title, author, edition);

        bookDisplayer.setOnMouseClicked(event -> {
            showBookDetails(book);
        });

        return bookDisplayer;
    }

    private void showBookDetails(Book book) {
        String details = "Titre : " + book.getTitle() + "\n" +
                "Auteur : " + book.getAuthorFirstName() + " " + book.getAuthorLastName();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails du livre");
        alert.setHeaderText(null);
        alert.setContentText(details);
        alert.showAndWait();
    }

    private GridPane createBookGrid(ArrayList<Book> searchedBooksList) {
        GridPane bookGrid = new GridPane();
        bookGrid.setPrefWidth(600);
        bookGrid.setHgap(20);
        bookGrid.setVgap(20);
        bookGrid.setPadding(new Insets(20));

        int row = 0;
        int col = 0;

        for (int i = startIndex; i < startIndex + 16 && i < searchedBooksList.size(); i++) {
            Book book = searchedBooksList.get(i);
            VBox bookDisplayer = bookDisplayer(book);
            bookGrid.add(bookDisplayer, col, row);

            col++;
            if (col > 3) {
                col = 0;
                row++;
            }
        }

        return bookGrid;
    }


    private HBox createPaginationButtons(ArrayList<Book> searchedBooksList) {
        HBox paginationBox = new HBox();
        paginationBox.setSpacing(10);
        paginationBox.setPadding(new Insets(10));
        paginationBox.setAlignment(Pos.CENTER);

        Button previousButton = new Button("<-");
        previousButton.setOnAction(e -> {
            startIndex = Math.max(0, startIndex - 16);
            updateBookGrid(searchedBooksList);
        });

        Button nextButton = new Button("->");
        nextButton.setOnAction(e -> {
            startIndex = Math.min(searchedBooksList.size() - 1, startIndex + 16);
            updateBookGrid(searchedBooksList);
        });

        // Vérifier si le bouton "Précédent" doit être affiché
        if (startIndex > 0) {
            paginationBox.getChildren().add(previousButton);
        }

        // Vérifier si le bouton "Suivant" doit être affiché
        if (startIndex + 16 < searchedBooksList.size()) {
            paginationBox.getChildren().add(nextButton);
        }

        return paginationBox;
    }

    private VBox createBookDisplayContainer(ArrayList<Book> searchedBooksList) {
        VBox container = new VBox();
        container.setPrefWidth(1200); // Définir une largeur préférée de 600 pixels
        container.setPrefHeight(450); // Définir une largeur préférée de 600 pixels

        GridPane bookGrid = createBookGrid(searchedBooksList);
        ScrollPane scrollPane = new ScrollPane(bookGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        container.getChildren().add(scrollPane);

        HBox paginationBox = createPaginationButtons(searchedBooksList);
        container.getChildren().add(paginationBox);

        return container;
    }


    private void updateBookGrid(ArrayList<Book> searchedBooksList) {
        basePane.getChildren().clear();
        if (searchedBooksList.isEmpty()) {
            Label noResultLabel = new Label("No results found");
            noResultLabel.setStyle("-fx-font-weight: bold");
            basePane.getChildren().add(noResultLabel);
        }
        else{
            VBox bookDisplayContainer = createBookDisplayContainer(searchedBooksList);
            GridPane.setConstraints(bookDisplayContainer, 0, 0, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS, new Insets(10));

            basePane.getChildren().add(bookDisplayContainer);
        }
    }

// Autres méthodes inchangées



    protected void displayBookArk() {
        ArrayList<Book> searchedBooksList = Book.searchByArk(arkSearchBar.getText());
        updateBookGrid(searchedBooksList);
    }

    protected void displayBookTitleAndAuthor() {
        ArrayList<Book> searchedBooksList = Book.searchByTitleAndAuthor(titleSearchBar.getText(), firstNameSearchBar.getText() + " " + lastNameSearchBar.getText());
        updateBookGrid( searchedBooksList);
    }

    protected void displayBookTitle(){
        ArrayList<Book> searchedBooksList = Book.searchByTitle(titleSearchBar.getText());
        updateBookGrid(searchedBooksList);
    }


    protected void displayBookIsbn(){
        ArrayList<Book> searchedBooksList = Book.searchByISBN(isbnSearchBar.getText());
        updateBookGrid(searchedBooksList);
    }

    protected void displayBookAuthor(){
        ArrayList<Book> searchedBooksList = Book.searchByAuthor(firstNameSearchBar.getText(), lastNameSearchBar.getText());
        updateBookGrid( searchedBooksList);
    }

    @FXML
    protected void handleTitleSearchButton(){
        displayBookTitle();
    }

    @FXML
    protected void handleArkSearchButton(){
        displayBookArk();
    }

    @FXML
    protected void handleTitleAndAuthorSearchButton(){
        displayBookTitleAndAuthor();
    }

    @FXML
    protected void handleIsbnSearchButton(){
        displayBookIsbn();
    }

    @FXML
    protected void handleAuthorSearchButton(){
        displayBookAuthor();
    }


    @FXML
    protected void returnToMenu() {
        loadPage("menu-view.fxml");
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