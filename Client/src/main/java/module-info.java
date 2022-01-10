module Client {
    requires javafx.controls;
    requires javafx.fxml;


    opens main to javafx.fxml;
    exports main;

    opens board to javafx.fxml;
    exports board;
}