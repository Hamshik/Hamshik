package in.hamshik.Controller.MainCtrl;

import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private Boolean errOn = null;

    @FXML
    private void browseCodeFile() {
        File selectedFile = browseFile(
                "Suvarnam Files (*.sa)", "*.sa", "Choose the source Code", filePathField);
        if (selectedFile == null)
            return;

        Path file = selectedFile.toPath();
        final String err = "Plese select vaild Source code";
        filePathField.setText(file.toString());
        if (!Files.isRegularFile(file)) {
            errOn = true;
            FXMLPipeline.showError(err, true, status, filePathField);
            return;
        }
        if(errOn != null && errOn)
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
        final String err = "Plese select vaild Executable file";
        if (!Files.isRegularFile(executable) || !Files.isExecutable(executable)) {
            errOn = false;
            FXMLPipeline.showError(err, true, status, saExecPathField);
            return;
        }
        if(errOn != null && !errOn) FXMLPipeline.showError("", false, status, saExecPathField);
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

        if (path.isEmpty()) {
            FXMLPipeline.showError("Please choose a source file.", true, status, filePathField);
            errOn = true;
            return;
        }

        final AtomicBoolean isErr = new AtomicBoolean(false);
        final String slicedPath = Path.of(path).getParent().toString();
        final String tmpFileSrc = "/tmp" + path.substring(path.lastIndexOf("/"));
        String err = ProcessSpawner.createProcess((errReason) -> {
            isErr.set(true);
            FXMLPipeline.setManagedVisibleTo(true, saFeildRoot);
            FXMLPipeline.showError(
                errReason.equals("Cannot run program \"suvarnam\": Exec failed, error: 2 (No such file or directory) ") ? 
                "Cannot find the suvarnam complier" : errReason, true, status, saExecPathField
            );
            errOn = false;
        }, compiler instanceof Path j ? j.toString() : (String)compiler, path, "--emit-desugared", tmpFileSrc, "-I", slicedPath);
        
        if(isErr.get()) return;

        terminalWindow.show();
        terminalWindow.clear();
        if(err != null && !err.isBlank()) {
            terminalWindow.append(err);
            return;
        }
        Parser parser = new Parser(tmpFileSrc, terminalWindow::append);
        ASTNode root = parser.startParsing();

    }
}
