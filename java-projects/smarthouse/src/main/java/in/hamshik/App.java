package in.hamshik;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

import in.hamshik.Controller.MainCtrl.*;
import in.hamshik.Helpers.FXMLPipeline;

public class App extends Application {
    private static Node homePage;
    private static Node dashboardPage;
    private static Node devicePage;
    private static Node fileMngPage;

    protected static Controller homeCtrl;
    protected static FileMngCtrl fileMngCtrl;
    public static DeviceMngCtrl deviceMngCtrl;
    public static DashCtrl dashCtrl;
    public static FXMLPipeline fxmlPipeline;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader homeLoader = createFXMLLoader("fxmlFiles/main.fxml");
        homePage = homeLoader.load();
        homeCtrl = homeLoader.getController();
        
        FXMLLoader dashboardLoader = createFXMLLoader("fxmlFiles/dashboard.fxml");
        dashboardPage = dashboardLoader.load();
        dashCtrl = dashboardLoader.getController();
        
        FXMLLoader deviceLoader = createFXMLLoader("fxmlFiles/deviceMg.fxml");
        devicePage = deviceLoader.load();
        deviceMngCtrl = deviceLoader.getController();

        FXMLLoader fileMngLoader = createFXMLLoader("fxmlFiles/fileMng.fxml");
        fileMngPage = fileMngLoader.load();
        fileMngCtrl = fileMngLoader.getController();
        
        StackPane pageHost = new StackPane(homePage, dashboardPage, devicePage, fileMngPage);
        Scene scene = new Scene(pageHost);
        stage.setScene(scene);
        stage.setTitle("IoT Dashboard");
        stage.setMinWidth(600);
        stage.setMinHeight(500);
        fxmlPipeline = new FXMLPipeline(homePage, dashboardPage, devicePage, fileMngPage);
        fxmlPipeline.showHome();
        fxmlPipeline.addSplitDividerListener();
        stage.show();
    }

    @Override
    public void stop() { deviceMngCtrl.dispose(); }

    public static FXMLLoader createFXMLLoader(String fileName) {
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
