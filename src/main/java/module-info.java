module com.example.cybookfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.cybookfx to javafx.fxml;
    exports com.example.cybookfx;
}