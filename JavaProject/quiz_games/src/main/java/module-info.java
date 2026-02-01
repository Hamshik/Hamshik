module in.hamshik {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive com.fasterxml.jackson.databind;
    requires transitive com.google.gson;
    requires java.desktop;
    requires javafx.base;

    opens in.hamshik to javafx.fxml;
    exports in.hamshik;
}
