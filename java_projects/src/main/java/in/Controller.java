package in;


import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import javafx.application.Platform;
import javafx.fxml.FXML;

public class Controller implements Initializable {
    Random random = new Random();
    private Image villain = new Image("coffeeBean.png");
    private Image coffee = new Image("coffeeBean.png");
    private ImageView coffee1View = new ImageView(coffee);
    private ImageView coffee2View = new ImageView(coffee);
    private ImageView coffee3View = new ImageView(coffee);
    private ImageView villainView = new ImageView(villain);
    @FXML
    private ImageView heroView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        coffee1View.setX(random.nextInt(224/4));
        coffee1View.setY(random.nextInt(225));
        coffee2View.setX(random.nextInt(224/4));
        coffee2View.setY(random.nextInt(225));
        coffee3View.setX(random.nextInt(224/4));
        coffee3View.setY(random.nextInt(225));

        Platform.runLater(() -> {
            heroView.getScene().setOnKeyPressed(event -> handle(event));
        });
    }

    public void handle(KeyEvent event) {
        KeyCode code = event.getCode();

        if (code == KeyCode.W || code == KeyCode.UP) {
            heroView.setY(heroView.getY() - 5);
        } else if (code == KeyCode.S || code == KeyCode.DOWN) {
            heroView.setY(heroView.getY() + 5);
        } else if (code == KeyCode.A || code == KeyCode.LEFT) {
            heroView.setX(heroView.getX() - 5);
        } else if (code == KeyCode.D || code == KeyCode.RIGHT) {
            heroView.setX(heroView.getX() + 5);
        }
    }
}
