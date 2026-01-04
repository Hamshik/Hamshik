package in.hamshik;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

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
    public static FileReader getFile() throws FileNotFoundException{ return new FileReader("ques_choice.txt");}
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        loader = new FXMLLoader(getClass().getResource("/main.fxml"));
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
}