package in.hamshik.Controller;

import in.hamshik.App;
import in.hamshik.Helpers.AppResponder;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;

public class Controller {
    @FXML private SplitPane pageContent;
    @FXML protected Button wifiBut;
    protected AppResponder appResponder;

    @FXML protected void showDashboard() { App.showDashboard(); }

    @FXML protected void showDevices() { App.showDevices(); }

    @FXML void showWifi(){
        ((DeviceMngController)App.getController("DeviceMngController")).wifiShow();
    }
}
