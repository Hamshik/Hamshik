module com.javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;

    opens com.javafx.quizgame to javafx.fxml;
}