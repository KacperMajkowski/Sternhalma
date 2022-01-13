module Server {
    requires javafx.controls;
    requires javafx.fxml;
    requires spring.context;
    requires java.sql;
    requires spring.jdbc;


    opens com.example.server to javafx.fxml;
    exports com.example.server;
}