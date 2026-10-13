package in.hamshik.Controller;

import in.hamshik.App;

import in.hamshik.Helpers.AppResponder;
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

    @FXML private AnchorPane pageContent;
    @FXML SplitPane root;
    private AppResponder appResponder;


    @FXML protected void initialize() {
        super.initialize(); // Initialize shared UI components
        super.sharedResponder = new AppResponder(root, sideBar, logo, sidebarFooter, homeBtn, dashBtn, devicesBtn, thingsBtn);
        super.sharedResponder.setupResponsiveLayout();

        homeBtn.setOnAction(event -> showHome());
        dashBtn.setOnAction(event -> showDashboard());
        devicesBtn.setOnAction(event -> showDevices());
        wifiBut.setOnAction(event -> showWifi());
    }

    public void setActiveNavigation(NavigationItem item) {
        homeBtn.getStyleClass().remove("sidebar-item-active");
        dashBtn.getStyleClass().remove("sidebar-item-active");
        devicesBtn.getStyleClass().remove("sidebar-item-active");
        thingsBtn.getStyleClass().remove("sidebar-item-active");

        switch (item) {
            case HOME -> homeBtn.getStyleClass().add("sidebar-item-active");
            case DASHBOARD -> dashBtn.getStyleClass().add("sidebar-item-active");
            case DEVICES -> devicesBtn.getStyleClass().add("sidebar-item-active");
            case THINGS -> thingsBtn.getStyleClass().add("sidebar-item-active");
        }
    }

    @FXML public void showHome() { App.showHome(); }

    @FXML protected void showDashboard() { App.showDashboard(); }

    @FXML protected void showDevices() { App.showDevices(); }

    @FXML void showWifi(){
        App.showDevices();
    }
}