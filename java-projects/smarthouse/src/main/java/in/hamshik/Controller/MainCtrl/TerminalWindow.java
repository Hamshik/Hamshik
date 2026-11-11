package in.hamshik.Controller.MainCtrl;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

final class TerminalWindow {
    private static final char ESC = '\u001B';
    private static final Color DEFAULT_COLOR = Color.web("#d4d4d4");
    private static final Color[] ANSI_COLORS = {
            Color.web("#000000"), Color.web("#cd3131"), Color.web("#0dbc79"),
            Color.web("#e5e510"), Color.web("#2472c8"), Color.web("#bc3fbc"),
            Color.web("#11a8cd"), Color.web("#e5e5e5")
    };
    private static final Color[] ANSI_BRIGHT_COLORS = {
            Color.web("#666666"), Color.web("#f14c4c"), Color.web("#23d18b"),
            Color.web("#f5f543"), Color.web("#3b8eea"), Color.web("#d670d6"),
            Color.web("#29b8db"), Color.web("#ffffff")
    };

    private final Stage terminalStage = new Stage();
    private final TextFlow textFlow = new TextFlow();
    private final ScrollPane outputScrollPane = new ScrollPane(textFlow);
    private Color textColor = DEFAULT_COLOR;
    private boolean boldText;

    TerminalWindow() {
        configureTextFlow();
        configureScrollPane();
        configureStage();
    }

    private void configureTextFlow() {
        textFlow.setStyle("-fx-background-color: #0a0000; -fx-padding: 14;");
    }

    private void configureScrollPane() {
        outputScrollPane.setFitToWidth(true);
        outputScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        outputScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        outputScrollPane.setStyle("-fx-background: #0a0000; -fx-background-color: #0a0000;");
    }

    private void configureStage() {
        StackPane root = new StackPane(outputScrollPane);
        root.setStyle("-fx-background-color: #0a0000;");
        terminalStage.setTitle("Suvarnam — Output");
        terminalStage.setScene(new Scene(root, 800, 500, Color.web("#0a0000")));
    }

    void show() {
        if (!terminalStage.isShowing())
            terminalStage.show();
        terminalStage.toFront();
    }

    void clear() {
        textFlow.getChildren().clear();
        resetTextStyle();
        outputScrollPane.setVvalue(0);
    }

    private void resetTextStyle() {
        textColor = DEFAULT_COLOR;
        boldText = false;
    }

    void append(String message) {
        int textStart = 0;
        AnsiSequence sequence = findNextAnsiSequence(message, textStart);
        while (sequence != null) {

            appendText(message.substring(textStart, sequence.start()));
            if (!applyAnsiCodes(sequence.encodedCodes()))
                appendText(message.substring(sequence.start(), sequence.end() + 1));

            textStart = sequence.end() + 1;
            sequence = findNextAnsiSequence(message, textStart);
        }

        appendText(message.substring(textStart));
        scrollToLatestOutput();
    }

    private AnsiSequence findNextAnsiSequence(String message, int searchFrom) {
        for (int index = searchFrom; index < message.length(); index++) {

            if (message.charAt(index) != ESC || index + 1 >= message.length()
                    || message.charAt(index + 1) != '[')
                continue;

            int end = message.indexOf('m', index + 2);
            if (end >= 0)
                return new AnsiSequence(index, end, message.substring(index + 2, end));
        
        }
        return null;
    }

    private void scrollToLatestOutput() {
        Platform.runLater(() -> outputScrollPane.setVvalue(1.0));
    }

    private void appendText(String value) {
        if (value.isEmpty())
            return;
        Text text = new Text(value);
        text.setFill(textColor);
        text.setFont(Font.font("\"JetBrains Mono\", \"Consolas\", monospace", boldText ? FontWeight.BOLD : FontWeight.NORMAL, 15));
        textFlow.getChildren().add(text);
    }

    private boolean applyAnsiCodes(String encodedCodes) {
        int[] codes = parseAnsiCodes(encodedCodes);
        if (codes == null)
            return false;

        for (int code : codes)
            applyAnsiCode(code);
        return true;
    }

    private int[] parseAnsiCodes(String encodedCodes) {
        String[] parts = encodedCodes.isEmpty() ? new String[] { "0" } : encodedCodes.split(";");
        int[] codes = new int[parts.length];
        try {
            for (int i = 0; i < parts.length; i++)
                codes[i] = parts[i].isEmpty() ? 0 : Integer.parseInt(parts[i]);
            return codes;
        } catch (NumberFormatException _) {
            return null;
        }
    }

    private void applyAnsiCode(int code) {
        if (code == 0) {
            resetTextStyle();
        } else if (code == 1) {
            boldText = true;
        } else if (code == 22) {
            boldText = false;
        } else if (code == 39) {
            textColor = DEFAULT_COLOR;
        } else if (code >= 30 && code <= 37) {
            textColor = ANSI_COLORS[code - 30];
        } else if (code >= 90 && code <= 97) {
            textColor = ANSI_BRIGHT_COLORS[code - 90];
        }
    }

    private record AnsiSequence(int start, int end, String encodedCodes) {}
}
