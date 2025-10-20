package in.hamshik;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.animation.TranslateTransition;


public class Controller implements Initializable {
    Random random = new Random();
    private KeyCode code;
    @FXML
    private ImageView coffee1View;
    @FXML
    private ImageView coffee2View;
    @FXML
    private ImageView coffee3View;
    @FXML
    private ImageView villainView;
    @FXML
    private ImageView heroView;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) throws  UnsupportedOperationException{
    }

    public void keyHandler(KeyCode code) {
        this.code = code;
    }
}
