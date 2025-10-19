module in {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.graphics;
    
    opens in to javafx.fxml;
    exports in;
}
