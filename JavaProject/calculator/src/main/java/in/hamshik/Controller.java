package in.hamshik;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Controller implements Initializable {

    // ✅ FXML UI elements (connected via Scene Builder)
    @FXML private Text calTextBox;   // Main display area (where numbers/operators appear)
    @FXML private Text errText;      // For small fade messages like "Limit reached"
    @FXML private Rectangle rect;    // For small highlight effect (optional)

    // ✅ Core variables
    private EvaluateExpression evaluateExpression; // handles math logic (calculation engine)
    private double a = 0, b = 0;                   // operands (numbers for expressions)
    private String currentOperator = null;         // operator currently being used (+, -, ×, ÷)
    private String lastOperator = null;            // remembers the previous operator (for repeated '=')
    private double lastOperand = 0;                // remembers last operand (for repeated '=')
    private boolean operatorJustPressed = false;   // helps manage when a user just pressed an operator

    // ✅ Character limit
    private static final int MAX_CHAR = 20;        // maximum number of characters allowed on screen

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Called automatically when the FXML is loaded
        calTextBox.setText("0");
        errText.setVisible(false);
        rect.setVisible(false);
        evaluateExpression = new EvaluateExpression(calTextBox.getText(), calTextBox, errText, rect);
    }

    /** 🎯 Handles all button presses from Scene Builder */
    @FXML
    public void ButHandler(ActionEvent e) {
        Button clicked = (Button) e.getSource();
        String text = clicked.getText();

        switch (text) {
            case "C" -> clearText("0");      // Clear everything
            case "Del" -> deleteLastChar();  // Delete last number
            case "=" -> calculateResult();   // Perform calculation
            default -> handleInput(text);    // Handle numbers/operators
        }
    }

    /** 🧮 Main input handler for digits, operators, π, e */
    private void handleInput(String text) {

        String currentText = calTextBox.getText();

        // ✅ Prevent input overflow for all (including π, e)
        if (currentText.length() >= MAX_CHAR && !isOperator(text)) {
            showLimitError();
            return;
        }

        // ✅ Handle operator press (+, −, ×, ÷, %, x², √, etc.)
        if (isOperator(text)) {
            try {
                String filtA = currentText
                        .replaceAll("π", String.valueOf(Math.PI))
                        .replaceAll("e", String.valueOf(Math.E));

                if (currentOperator != null && !operatorJustPressed) {
                    b = Double.parseDouble(filtA);
                    String result = evaluateExpression.handleExpression(a, b, currentOperator);
                    if (result != null && !result.isEmpty()) {
                        result = formatNumber(result);
                        showText(result, "result");
                        a = Double.parseDouble(result);
                    }
                } else {
                    a = Double.parseDouble(filtA);
                }

                currentOperator = text;
                lastOperator = text;
                operatorJustPressed = true;
                fader(calTextBox);

            } catch (NumberFormatException ex) {
                evaluateExpression.handleErr(false);
            }
            return;
        }

        // ✅ Handle π and e constants
        if (text.equals("π") || text.equals("e")) {
            // Prevent duplicate constants (like ππ or 3π)
            if (currentText.endsWith("π") || currentText.endsWith("e")) {
                showLimitError();
                return;
            }
            // If just pressed operator, replace screen with π/e
            if (operatorJustPressed) {
                clearText(text);
                operatorJustPressed = false;
            } else {
                // Append constant only if current isn't a number (to prevent 3π)
                if (currentText.equals("0") || isOperator(currentText)) clearText("");
                showText(text, "typing");
            }
            return;
        }

        // ✅ Handle normal digits and dot (.)
        if (operatorJustPressed) {
            clearText(text);
            operatorJustPressed = false;
            return;
        }

        if (currentText.equals("0")) clearText("");
        showText(text, "typing");
    }

    /** ⚙️ Calculates result when '=' pressed */
    private void calculateResult() {
        try {
            if (currentOperator != null) {
                String filtB = calTextBox.getText()
                        .replaceAll("π", String.valueOf(Math.PI))
                        .replaceAll("e", String.valueOf(Math.E));

                b = Double.parseDouble(filtB);
                lastOperand = b;
                String result = evaluateExpression.handleExpression(a, b, currentOperator);

                if (result == null || result.isEmpty()) {
                    showText(calTextBox.getText(), "result");
                } else {
                    result = formatNumber(result);
                    showText(result, "result");
                    a = Double.parseDouble(result);
                }

                lastOperator = currentOperator;
                currentOperator = null;
                operatorJustPressed = false;

            } else if (lastOperator != null) {
                // Repeat last operation when pressing "=" multiple times
                String result = evaluateExpression.handleExpression(a, lastOperand, lastOperator);
                result = formatNumber(result);
                showText(result, "result");
                a = Double.parseDouble(result);
            } else {
                showText(calTextBox.getText(), "result");
            }

        } catch (Exception e) {
            evaluateExpression.handleErr(false);
        }
    }


    /** 🔙 Deletes one character */
    private void deleteLastChar() {
        String current = calTextBox.getText();
        if (current.length() > 1)
            calTextBox.setText(current.substring(0, current.length() - 1));
        else
            calTextBox.setText("0");
        adjustFont();
    }

    /** ✨ Small fade animation (visual feedback) */
    private void fader(Text node) {
        FadeTransition fader = new FadeTransition(Duration.millis(150), node);
        fader.setCycleCount(1);
        fader.setInterpolator(Interpolator.LINEAR);
        fader.setFromValue(1);
        fader.setToValue(0.3);
        fader.play();
    }

    /** Checks if input is an operator */
    private boolean isOperator(String text) {
        return switch (text) {
            case "+", "−", "×", "÷", "%", "x²", "x³", "√", "3√" -> true;
            default -> false;
        };
    }

    /** Clears text or replaces display */
    private void clearText(String newText) {
        calTextBox.setText(newText);
        adjustFont();
    }

    /** Shows text on display */
    private void showText(String text, String mode) {
        switch (mode) {
            case "result" -> calTextBox.setText(text);
            case "typing" -> calTextBox.setText(calTextBox.getText() + text);
            default -> calTextBox.setText(text);
        }
        adjustFont();
    }

    /** 💻 Keyboard support */
    public void handleKeyPress(KeyEvent event) {
        String key = event.getCharacter();

        if ("+-*/%".contains(key)) {
            switch (key) {
                case "+" -> handleInput("+");
                case "-" -> handleInput("−");
                case "*" -> handleInput("×");
                case "/" -> handleInput("÷");
                case "%" -> handleInput("%");
            }
            return;
        }

        if (key.matches("[0-9.πe]")) { // ✅ Added π and e
            handleInput(key);
            return;
        }

        if (key.equals("\r") || key.equals("\n")) {
            calculateResult();
        }
    }

    /** 🚫 Shows "Limit reached" error */
    private void showLimitError() {
        errText.setText("Limit reached");
        errText.setFill(Color.RED);
        errText.setVisible(true);

        FadeTransition fade = new FadeTransition(Duration.millis(800), errText);
        fade.setFromValue(1);
        fade.setToValue(0);
        fade.play();
    }

    /** 🔠 Adjusts font size for better readability */
    private void adjustFont() {
        int len = calTextBox.getText().length();
        double baseSize = 42; // default
        if (len > 12) baseSize = 38;
        if (len > 16) baseSize = 34;
        if (len > 18) baseSize = 30;
        calTextBox.setStyle("-fx-font-size: " + baseSize + "px;");
    }

    /** ✨ Makes results cleaner by removing unnecessary decimals */
    private String formatNumber(String numStr) {
        try {
            double val = Double.parseDouble(numStr);
            if (Math.abs(val - Math.round(val)) < 1e-10)
                return String.valueOf((long) val);
            else
                return String.format("%.6f", val)
                        .replaceAll("0+$", "")
                        .replaceAll("\\.$", "");
        } catch (Exception e) {
            return numStr;
        }
    }
}
