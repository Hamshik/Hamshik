package in.hamshik.Controller;

import in.hamshik.App;

import in.hamshik.Helpers.AppResponder;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;

public class Controller extends SharedUIComponents {
    private static boolean syncingDividerPositions;

    public enum NavigationItem {
        HOME,
        DASHBOARD,
        DEVICES,
        THINGS
    }

    @FXML private AnchorPane pageContent;
    @FXML public SplitPane root;


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

    @FXML void showWifi(){ App.showDevices(); }

    public void addSplitDividerListener(SplitPane pane) {
        pane.getDividers().get(0).positionProperty().addListener(
            (obs, oldVal, newVal) -> {
                if (syncingDividerPositions) {
                    return;
                }

                syncingDividerPositions = true;
                try {
                    double position = newVal.doubleValue();
                    if (pane != App.homeController.root) {
                        App.homeController.root.setDividerPosition(0, position);
                    }
                    if (pane != App.dashController.root) {
                        App.dashController.root.setDividerPosition(0, position);
                    }
                    if (pane != App.deviceMngController.root) {
                        App.deviceMngController.root.setDividerPosition(0, position);
                    }
                } finally {
                    syncingDividerPositions = false;
                }
            });
    }
}