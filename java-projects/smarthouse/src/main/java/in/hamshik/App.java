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
        var root = loadFXML("main");
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        java.net.URL resource = App.class.getResource(fxml + ".fxml");
        if (resource == null) 
            resource = App.class.getResource("/in/hamshik/" + fxml + ".fxml");
        
        if (resource == null) 
            throw new IllegalStateException("Cannot find FXML file: " + fxml + ".fxml");
;
        return new FXMLLoader(resource).load();
    }

    public static void main(String[] args) {
        launch();
    }

}