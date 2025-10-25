package in.hamshik;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class App extends Application {

    static public void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Scene.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 500, 650);
        Image icon = new Image(getClass().getResourceAsStream("/coffee.png"));
        stage.setResizable(false);
        stage.getIcons().add(icon);

        Controller controller = loader.getController();
        ImageView hero = controller.getHeroView();

        // Track pressed keys
        Set<KeyCode> keysPressed = new HashSet<>();
        scene.setOnKeyPressed(event -> keysPressed.add(event.getCode()));
        scene.setOnKeyReleased(event -> keysPressed.remove(event.getCode()));

        // AnimationTimer for smooth movement
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (controller.isGameOver()) return;

                double step = 5;
                double maxX = 170, minX = -180, maxY = -25, minY = -480;

                for (KeyCode code : keysPressed) {
                    switch (code) {
                        case LEFT, A -> hero.setTranslateX(Math.max(hero.getTranslateX() - step, minX));
                        case RIGHT, D -> hero.setTranslateX(Math.min(hero.getTranslateX() + step, maxX));
                        case UP, W -> hero.setTranslateY(Math.max(hero.getTranslateY() - step, minY));
                        case DOWN, S -> hero.setTranslateY(Math.min(hero.getTranslateY() + step, maxY));
                        default -> { }
                    }
                }
            }
        };
        timer.start();

        stage.setScene(scene);
        stage.setTitle("Coffee Bean Eater Game");
        stage.show();
        scene.getRoot().requestFocus();

        // Handle window close
        stage.setOnCloseRequest((WindowEvent e) -> {
            e.consume(); // prevent immediate close
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION, "Do you really want to exit?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(type -> {
                if (type == ButtonType.YES) {
                    controller.stopThreads();
                    Platform.exit();
                    System.exit(0);
                }
            });
        });
    }
}
