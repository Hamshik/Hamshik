package in.hamshik.Controller.MainCtrl;

import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.IOException;

import in.hamshik.Helpers.ProcessSpawner;
import in.hamshik.Interpreter.Parser.Parser;
import in.hamshik.Interpreter.shared.ASTNode;

public class CodeRunner extends Controller {

    @FXML
    private TextField filePathField;
    @FXML
    private Button codeRunBtn;

    private TerminalWindow terminalWindow;

    @FXML
    private void browseCodeFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose ESP32 firmware");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Suvarnam file (*.sa)", "*.sa"));

        Window window = filePathField.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(window);
        if (selectedFile != null)
            filePathField.setText(selectedFile.getAbsolutePath());

    }

    @FXML
    void eval() {
        if (terminalWindow == null)
            terminalWindow = new TerminalWindow();
        terminalWindow.show();
        terminalWindow.clear();

        String path = filePathField.getText().trim();
        if (path.isEmpty()) {
            terminalWindow.append("\u001B[31mPlease choose a source file.\u001B[0m\n");
            return;
        }
        boolean ok = false;

        ProcessSpawner.createProcess((err) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose Suvarnam excutable");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Suvarnam bin"));

            Window window = filePathField.getScene().getWindow();
            File selectedFile = fileChooser.showOpenDialog(window);
            if (selectedFile != null)
                filePathField.setText(selectedFile.getAbsolutePath());
            

        }, "suvarnam", "w");
        Parser parser = new Parser(path, terminalWindow::append);
        ASTNode root = parser.startParsing();

    }
}
