package in.hamshik;

import java.net.URL;
import java.util.ResourceBundle;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class Controller implements Initializable {
    @FXML private Text CalTextBox, errText;
    @FXML private Rectangle rect;

    // All buttons (injected from FXML)
    @FXML private Button but0, but1, but2, but3, but4, but5, but6, but7, but8, but9;
    @FXML private Button butAdd, butSub, butMul, butDiv, butDot, butEqual, butClear, butDel, butMod, butSign;
    @FXML private Button butSqrt, butPi, butE, butLn, butLog, butSin, butCos, butTan, butPow;

    private final StringBuilder expression = new StringBuilder();
    private boolean justEvaluated = false;
    private boolean waitingForExponent = false;
    private Double baseValue = null;

    private static final double MAX_VALUE = 1E100;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CalTextBox.setText("0");
        errText.setVisible(false);
        rect.setVisible(false);
    }

    // ---------------------------------------------------------------------

    @FXML
    public void ButHandler(ActionEvent e) {
        Button clicked = (Button) e.getSource();
        String text = clicked.getText();

        if (CalTextBox.getText().equals("Error") || CalTextBox.getText().equals("Max limit reached")) {
            clearAll();
        }

        switch (text) {
            case "C" -> clearAll();
            case "Del" -> deleteLast();
            case "=" -> calculateResult();
            case "+/−" -> toggleSign();
            case "xʸ" -> startPowerOperation();
            default -> handleInput(text);
        }
    }

    // ---------------------------------------------------------------------

    private void clearAll() {
        expression.setLength(0);
        baseValue = null;
        waitingForExponent = false;
        showText("0");
        hideError();
    }

    private void deleteLast() {
        if (expression.length() > 0) {
            expression.deleteCharAt(expression.length() - 1);
            showText(expression.length() > 0 ? expression.toString() : "0");
        }
        hideError();
    }

    private void handleInput(String text) {
        hideError();

        // Reset after "=" if next input is number
        if (justEvaluated && isNumericOrConstant(text)) {
            expression.setLength(0);
            justEvaluated = false;
        }

        // Handle power exponent entry
        if (waitingForExponent) {
            if (isOperator(text)) return; // don’t allow operators in exponent
            expression.append(text);
            showText(baseValue + superscript(expression.toString()));
            return;
        }

        String current = expression.toString();

        // Prevent starting with +, ×, ÷, %
        if (current.isEmpty() && isOperator(text) && !text.equals("-")) return;

        // Prevent consecutive operators (e.g. "++", "+-", "--", etc.)
        if (current.length() > 0 && isOperatorEnding(current)) {
            if (isOperator(text)) {
                // Replace the last operator with the new one
                expression.setCharAt(expression.length() - 1, text.charAt(0));
            } else {
                expression.append(text);
            }
        } else {
            expression.append(text);
        }

        showText(expression.toString());
        justEvaluated = false;
    }

    // ---------------------------------------------------------------------

    private void startPowerOperation() {
        try {
            double base = Double.parseDouble(CalTextBox.getText());
            baseValue = base;
            waitingForExponent = true;
            expression.setLength(0);
            showText(base + "ⁿ");
        } catch (Exception e) {
            showError("Error");
        }
    }

    private void calculateResult() {
        try {
            // Power mode (xʸ)
            if (waitingForExponent && baseValue != null) {
                if (expression.isEmpty()) {
                    showError("Error");
                    return;
                }
                double exp = Double.parseDouble(expression.toString());
                double res = Math.pow(baseValue, exp);
                checkAndShow(res);
                waitingForExponent = false;
                baseValue = null;
                return;
            }

            // Normal mode
            if (expression.isEmpty()) {
                showText(CalTextBox.getText());
                return;
            }

            double res = evaluateExpression(expression.toString());
            checkAndShow(res);
        } catch (Exception ex) {
            showError("Error");
            waitingForExponent = false;
            baseValue = null;
        }
    }

    private void checkAndShow(double res) {
        if (Double.isNaN(res) || Double.isInfinite(res) || Math.abs(res) > MAX_VALUE) {
            showError("Max limit reached");
            return;
        }
        finishCalculation(res);
    }

    private void finishCalculation(double result) {
        String out = formatResult(result);
        showText(out);
        expression.setLength(0);
        expression.append(out);
        justEvaluated = true;
        hideError();
    }

    // ---------------------------------------------------------------------

    private double evaluateExpression(String expr) throws ScriptException {
        // Normalize expression
        expr = expr.replaceAll("×", "*")
                   .replaceAll("÷", "/")
                   .replaceAll("%", "/100")
                   .replaceAll("π", String.valueOf(Math.PI))
                   .replaceAll("e", String.valueOf(Math.E))
                   .replaceAll("√", "Math.sqrt")
                   .replaceAll("sin", "Math.sin")
                   .replaceAll("cos", "Math.cos")
                   .replaceAll("tan", "Math.tan")
                   .replaceAll("log", "Math.log10")
                   .replaceAll("ln", "Math.log");

        // Handle power (^)
        expr = expr.replaceAll("(\\d+(?:\\.\\d+)?)\\^(\\d+(?:\\.\\d+)?)", "Math.pow($1,$2)");

        ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
        Object result = engine.eval(expr);

        return Double.parseDouble(result.toString());
    }

    private String formatResult(double result) {
        if (result == (int) result)
            return String.valueOf((int) result);
        return String.format("%.8f", result)
                     .replaceAll("0+$", "")
                     .replaceAll("\\.$", "");
    }

    // ---------------------------------------------------------------------

    private void toggleSign() {
        if (expression.isEmpty()) return;

        // find the last number in the expression
        int i = expression.length() - 1;
        while (i >= 0 && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) i--;

        String before = expression.substring(0, i + 1);
        String number = expression.substring(i + 1);

        if (number.startsWith("-"))
            number = number.substring(1);
        else
            number = "-" + number;

        expression.setLength(0);
        expression.append(before).append(number);
        showText(expression.toString());
    }

    private boolean isOperator(String s) {
        return s.matches("[+\\-*/×÷%]");
    }

    private boolean isOperatorEnding(String s) {
        if (s.isEmpty()) return false;
        char last = s.charAt(s.length() - 1);
        return "+-*/×÷%".indexOf(last) != -1;
    }

    private boolean isNumericOrConstant(String s) {
        return s.matches("[0-9πe.]");
    }

    // ---------------------------------------------------------------------

    private void showError(String msg) {
        errText.setText(msg);
        errText.setVisible(true);
        rect.setVisible(true);
        showText("Error");
    }

    private void hideError() {
        errText.setVisible(false);
        rect.setVisible(false);
    }

    private void showText(String text) {
        CalTextBox.setText(text);
    }

    // Superscript conversion for exponent display
    private String superscript(String normal) {
        StringBuilder sb = new StringBuilder();
        for (char c : normal.toCharArray()) sb.append(toSuperscript(c));
        return sb.toString();
    }

    private char toSuperscript(char c) {
        return switch (c) {
            case '0' -> '⁰';
            case '1' -> '¹';
            case '2' -> '²';
            case '3' -> '³';
            case '4' -> '⁴';
            case '5' -> '⁵';
            case '6' -> '⁶';
            case '7' -> '⁷';
            case '8' -> '⁸';
            case '9' -> '⁹';
            case '-' -> '⁻';
            case '+' -> '⁺';
            default -> c;
        };
    }
}
