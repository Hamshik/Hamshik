package in.hamshik;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Game.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 500, 650);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/coffee.png")));
        stage.setResizable(false);

        Controller controller = loader.getController();
        ImageView hero = controller.getHeroView();

        Set<KeyCode> keysPressed = new HashSet<>();
        scene.setOnKeyPressed(event -> keysPressed.add(event.getCode()));
        scene.setOnKeyReleased(event -> keysPressed.remove(event.getCode()));

        AnimationTimer movementTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!controller.getGameOverClass().isGameOver()){
                    double step = 5;
                    double maxX = 170, minX = -180, maxY = -25, minY = -480;

                    for (KeyCode code : keysPressed) {
                        switch (code) {
                            case LEFT, A -> hero.setTranslateX(Math.max(hero.getTranslateX() - step, minX));
                            case RIGHT, D -> hero.setTranslateX(Math.min(hero.getTranslateX() + step, maxX));
                            case UP, W -> hero.setTranslateY(Math.max(hero.getTranslateY() - step, minY));
                            case DOWN, S -> hero.setTranslateY(Math.min(hero.getTranslateY() + step, maxY));
                            default -> {return;}
                        }
                    }
                }
            }
        };
        movementTimer.start();

        stage.setScene(scene);
        stage.setTitle("Coffee Bean Eater");
        stage.show();
        scene.getRoot().requestFocus();

        stage.setOnCloseRequest((WindowEvent e) ->
            AlertBox.showExitConfirmation(e, controller::stopThreads)
        );
    }

    public static void main(String[] args) {
        launch();
    }
}
