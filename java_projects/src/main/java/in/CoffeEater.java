package in;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
public class CoffeEater extends Application{
    static public void main(String [] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException{
        Parent root = new FXMLLoader(getClass().getResource("Scene.fxml")).load();
        Scene scene = new Scene(root);
        Image icon = new Image("coffee.png");
        stage.setWidth(255);
        stage.setHeight(255);
        stage.setResizable(false);
        stage.getIcons().add(icon);
        stage.setScene(scene);
        stage.setTitle("Coffee Bean Eater Game");
        stage.show();
    }
}
