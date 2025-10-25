package in.hamshik;

import javafx.animation.AnimationTimer;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameOver {

    private final Pane rootPanel;
    private final ImageView heroView;
    private final ImageView batTemplate, blockTemplate, bombTemplate;
    private final Label gameOver;
    private final Button gaOvButton;
    private final CopyOnWriteArrayList<ImageView> bats = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<ImageView> blocks = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<ImageView> bombs = new CopyOnWriteArrayList<>();
    private final String[] choice = {"bat", "block", "bomb"};
    private Thread obstacleThread;
    private final Random random = new Random();
    private volatile boolean gameOverFlag = false;

    public GameOver(Pane rootPanel, ImageView batTemplate, ImageView heroView, ImageView blockTemplate, ImageView bombTemplate, Label gameOver, Button gaOvButton) {
        this.rootPanel = rootPanel;
        this.heroView = heroView;
        this.batTemplate = batTemplate;
        this.blockTemplate = blockTemplate;
        this.bombTemplate = bombTemplate;
        this.gameOver = gameOver;
        this.gaOvButton = gaOvButton;
    }

    public void obstacle() {
        obstacleThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted() && !gameOverFlag) {
                    Thread.sleep(20000);
                    String choiceNo = choice[random.nextInt(choice.length)];
                    if (gameOverFlag) break;
                    switch (choiceNo) {
                        case "bat" -> javafx.application.Platform.runLater(this::spawnBat);
                        case "block" -> javafx.application.Platform.runLater(this::spawnBlock);
                        case "bomb" -> javafx.application.Platform.runLater(this::spawnBomb);
                    }
                }
            } catch (InterruptedException ignored) {}
        });
        obstacleThread.setDaemon(true);
        obstacleThread.start();

        startCollisionChecker();
    }

    private void startCollisionChecker() {
        AnimationTimer collisionChecker = new AnimationTimer() {
            @Override
            public void handle(long now) {
                checkCollision(bats);
                checkCollision(blocks);
                checkCollision(bombs);
            }
        };
        collisionChecker.start();
    }

    private void checkCollision(CopyOnWriteArrayList<ImageView> list) {
        for (ImageView obj : list) {
            if (obj.getBoundsInParent().intersects(heroView.getBoundsInParent())) {
                endGame();
            }
        }
    }

    private void spawnBat() { spawnObstacle(batTemplate, bats); }
    private void spawnBlock() { spawnObstacle(blockTemplate, blocks); }
    private void spawnBomb() { spawnObstacle(bombTemplate, bombs); }

    private void spawnObstacle(ImageView template, CopyOnWriteArrayList<ImageView> list) {
        ImageView obj = new ImageView(template.getImage());
        obj.setFitWidth(template.getFitWidth());
        obj.setFitHeight(template.getFitHeight());
        obj.setPreserveRatio(true);
        obj.setX(random.nextInt(-225, 225));
        obj.setY(-50);

        rootPanel.getChildren().add(obj);
        list.add(obj);

        TranslateTransition tt = new TranslateTransition(Duration.seconds(4 + random.nextDouble()), obj);
        tt.setByY(rootPanel.getHeight() + obj.getFitHeight());
        tt.setCycleCount(TranslateTransition.INDEFINITE);
        tt.play();
    }

    public boolean isGameOver() { return gameOverFlag; }

    public void endGame() {
        gameOver.setVisible(true);
        gaOvButton.setVisible(true);
        gameOverFlag = true;
        stopThreads();
    }

    public void stopThreads() {
        if (obstacleThread != null) obstacleThread.interrupt();
    }
}
