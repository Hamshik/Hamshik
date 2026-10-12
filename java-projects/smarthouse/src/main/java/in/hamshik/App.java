package in.hamshik;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

import in.hamshik.Controller.*;

public class App extends Application {
    private static Node dashboardPage;
    private static Node devicePage;

    private static DeviceMngController deviceMngController;
    private static DashController dashController;

    @Override
    public void start(Stage stage) throws IOException {
        dashboardPage = loadFXML("dashboard.fxml", dashController);
        devicePage = loadFXML("deviceMg.fxml", deviceMngController);

        StackPane pageHost = new StackPane(dashboardPage, devicePage);
        devicePage.setVisible(false);
        devicePage.setManaged(false);

        stage.setScene(new Scene(pageHost));
        stage.setTitle("IoT Dashboard");
        stage.setMinWidth(400);
        stage.setMinHeight(400);
        stage.show();
    }

    public static void showDashboard() {
        showPage(dashboardPage);
    }

    public static void showDevices() {
        showPage(devicePage);
    }

    private static void showPage(Node activePage) {
        if (dashboardPage == null || devicePage == null) {
            throw new IllegalStateException("Application pages have not been loaded.");
        }
        dashboardPage.setVisible(activePage == dashboardPage);
        dashboardPage.setManaged(activePage == dashboardPage);
        devicePage.setVisible(activePage == devicePage);
        devicePage.setManaged(activePage == devicePage);
    }

    private static Parent loadFXML(String fileName, Object obj) throws IOException {
        java.net.URL resource = App.class.getResource("/in/hamshik/" + fileName);
        if (resource == null) {
            throw new IllegalStateException("Cannot find FXML file: " + fileName);
        }
        var loader = new FXMLLoader(resource);
        Parent root = loader.load();
        obj = loader.getController();
        return root;
    }

    @SuppressWarnings("exports")
    public static Controller getController(String className) {
        if (className.equals("DeviceMngController")) {
            if (deviceMngController == null)
                throw new IllegalStateException("DeviceMngController has not been initialized.");
            return deviceMngController;
        } else if (className.equals("DashController")) {
            if (dashController == null)
                throw new IllegalStateException("DashController has not been initialized.");
            return dashController;
        } else {
            throw new IllegalArgumentException("Unknown controller class: " + className);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
