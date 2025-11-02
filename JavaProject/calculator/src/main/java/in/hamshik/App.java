package in.hamshik;

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

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        try{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 400, 500);
        stage.setScene(scene);
        stage.setTitle("Simple Calculator");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/calculator.png")));
        stage.show();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}