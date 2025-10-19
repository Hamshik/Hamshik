package in;


import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;

public class Controller implements Initializable {
    private boolean isRigth;
    private boolean isLeft;
    private boolean isDown;
    private boolean isUp;

    Random random = new Random();
    private Image hero = new Image("coffeeBean.png");
    private Image villan = new Image("coffeeBean.png");
    private Image coffee = new Image("coffeeBean.png");
    private ImageView coffee1View = new ImageView(coffee);
    private ImageView coffee2View = new ImageView(coffee);

    private ImageView coffee3View = new ImageView(coffee);
    public void initialize(URL arg0, ResourceBundle arg1){
        coffee1View.setY(random.nextInt(0,225));
        coffee2View.setY(random.nextInt(0,225));
        coffee3View.setY(random.nextInt(0,225));

        coffee1View.setX(random.nextInt((224/4),225));
        coffee2View.setX(random.nextInt((224/4),225));
        coffee3View.setX(random.nextInt((224/4),225));

        

    }
    public void handle(KeyEvent event){
        switch (event.getCode()) {
            case RIGHT, D:
                isRigth = true;
                break;
            case LEFT,A:
                isLeft = true;
            case W,UP:
                isUp = true;
                break;
            case S,DOWN:
                isDown = true;
                break;
            default:
                break;
        }
    }
}
