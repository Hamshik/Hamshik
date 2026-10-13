package in.hamshik.Helpers;

import in.hamshik.App;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;

public class AppResponder {
    private static final double SIDEBAR_FULL = 140;
    private static final double SIDEBAR_MIN  = 60;
    private static final double COLLAPSE_AT  = 560;   // px window width

    private boolean collapsed = false;

    private SplitPane root;

    private Label logoText;
    private VBox sidebar;
    private Label sidebarFooter;
    private Button sideHomeBut;
    private Button sideDashBut;
    private Button sideDeviceBut;
    private Button sideThingsBut;

    public AppResponder(SplitPane root, VBox sidebar, Label logoText, Label sidebarFooter,
        Button sideHomeBut, Button sideDashBut, Button sideDeviceBut, Button sideThingsBut) {
        this.root = root;
        this.sidebar = sidebar;
        this.logoText = logoText;
        this.sidebarFooter = sidebarFooter;
        this.sideHomeBut = sideHomeBut;
        this.sideDashBut = sideDashBut;
        this.sideDeviceBut = sideDeviceBut;
        this.sideThingsBut = sideThingsBut;
    }

    /* ================= Website-like responsive layout ================= */

    public void setupResponsiveLayout() {
        // Collapse sidebar below the threshold, like a website nav rail
        root.widthProperty().addListener((obs, oldW, newW) -> applyLayout(newW.doubleValue()));
        applyLayout(root.getWidth());
    }

    private void applyLayout(double width) {
        if (width <= 0) {
            return;
        }

        boolean wantCollapsed = width < COLLAPSE_AT;
        double sidebarWidth = wantCollapsed ? SIDEBAR_MIN : SIDEBAR_FULL;

        if (wantCollapsed != collapsed) {
            collapsed = wantCollapsed;
            if (collapsed) {
                // icon-rail mode
                App.setManagedVisibleTo(false, logoText, sidebarFooter);
                setNav(sideHomeBut, "⌂", 40);
                setNav(sideDashBut, "▦", 40);
                setNav(sideDeviceBut, "◉", 40);
                setNav(sideThingsBut, "⚙", 40);
            } else {
                // full sidebar mode
                App.setManagedVisibleTo(true, sidebarFooter, logoText);
                setNav(sideHomeBut, "⌂   Home", 120);
                setNav(sideDashBut, "▦   Dashboard", 120);
                setNav(sideDeviceBut, "◉   Devices", 120);
                setNav(sideThingsBut, "⚙   Things", 120);
            }

            root.setDividerPositions(Math.min(sidebarWidth / width, 1.0));
        }

        sidebar.setPrefWidth(sidebarWidth);
    }

    private void setNav(Button btn, String text, double prefWidth) {
        btn.setText(text);
        btn.setPrefWidth(prefWidth);
    }

}
