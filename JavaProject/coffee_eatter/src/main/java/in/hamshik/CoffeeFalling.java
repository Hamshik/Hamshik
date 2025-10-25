package in.hamshik;

import javafx.animation.AnimationTimer;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class CoffeeFalling {

    private final Pane rootPanel;
    private final ImageView coffeeTemplate;
    private final ImageView heroView;
    private final Label scoreLabel; // Add this
    private final Random random = new Random();
    private final AtomicInteger score = new AtomicInteger(0);
    private final CopyOnWriteArrayList<ImageView> coffees = new CopyOnWriteArrayList<>();

    public CoffeeFalling(AnchorPane rootPanel, ImageView coffeeTemplate, ImageView heroView, Label scoreLabel) {
    this.rootPanel = rootPanel;
    this.coffeeTemplate = coffeeTemplate;
    this.heroView = heroView;
    this.scoreLabel = scoreLabel; // assign the Label
}

    public void startFalling() {
        // Spawn coffee beans periodically
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep((3*1000)); // spawn every 5 sec
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                javafx.application.Platform.runLater(this::spawnCoffee);
            }
        }).start();
        

        // Real-time collision & score checking
        // AnimationTimer collisionChecker = new AnimationTimer() {
        //     @Override
        //     public void handle(long now) {
        //         for (ImageView coffee : coffees) {
        //             if (coffee.getBoundsInParent().intersects(heroView.getBoundsInParent())) {
        //                 int newScore = score.incrementAndGet();

        //                 // Update scoreLabel on JavaFX thread
        //                 scoreLabel.setText(Integer.toString(newScore));

        //                 // Reset coffee to top
        //                 coffee.setX(random.nextInt(-225, 225));
        //                 coffee.setY(-200);

        //             }
        //         }
        //     }
        // };
        // collisionChecker.start();
    }

    private void spawnCoffee() {
        ImageView coffee = new ImageView(coffeeTemplate.getImage());
        coffee.setFitWidth(coffeeTemplate.getFitWidth());
        coffee.setFitHeight(coffeeTemplate.getFitHeight());
        coffee.setPreserveRatio(true);

        coffee.setX(random.nextInt(-225, 225));
        coffee.setY(-50);

        rootPanel.getChildren().add(coffee);
        coffees.add(coffee);

        TranslateTransition tt = new TranslateTransition(Duration.seconds(3 + random.nextDouble()), coffee);
        tt.setByY(rootPanel.getHeight() + coffee.getFitHeight());
        tt.setCycleCount(TranslateTransition.INDEFINITE);
        tt.play();
    }
}
