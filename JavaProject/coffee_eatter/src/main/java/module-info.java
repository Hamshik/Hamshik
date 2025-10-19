module in.hamshik {
    requires javafx.controls;
    requires javafx.fxml;

    opens in.hamshik to javafx.fxml;
    exports in.hamshik;
}
