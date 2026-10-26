package in.hamshik.Controller.CardCtrl;

import java.util.ArrayList;
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

    /** Tracks the card currently being edited, or null if creating a new card. */
    private CardShowerCtrl currentEditingCard = null;

    private Consumer<VBox> onClose;
    private Runnable onDismiss;

    /** Full list of variables received from the ESP. */
    private HashMap<String, Variable> variableData;
    private Collection<String> variableNames = new ArrayList<>();
    /** The Variable that was resolved from the autocomplete selection. */
    private String selectedVariable = null;

    public void setVariableNames(Collection<Variable> names) {
        variableData = names == null ? new HashMap<>() : convertToHash(names.stream()
                .filter(v -> v != null && !v.name().isBlank())
                .distinct()
                .toList());
    }

    private HashMap<String, Variable> convertToHash(Collection<Variable> names) {
        HashMap<String, Variable> map = new HashMap<>();
        for(var name : names){
            map.put(name.name(), name);
            variableNames.add(name.name());
        }
        return map;
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
                    .filter(v -> v.toLowerCase().contains(query)).toList();
        });
        completion.setVisibleRowCount(5);
        completion.setPrefWidth(varNameField.getPrefWidth());

        // When user selects a suggestion, auto-fill type and permission labels]

        completion.setOnAutoCompleted(event -> {
            String v = event.getCompletion();
            applyVariable(v);
        });

        // Also try to resolve when user finishes typing manually (focus lost / Enter)
        varNameField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused) resolveVariableByName(varNameField.getText());
        });
    }

    /** Resolves type/permission from the variable list by matching name. */
    private void resolveVariableByName(String name) {
        if (name == null || name.isBlank()) return;
        variableNames.stream()
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
        varNameField.setText(info.varName());
        rangeFrom.setText(String.valueOf(info.range().from()));
        rangeTo.setText(String.valueOf(info.range().to()));

        // Re-resolve variable metadata from ESP list
        resolveVariableByName(info.varName());

        if (cardTitleLabel != null) cardTitleLabel.setText("Edit Variable");
        if (saveBtn != null) saveBtn.setText("Update Variable");
    }

    public void resetToNew() {
        this.currentEditingCard = null;
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
        String sensorTy    = sensorField.getText();
        String varName     = varNameField.getText();
        String rangeFromVal = rangeFrom.getText();
        String rangeToVal   = rangeTo.getText();

        // Require a resolved variable (so type + permission come from ESP)
        if (selectedVariable == null) {
            resolveVariableByName(varName);
        }

        if (selectedVariable == null ||
                sensorTy == null || sensorTy.isBlank() ||
                varName  == null || varName.isBlank()  ||
                rangeFromVal == null || rangeFromVal.isBlank() ||
                rangeToVal   == null || rangeToVal.isBlank())
            return;

        try {
            int from = Integer.parseInt(rangeFromVal);
            int to   = Integer.parseInt(rangeToVal);

            // Convert ESP enums → the string labels CardShowerCtrl / CardHolder use
            String typeName = variableData.get(selectedVariable).type().displayName();    // "INT", "FLOAT", …
            String permName = variableData.get(selectedVariable).permission().displayName(); // "Read Only", …

            DeviceInfo newInfo = new DeviceInfo(sensorTy, typeName, varName, permName, new Range(from, to));

            var cardMg = App.fxmlPipeline.getDashCtrl().cardManager;

            if (currentEditingCard != null) {
                currentEditingCard.setup(newInfo);
                if (cardMg != null) cardMg.reapplyCachedValue(currentEditingCard);
                currentEditingCard = null;
            } else {
                DragHelper.makeDraggable(cardMg.addCard(newInfo), cardMg.board);
            }

            handleCancel(e);
        } catch (NumberFormatException ignored) {
        }
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
