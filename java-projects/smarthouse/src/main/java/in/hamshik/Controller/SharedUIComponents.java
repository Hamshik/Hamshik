package in.hamshik.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import java.io.IOException;

import in.hamshik.Helpers.AppResponder;

public class SharedUIComponents {

    Button wifiBut;
    Label logo, sidebarFooter;
    VBox sideBar;
    Button homeBtn, dashBtn, devicesBtn, thingsBtn;

    AnchorPane sidebarPane, topbarPane;
    @FXML
    private AnchorPane sidebarContainer, topbarContainer;

    AppResponder sharedResponder;

    @FXML
    void initialize() {
        try {
            FXMLLoader sidebarLoader = new FXMLLoader(
                    SharedUIComponents.class.getResource("/in/hamshik/commonFxml/sidebar.fxml"));
            sidebarPane = sidebarLoader.load();
            FXMLLoader topbarLoader = new FXMLLoader(
                    SharedUIComponents.class.getResource("/in/hamshik/commonFxml/topbar.fxml"));
            topbarPane = topbarLoader.load();

            TopBarContoller topBarContoller = topbarLoader.getController();
            SideBarController sideBarController = sidebarLoader.getController();

            setup(sideBarController, topBarContoller);

            sidebarContainer.getChildren().setAll(sidebarPane);
            topbarContainer.getChildren().setAll(topbarPane);

            anchorToParent(sidebarPane);
            anchorToParent(sideBar);

            topbarContainer.setPrefHeight(56.0);
            anchorToParent(topbarPane);
            AnchorPane.setLeftAnchor(topbarContainer, 12.0);
            AnchorPane.setRightAnchor(topbarContainer, 12.0);
            AnchorPane.setTopAnchor(topbarContainer, 11.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void anchorToParent(Node node) {
        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
    }

    private void setup(SideBarController sideBarController, TopBarContoller topBarContoller) {
        homeBtn = sideBarController.homeBtn;
        dashBtn = sideBarController.dashBtn;
        devicesBtn = sideBarController.devicesBtn;
        thingsBtn = sideBarController.thingsBtn;
        sidebarFooter = sideBarController.sidebarFooter;
        logo = sideBarController.logo;
        sideBar = sideBarController.sideBar;

        wifiBut = topBarContoller.wifiBut;
    }

}