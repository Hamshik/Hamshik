package in.hamshik.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class SideBarController extends SharedUIComponents implements SharableUI {
    @FXML Button homeBtn, dashBtn, devicesBtn, thingsBtn;
    @FXML Label sidebarFooter, logo;
    @FXML VBox sideBar;
    
    @FXML void initialize(){
        homeBtn.setOnAction(event -> fxmlPipeline.showHome());
        dashBtn.setOnAction(event -> fxmlPipeline.showDashboard());
        devicesBtn.setOnAction(event -> fxmlPipeline.showDevices());
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