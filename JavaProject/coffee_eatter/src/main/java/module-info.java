module in.hamshik {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires java.desktop;
    requires javafx.base;
    requires java.compiler;

    opens in.hamshik to javafx.fxml;
    exports in.hamshik;
}
