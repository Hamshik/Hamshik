module in {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.graphics;
    requires com.fasterxml.jackson.databind;
    
    opens in to javafx.fxml;
    exports in;
}
