package in.hamshik;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

import in.hamshik.Controller.*;
import in.hamshik.Helpers.FXMLPipeline;

public class App extends Application {
    private static Node homePage;
    private static Node dashboardPage;
    private static Node devicePage;

    protected static Controller homeCtrl;
    protected static deviceMngCtrl deviceMngCtrl;
    protected static dashCtrl dashCtrl;
    public static FXMLPipeline fxmlPipeline;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader homeLoader = createFXMLLoader("main.fxml");
        homePage = homeLoader.load();
        homeCtrl = homeLoader.getController();
        
        FXMLLoader dashboardLoader = createFXMLLoader("dashboard.fxml");
        dashboardPage = dashboardLoader.load();
        dashCtrl = dashboardLoader.getController();
        
        FXMLLoader deviceLoader = createFXMLLoader("deviceMg.fxml");
        devicePage = deviceLoader.load();
        deviceMngCtrl = deviceLoader.getController();
        
        StackPane pageHost = new StackPane(homePage, dashboardPage, devicePage);
        stage.setScene(new Scene(pageHost));
        stage.setTitle("IoT Dashboard");
        stage.setMinWidth(600);
        stage.setMinHeight(500);
        stage.show();
        fxmlPipeline = new FXMLPipeline(homePage, dashboardPage, devicePage);
        fxmlPipeline.showHome();
        fxmlPipeline.addSplitDividerListener(homeCtrl.root);
        fxmlPipeline.addSplitDividerListener(dashCtrl.root);
        fxmlPipeline.addSplitDividerListener(deviceMngCtrl.root);
    }

    @Override
    public void stop() { deviceMngCtrl.dispose(); }

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
