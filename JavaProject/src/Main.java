

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
public class Main extends Application{
    static public void main(String [] args){
        launch(args);
    }

    @Override
    public void start(Stage stage){
        Stirng PATH = " ";
        Parent root  = new FXMLLoader(getClass().getResource("Scene.fxml")).load();
        Scene scene = new Scene(root);
        Image icon = new Image(PATH);
        stage.getIcon().add(icon);
        stage.setTitle("Coffe been Eater Game");
        stage.setWidth(225);
        stage.setHeight(225);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
}
