package in.hamshik.Helpers;

import java.util.ArrayList;
import java.util.List;

import in.hamshik.Controller.CardController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Manages IoT variable cards on the dashboard "board" (Pane).
 * Cards are loaded from modern-card.fxml — no UI code here.
 */
public class CardManager {
    public final Pane board;
    private final List<DeviceInfo> removedCards = new ArrayList<>();
    public record Range(int from, int to) {}
    public record DeviceInfo(String sensorName, String dataTy, 
        String varName, String permissions, Range range){}
    public CardController cardCtrl;

    public CardManager(Pane board) {
        this.board = board;
    }

    public VBox addCard(DeviceInfo info){
        VBox card = createCard(info);
        positionCard(card);
        board.getChildren().add(card);
        return card;
    }

    private VBox createCard(DeviceInfo info) {
        VBox card = new VBox(14);
        card.getStyleClass().add("card");
        card.setPrefWidth(340);
        card.setPadding(new Insets(16));

        var cardStylesheet = CardManager.class.getResource(
                "/in/hamshik/styleSheets/dash-card.css");
        if (cardStylesheet == null)
            throw new IllegalStateException("Missing dashboard card stylesheet");
        card.getStylesheets().add(cardStylesheet.toExternalForm());
        card.getChildren().addAll(
            createHeader(info),
            createReadingSection(info),
            createAccessLabel(info));
        return card;
    }

    private HBox createHeader(DeviceInfo info) {
        Label name = new Label(info.varName());
        name.getStyleClass().add("card-heading");

        Label sensor = new Label(info.sensorName());
        sensor.getStyleClass().add("card-subheading");

        VBox title = new VBox(4, name, sensor);
        HBox.setHgrow(title, Priority.ALWAYS);

        HBox header = new HBox(12, title, createEditButton());
        header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }

    private Button createEditButton() {
        Button editButton = new Button("Edit");
        editButton.getStyleClass().add("ghost-btn");
        editButton.setDisable(true);
        editButton.setTooltip(new Tooltip("Editing will be available later"));
        return editButton;
    }

    private HBox createReadingSection(DeviceInfo info) {
        StackPane indicator = createPercentageIndicator();

        Label readingLabel = new Label("Current value");
        readingLabel.getStyleClass().add("field-label");
        Label typeLabel = new Label(info.dataTy());
        typeLabel.getStyleClass().add("card-heading");
        VBox reading = new VBox(6, readingLabel, typeLabel);
        reading.setAlignment(Pos.CENTER_LEFT);

        HBox valueSection = new HBox(16, indicator, reading);
        valueSection.setAlignment(Pos.CENTER_LEFT);
        return valueSection;
    }

    private StackPane createPercentageIndicator() {
        ProgressIndicator percentageIndicator = new ProgressIndicator(0);
        percentageIndicator.setPrefSize(72, 72);
        percentageIndicator.setMinSize(72, 72);
        percentageIndicator.setMaxSize(72, 72);
        percentageIndicator.setStyle("-fx-progress-color: #00A9A5;");

        Label percentage = new Label("0");
        percentage.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; "
                + "-fx-text-fill: #102027;");
        return new StackPane(percentageIndicator, percentage);
    }

    private Label createAccessLabel(DeviceInfo info) {
        Label permissionLabel = new Label("ACCESS  " + info.permissions());
        permissionLabel.getStyleClass().add("card-subheading");
        return permissionLabel;
    }

    private void positionCard(VBox card) {
        final double cardWidth = card.getPrefWidth();
        final double cardGap = 16;
        int index = board.getChildren().size();
        int columns = Math.max(1, (int) (board.getWidth() / (cardWidth + cardGap)));
        card.setLayoutX(16 + (index % columns) * (cardWidth + cardGap));
        card.setLayoutY(16 + (index / columns) * 190);
    }
}
