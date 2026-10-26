package in.hamshik.Controller.Shared;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import in.hamshik.Helpers.AppResponder;

public class SharedUIComponents {

    public interface SharableUI {
        void initializeUIComponents(SharedUIComponents components);
    }
    
    public Button homeBtn, dashBtn, devicesBtn, thingsBtn;
    public Label sidebarFooter, logo, titleLabel;
    public VBox sideBar;
    public Button wifiBut;

    AppResponder sharedResponder;

    public void initialize(SharedUIComponents.SharableUI... uiControllers) {
        for(var uiController: uiControllers)
            uiController.initializeUIComponents(this);
    }

}