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
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
            Parent root = loader.load();
            Scene scene;
            if(root == null)
                scene = new Scene(root, Controller.root.getWidth(), Controller.root.getHeight());
            else
                scene = new Scene(root, 400,450);

            Controller controller = loader.getController();
            scene.setOnKeyTyped(e -> controller.handleKeyPress(e));

            stage.setScene(scene);
            stage.setTitle("Simple Calculator");
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/calculator.png")));
            stage.setResizable(false);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
