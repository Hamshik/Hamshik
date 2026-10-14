package in.hamshik.Controller;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import in.hamshik.App;
import in.hamshik.Helpers.AppResponder;
import in.hamshik.Helpers.FXMLPipeline;

public class SharedUIComponents {
    
    Button homeBtn, dashBtn, devicesBtn, thingsBtn, wifiBut;
    Label sidebarFooter, logo, titleLabel;
    VBox sideBar;

    AppResponder sharedResponder;

    void initialize(SharableUI... uiControllers) {
        for(var uiController: uiControllers)
            uiController.initializeUIComponents(this);
    }

}
interface SharableUI {
    FXMLPipeline fxmlPipeline = App.fxmlPipeline;
    void initializeUIComponents(SharedUIComponents components);
}