package com.example.demo;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


/**
 Represents a borrowing record in the CYBook system, indicating the loan of a book by a user for a specific period.
 */
public class Borrowing {
    private int id_user;
    private LocalDate STARTING_DATE;
    private LocalDate END_DATE;
    private String ark;

    /**
     * Constructeur de la classe Borrowing avec toutes les informations spécifiées.
     *
     * @param id_user       L'ID de l'utilisateur associé à cet emprunt.
     * @param STARTING_DATE La date de début de cet emprunt.
     * @param END_DATE      La date de fin de cet emprunt.
     * @param ark           L'ARK du livre associé à cet emprunt.
     */
    public Borrowing(int id_user, LocalDate STARTING_DATE, LocalDate END_DATE, String ark) {
        this.id_user = id_user;
        this.STARTING_DATE = STARTING_DATE;
        this.END_DATE = END_DATE;
        this.ark = ark;
    }

    /**
     * Constructeur de la classe Borrowing avec les informations minimales spécifiées.
     * La date de début est définie sur la date actuelle, et la date de fin est définie sur deux semaines après la date actuelle.
     *
     * @param id_user L'ID de l'utilisateur associé à cet emprunt.
     * @param ark     L'ARK du livre associé à cet emprunt.
     */
    public Borrowing(int id_user, String ark) {
        this.id_user = id_user;
        this.STARTING_DATE = LocalDate.now();
        this.END_DATE = LocalDate.now().plusWeeks(2);
        this.ark = ark;
    }


    /**
     * Checks if the user has less than three borrowings in the database.
     *
     * @param connection The database connection.
     * @param id_user The ID of the user.
     * @return true if the user has less than three borrowings, false otherwise.
     */
    public static boolean borrowingAvailable(Connection connection, int id_user) {
        String query = "SELECT COUNT(*) FROM borrowing WHERE  borrowed = true AND id_customer = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id_user);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) < 3;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * Checks if a book with the specified Ark is already borrowed.
     *
     * @param connection The database connection.
     * @param ark The Ark of the book to check.
     * @return true if the book is not already borrowed, false otherwise.
     */
    public static boolean bookAlreadyBorrowed(Connection connection, String ark){

        String query = "SELECT COUNT(*) FROM borrowing WHERE borrowed = true AND isbn = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, ark);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) == 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Renvoie l'ID de l'utilisateur associé à cet objet Borrowing.
     *
     * @return L'ID de l'utilisateur associé à cet objet Borrowing.
     */
    public int getId_user() {
        return id_user;
    }

    /**
     * Définit l'ID de l'utilisateur associé à cet objet Borrowing.
     *
     * @param id_user Le nouvel ID de l'utilisateur.
     */
    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    /**
     * Renvoie la date de début de l'emprunt associé à cet objet Borrowing.
     *
     * @return La date de début de l'emprunt associé à cet objet Borrowing.
     */
    public LocalDate getSTARTING_DATE() {
        return STARTING_DATE;
    }

    /**
     * Définit la date de début de l'emprunt associé à cet objet Borrowing.
     *
     * @param STARTING_DATE La nouvelle date de début de l'emprunt.
     */
    public void setSTARTING_DATE(LocalDate STARTING_DATE) {
        this.STARTING_DATE = STARTING_DATE;
    }

    /**
     * Renvoie la date de fin de l'emprunt associé à cet objet Borrowing.
     *
     * @return La date de fin de l'emprunt associé à cet objet Borrowing.
     */
    public LocalDate getEND_DATE() {
        return END_DATE;
    }

    /**
     * Définit la date de fin de l'emprunt associé à cet objet Borrowing.
     *
     * @param END_DATE La nouvelle date de fin de l'emprunt.
     */
    public void setEND_DATE(LocalDate END_DATE) {
        this.END_DATE = END_DATE;
    }

    /**
     * Renvoie l'ARK du livre associé à cet objet Borrowing.
     *
     * @return L'ARK du livre associé à cet objet Borrowing.
     */
    public String getArk() {
        return ark;
    }

    /**
     * Définit l'ARK du livre associé à cet objet Borrowing.
     *
     * @param ark Le nouvel ARK du livre.
     */
    public void setArk(String ark) {
        this.ark = ark;
    }

    /**
     * Retrieves a list of overdue books based on the borrowing records where the end date is past the current date
     * and the book is still marked as borrowed. This method returns an ArrayList of Book objects corresponding to
     * the overdue books.
     *
     * @param connection the database connection to use for the query.
     * @return an ArrayList of Book objects corresponding to the overdue books.
     */
    public static ArrayList<Book> displayOverdueBooksISBN(Connection connection) {
        ArrayList<String> overdueBooksISBN = new ArrayList<>();
        String sql = "SELECT isbn FROM borrowing WHERE end_date < CURDATE() AND borrowed = true";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String isbn = rs.getString("isbn");
                overdueBooksISBN.add(isbn);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        ArrayList<Book> overdueBooks = new ArrayList<>();
        for (String isbn : overdueBooksISBN) {
            overdueBooks.addAll(Book.searchByArk(isbn));
        }

        return overdueBooks;
    }

    /**
     * Retrieves a list of the most borrowed books in the last 30 days, limited to a specified number of books.
     * The method queries the borrowing records, identifies the most borrowed books,
     * and retrieves detailed information about each book using the searchByArk method.
     *
     * @param connection the database connection to use for the query.
     * @param limit the maximum number of most borrowed books to return.
     * @return an ArrayList of Book objects corresponding to the most borrowed books in the last 30 days.
     */
    public static ArrayList<Book> getMostBorrowedBooksLast30Days(Connection connection, int limit) {
        ArrayList<String> isbnList = new ArrayList<>();
        String sql = "SELECT isbn FROM borrowing " +
                "WHERE start_date >= ? " +
                "GROUP BY isbn " +
                "ORDER BY COUNT(*) DESC " +
                "LIMIT ?";

        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        String dateThirtyDaysAgo = thirtyDaysAgo.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, dateThirtyDaysAgo);
            stmt.setInt(2, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    isbnList.add(rs.getString("isbn"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ArrayList<Book> books = new ArrayList<>();
        for (String isbn : isbnList) {
            books.addAll(Book.searchByArk(isbn));
        }

        return books;
    }

    /**
     * Renvoie une représentation sous forme de chaîne de caractères de cet objet Borrowing.
     *
     * @return Une chaîne de caractères représentant cet objet Borrowing.
     */
    @Override
    public String toString() {
        return "Borrowing{" +
                "id_user=" + id_user +
                ", STARTING_DATE=" + STARTING_DATE +
                ", END_DATE=" + END_DATE +
                ", ark=" + ark +
                '}';
    }
}