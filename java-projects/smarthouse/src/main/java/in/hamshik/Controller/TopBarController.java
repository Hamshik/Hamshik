package in.hamshik.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class TopBarController extends SharedUIComponents implements SharableUI {
    @FXML Button wifiBut;
    @FXML Label titleLabel;

    @FXML void initialize() {
        wifiBut.setOnAction(e -> fxmlPipeline.wifiShow());
    }

    public void initializeUIComponents(SharedUIComponents components){
        components.wifiBut = wifiBut;
        components.titleLabel = titleLabel;
    }
}