package com.example.demo;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

/**
 * Represents a user borrowing books.
 */
public class User {
    private String firstName;
    private String lastName;
    private String email;

    /**
     * Constructs a User with the specified first name, last name, and email.
     *
     * @param firstName the first name of the user
     * @param lastName  the last name of the user
     * @param email     the email address of the user
     */
    public User(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    /**
     * Returns the first name of the user.
     *
     * @return the first name of the user
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Returns the last name of the user.
     *
     * @return the last name of the user
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Returns the email address of the user.
     *
     * @return the email address of the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the last name of the user.
     *
     * @param lastName the new last name of the user
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Sets the email address of the user.
     *
     * @param email the new email address of the user
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Sets the first name of the user.
     *
     * @param firstName the new first name of the user
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    // ---------------------------------------------------------------------------------------------
    /**
     * Ajoute un nouveau client à la base de données s'il n'existe pas déjà.
     *
     * @param connexion Connexion à la base de données.
     * @return true si le client a été ajouté avec succès, false sinon.
     */
    public boolean addToDataBase(Connection connexion) {
        boolean userAdded = false;
        try {
            if (this.isInDataBase(connexion)) {
                System.out.println("This customer is already in the data base");
                return false;
            }

            String query = "INSERT INTO customer (name_customer, last_name_customer, email_customer) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connexion.prepareStatement(query)) {
                preparedStatement.setString(1, this.firstName);
                preparedStatement.setString(2, this.lastName);
                preparedStatement.setString(3, this.email);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    userAdded = true;
                    System.out.println("Customer add with success");
                } else {
                    System.out.println("The user can't be added");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userAdded;
    }

    /**
     * Vérifie si le client est déjà présent dans la base de données.
     *
     * @param connection Connexion à la base de données.
     * @return true si le client est déjà dans la base de données, false sinon.
     */
    public boolean isInDataBase(Connection connection) {
        String query = "SELECT id_customer FROM customer WHERE name_customer = ? AND last_name_customer = ? AND email_customer = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, this.firstName);
            preparedStatement.setString(2, this.lastName);
            preparedStatement.setString(3, this.email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Connecte le client à partir de son nom, prénom et adresse e-mail, et retourne son identifiant.
     *
     * @param connection Connexion à la base de données.
     * @return L'identifiant du client s'il est trouvé dans la base de données, sinon -1.
     */
    public int userConnection(Connection connection) {
        int userId = -1;
        String query = "SELECT id_customer FROM customer WHERE name_customer = ? AND last_name_customer = ? AND email_customer = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, this.firstName);
            statement.setString(2, this.lastName);
            statement.setString(3, this.email);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    userId = resultSet.getInt("id_customer");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userId;
    }


// ----------------------------------------------------------------------------------------------
    /**
     * Met à jour le prénom du client dans la base de données en fonction de son ID.
     *
     * @param id_customer Identifiant du client dont le prénom doit être mis à jour.
     * @param newFirstName Nouveau prénom à attribuer au client.
     * @param connection Connexion à la base de données.
     * @return true si la mise à jour a été effectuée avec succès, false sinon.
     */
    public static boolean updateFirstNameById(int id_customer ,String newFirstName, Connection connection) {

        boolean updated = false;
        try {
            String query = "UPDATE customer SET name_customer = ? WHERE id_customer = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, newFirstName);
                preparedStatement.setInt(2, id_customer);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    updated = true;
                    System.out.println("First name updated successfully");
                } else {
                    System.out.println("Failed to update first name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return updated;
    }

    /**
     * Met à jour le nom de famille du client dans la base de données en fonction de son ID.
     *
     * @param id_customer Identifiant du client dont le nom de famille doit être mis à jour.
     * @param newLastName Nouveau nom de famille à attribuer au client.
     * @param connection Connexion à la base de données.
     * @return true si la mise à jour a été effectuée avec succès, false sinon.
     */
    public static boolean updateLastNameById(int id_customer, String newLastName, Connection connection) {
        boolean updated = false;
        try {
            String query = "UPDATE customer SET last_name_customer = ? WHERE id_customer = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, newLastName);
                preparedStatement.setInt(2, id_customer);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    updated = true;
                    System.out.println("Last name updated successfully");
                } else {
                    System.out.println("Failed to update last name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return updated;
    }

    /**
     * Met à jour l'adresse e-mail du client dans la base de données en fonction de son ID.
     *
     * @param id_customer Identifiant du client dont l'adresse e-mail doit être mise à jour.
     * @param newEmail Nouvelle adresse e-mail à attribuer au client.
     * @param connection Connexion à la base de données.
     * @return true si la mise à jour a été effectuée avec succès, false sinon.
     */
    public static boolean updateEmailById(int id_customer, String newEmail, Connection connection) {
        boolean updated = false;
        try {
            String query = "UPDATE customer SET email_customer = ? WHERE id_customer = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, newEmail);
                preparedStatement.setInt(2, id_customer);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    updated = true;
                    System.out.println("Email updated successfully");
                } else {
                    System.out.println("Failed to update email");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return updated;
    }

    // -----------------------------------------------------------------------------------------


    /**
     * Adds a new borrowing to the database for the specified user.
     *
     * @param connection The database connection.
     * @param borrowing  The Borrowing object representing the new borrowing.
     * @return 0 if the borrowing is successfully added,
     *         -1 if the user does not exist,
     *         -2 if the user already has 3 borrowings,
     *         -3 if there's an SQL error during the insertion.
     *         -4 if the book is already borrowed.
     */
    public int addBorrowing(Connection connection, Borrowing borrowing){
        int id_user = this.userConnection(connection);
        if(id_user == -1 ) {
            return -1;
        }
        if( !(Borrowing.bookAlreadyBorrowed(connection,borrowing.getArk())))
            return -4;
        if(Borrowing.borrowingAvailable(connection,id_user)){
            String query = "INSERT INTO borrowing (id_customer, isbn, start_date, end_date,borrowed) VALUES (?, ?, ?, ?,?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, id_user);
                preparedStatement.setString(2, borrowing.getArk());
                preparedStatement.setDate(3, Date.valueOf(borrowing.getSTARTING_DATE()));
                preparedStatement.setDate(4, Date.valueOf(borrowing.getEND_DATE()));
                preparedStatement.setBoolean(5,true);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    return 0;
                } else {
                    return -3;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return -3;
            }
        } else {
            return -2;
        }
    }


    /**
     * Modifies a borrowing record in the database to mark it as returned.
     *
     * This method updates the borrowing record for a given user and ARK (ISBN) to set the
     * borrowed status to 0, indicating that the book has been returned.
     *
     * @param connection the database connection to use for the query
     * @param ARK the ARK (ISBN) of the book to mark as returned
     * @return 0 if the update was successful,
     *        -1 if the user ID could not be retrieved,
     *        -2 if no rows were affected or an error occurred
     */
    public int modifBorrowing(Connection connection, String ARK) {

        int user_id = this.userConnection(connection);

        if (user_id == -1) {
            return -1;
        }

        String query = "UPDATE borrowing SET borrowed = 0 WHERE id_customer = ? AND isbn = ? AND borrowed = 1";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, user_id);
            stmt.setString(2, ARK);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                return 0;
            } else {
                return -2;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -2;
        }
    }


    /**
     * Deletes the user from the database if the user has no borrowed books.
     *
     * @param connection The database connection.
     * @return 0 if the user is successfully deleted,
     *         -1 if the user does not exist,
     *         -2 if the user has borrowed books,
     *         -3 if there's an SQL error during the deletion.
     */
    public int deleteUser(Connection connection) {
        int id_user = this.userConnection(connection);

        if (id_user == -1) {
            return -1;
        }

        try {
            String queryCheckBorrowing = "SELECT COUNT(*) FROM borrowing WHERE id_customer = ? AND borrowed = true";
            try (PreparedStatement preparedStatement = connection.prepareStatement(queryCheckBorrowing)) {
                preparedStatement.setInt(1, id_user);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int countBorrowedBooks = resultSet.getInt(1);
                        if (countBorrowedBooks > 0) {
                            return -2;
                        }
                    }
                }
            }

            String queryDeleteUser = "DELETE FROM customer WHERE id_customer = ?";
            try (PreparedStatement deleteStatement = connection.prepareStatement(queryDeleteUser)) {
                deleteStatement.setInt(1, id_user);

                // Execute the user deletion query
                int rowsAffected = deleteStatement.executeUpdate();
                if (rowsAffected > 0) {
                    return 0;
                } else {
                    return -3;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -3;
        }
    }

    /////////////////////////////////////////////// USER STAT /////////////////////////////////////////////////////////

    /**
     * Retrieves a list of borrowings that are still outstanding for the currently connected user.
     *
     * @param connection the database connection to use for the query.
     * @return an ArrayList of Borrowing objects representing the outstanding borrowings.
     */
    private ArrayList<Borrowing> borrowingStillOutstanding(Connection connection) {

        ArrayList<Borrowing> borrowings = new ArrayList<>();

        int id_user = this.userConnection(connection);
        if (id_user > 0) {
            String query = "SELECT * FROM borrowing WHERE id_customer = ? AND end_date > CURRENT_DATE() AND borrowed = TRUE";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, id_user);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        LocalDate startingDate = resultSet.getDate("start_date").toLocalDate();
                        LocalDate endDate = resultSet.getDate("end_date").toLocalDate();
                        String isbn = resultSet.getString("isbn");
                        borrowings.add(new Borrowing(id_user, startingDate, endDate, isbn));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return borrowings;
    }

    /**
     * Retrieves a list of overdue borrowings that have been returned for the currently connected user.
     *
     * @param connection the database connection to use for the query.
     * @return an ArrayList of Borrowing objects representing the overdue borrowings that have been returned.
     */
    private ArrayList<Borrowing> overDueBorrowing(Connection connection ){

        ArrayList<Borrowing> borrowings = new ArrayList<>();

        int id_user = this.userConnection(connection);
        if (id_user > 0) {
            String query = "SELECT * from borrowing WHERE id_customer = ? AND  borrowed = false";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, id_user);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        LocalDate startingDate = resultSet.getDate("start_date").toLocalDate();
                        LocalDate endDate = resultSet.getDate("end_date").toLocalDate();
                        String isbn = resultSet.getString("isbn");
                        borrowings.add(new Borrowing(id_user, startingDate, endDate, isbn));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return borrowings;

    }

    /**
     * Retrieves a list of overdue borrowings that have not been returned for the currently connected user.
     *
     * @param connection the database connection to use for the query.
     * @return an ArrayList of Borrowing objects representing the overdue borrowings that have not been returned.
     */
    private ArrayList<Borrowing> overDueBorrowingNotReturned(Connection connection ){

        ArrayList<Borrowing> borrowings = new ArrayList<>();

        int id_user = this.userConnection(connection);
        if (id_user > 0) {
            String query = "SELECT * from borrowing WHERE id_customer = ? AND end_date < current_date() AND borrowed = true";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, id_user);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        LocalDate startingDate = resultSet.getDate("start_date").toLocalDate();
                        LocalDate endDate = resultSet.getDate("end_date").toLocalDate();
                        String isbn = resultSet.getString("isbn");
                        borrowings.add(new Borrowing(id_user, startingDate, endDate, isbn));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return borrowings;

    }

    /**
     * Retrieves a list of books that are still outstanding for the currently connected user.
     *
     * @param connection the database connection to use for the query.
     * @return an ArrayList of Book objects representing the outstanding books.
     */
    public ArrayList<Book> borrowingStillOutstandingBook(Connection connection) {
        ArrayList<Book> books = new ArrayList<>();
        ArrayList<Borrowing> borrowings = this.borrowingStillOutstanding(connection);

        for (Borrowing borrowing : borrowings) {
            books.addAll(Book.searchByArk(borrowing.getArk()));
        }

        return books;
    }

    /**
     * Retrieves a list of books that are overdue for the currently connected user.
     *
     * @param connection the database connection to use for the query.
     * @return an ArrayList of Book objects representing the overdue books.
     */
    public ArrayList<Book> overDueBorrowingBook(Connection connection) {
        ArrayList<Book> books = new ArrayList<>();
        ArrayList<Borrowing> borrowings = this.overDueBorrowing(connection);

        for (Borrowing borrowing : borrowings) {
            books.addAll(Book.searchByArk(borrowing.getArk()));
        }

        return books;
    }

    /**
     * Retrieves a list of books that are overdue and not returned for the currently connected user.
     *
     * @param connection the database connection to use for the query.
     * @return an ArrayList of Book objects representing the overdue and not returned books.
     */
    public ArrayList<Book> overDueBorrowingNotReturnedBook(Connection connection) {
        ArrayList<Book> books = new ArrayList<>();
        ArrayList<Borrowing> borrowings = this.overDueBorrowingNotReturned(connection);

        for (Borrowing borrowing : borrowings) {
            books.addAll(Book.searchByArk(borrowing.getArk()));
        }

        return books;
    }

    /**
     * Returns a string representation of the user, which includes the first name and last name.
     *
     * @return A string in the format "firstName lastName".
     */
    @Override
    public String toString() {
        return this.firstName + " " + this.lastName;
    }



}