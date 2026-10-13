package in.hamshik;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML private ImageView bat, block, bomb, coffView1, heroView;
    @FXML private AnchorPane rootPanel;
    @FXML private Label scoreLabel, gameOverLabel;

    private GameOver gameOver;
    private CoffeeFalling coffeeFalling;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupGame();
    }

    private void setupGame() {
        // 1️⃣ Initialize CoffeeFalling first
        coffeeFalling = new CoffeeFalling(rootPanel, coffView1, heroView, scoreLabel, false);
        // 2️⃣ Then GameOver, passing the same CoffeeFalling
        gameOver = new GameOver(rootPanel, bat, heroView, block, bomb, gameOverLabel, coffeeFalling);

        // 3️⃣ Start both
        coffeeFalling.startFalling();
        gameOver.start();
    }


    public void stopThreads() {
        if (gameOver != null && gameOver.obstacleThread() != null)
            gameOver.obstacleThread().interrupt();
        if (coffeeFalling != null)
            coffeeFalling.stopThread();
    }

    public ImageView getHeroView() { return heroView; }
    public CoffeeFalling getCoffeeClass() { return coffeeFalling; }
    public GameOver getGameOverClass() { return gameOver; }
}
