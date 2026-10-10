module in.hamshik {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires javax.jmdns;

    opens in.hamshik to javafx.fxml;
    exports in.hamshik;
}