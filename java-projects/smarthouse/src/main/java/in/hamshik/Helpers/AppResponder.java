package in.hamshik.Helpers;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AppResponder {
    private static final double SIDEBAR_FULL = 140;
    private static final double SIDEBAR_MIN  = 60;
    private static final double COLLAPSE_AT  = 560;   // px window width

    private boolean collapsed = false;

    private AnchorPane root;

    // Responsive layout elements from FXML
    private AnchorPane topbar;
    private AnchorPane contentPane;
    private VBox sidebar;
    private Label logoText;
    private Label sidebarFooter;
    private Button navDashboard;
    private Button navDevices;
    private Button navThings;

    public AppResponder(AnchorPane root, AnchorPane topbar, AnchorPane contentPane, VBox sidebar, Label logoText, Label sidebarFooter, Button navDashboard, Button navDevices, Button navThings) {
        this.root = root;
        this.topbar = topbar;
        this.contentPane = contentPane;
        this.sidebar = sidebar;
        this.logoText = logoText;
        this.sidebarFooter = sidebarFooter;
        this.navDashboard = navDashboard;
        this.navDevices = navDevices;
        this.navThings = navThings;
    }

    /* ================= Website-like responsive layout ================= */

    public void setupResponsiveLayout() {
        // Enforce the 400x400 minimum window size (FXML alone can't do this)
        root.sceneProperty().addListener((obs, oldScene, scene) -> {
            if (scene != null) {
                scene.windowProperty().addListener((o, oldWin, win) -> {
                    if (win instanceof Stage stage) {
                        stage.setMinWidth(400);
                        stage.setMinHeight(400);
                    }
                });
            }
        });

        // Collapse sidebar below the threshold, like a website nav rail
        root.widthProperty().addListener((obs, oldW, newW) -> applyLayout(newW.doubleValue()));
        applyLayout(root.getWidth());
    }

    private void applyLayout(double width) {
        boolean wantCollapsed = width < COLLAPSE_AT;

        if (wantCollapsed != collapsed) {
            collapsed = wantCollapsed;
            if (collapsed) {
                // icon-rail mode
                logoText.setVisible(false);
                logoText.setManaged(false);
                sidebarFooter.setVisible(false);
                sidebarFooter.setManaged(false);
                setNav(navDashboard, "▦", 40);
                setNav(navDevices, "◉", 40);
                setNav(navThings, "⚙", 40);
            } else {
                // full sidebar mode
                logoText.setVisible(true);
                logoText.setManaged(true);
                sidebarFooter.setVisible(true);
                sidebarFooter.setManaged(true);
                setNav(navDashboard, "▦   Dashboard", 120);
                setNav(navDevices, "◉   Devices", 120);
                setNav(navThings, "⚙   Things", 120);
            }
        }

        // topbar + content always start right after the sidebar
        sidebar.setPrefWidth(collapsed ? SIDEBAR_MIN : SIDEBAR_FULL);
        AnchorPane.setLeftAnchor(topbar, sidebar.getPrefWidth());
        AnchorPane.setLeftAnchor(contentPane, sidebar.getPrefWidth());
    }

    private void setNav(Button btn, String text, double prefWidth) {
        btn.setText(text);
        btn.setPrefWidth(prefWidth);
    }

}
