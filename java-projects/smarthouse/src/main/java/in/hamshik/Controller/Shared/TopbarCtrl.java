package in.hamshik.Controller.Shared;
import in.hamshik.App;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class TopbarCtrl extends SharedUIComponents implements SharedUIComponents.SharableUI {
    @FXML Button wifiBut;
    @FXML Label titleLabel;

    @FXML void initialize() {
        wifiBut.setOnAction(e -> App.fxmlPipeline.wifiShow());
    }

    public void initializeUIComponents(SharedUIComponents components){
        components.wifiBut = wifiBut;
        components.titleLabel = titleLabel;
    }
}