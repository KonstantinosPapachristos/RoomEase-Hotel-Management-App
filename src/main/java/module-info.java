module com.example.roomease {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires itextpdf;


    opens com.example.roomease to javafx.fxml;
    exports com.example.roomease;
}