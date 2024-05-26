package com.example.demo;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    public Connection databaseLink;
    public Connection getDBConnection() {
        String databaseUser = "root";
        String databasePassword = "book";
        String databaseUrl = "jdbc:mysql://127.0.0.1:3306/cy_book";

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");

            databaseLink = DriverManager.getConnection(databaseUrl, databaseUser, databasePassword);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return databaseLink;
    }
}
