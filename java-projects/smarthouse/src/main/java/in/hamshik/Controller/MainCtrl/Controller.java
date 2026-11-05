package in.hamshik.Controller.MainCtrl;

import in.hamshik.App;
import in.hamshik.Controller.Shared.SharedUIComponents;
import in.hamshik.Controller.Shared.SidebarCtrl;
import in.hamshik.Controller.Shared.TopbarCtrl;
import in.hamshik.Helpers.AppResponder;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;

import java.util.Map;

public class Controller extends SharedUIComponents {

    public enum NavigationItem {
        HOME("Home"),
        DASHBOARD("DashBoard"),
        DEVICES("Device Manager"),
        THINGS("Things"),
        FILE_MNG("File Manager");

        private final String title;

        NavigationItem(String title) {
            this.title = title;
        }
    }
    
    @FXML private AnchorPane sidebarContainer, topbarContainer;
    @FXML private AnchorPane pageContent;
    @FXML public SplitPane root;
    public AppResponder appResponder;

    @FXML SidebarCtrl sidebarController;
    @FXML TopbarCtrl topbarController;

    @FXML protected void initialize() {
        super.initialize(topbarController, sidebarController);
        appResponder = new AppResponder(root, sideBar, logo, sidebarFooter, homeBtn, dashBtn, devicesBtn, thingsBtn, fileMngBtn);
        appResponder.setupResponsiveLayout();
    }

    public void setActiveNavTitle(NavigationItem item) {
        if (homeBtn == null || titleLabel == null)  throw new NullPointerException(homeBtn == null ? "HomeBtn is null" : "titleLable is null");

        Map<NavigationItem, Button> navigationButtons = Map.of(
                NavigationItem.HOME, homeBtn,
                NavigationItem.DASHBOARD, dashBtn,
                NavigationItem.DEVICES, devicesBtn,
                NavigationItem.THINGS, thingsBtn,
                NavigationItem.FILE_MNG, fileMngBtn);

        for (Button button : navigationButtons.values())
            button.getStyleClass().remove("sidebar-item-active");

        navigationButtons.get(item).getStyleClass().add("sidebar-item-active");
        titleLabel.setText(item.title);
    }

    // Navigation methods...
    @FXML public void showHome() { App.fxmlPipeline.showHome(); }
    @FXML protected void showDashboard() { App.fxmlPipeline.showDashboard(); }
    @FXML protected void showDevices() { App.fxmlPipeline.showDevices(); }
    @FXML void showWifi(){ App.fxmlPipeline.showDevices(); }
}