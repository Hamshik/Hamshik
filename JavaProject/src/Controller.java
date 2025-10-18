
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.scene.input.KeyEvent;

public class Controller implements Initializable {

    public void initialize(URL arg0, ResourceBundle arg1){

    }
    @Override
    public void handle(KeyEvent event){
        System.out.println(event.getCode());
    }
}
