package in.hamshik;

import javafx.scene.control.Label;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;


public class Controller implements Initializable {
    Random random = new Random();
    @FXML
    private AnchorPane rootPanel;
    @FXML
    private ImageView coffView1;
    @FXML
    public ImageView heroView;
    @FXML
    public Label scoreLabel;
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        Platform.runLater(() -> {
            CoffeeFalling coffeeFalling = new CoffeeFalling(rootPanel, coffView1, heroView, scoreLabel);
            coffeeFalling.startFalling();
        });
    }

    

    public ImageView getHeroView(){
        return heroView;
    }
}
