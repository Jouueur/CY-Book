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
import javafx.scene.control.Alert.AlertType;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class BookLoanController {

    @FXML
    private GridPane basePane;
    @FXML
    private Button cancelButton;
    @FXML
    private Button selectButton;
    @FXML
    private Button titleSearchButton;
    @FXML
    private TextField titleSearchBar;
    @FXML
    private Button authorSearchButton;
    @FXML
    private TextField firstNameSearchBar;
    @FXML
    private TextField lastNameSearchBar;
    @FXML
    private Button titleAndAuthorSearchButton;
    @FXML
    private Button isbnSearchButton;
    @FXML
    private TextField isbnSearchBar;
    @FXML
    private Button arkSearchButton;
    @FXML
    private TextField arkSearchBar;
    @FXML
    private VBox baseVbox;
    @FXML
    private HBox Hbox;
    private int startIndex;

    /**
     * Creates a VBox to display book information.
     *
     * @param book The book to display.
     * @return The VBox containing book details.
     */
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
            handleBorrowing(book);
        });

        return bookDisplayer;
    }

    /**
     * Shows book details in an alert.
     *
     * @param book The book to display details for.
     */
    private void showBookDetails(Book book) {
        String details = "Title: " + book.getTitle() + "\n" +
                "Author: " + book.getAuthorFirstName() + " " + book.getAuthorLastName();

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Book Details");
        alert.setHeaderText(null);
        alert.setContentText(details);
        alert.showAndWait();
    }

    /**
     * Creates a grid of books.
     *
     * @param searchedBooksList The list of books to display.
     * @return The GridPane containing book display VBoxes.
     */
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

    /**
     * Creates pagination buttons for book navigation.
     *
     * @param searchedBooksList The list of books to paginate.
     * @return The HBox containing pagination buttons.
     */
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

        if (startIndex > 0) {
            paginationBox.getChildren().add(previousButton);
        }

        if (startIndex + 16 < searchedBooksList.size()) {
            paginationBox.getChildren().add(nextButton);
        }

        return paginationBox;
    }

    /**
     * Creates a container for displaying books with pagination.
     *
     * @param searchedBooksList The list of books to display.
     * @return The VBox container with book displays and pagination.
     */
    private VBox createBookDisplayContainer(ArrayList<Book> searchedBooksList) {
        VBox container = new VBox();
        container.setPrefWidth(1200);
        container.setPrefHeight(450);

        GridPane bookGrid = createBookGrid(searchedBooksList);
        ScrollPane scrollPane = new ScrollPane(bookGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        container.getChildren().add(scrollPane);

        HBox paginationBox = createPaginationButtons(searchedBooksList);
        container.getChildren().add(paginationBox);

        return container;
    }

    /**
     * Updates the book grid with the searched book list.
     *
     * @param searchedBooksList The list of books to display.
     */
    private void updateBookGrid(ArrayList<Book> searchedBooksList) {
        basePane.getChildren().clear();
        if (searchedBooksList.isEmpty()) {
            Label noResultLabel = new Label("No results found");
            noResultLabel.setStyle("-fx-font-weight: bold");
            basePane.getChildren().add(noResultLabel);
        } else {
            VBox bookDisplayContainer = createBookDisplayContainer(searchedBooksList);
            GridPane.setConstraints(bookDisplayContainer, 0, 0, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS, new Insets(10));

            basePane.getChildren().add(bookDisplayContainer);
        }
    }

    /**
     * Displays books based on ARK search.
     */
    protected void displayBookArk() {
        ArrayList<Book> searchedBooksList = Book.searchByArk(arkSearchBar.getText());
        updateBookGrid(searchedBooksList);
    }

    /**
     * Displays books based on title and author search.
     */
    protected void displayBookTitleAndAuthor() {
        ArrayList<Book> searchedBooksList = Book.searchByTitleAndAuthor(titleSearchBar.getText(), firstNameSearchBar.getText() + " " + lastNameSearchBar.getText());
        updateBookGrid(searchedBooksList);
    }

    /**
     * Displays books based on title search.
     */
    protected void displayBookTitle() {
        ArrayList<Book> searchedBooksList = Book.searchByTitle(titleSearchBar.getText());
        updateBookGrid(searchedBooksList);
    }

    /**
     * Displays books based on ISBN search.
     */
    protected void displayBookIsbn() {
        ArrayList<Book> searchedBooksList = Book.searchByISBN(isbnSearchBar.getText());
        updateBookGrid(searchedBooksList);
    }

    /**
     * Displays books based on author search.
     */
    protected void displayBookAuthor() {
        ArrayList<Book> searchedBooksList = Book.searchByAuthor(firstNameSearchBar.getText(), lastNameSearchBar.getText());
        updateBookGrid(searchedBooksList);
    }

    @FXML
    protected void handleTitleSearchButton() {
        displayBookTitle();
    }

    @FXML
    protected void handleArkSearchButton() {
        displayBookArk();
    }

    @FXML
    protected void handleTitleAndAuthorSearchButton() {
        displayBookTitleAndAuthor();
    }

    @FXML
    protected void handleIsbnSearchButton() {
        displayBookIsbn();
    }

    @FXML
    protected void handleAuthorSearchButton() {
        displayBookAuthor();
    }

    @FXML
    protected void returnToUserConsult() {
        loadPage("userconsult-view.fxml");
    }

    /**
     * Loads a new FXML page.
     *
     * @param fxmlFile The FXML file to load.
     */
    private void loadPage(String fxmlFile) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private User user;

    /**
     * Sets the user for this controller.
     *
     * @param user The user to set.
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Shows an alert with the specified title, message, and alert type.
     *
     * @param title The title of the alert.
     * @param message The message to display.
     * @param alertType The type of alert.
     */
    private void showAlert(String title, String message, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Handles the borrowing process for a book.
     *
     * @param book The book to borrow.
     */
    private void handleBorrowing(Book book) {
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/cy_book",
                "root",
                "book")) {

            int user_id = user.userConnection(connection);

            Borrowing borrowing = new Borrowing(user_id, book.getARK());

            switch (user.addBorrowing(connection, borrowing)) {
                case 0:
                    showAlert(user.getFirstName() + " " + user.getLastName(), "The book is now on loan and you have 2 weeks to return it.", AlertType.INFORMATION);
                    break;
                case -1:
                    showAlert("User Error", "The user does not exist", AlertType.ERROR);
                    break;
                case -2:
                    showAlert("User Error", "The user already has 3 loans in progress", AlertType.ERROR);
                    break;
                case -3:
                    showAlert("Database Error", "There's an SQL error during the insertion", AlertType.ERROR);
                    break;
                case -4:
                    showAlert("Loan Error", "The book is already on loan", AlertType.ERROR);
                    break;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
