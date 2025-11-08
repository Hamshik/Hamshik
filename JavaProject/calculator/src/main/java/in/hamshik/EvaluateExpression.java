package in.hamshik;

import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class EvaluateExpression {

    private String expression;
    private static final double MAX_VALUE = 1E100;
    private Text text, errText;
    private double rawResult = 0;
    private String result = null;
    private Rectangle rect;

    public EvaluateExpression(String expression, Text text, Text errText, Rectangle rect) {
        this.expression = expression;
        this.text = text;
        this.errText = errText;
        this.rect = rect;
    }

    public String handleExpression(double a, double b, String whichOperation) {
        try {
            result = evaluate(a, b, whichOperation);
        } catch (Exception e) {
            handleErr(false);
        }
        return result;
    }

    private String evaluate(double a, double b, String whichOperation) {
        switch (whichOperation) {
            case "+" -> rawResult = a + b;
            case "−" -> rawResult = a - b;
            case "×" -> rawResult = a * b;
            case "÷" -> {
                if (b == 0) return "Can't divide by 0";
                rawResult = a / b;
            }
            case "%" -> rawResult = a % b;
            case "x²" -> rawResult = Math.pow(a, 2);
            case "x³" -> rawResult = Math.pow(a, 3);
            case "√" -> rawResult = Math.sqrt(a);
            case "3√" -> rawResult = Math.cbrt(a);
            default -> result = "Invalid";
        }

        if (Math.abs(rawResult) > MAX_VALUE) {
            handleErr(true);
            return "Overflow";
        }

        result = String.format("%.4f", rawResult);
        result = result.replaceAll("\\.?0+$", ""); // remove trailing zeros
        return result;
    }

    public void handleErr(boolean isOverFlowedErr) {
        errText.setVisible(true);
        rect.setVisible(true);
        if (isOverFlowedErr) {
            errText.setText("Max limit reached");
            text.setText("Max limit reached");
        } else {
            errText.setText("Error");
            text.setText("Error");
        }
    }

    public boolean getIsOperator(String text) {
        String[] operators = {"+", "−", "÷", "%", "3√", "√", "x³", "x²", "×"};
        for (String op : operators) {
            if (text.equals(op)) return true;
        }
        return false;
    }

    public String getResult() {
        return result;
    }
}
