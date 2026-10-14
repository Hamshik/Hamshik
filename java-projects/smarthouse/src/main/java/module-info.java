module in.hamshik {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires javax.jmdns;
    requires javafx.base;
    requires com.google.gson;
    requires java.net.http;

    opens in.hamshik to javafx.fxml;
    opens in.hamshik.Controller to javafx.fxml;
    exports in.hamshik;
}