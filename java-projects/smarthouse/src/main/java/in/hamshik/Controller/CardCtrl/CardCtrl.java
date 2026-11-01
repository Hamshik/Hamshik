package in.hamshik.Controller.CardCtrl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import in.hamshik.App;
import in.hamshik.Helpers.DragHelper;
import in.hamshik.Helpers.CardManager.*;
import in.hamshik.Helpers.VarHelpers.Variable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.controlsfx.control.textfield.TextFields;

/**
 * Controller for the IoT variable card (card.fxml).
 *
 * Type and Permission are driven by the ESP — they are shown as read-only labels
 * and filled automatically when the user picks a variable from the autocomplete.
 * The user only enters: Sensor name, Variable name (from autocomplete), and Range.
 */
public class CardCtrl {

    @FXML private VBox cardRoot;
    @FXML private Label cardTitleLabel;
    @FXML private Button closeBtn;
    @FXML private TextField sensorField;
    @FXML private TextField varNameField, rangeFrom, rangeTo;
    @FXML private Button typeInfoLabel, permissionInfoLabel;
    @FXML private Button saveBtn;
    @FXML private Label errLabel;

    /** Tracks the card currently being edited, or null if creating a new card. */
    private CardShowerCtrl currentEditingCard = null;

    private Consumer<VBox> onClose;
    private Runnable onDismiss;

    /** Full list of variables received from the ESP. */
    private HashMap<String, Variable> variableData = new HashMap<>();
    /** The Variable that was resolved from the autocomplete selection. */
    private String selectedVariable = null;

    public void setVariableNames(Collection<Variable> names) {
        convertToHash(names == null ? null : names.stream()
                .filter(v -> v != null && !v.name().isBlank())
                .distinct()
                .toList());
    }

    private void convertToHash(Collection<Variable> names) {
        variableData.clear();
        if (names == null) return;
        for(var name : names)
            variableData.put(name.name(), name);
    }

