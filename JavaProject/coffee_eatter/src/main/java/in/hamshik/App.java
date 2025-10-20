package in.hamshik;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
public class App extends Application{
    static public void main(String [] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException{
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Scene.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Image icon = new Image(getClass().getResourceAsStream("/coffee.png"));
            stage.setWidth(500);
            stage.setHeight(650);
            stage.setResizable(false);
            stage.getIcons().add(icon);

            Controller controller = loader.getController();

            scene.setOnKeyPressed(e -> {
                controller.keyHandler(e.getCode());
            });
            stage.setScene(scene);
            stage.setTitle("Coffee Bean Eater Game");
            stage.show();

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
