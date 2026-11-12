package in.hamshik.Controller.MainCtrl;

import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import in.hamshik.Helpers.FXMLPipeline;
import in.hamshik.Helpers.ProcessSpawner;
import in.hamshik.Interpreter.Parser.Parser;
import in.hamshik.Interpreter.shared.ASTNode;

public class CodeRunner extends Controller {

    @FXML private TextField filePathField, saExecPathField;
    @FXML private Label status;
    @FXML private AnchorPane saFeildRoot;
    private Object compiler = "suvarnam";
    private TerminalWindow terminalWindow;

    @FXML
    private void browseCodeFile() {
        File selectedFile = browseFile(
                "Suvarnam Files (*.sa)", "*.sa", "Choose the source Code", filePathField);
        if (selectedFile == null)
            return;

        Path file = selectedFile.toPath();
        filePathField.setText(file.toString());
        if (!Files.isRegularFile(file)) {
            FXMLPipeline.showError("Plese select vaild Source code", true, status, filePathField);
            return;
        }
        FXMLPipeline.showError("", false, status, filePathField);
    }

    @FXML
    void browseSAExecFile() {
        File selectedFile = browseFile(
                "Suvarnam executable", null,
                "Select suvarnam compiler executable", saExecPathField);
        if (selectedFile == null)
            return;

        Path executable = selectedFile.toPath();
        saExecPathField.setText(executable.toString());
        if (!Files.isRegularFile(executable) || !Files.isExecutable(executable)) {
            FXMLPipeline.showError("Plese select vaild Executable file", true, status, saExecPathField);
            return;
        }
        FXMLPipeline.showError("", false, status, saExecPathField);
        compiler = executable;
    }

    private File browseFile(String description, String extension, String title, TextField field) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        if (extension != null)
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(description, extension));

        Window window = field.getScene().getWindow();
        return fileChooser.showOpenDialog(window);
    }

    @FXML
    void eval() {
        String path = filePathField.getText().trim();
        if (terminalWindow == null)
            terminalWindow = new TerminalWindow();
        terminalWindow.show();
        terminalWindow.clear();

        if (path.isEmpty()) {
            FXMLPipeline.showError("Please choose a source file.", true, status, filePathField);
            return;
        }

        String err = ProcessSpawner.createProcess(terminalWindow::append, compiler instanceof Path j ? j.toString() : (String)compiler, "");
        if (err == null) {
            FXMLPipeline.showError("Suvarnam complier is not found.", true, status, saExecPathField);
            return;
        }

        Parser parser = new Parser(path, terminalWindow::append);
        ASTNode root = parser.startParsing();

    }
}
