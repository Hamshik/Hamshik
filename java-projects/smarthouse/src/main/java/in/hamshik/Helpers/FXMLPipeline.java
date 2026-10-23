package in.hamshik.Helpers;

import in.hamshik.App;
import in.hamshik.Controller.*;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;

public class FXMLPipeline extends App {
    private Node homePage;
    private Node dashboardPage;
    private Node devicePage;
    private Controller homeCtrl = App.homeCtrl;
    private DashCtrl dashCtrl = App.dashCtrl;
    private DeviceMngCtrl deviceMngCtrl = App.deviceMngCtrl;
    private boolean syncingDividerPos = false;
    private double sidebarWidth = 140.0;
    Controller[] ctrls = { dashCtrl, deviceMngCtrl, homeCtrl };
    public static final double WIDTH_MIN = 73;
    public static final double WIDTH_MAX = 141.321;

    public FXMLPipeline(Node homePage, Node dashboardPage, Node devicePage) {
        this.homePage = homePage;
        this.dashboardPage = dashboardPage;
        this.devicePage = devicePage;
    }

    public void showHome() {
        showPage(homePage, Controller.NavigationItem.HOME);
    }

    public void showDashboard() {
        showPage(dashboardPage, Controller.NavigationItem.DASHBOARD);
    }

    public void showDevices() {
        showPage(devicePage, Controller.NavigationItem.DEVICES);
    }

    private void showPage(Node activePage, Controller.NavigationItem activeItem) {
        if (homePage == null || dashboardPage == null || devicePage == null
                || homeCtrl == null || dashCtrl == null || deviceMngCtrl == null) {
            throw new IllegalStateException("Application pages have not been loaded.");
        }

        setManagedVisibleTo(activePage == homePage, homePage);
        setManagedVisibleTo(activePage == dashboardPage, dashboardPage);
        setManagedVisibleTo(activePage == devicePage, devicePage);

        for (var ctrl : ctrls)
            ctrl.setActiveNavTitle(activeItem);
    }

    public void setManagedVisibleTo(boolean val, Node... nodes) {
        for (var n : nodes) {
            n.setManaged(val);
            n.setVisible(val);
        }
    }

    public void addSplitDividerListener(SplitPane pane) {
        pane.getDividers().get(0).positionProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (!syncingDividerPos && pane.getWidth() > 0) {
                        setSidebarWidth(newVal.doubleValue() * pane.getWidth());
                    }
                });
        pane.widthProperty().addListener((obs, oldWidth, newWidth) -> applySidebarWidth());
        applySidebarWidth();
    }

    public void setSidebarWidth(double width) {
        sidebarWidth = Math.clamp(width, WIDTH_MIN, WIDTH_MAX);
        applySidebarWidth();
    }

    private void applySidebarWidth() {
        if (syncingDividerPos)
            return;
        syncingDividerPos = true;
        try {
            for (var ctrl : ctrls)
                setDividerWidth(ctrl.root);
        } finally {
            syncingDividerPos = false;
        }
    }

    private void setDividerWidth(SplitPane pane) {
        if (pane.getWidth() > 0)
            pane.setDividerPosition(0, sidebarWidth / pane.getWidth());
    }

    public void wifiShow() {
        showDevices();
        deviceMngCtrl.wifiShow();
    }

    public void setWifiButText(String text) {
        for (var ctrl : ctrls)
            ctrl.wifiBut.setText(text);
    }

    public void setWifiButtonConnected(boolean connected) {
        for (var ctrl : ctrls) {
            ctrl.wifiBut.getStyleClass().removeAll("connect-btn", "disconnect-btn");
            ctrl.wifiBut.getStyleClass().add(connected ? "connect-btn" : "disconnect-btn");
        }
    }

    public DashCtrl getDashCtrl(){ return dashCtrl; }
}
