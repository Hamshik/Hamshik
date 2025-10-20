package in.hamshik;

import java.io.IOException;

import javafx.animation.TranslateTransition;
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
            Scene scene = new Scene(root,500,650);
            Image icon = new Image(getClass().getResourceAsStream("/coffee.png"));
            stage.setResizable(false);
            stage.getIcons().add(icon);

            Controller controller = loader.getController();

            scene.setOnKeyPressed(event -> {
                System.out.println("Key Pressed: " + event.getCode());
                TranslateTransition transition = new TranslateTransition();
                transition.setNode(controller.heroView);
                transition.setDuration(javafx.util.Duration.millis(200));
                switch (event.getCode()) {
                    case LEFT, A:
                        transition.setByX(-20);
                        break;
                    case RIGHT, D:
                        transition.setByX(+20);
                        break;
                    default:
                        return; // Ignore other keys.
                
                }
                transition.play();

            });

            stage.setScene(scene);
            stage.setTitle("Coffee Bean Eater Game");
            stage.show();
            scene.getRoot().requestFocus();
            

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
