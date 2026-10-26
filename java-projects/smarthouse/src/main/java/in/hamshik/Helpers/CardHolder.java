package in.hamshik.Helpers;

import com.google.gson.JsonElement;

import in.hamshik.Helpers.CardManager.DeviceInfo;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;

/**
 * Holds references to the card's UI elements and binds them to observable
 * properties.
 */
public class CardHolder {
    private final DeviceInfo info;
    private final VBox cardRoot;
    private final Label valueLabel;

    // Observable Properties
    private final DoubleProperty numericValue = new SimpleDoubleProperty(0.0);
    private final StringProperty displayValue = new SimpleStringProperty("--");

    public CardHolder(DeviceInfo info, VBox cardRoot, Label valueLabel,
            ProgressIndicator indicator, Label percentageLabel) {
        this.info = info;
        this.cardRoot = cardRoot;
        this.valueLabel = valueLabel;

        // 1. Reactive UI Binding: bind the Label's text to displayValue property
        this.valueLabel.textProperty().bind(displayValue);

        // 2. Property Listener: react whenever numericValue changes to update progress
        this.numericValue.addListener((obs, oldVal, newVal) -> {
            double val = newVal.doubleValue();
            int min = info.range().from();
            int max = info.range().to();
            double progress = 0.0;
            if (max > min) {
                progress = Math.max(0.0, Math.min(1.0, (val - min) / (double) (max - min)));
            } else if (min > max) {
                progress = Math.max(0.0, Math.min(1.0, (min - val) / (double) (min - max)));
            }
            indicator.setProgress(progress);
            percentageLabel.setText(String.format("%.0f%%", progress * 100.0));
        });
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
                if ("FLOAT".equalsIgnoreCase(info.dataTy())) {
                    displayValue.set(String.format("%.2f", val));
                } else if ("INT".equalsIgnoreCase(info.dataTy()) || "UNSIGNED".equalsIgnoreCase(info.dataTy())) {
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
                } catch (NumberFormatException ignored) {
                }
            }
        } else {
            displayValue.set(element.toString());
        }
    }

    public DeviceInfo info() {
        return info;
    }

    public VBox cardRoot() {
        return cardRoot;
    }

    public DoubleProperty numericValueProperty() {
        return numericValue;
    }

    public StringProperty displayValueProperty() {
        return displayValue;
    }
}