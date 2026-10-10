package in.hamshik;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("main"), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        java.net.URL resource = App.class.getResource(fxml + ".fxml");
        if (resource == null) {
            resource = App.class.getResource("/in/hamshik/" + fxml + ".fxml");
        }
        if (resource == null) {
            throw new IllegalStateException("Cannot find FXML file: " + fxml + ".fxml (ensure resources are built to target/classes)");
        }
        FXMLLoader fxmlLoader = new FXMLLoader(resource);
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}