module in.hamshik {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.graphics;
    requires javafx.swing;
    requires javafx.base;
    requires java.scripting;

    opens in.hamshik to javafx.fxml;
    exports in.hamshik;
}
