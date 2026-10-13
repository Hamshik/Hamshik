package in.hamshik;

import javafx.animation.AnimationTimer;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameOver {

    private final Pane rootPanel;
    private final ImageView heroView, batTemplate, blockTemplate, bombTemplate;
    private final Label gameOverLabel;
    private final CoffeeFalling coffeeFalling;
    private final Random random = new Random();
    private Thread obstacleThread;
    private final CopyOnWriteArrayList<ImageView> allObstacles = new CopyOnWriteArrayList<>();

    public volatile boolean gameOverFlag = false;

    public GameOver(Pane rootPanel, ImageView batTemplate, ImageView heroView,
                    ImageView blockTemplate, ImageView bombTemplate,
                    Label gameOverLabel, CoffeeFalling coffeeFalling) {
        this.rootPanel = rootPanel;
        this.heroView = heroView;
        this.batTemplate = batTemplate;
        this.blockTemplate = blockTemplate;
        this.bombTemplate = bombTemplate;
        this.gameOverLabel = gameOverLabel;
        this.coffeeFalling = coffeeFalling;
    }

    public void start() {
        gameOverFlag = false;
        obstacleThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted() && !gameOverFlag) {
                    Thread.sleep(5000);
                    String type = switch (random.nextInt(3)) {
                        case 0 -> "bat";
                        case 1 -> "block";
                        default -> "bomb";
                    };
                    Platform.runLater(() -> spawnObstacle(type));
                }
            } catch (InterruptedException ignored) { }
        });
        obstacleThread.setDaemon(true);
        obstacleThread.start();

        AnimationTimer collisionChecker = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (gameOverFlag) return;
                allObstacles.forEach(obj -> {
                    if (obj.getBoundsInParent().intersects(heroView.getBoundsInParent())) {
                        endGame();
                    }
                });
            }
        };
        collisionChecker.start();
    }

    private void spawnObstacle(String type) {
        if (gameOverFlag) return;
        ImageView template = switch (type) {
            case "bat" -> batTemplate;
            case "block" -> blockTemplate;
            case "bomb" -> bombTemplate;
            default -> null;
        };
        if (template == null) return;

        ImageView obj = new ImageView(template.getImage());
        obj.setFitWidth(template.getFitWidth());
        obj.setFitHeight(template.getFitHeight());
        obj.setPreserveRatio(true);

        double paneWidth = rootPanel.getWidth();
        obj.setX(random.nextDouble() * (paneWidth - obj.getFitWidth()) - (paneWidth / 2));
        obj.setY(-50);

        rootPanel.getChildren().add(obj);
        allObstacles.add(obj);

        TranslateTransition tt = new TranslateTransition(Duration.seconds(4 + random.nextDouble()), obj);
        tt.setByY(rootPanel.getHeight() + obj.getFitHeight());
        tt.play();
    }

    public boolean isGameOver() { return gameOverFlag; }

    public void endGame() {
        if (gameOverFlag) return;
        gameOverFlag = true;
        coffeeFalling.setGameOver(true);
        Platform.runLater(() -> {
            heroView.setVisible(false);
            batTemplate.setVisible(false);
            blockTemplate.setVisible(false);
            bombTemplate.setVisible(false);
            gameOverLabel.setVisible(true);
        });
    }

    public Thread obstacleThread() { return obstacleThread; }

    public Thread coffeeThread() { return coffeeFalling.getCoffeeThread(); }
}
