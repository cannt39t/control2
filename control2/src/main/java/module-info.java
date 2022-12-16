module com.example.control2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;

    // module com.example.control2 does not export com.example.control2.chat to module javafx.graphics

    opens com.example.control2 to javafx.fxml;
    opens com.example.control2.chat to javafx.graphics;
    exports com.example.control2;
    exports com.example.control2.chat;
}