    @FXML
    protected void initialize() {
        var completion = TextFields.bindAutoCompletion(varNameField, request -> {
            String query = request.getUserText() == null
                    ? ""
                    : request.getUserText().trim().toLowerCase();
            if (query.isEmpty() || variableData.keySet() == null)
                return List.of();
            return variableData.keySet().stream()
                    .filter(v -> v.toLowerCase().contains(query)).toList();
        });
        completion.setVisibleRowCount(5);
        completion.setPrefWidth(varNameField.getPrefWidth());

        // When user selects a suggestion, auto-fill type and permission labels]

        completion.setOnAutoCompleted(event -> {
            String v = event.getCompletion();
            applyVariable(v);
        });

        varNameField.textProperty().addListener((obs, oldValue, newValue) -> {
            String value = newValue == null ? "" : newValue.trim();
            if (selectedVariable != null && !selectedVariable.equalsIgnoreCase(value))
                clearVariableInfo();
        });

        // Also try to resolve when user finishes typing manually (focus lost / Enter)
        varNameField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused) resolveVariableByName(varNameField.getText());
        });
    }

    /** Resolves type/permission from the variable list by matching name. */
    private void resolveVariableByName(String name) {
        if (name == null || name.isBlank() || variableData.keySet() == null) return;
        variableData.keySet().stream()
                .filter(v -> v.equalsIgnoreCase(name.trim()))
                .findFirst()
                .ifPresent(this::applyVariable);
    }

    /** Applies a resolved Variable — updates the info labels and records selection. */
    private void applyVariable(String v) {
        selectedVariable = v;
        var variable = variableData.get(v);
        typeInfoLabel.setText(variable.type().displayName());
        permissionInfoLabel.setText(variable.permission().displayName());
    }

    /** Clears the resolved variable and resets the info labels. */
    private void clearVariableInfo() {
        selectedVariable = null;
        typeInfoLabel.setText("— select a variable —");
        permissionInfoLabel.setText("— select a variable —");
    }

    // ─── Edit support ────────────────────────────────────────────────────────

    public void startEdit(CardShowerCtrl card) {
        if (card == null || card.getInfo() == null) return;
        this.currentEditingCard = card;

        DeviceInfo info = card.getInfo();
        sensorField.setText(info.sensorName());
        varNameField.setText(info.var().name());
        rangeFrom.setText(String.valueOf(info.range().from()));
        rangeTo.setText(String.valueOf(info.range().to()));

        // Re-resolve variable metadata from ESP list
        resolveVariableByName(info.var().name());

        if (cardTitleLabel != null) cardTitleLabel.setText("Edit Variable");
        if (saveBtn != null) saveBtn.setText("Update Variable");
    }

    public void resetToNew() {
        this.currentEditingCard = null;
        App.fxmlPipeline.showError(null, false, errLabel, varNameField, rangeFrom, rangeTo, sensorField);
        sensorField.clear();
        varNameField.clear();
        rangeFrom.clear();
        rangeTo.clear();
        clearVariableInfo();
        if (cardTitleLabel != null) cardTitleLabel.setText("New Variable");
        if (saveBtn != null) saveBtn.setText("Save Variable");
    }

    // ─── FXML handlers ───────────────────────────────────────────────────────

    @FXML
    private void handleClose(ActionEvent e) {
        if (onClose != null)
            onClose.accept(cardRoot);
        else if (onDismiss != null)
            onDismiss.run();
    }

    @FXML
    private void handleSave(ActionEvent e) {
        App.fxmlPipeline.showError(null, false, errLabel, sensorField, varNameField,
                rangeFrom, rangeTo);

        String sensorName = sensorField.getText().trim();
        String variableName = varNameField.getText().trim();
        String rangeFromVal = rangeFrom.getText().trim();
        String rangeToVal = rangeTo.getText().trim();

        if (sensorName.isEmpty() || variableName.isEmpty()
                || rangeFromVal.isEmpty() || rangeToVal.isEmpty()) {
            App.fxmlPipeline.showError("Complete all required fields.", true, errLabel,
                    sensorName.isEmpty() ? sensorField : null,
                    variableName.isEmpty() ? varNameField : null,
                    rangeFromVal.isEmpty() ? rangeFrom : null,
                    rangeToVal.isEmpty() ? rangeTo : null);
            return;
        }

        selectedVariable = null;
        resolveVariableByName(variableName);
        if (selectedVariable == null) {
            App.fxmlPipeline.showError("Select a variable available on the connected device.",
                    true, errLabel, varNameField);
            return;
        }

        final int from;
        try {
            from = Integer.parseInt(rangeFromVal);
        } catch (NumberFormatException ex) {
            App.fxmlPipeline.showError("Minimum range value must be a whole number.", true,
                    errLabel, rangeFrom);
            return;
        }

        final int to;
        try {
            to = Integer.parseInt(rangeToVal);
        } catch (NumberFormatException ex) {
            App.fxmlPipeline.showError("Maximum range value must be a whole number.", true,
                    errLabel, rangeTo);
            return;
        }

        DeviceInfo newInfo = new DeviceInfo(sensorName, variableData.get(selectedVariable),
                new Range(from, to));

        var cardMg = App.fxmlPipeline.getDashCtrl().cardManager;

        if (currentEditingCard != null) {
            currentEditingCard.setup(newInfo);
            if (cardMg != null) cardMg.reapplyCachedValue(currentEditingCard);
            currentEditingCard = null;
        } else {
            DragHelper.makeDraggable(cardMg.addCard(newInfo), cardMg.board);
        }

        handleCancel(e);
    }

    @FXML
    public void handleCancel(ActionEvent e) {
        resetToNew();
        if (onDismiss != null) onDismiss.run();
    }

    // ─── Wiring API ──────────────────────────────────────────────────────────

    public void setOnClose(Consumer<VBox> onClose) {
        this.onClose = onClose;
    }

    public void setOnDismiss(Runnable onDismiss) {
        this.onDismiss = onDismiss;
    }

}
