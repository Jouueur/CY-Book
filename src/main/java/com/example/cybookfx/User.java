package com.example.cybookfx;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    private String firstName;
    private String lastName;
    private String email;
    public User(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;

    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
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

    public static boolean updateFirstNameById(int id_customer, String newFirstName, Connection connection) {
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

    @Override
    public String toString(){
        return this.firstName + " " + this.lastName;
    }

    /*
    public static boolean deleteUserById(int id_customer, Connection connection) {
        boolean userDeleted = false;
        String query = "DELETE FROM customer WHERE id_customer = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id_customer);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                userDeleted = true;
                System.out.println("Customer deleted successfully");
            } else {
                System.out.println("The user couldn't be deleted");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userDeleted;
    }
    */

}