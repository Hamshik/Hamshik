package in.hamshik;

import java.net.URL;
import java.time.Duration;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.animation.TranslateTransition;


public class Controller implements Initializable {
    Random random = new Random();
    @FXML
    private ImageView coffee1View;
    @FXML
    private ImageView coffee2View;
    @FXML
    private ImageView coffee3View;
    @FXML
    public ImageView villainView;
    @FXML
    public ImageView heroView;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        Random random = new Random();

// Start X anywhere in window width (0 to 450), leave some margin (50px cup width)
coffee1View.setX(random.nextInt(450));
coffee2View.setX(random.nextInt(450));
coffee3View.setX(random.nextInt(450));

// Set all Y positions to 0 (top of the window)
coffee1View.setY(0);
coffee2View.setY(0);
coffee3View.setY(0);

// Animate each coffee to fall downwards (to bottom of scene = 650)
int s3 = 2;
TranslateTransition transition1 = new TranslateTransition();
transition1.setNode(coffee1View);
transition1.setByY(650);  // Fall down 650 pixels
transition1.setDuration(javafx.util.Duration.seconds(s3));
transition1.setCycleCount(10);
transition1.play();

int s2 = 2;
TranslateTransition transition2 = new TranslateTransition();
transition2.setNode(coffee2View);
transition2.setByY(650);
transition2.setDuration(javafx.util.Duration.seconds(s2));  // Slightly slower
transition2.play();
int s1 = 2;
TranslateTransition transition3 = new TranslateTransition();
transition3.setNode(coffee3View);
transition3.setByY(650);
transition3.setDuration(javafx.util.Duration.seconds(s1));  // Slightly faster
transition3.play();


    }
}
