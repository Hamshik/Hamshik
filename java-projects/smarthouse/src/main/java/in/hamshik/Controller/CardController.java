package in.hamshik.Controller;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import in.hamshik.App;
import in.hamshik.Helpers.DragHelper;
import in.hamshik.Helpers.CardManager.DeviceInfo;
import in.hamshik.Helpers.CardManager.Range;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.controlsfx.control.textfield.TextFields;

/**
 * Controller for the modern IoT variable card (modern-card.fxml).
 * Handles close / permission toggle / data-type chip selection / save & cancel.
 */
public class CardController {

    @FXML
    private VBox cardRoot;
    @FXML
    private Button closeBtn;
    @FXML
    private ComboBox<String> sensorCombo;
    @FXML
    private Button permissionRead;
    @FXML
    private Button permissionWrite;
    @FXML
    private Button permissionBoth;
    @FXML
    private TextField varNameField, rangeFrom, rangeTo;

    /** Called by CardManager when the card is deleted (undo support). */
    private Consumer<VBox> onClose;
    private Runnable onDismiss;

    private List<String> variableNames = List.of();

    public void setVariableNames(Collection<String> names) {
        variableNames = names == null ? List.of() : names.stream()
                .filter(name -> name != null && !name.isBlank())
                .distinct()
                .toList();
    }

    @FXML
    protected void initialize() {
        var completion = TextFields.bindAutoCompletion(varNameField, request -> {
            String query = request.getUserText() == null
                    ? ""
                    : request.getUserText().trim().toLowerCase();
            if (query.isEmpty())
                return List.of();

            return variableNames.stream()
                    .filter(name -> name.toLowerCase().contains(query))
                    .toList();
        });
        completion.setVisibleRowCount(5);
        completion.setPrefWidth(varNameField.getPrefWidth());

        sensorCombo.getItems().addAll(
                "DHT11 (Temp/Hum)",
                "Ultrasonic (HC-SR04)",
                "BMP280 (Pressure)",
                "MQ-2 (Gas)");

        // sets the default to Read/Write
        setActive(permissionBoth, permissionRead, permissionWrite);
    }

    /* ================= FXML handlers ================= */

    @FXML
    private void handleClose(ActionEvent e) {
        if (onClose != null)
            onClose.accept(cardRoot);
        else if (onDismiss != null)
            onDismiss.run();
    }

    @FXML
    private void handlePermission(ActionEvent e) {
        Button selected = (Button) e.getSource();
        setActive(selected, permissionRead, permissionWrite, permissionBoth);
    }

    @FXML
    private void handleTypes(ActionEvent e) {
        Button selected = (Button) e.getSource();
        // chips live inside the same GridPane — toggle within its siblings
        for (var node : selected.getParent().getChildrenUnmodifiable()) {
            if (node instanceof Button b && b != selected)
                b.getStyleClass().remove("type-btn-active");
        }
        if (!selected.getStyleClass().contains("type-btn-active"))
            selected.getStyleClass().add("type-btn-active");
    }

    @FXML
    private void handleSave(ActionEvent e) {
        String type = activeText((Button) cardRoot.lookup(".type-btn-active"));
        String sensorTy = sensorCombo.getValue();
        String varName = varNameField.getText();
        String rangeFromVal = rangeFrom.getText();
        String rangeToVal = rangeTo.getText();

        if (type == null || type.isEmpty() ||
                sensorTy == null || sensorTy.isEmpty() ||
                varName == null || varName.isEmpty() ||
                rangeToVal == null || rangeToVal.isEmpty() ||
                rangeFromVal == null || rangeFromVal.isEmpty())
            return;
        try {
            int rangeFromIntVal = Integer.parseInt(rangeFromVal);
            int rangeToIntVal = Integer.parseInt(rangeToVal);
            var cardMg = App.fxmlPipeline.getDashCtrl().cardManager;
            DragHelper.makeDraggable(cardMg.addCard(
                    new DeviceInfo(sensorTy, type, varName,
                            activeText(permissionBoth, permissionRead, permissionWrite),
                            new Range(rangeFromIntVal, rangeToIntVal))),
                    cardMg.board);
            handleCancel(e);
        } catch (NumberFormatException _) {
            return;
        }
    }

    @FXML
    public void handleCancel(ActionEvent e) {
        sensorCombo.getSelectionModel().clearSelection();
        varNameField.clear();
        setActive(permissionBoth, permissionRead, permissionWrite);
        if (onDismiss != null)
            onDismiss.run();
        // re-select default chip if desired, or leave as-is
    }

    /* ================= Wiring API ================= */

    public void setOnClose(Consumer<VBox> onClose) {
        this.onClose = onClose;
    }

    public void setOnDismiss(Runnable onDismiss) {
        this.onDismiss = onDismiss;
    }

    /* ================= Helpers ================= */

    private void setActive(Button active, Button... group) {
        for (Button b : group)
            b.getStyleClass().remove("perm-btn-active");
        active.getStyleClass().add("perm-btn-active");
    }

    private String activeText(Button... group) {
        for (Button b : group)
            if (b != null
                    && (b.getStyleClass().contains("perm-btn-active")
                            || b.getStyleClass().contains("type-btn-active")))
                return b.getText();
        return null;
    }
}
