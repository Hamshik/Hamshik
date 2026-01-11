module in.hamshik {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.graphics;
    requires javafx.swing;
    requires javafx.base;
    requires java.scripting;
    requires com.fasterxml.jackson.databind;
    requires java.net.http;

    opens in.hamshik to javafx.fxml;
    exports in.hamshik;
}
