package in.hamshik.Controller.Shared;
import in.hamshik.App;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class SidebarCtrl extends SharedUIComponents implements SharedUIComponents.SharableUI {
    @FXML Button homeBtn, dashBtn, devicesBtn, thingsBtn;
    @FXML Label sidebarFooter, logo;
    @FXML VBox sideBar;
    
    @FXML void initialize(){
        homeBtn.setOnAction(event -> App.fxmlPipeline.showHome());
        dashBtn.setOnAction(event -> App.fxmlPipeline.showDashboard());
        devicesBtn.setOnAction(event -> App.fxmlPipeline.showDevices());
    }

    public void initializeUIComponents(SharedUIComponents components){
        components.homeBtn = homeBtn;
        components.dashBtn = dashBtn;
        components.devicesBtn = devicesBtn;
        components.thingsBtn = thingsBtn;
        components.sidebarFooter = sidebarFooter;
        components.logo = logo;
        components.sideBar = sideBar;
    }

}