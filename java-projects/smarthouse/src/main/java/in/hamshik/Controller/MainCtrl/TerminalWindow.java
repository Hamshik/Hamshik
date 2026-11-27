package in.hamshik.Controller.MainCtrl;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;

final class TerminalWindow {
    private static final char ESC = '\u001B';
    private static final int MAX_PENDING_CHARS = 32_768;
    private static final int MAX_LINES = 1000;
    private static final Duration FLUSH_INTERVAL = Duration.millis(50);
    private static final Color DEFAULT_COLOR = Color.web("#d4d4d4");
    private static final Font REGULAR_FONT = Font.font("\"JetBrains Mono\", \"Consolas\", monospace", FontWeight.NORMAL, 15);
    private static final Font BOLD_FONT = Font.font("\"JetBrains Mono\", \"Consolas\", monospace", FontWeight.BOLD, 15);

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
    private final ObservableList<TerminalLine> lines = FXCollections.observableArrayList();
    private final ListView<TerminalLine> outputListView = new ListView<>(lines);
    private final Timeline flushTimeline = new Timeline(new KeyFrame(FLUSH_INTERVAL, _ -> flushPending()));

    private final Object bufferLock = new Object();
    private final StringBuilder pendingBuffer = new StringBuilder();

    private Color textColor = DEFAULT_COLOR;
    private boolean boldText;

    TerminalWindow(Runnable onClose) {
        configureListView();
        configureStage();
        flushTimeline.setCycleCount(Animation.INDEFINITE);
        lines.add(new TerminalLine());
        terminalStage.setOnCloseRequest(_ -> {
            flushTimeline.stop();
            onClose.run();
        });
    }

    private void configureListView() {
        outputListView.setStyle(
                "-fx-background-color: #141313;"
                + "-fx-control-inner-background: #141313;"
                + "-fx-control-inner-background-alt: #141313;"
                + "-fx-background-insets: 0;"
                + "-fx-padding: 10;");
        outputListView.setCellFactory(_ -> new TerminalCell());
    }

    private void configureStage() {
        StackPane root = new StackPane(outputListView);
        root.setStyle("-fx-background-color: #141313;");
        terminalStage.setTitle("Suvarnam — Output");
        terminalStage.setScene(new Scene(root, 800, 500, Color.web("#141313")));
    }

    void show() {
        if (!terminalStage.isShowing())
            terminalStage.show();
        terminalStage.toFront();
        if (flushTimeline.getStatus() != Animation.Status.RUNNING)
            flushTimeline.play();
    }

    void clear() {
        synchronized (bufferLock) {
            pendingBuffer.setLength(0);
        }
        lines.clear();
        lines.add(new TerminalLine());
        resetTextStyle();
    }

    private void resetTextStyle() {
        textColor = DEFAULT_COLOR;
        boldText = false;
    }

    /** Buffers background-thread output; the 50ms Timeline drains it on the FX thread. */
    void appendAsync(String message) {
        if (message == null || message.isEmpty())
            return;

        synchronized (bufferLock) {
            pendingBuffer.append(message);
            if (pendingBuffer.length() > MAX_PENDING_CHARS)
                pendingBuffer.delete(0, pendingBuffer.length() - MAX_PENDING_CHARS);
        }
    }

    private void flushPending() {
        String batch;
        synchronized (bufferLock) {
            if (pendingBuffer.isEmpty())
                return;
            batch = pendingBuffer.toString();
            pendingBuffer.setLength(0);
        }
        append(batch);
    }

    void append(String message) {
        if (message == null || message.isEmpty())
            return;

        if (lines.isEmpty())
            lines.add(new TerminalLine());

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

        if (lines.size() > MAX_LINES)
            lines.remove(0, lines.size() - MAX_LINES);

        // Refresh the active tail line in case spans were appended without a trailing newline.
        int lastIdx = lines.size() - 1;
        lines.set(lastIdx, new TerminalLine(lines.get(lastIdx).spans));
        outputListView.scrollTo(lastIdx);
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

    private void appendText(String value) {
        if (value.isEmpty())
            return;

        int start = 0;
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) == '\n') {
                String segment = value.substring(start, i);
                if (!segment.isEmpty())
                    lines.getLast().addSpan(segment, textColor, boldText);
                lines.add(new TerminalLine());
                start = i + 1;
            }
        }

        if (start < value.length())
            lines.getLast().addSpan(value.substring(start), textColor, boldText);
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

    private record StyledSpan(String text, Color color, boolean bold) {}

    private static final class TerminalLine {
        final List<StyledSpan> spans;

        TerminalLine() {
            this.spans = new ArrayList<>();
        }

        TerminalLine(List<StyledSpan> spans) {
            this.spans = spans;
        }

        void addSpan(String text, Color color, boolean bold) {
            if (!spans.isEmpty()) {
                StyledSpan last = spans.getLast();
                if (last.bold() == bold && last.color().equals(color)) {
                    spans.set(spans.size() - 1, new StyledSpan(last.text() + text, color, bold));
                    return;
                }
            }
            spans.add(new StyledSpan(text, color, bold));
        }
    }

    private static final class TerminalCell extends ListCell<TerminalLine> {
        private final TextFlow flow = new TextFlow();

        TerminalCell() {
            setStyle("-fx-background-color: transparent; -fx-padding: 1 4 1 4;");
        }

        @Override
        protected void updateItem(TerminalLine item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
                return;
            }

            flow.getChildren().clear();
            for (StyledSpan span : item.spans) {
                Text text = new Text(span.text());
                text.setFill(span.color());
                text.setFont(span.bold() ? BOLD_FONT : REGULAR_FONT);
                flow.getChildren().add(text);
            }
            setGraphic(flow);
        }
    }
}
