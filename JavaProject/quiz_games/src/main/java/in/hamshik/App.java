package in.hamshik;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {
    FXMLLoader loader;
    Parent root;
    double height;
    double len;
    Scene scene;
    public static void main(String[] args) {
        Thread pyThread = new Thread(App::writeJson);
        pyThread.start();
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        loader = new FXMLLoader(getClass().getResource("/start.fxml"));
        root = loader.load();
        height = 512;
        len = 416;
        scene = new Scene(root, len, height);


        stage.setScene(scene);
        stage.setTitle("Quiz Game");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/java_quiz_img.png")));
        stage.setResizable(false);
        stage.show();      
    }

    public static void writeJson(){
        
        final Path HOME = Paths.get(System.getProperty("user.home"));

        final Path PROJECT_PATH = HOME.resolve(
                Paths.get("Documents", "hamshik", "quiz_games",
                        "src", "main", "java", "resource", "in", "hamshik")
        );

        // ✅ Python executable (NOT activate)
        final Path PY_PATH = HOME.resolve(
                Paths.get(".venv", "bin", "python")
        );

        // ✅ Script inside project directory
        final Path SCRIPT_PATH = PROJECT_PATH.resolve("script.py");

        final Path WORKING_DIR = PROJECT_PATH;

        try {
            Reader.writeJson(
                    PY_PATH.toString(),
                    SCRIPT_PATH.toString(),
                    WORKING_DIR.toString()
            );
        } catch (Exception e) {e.getStackTrace();}
    } 
}