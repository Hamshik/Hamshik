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
    @FXML private Button resetButton;

    public CoffeeFalling coffeeFalling;
    public GameOver gameOver;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupGame();
        resetButton.setOnAction(this::resetGame);
    }

    private void setupGame() {
        coffeeFalling = new CoffeeFalling(rootPanel, coffView1, heroView, scoreLabel);
        coffeeFalling.startFalling();
        Thread coffeeThread = coffeeFalling.getCoffeeThread();

        gameOver = new GameOver(rootPanel, bat, heroView, block, bomb, gameOverLabel, resetButton, coffeeThread);
        gameOver.start();
    }

    public ImageView getHeroView() { return heroView; }
    public boolean isGameOver() { return gameOver.isGameOver(); }

    @FXML private void resetGame(ActionEvent e) {
        gameOverLabel.setVisible(false);
        resetButton.setVisible(false);

        endGame(); // stop all threads and hide all obstacles
        scoreLabel.setText("0");

        setupGame(); // start everything fresh
    }
    private void endGame() {gameOver.endGame();}
    public void stopThreads() {
        if (gameOver.obstacleThread() != null) gameOver.obstacleThread().interrupt();
        if (gameOver.coffeeThread() != null) gameOver.coffeeThread().interrupt();
    }
    public CoffeeFalling getCoffeeClass() {return coffeeFalling;}
    public GameOver getGameOverClass() {return gameOver;}
}
