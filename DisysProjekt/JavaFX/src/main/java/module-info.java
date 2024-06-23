module com.example.javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires com.fasterxml.jackson.databind;
    requires javafx.media;
    requires java.net.http;
    requires java.desktop;


    opens com.example.javafx to javafx.fxml;
    exports com.example.javafx;
}