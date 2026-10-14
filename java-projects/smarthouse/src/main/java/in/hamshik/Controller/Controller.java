package in.hamshik.Controller;

import in.hamshik.App;

import in.hamshik.Helpers.AppResponder;
import in.hamshik.Helpers.FXMLPipeline;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;

public class Controller extends SharedUIComponents {

    public enum NavigationItem {
        HOME,
        DASHBOARD,
        DEVICES,
        THINGS
    }
    
    @FXML private AnchorPane sidebarContainer, topbarContainer;
    @FXML private AnchorPane pageContent;
    @FXML public SplitPane root;
    public AppResponder appResponder;
    FXMLPipeline fxmlPipeline = App.fxmlPipeline;

    @FXML SideBarController sidebarController;
    @FXML TopBarController topbarController;

    @FXML protected void initialize() {
        super.initialize(topbarController, sidebarController);
        appResponder = new AppResponder(root, sideBar, logo, sidebarFooter, homeBtn, dashBtn, devicesBtn, thingsBtn);
        appResponder.setupResponsiveLayout();
    }

    public void setActiveNavTitle(NavigationItem item) {
        if (homeBtn == null || titleLabel == null)  throw new NullPointerException(homeBtn == null ? "HomeBtn is null" : "titleLable is null");

        homeBtn.getStyleClass().remove("sidebar-item-active");
        dashBtn.getStyleClass().remove("sidebar-item-active");
        devicesBtn.getStyleClass().remove("sidebar-item-active");
        thingsBtn.getStyleClass().remove("sidebar-item-active");

        switch (item) {
            case HOME -> {
                homeBtn.getStyleClass().add("sidebar-item-active");
                if (titleLabel != null) titleLabel.setText("Home");
            }
            case DASHBOARD -> {
                dashBtn.getStyleClass().add("sidebar-item-active");
                if (titleLabel != null) titleLabel.setText("DashBoard");
            }
            case DEVICES -> {
                devicesBtn.getStyleClass().add("sidebar-item-active");
                if (titleLabel != null) titleLabel.setText("Device Manager");
            }
            case THINGS -> {
                thingsBtn.getStyleClass().add("sidebar-item-active");
                if (titleLabel != null) titleLabel.setText("Things");
            }
        }
    }

    // Navigation methods...
    @FXML public void showHome() { fxmlPipeline.showHome(); }
    @FXML protected void showDashboard() { fxmlPipeline.showDashboard(); }
    @FXML protected void showDevices() { fxmlPipeline.showDevices(); }
    @FXML void showWifi(){ fxmlPipeline.showDevices(); }
}