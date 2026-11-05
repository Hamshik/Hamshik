package in.hamshik.Controller.CardCtrl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Consumer;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import in.hamshik.Helpers.CloudRequest;
import in.hamshik.Helpers.FXMLPipeline;
import in.hamshik.Helpers.VarHelpers;
import in.hamshik.Controller.MainCtrl.DeviceMngCtrl;
import in.hamshik.Helpers.CardManager.DeviceInfo;
import in.hamshik.WifiHandler.WifiSender;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

public class CardShowerCtrl {

    @FXML private VBox cardRoot;
    @FXML private Label varNameLabel, 
        sensorNameLabel, percentageLabel, 
        readingLabel, valueLabel, permissionLabel;
    @FXML private ProgressIndicator percentageIndicator;
    @FXML private Button editBtn, deleteBtn;
    @FXML private TextField setterField;
    @FXML private ToggleButton toggleBtn;

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
        varNameLabel.setText(info.var().name());
        sensorNameLabel.setText(info.sensorName());
        readingLabel.setText("Current value (" + info.var().type().toString() + ")");
        permissionLabel.setText("ACCESS  " + info.var().permission());

        boolean isBool = info.var().type() == VarHelpers.Type.BOOL;

        switch (info.var().permission()) {
            case RW -> {
                if (isBool) 
                    FXMLPipeline.setManagedVisibleTo(true, toggleBtn);
                else 
                    FXMLPipeline.setManagedVisibleTo(true, percentageIndicator, percentageLabel, setterField);
            }
            case RO -> {
                if (isBool) {
                    FXMLPipeline.setManagedVisibleTo(true, toggleBtn);
                    toggleBtn.setDisable(true);
                } else 
                    FXMLPipeline.setManagedVisibleTo(true, percentageIndicator, percentageLabel);
            }
            
            case WO -> {
                if (isBool) 
                    // BOOL + WO: toggle only for writing
                    FXMLPipeline.setManagedVisibleTo(true, toggleBtn);
                else 
                    FXMLPipeline.setManagedVisibleTo(true, setterField);
            }
        }
        updateValue(toDstTy(info.var().type(),
            DeviceMngCtrl.getVal(info.var().type(),
                WifiSender.sendDataToESP32(info.device(),CloudRequest.createGetVarJson(info.var().name()).toString()).get("Value"))
            )
        );
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
                if (info != null && "FLOAT".equalsIgnoreCase(info.var().type().toString())) {
                    displayValue.set(String.format("%.2f", val));
                } else if (info != null && ("INT".equalsIgnoreCase(info.var().type().toString()) || "UNSIGNED".equalsIgnoreCase(info.var().type().toString()))) {
                    displayValue.set(String.valueOf(prim.getAsLong()));
                } else {
                    displayValue.set(prim.getAsString());
                }
            } else if (prim.isBoolean()) {
                boolean b = prim.getAsBoolean();
                numericValue.set(b ? 1.0 : 0.0);
                displayValue.set(b ? "HIGH" : "LOW");
                // Sync toggle button state without firing the action handler
                if (toggleBtn != null && toggleBtn.isManaged()) {
                    toggleBtn.setSelected(b);
                    toggleBtn.setText(b ? "ON" : "OFF");
                    toggleBtn.getStyleClass().removeAll("bool-toggle-on", "bool-toggle-off");
                    toggleBtn.getStyleClass().add(b ? "bool-toggle-on" : "bool-toggle-off");
                }
            } else {
                String str = prim.getAsString();
                displayValue.set(str);
                try {
                    numericValue.set(Double.parseDouble(str));
                } catch (NumberFormatException _) {}
            }
        }
    }

    @FXML
    private void handleToggle(ActionEvent event) {
        if (info == null) return;
        boolean newState = toggleBtn.isSelected();
        toggleBtn.setText(newState ? "ON" : "OFF");
        toggleBtn.getStyleClass().removeAll("bool-toggle-on", "bool-toggle-off");
        toggleBtn.getStyleClass().add(newState ? "bool-toggle-on" : "bool-toggle-off");
        displayValue.set(newState ? "HIGH" : "LOW");
        numericValue.set(newState ? 1.0 : 0.0);
        WifiSender.sendDataToESP32(info.device(),
            CloudRequest.createSetVarJson(info.var().name(), new JsonPrimitive(newState)).toString());
    }

    @FXML
    private void handleEdit(ActionEvent event) {
        if (onEdit != null) 
            onEdit.accept(this);

    }

    @FXML
    private void handleDelete(ActionEvent event) {
        if (onDelete != null) 
            onDelete.accept(this); 
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

    @FXML void atEnter(KeyEvent e) {
        switch(e.getCode()){
            case ENTER -> WifiSender.sendDataToESP32(info.device(), 
                CloudRequest.createSetVarJson(info.var().name(), toDstTy(info.var().type(), setterField.getText())).toString());
            default -> {}
        }
    }

    private JsonPrimitive toDstTy(VarHelpers.Type type, String val){
        switch (type) {
            case BOOL -> {
                if(val.equalsIgnoreCase("on") || val.equals("1") || val.equalsIgnoreCase("true")){
                    FXMLPipeline.showError(null, false, null, setterField);
                    return new JsonPrimitive(true);
                }
                else if(val.equalsIgnoreCase("off") || val.equals("0") || val.equalsIgnoreCase("false")){
                    FXMLPipeline.showError(null, false, null, setterField);
                    return new JsonPrimitive(false);
                }
                else {
                    FXMLPipeline.showError("Type mismatch, Pls enter vaild input", true, percentageLabel, setterField);
                    return null;
                }
            }

            case INT -> {
                try {
                    BigInteger intVal = new BigInteger(val);
                    FXMLPipeline.showError(null, false, null, setterField);
                    return new JsonPrimitive(intVal);
                } catch(NumberFormatException _){
                    FXMLPipeline.showError("Type mismatch, Pls enter the vaild number", true, percentageLabel, setterField);
                    return null;
                }
            }
            case FLOAT -> {
                try {
                    BigDecimal fVal = new BigDecimal(val);
                    FXMLPipeline.showError(null, false, null, setterField);
                    return new JsonPrimitive(fVal);
                } catch(NumberFormatException _){
                    FXMLPipeline.showError("Type mismatch, Pls enter the vaild number", true, percentageLabel, setterField);
                    return null;
                }
            }
            default -> {}
        }
        return null;
    }
}
