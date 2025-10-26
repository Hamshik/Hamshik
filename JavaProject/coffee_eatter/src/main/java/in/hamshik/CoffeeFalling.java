package in.hamshik;

import javafx.animation.AnimationTimer;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class CoffeeFalling {

    private final Controller controller = new Controller();


    private final Pane rootPanel;
    private final ImageView  heroView;
    private final Label scoreLabel;
    private final Random random = new Random();
    private final CopyOnWriteArrayList<ImageView> coffees = new CopyOnWriteArrayList<>();
    private Thread coffeeThread;
    public final ImageView coffeeTemplate;

    public CoffeeFalling(Pane rootPanel, ImageView coffeeTemplate, ImageView heroView, Label scoreLabel) {
        this.rootPanel = rootPanel;
        this.coffeeTemplate = coffeeTemplate;
        this.heroView = heroView;
        this.scoreLabel = scoreLabel;
    }

    public void startFalling() {
        coffeeThread = new Thread(() -> {
            try {
                while (!controller.gameOver.gameOverFlag) {
                    Thread.sleep(3000);
                    javafx.application.Platform.runLater(this::spawnCoffee);
                }
            } catch (InterruptedException ignored) {}
        });
        coffeeThread.setDaemon(true);
        coffeeThread.start();

        AnimationTimer collisionChecker = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!controller.gameOver.gameOverFlag) return; // freeze game
                coffees.removeIf(coffee -> {
                    if (coffee.getBoundsInParent().intersects(heroView.getBoundsInParent())) {
                        rootPanel.getChildren().remove(coffee);
                        int newScore = Integer.parseInt(scoreLabel.getText().isEmpty() ? "0" : scoreLabel.getText()) + 1;
                        scoreLabel.setText(String.valueOf(newScore));
                        return true;
                    }
                    return false;
                });
            }
        };
        collisionChecker.start();
    }

    private void spawnCoffee() {
        double paneWidth = rootPanel.getWidth();
        ImageView coffee = new ImageView(coffeeTemplate.getImage());
        coffee.setFitWidth(coffeeTemplate.getFitWidth());
        coffee.setFitHeight(coffeeTemplate.getFitHeight());
        coffee.setPreserveRatio(true);
        double x = random.nextDouble() * (paneWidth - coffee.getFitWidth());
        coffee.setX(x - (paneWidth / 2));
        coffee.setY(-50);

        rootPanel.getChildren().add(coffee);
        coffees.add(coffee);

        TranslateTransition tt = new TranslateTransition(Duration.seconds(3 + random.nextDouble()), coffee);
        tt.setByY(rootPanel.getHeight() + coffee.getFitHeight());
        tt.play();

        if(controller.gameOver.gameOverFlag) {
            stopThread();
        }
    }

    public void stopThread() {
        if (coffeeThread != null) coffeeThread.interrupt();
    }

    public void reset() {
        stopThread();
        coffees.forEach(c -> rootPanel.getChildren().remove(c));
        coffees.clear();
        scoreLabel.setText("0");
    }
    public Thread getCoffeeThread() {
        return coffeeThread;
    }
}
