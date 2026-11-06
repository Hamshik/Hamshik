package in.hamshik.Helpers;

import in.hamshik.App;
import in.hamshik.Controller.MainCtrl.Controller.*;
import in.hamshik.Controller.MainCtrl.*;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;

public class FXMLPipeline extends App {
    public Node[] pages;
    private Controller homeCtrl = App.homeCtrl;
    private DashCtrl dashCtrl = App.dashCtrl;
    private DeviceMngCtrl deviceMngCtrl = App.deviceMngCtrl;
    private FileMngCtrl fileMngCtrl = App.fileMngCtrl;
    private CodeRunner codeCtrl = App.codeCtrl;
    private boolean syncingDividerPos = false;
    private double sidebarWidth = 140.0;
    Controller[] ctrls = { dashCtrl, deviceMngCtrl, homeCtrl, fileMngCtrl, codeCtrl };
    public static final double WIDTH_MIN = 73;
    public static final double WIDTH_MAX = 141.321;

    public FXMLPipeline(Node... pages) {
        this.pages = pages;
    }

    public void showHome() {
        showPage(NavigationItem.HOME);
    }

    public void showDashboard() {
        showPage(NavigationItem.DASHBOARD);
    }

    public void showDevices() {
        showPage(NavigationItem.DEVICES);
    }

    public void showFileMng(){
        showPage(NavigationItem.FILE_MNG);
    }

    public void showCode(){
        showPage(NavigationItem.CODE);
    }

    private void showPage(NavigationItem activeItem) {
        for(Node page : pages)
            if(page == null)
                throw new IllegalStateException("Pages are not loaded");

        for (Node page : pages)
            setManagedVisibleTo(page == pages[activeItem.val()], page);

        for (var ctrl : ctrls)
            ctrl.setActiveNavTitle(activeItem);
    }

    public static void setManagedVisibleTo(boolean val, Node... nodes) {
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

    public static void showError(String msg, boolean showErr, Label errLabel, Node... errNodes) {
        if (errLabel != null) {
            if (!showErr) {
                errLabel.setText("");
                errLabel.getStyleClass().remove("error-message");
            } else if (msg != null) {
                errLabel.setText(msg);
                if (!msg.isBlank() && !errLabel.getStyleClass().contains("error-message"))
                    errLabel.getStyleClass().add("error-message");
            }
        }

        for (var node : errNodes) {
            if (node == null) continue;
            if (showErr) {
                if (!node.getStyleClass().contains("error"))
                    node.getStyleClass().add("error");
            } else {
                node.getStyleClass().remove("error");
            }
        }
    }

    public void addSplitDividerListener(){
        for(var controller : ctrls) fxmlPipeline.addSplitDividerListener(controller.root);
    }
}
