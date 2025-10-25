package in.hamshik;

import javafx.application.Platform;
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
    @FXML private Label scoreLabel, gameOver;
    @FXML private Button resetBut;

    private CoffeeFalling coffeeFalling;
    private GameOver gameOverObj;
    private volatile boolean gameOverFlag = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        coffeeFalling = new CoffeeFalling(rootPanel, coffView1, heroView, scoreLabel);
        coffeeFalling.startFalling();

        gameOverObj = new GameOver(rootPanel, bat, heroView, block, bomb, gameOver, resetBut);
        gameOverObj.obstacle();

        resetBut.setOnAction(this::resetGame);
    }

    public ImageView getHeroView() { return heroView; }
    public boolean isGameOver() { return gameOverObj.isGameOver(); }

    private void resetGame(ActionEvent e) {
        System.out.println("Reset Clicked");
        gameOver.setVisible(false);
        resetBut.setVisible(false);

        gameOverObj.stopThreads();
        coffeeFalling.stopThread();

        // Restart game
        coffeeFalling = new CoffeeFalling(rootPanel, coffView1, heroView, scoreLabel);
        coffeeFalling.startFalling();

        gameOverObj = new GameOver(rootPanel, bat, heroView, block, bomb, gameOver, resetBut);
        gameOverObj.obstacle();
    }

    public void stopThreads() {
        coffeeFalling.stopThread();
        gameOverObj.stopThreads();
    }
    @FXML
    public void resetB(javafx.event.ActionEvent e) {
        System.out.println("Reset Clicked");
        resetBut.setVisible(false);
        gameOver.setVisible(false);
    }
}

