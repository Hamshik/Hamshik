package in.hamshik;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

import in.hamshik.Controller.*;

public class App extends Application {
    private static Node homePage;
    private static Node dashboardPage;
    private static Node devicePage;

    private static Controller homeController;
    private static DeviceMngController deviceMngController;
    private static DashController dashController;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader homeLoader = createFXMLLoader("main.fxml");
        homePage = homeLoader.load();
        homeController = homeLoader.getController();

        FXMLLoader dashboardLoader = createFXMLLoader("dashboard.fxml");
        dashboardPage = dashboardLoader.load();
        dashController = dashboardLoader.getController();

        FXMLLoader deviceLoader = createFXMLLoader("deviceMg.fxml");
        devicePage = deviceLoader.load();
        deviceMngController = deviceLoader.getController();

        StackPane pageHost = new StackPane(homePage, dashboardPage, devicePage);
        showHome();
        stage.setScene(new Scene(pageHost));
        stage.setTitle("IoT Dashboard");
        stage.setMinWidth(600);
        stage.setMinHeight(500);
        stage.show();
    }

    @Override
    public void stop() {
        if (deviceMngController != null) {
            deviceMngController.dispose();
        }
    }

    public static void showHome() {
        showPage(homePage, Controller.NavigationItem.HOME);
    }

    public static void showDashboard() {
        showPage(dashboardPage, Controller.NavigationItem.DASHBOARD);
    }

    public static void showDevices() {
        showPage(devicePage, Controller.NavigationItem.DEVICES);
    }

    private static void showPage(Node activePage, Controller.NavigationItem activeItem) {
        if (homePage == null || dashboardPage == null || devicePage == null
                || homeController == null || dashController == null || deviceMngController == null) {
            throw new IllegalStateException("Application pages have not been loaded.");
        }

        setManagedVisibleTo(activePage == homePage, homePage);
        setManagedVisibleTo(activePage == dashboardPage,dashboardPage);
        setManagedVisibleTo(activePage == devicePage, devicePage);

        homeController.setActiveNavigation(activeItem);
        dashController.setActiveNavigation(activeItem);
        deviceMngController.setActiveNavigation(activeItem);
    }

    public static void setManagedVisibleTo(boolean val, Node... nodes){
        for(var n : nodes){
            n.setManaged(val);
            n.setVisible(val);
        }
    }

    private static FXMLLoader createFXMLLoader(String fileName) {
        java.net.URL resource = App.class.getResource("/in/hamshik/" + fileName);
        if (resource == null) {
            throw new IllegalStateException("Cannot find FXML file: " + fileName);
        }
        return new FXMLLoader(resource);
    }

    public static void main(String[] args) {
        launch();
    }
}
