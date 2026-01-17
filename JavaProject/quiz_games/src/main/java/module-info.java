module in.hamshik {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive com.fasterxml.jackson.databind;

    opens in.hamshik to javafx.fxml;
    exports in.hamshik;
}
