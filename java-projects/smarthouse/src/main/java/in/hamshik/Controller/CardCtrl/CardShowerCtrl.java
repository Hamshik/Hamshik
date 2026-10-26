package in.hamshik.Controller.CardCtrl;

import java.util.function.Consumer;

import com.google.gson.JsonElement;

import in.hamshik.Helpers.CardManager.DeviceInfo;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;

public class CardShowerCtrl {

    @FXML private VBox cardRoot;
    @FXML private Label varNameLabel;
    @FXML private Label sensorNameLabel;
    @FXML private ProgressIndicator percentageIndicator;
    @FXML private Label percentageLabel;
    @FXML private Label readingLabel;
    @FXML private Label valueLabel;
    @FXML private Label permissionLabel;
    @FXML private Button editBtn;
    @FXML private Button deleteBtn;

    private DeviceInfo info;
    private Consumer<CardShowerCtrl> onDelete;
    private Consumer<CardShowerCtrl> onEdit;

    // Observable properties for reactive UI updates
    private final DoubleProperty numericValue = new SimpleDoubleProperty(0.0);
    private final StringProperty displayValue = new SimpleStringProperty("--");

    @FXML
    protected void initialize() {
        // 1. Unidirectional binding: UI label updates automatically with displayValue
        valueLabel.textProperty().bind(displayValue);

        // 2. Change listener: whenever numericValue changes, recalculate range percentage
        numericValue.addListener((obs, oldVal, newVal) -> {
            if (info == null) return;
            double val = newVal.doubleValue();
            int min = info.range().from();
            int max = info.range().to();
            double progress = 0.0;
            if (max > min) {
                progress = Math.max(0.0, Math.min(1.0, (val - min) / (double) (max - min)));
            } else if (min > max) {
                progress = Math.max(0.0, Math.min(1.0, (min - val) / (double) (min - max)));
            }
            percentageIndicator.setProgress(progress);
            percentageLabel.setText(String.format("%.0f%%", progress * 100.0));
        });
    }

    public void setup(DeviceInfo info) {
        this.info = info;
        varNameLabel.setText(info.varName());
        sensorNameLabel.setText(info.sensorName());
        readingLabel.setText("Current value (" + info.dataTy() + ")");
        permissionLabel.setText("ACCESS  " + info.permissions());
    }

    public void updateValue(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            displayValue.set("--");
            numericValue.set(0.0);
            return;
        }

        if (element.isJsonPrimitive()) {
            var prim = element.getAsJsonPrimitive();
            if (prim.isNumber()) {
                double val = prim.getAsDouble();
                numericValue.set(val);
                if (info != null && "FLOAT".equalsIgnoreCase(info.dataTy())) {
                    displayValue.set(String.format("%.2f", val));
                } else if (info != null && ("INT".equalsIgnoreCase(info.dataTy()) || "UNSIGNED".equalsIgnoreCase(info.dataTy()))) {
                    displayValue.set(String.valueOf(prim.getAsLong()));
                } else {
                    displayValue.set(prim.getAsString());
                }
            } else if (prim.isBoolean()) {
                boolean b = prim.getAsBoolean();
                numericValue.set(b ? 1.0 : 0.0);
                displayValue.set(b ? "HIGH" : "LOW");
            } else {
                String str = prim.getAsString();
                displayValue.set(str);
                try {
                    numericValue.set(Double.parseDouble(str));
                } catch (NumberFormatException ignored) {}
            }
        } else {
            displayValue.set(element.toString());
        }
    }

    @FXML
    private void handleEdit(ActionEvent event) {
        if (onEdit != null) {
            onEdit.accept(this);
        }
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        if (onDelete != null) {
            onDelete.accept(this);
        }
    }

    public void setOnDelete(Consumer<CardShowerCtrl> onDelete) {
        this.onDelete = onDelete;
    }

    public void setOnEdit(Consumer<CardShowerCtrl> onEdit) {
        this.onEdit = onEdit;
    }

    public VBox getCardRoot() {
        return cardRoot;
    }

    public DeviceInfo getInfo() {
        return info;
    }

    public DoubleProperty numericValueProperty() {
        return numericValue;
    }

    public StringProperty displayValueProperty() {
        return displayValue;
    }
}
