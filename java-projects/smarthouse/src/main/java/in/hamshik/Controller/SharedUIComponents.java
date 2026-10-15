package in.hamshik.Controller;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import in.hamshik.Helpers.AppResponder;

public class SharedUIComponents {
    
    Button homeBtn, dashBtn, devicesBtn, thingsBtn;
    Label sidebarFooter, logo, titleLabel;
    VBox sideBar;
    public Button wifiBut;

    AppResponder sharedResponder;

    void initialize(SharableUI... uiControllers) {
        for(var uiController: uiControllers)
            uiController.initializeUIComponents(this);
    }

}
interface SharableUI {
    void initializeUIComponents(SharedUIComponents components);
}