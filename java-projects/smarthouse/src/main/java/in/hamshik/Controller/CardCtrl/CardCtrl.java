package in.hamshik.Controller.CardCtrl;

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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.controlsfx.control.textfield.TextFields;

/**
 * Controller for the modern IoT variable card (card.fxml).
 * Handles create, edit, close, permission toggle, data-type chip selection, save & cancel.
 */
public class CardCtrl {

    @FXML
    private VBox cardRoot;
    @FXML
    private Label cardTitleLabel;
    @FXML
    private Button closeBtn;
    @FXML
    private TextField sensorField;
    @FXML
    private Button permissionRead;
    @FXML
    private Button permissionWrite;
    @FXML
    private Button permissionBoth;
    @FXML
    private TextField varNameField, rangeFrom, rangeTo;
    @FXML
    private Button saveBtn;

    /** Tracks the card currently being edited, or null if creating a new card. */
    private CardShowerCtrl currentEditingCard = null;

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

        // sets the default to Read/Write
        setActive(permissionBoth, permissionRead, permissionWrite);
    }

    public void startEdit(CardShowerCtrl card) {
        if (card == null || card.getInfo() == null) return;
        this.currentEditingCard = card;

        DeviceInfo info = card.getInfo();
        sensorField.setText(info.sensorName());
        varNameField.setText(info.varName());
        rangeFrom.setText(String.valueOf(info.range().from()));
        rangeTo.setText(String.valueOf(info.range().to()));

        // Set permission button
        if ("Read".equalsIgnoreCase(info.permissions())) {
            setActive(permissionRead, permissionWrite, permissionBoth);
        } else if ("Write".equalsIgnoreCase(info.permissions())) {
            setActive(permissionWrite, permissionRead, permissionBoth);
        } else {
            setActive(permissionBoth, permissionRead, permissionWrite);
        }

        // Set type button
        selectType(info.dataTy());

        if (cardTitleLabel != null) cardTitleLabel.setText("Edit Variable");
        if (saveBtn != null) saveBtn.setText("Update Variable");
    }

    public void resetToNew() {
        this.currentEditingCard = null;
        sensorField.clear();
        varNameField.clear();
        rangeFrom.clear();
        rangeTo.clear();
        setActive(permissionBoth, permissionRead, permissionWrite);
        clearTypeSelection();
        if (cardTitleLabel != null) cardTitleLabel.setText("New Variable");
        if (saveBtn != null) saveBtn.setText("Save Variable");
    }

    private void selectType(String type) {
        if (type == null) return;
        for (var node : cardRoot.lookupAll(".type-btn")) {
            if (node instanceof Button b) {
                if (b.getText().equalsIgnoreCase(type)) {
                    if (!b.getStyleClass().contains("type-btn-active")) {
                        b.getStyleClass().add("type-btn-active");
                    }
                } else {
                    b.getStyleClass().remove("type-btn-active");
                }
            }
        }
    }

    private void clearTypeSelection() {
        for (var node : cardRoot.lookupAll(".type-btn")) {
            if (node instanceof Button b) {
                b.getStyleClass().remove("type-btn-active");
            }
        }
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
        String sensorTy = sensorField.getText();
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
            DeviceInfo newInfo = new DeviceInfo(sensorTy, type, varName,
                    activeText(permissionBoth, permissionRead, permissionWrite),
                    new Range(rangeFromIntVal, rangeToIntVal));

            var cardMg = App.fxmlPipeline.getDashCtrl().cardManager;

            if (currentEditingCard != null) {
                // Edit existing card in-place
                currentEditingCard.setup(newInfo);
                if (cardMg != null) {
                    cardMg.reapplyCachedValue(currentEditingCard);
                }
                currentEditingCard = null;
            } else {
                // Add new card
                DragHelper.makeDraggable(cardMg.addCard(newInfo), cardMg.board);
            }

            handleCancel(e);
        } catch (NumberFormatException _) {
            return;
        }
    }

    @FXML
    public void handleCancel(ActionEvent e) {
        resetToNew();
        if (onDismiss != null)
            onDismiss.run();
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
