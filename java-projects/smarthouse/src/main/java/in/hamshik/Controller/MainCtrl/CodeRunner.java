package in.hamshik.Controller.MainCtrl;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import in.hamshik.Helpers.CloudRequest;
import in.hamshik.Helpers.FXMLPipeline;
import in.hamshik.Helpers.ProcessSpawner;
import in.hamshik.Helpers.VarHelpers.Permissions;
import in.hamshik.Interpreter.Evaluvator.Evaluvator;
import in.hamshik.Interpreter.Parser.Parser;
import in.hamshik.Interpreter.shared.ASTNode;
import in.hamshik.WifiHandler.WifiSender;

public class CodeRunner extends Controller {

    @FXML
    private TextField filePathField, saExecPathField;
    @FXML
    private Label status;
    @FXML
    private AnchorPane saFeildRoot;
    public static Object compiler = "suvarnam";
    private TerminalWindow terminalWindow;
    private Thread interpreterThread;
    private Boolean errOn = null;

    @FXML
    private void browseCodeFile() {
        File selectedFile = browseFile("Suvarnam Files (*.sa)", "*.sa", "Choose the source Code", filePathField);
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
        if (errOn != null && errOn)
            FXMLPipeline.showError("", false, status, filePathField);
    }

    @FXML
    void browseSAExecFile() {
        File selectedFile = browseFile("Suvarnam executable", null, "Select suvarnam compiler executable",
                saExecPathField);
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
        if (errOn != null && !errOn)
            FXMLPipeline.showError("", false, status, saExecPathField);
        compiler = executable;
    }

    private File browseFile(String description, String extension, String title, TextField field) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        if (extension != null)
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(description, extension));

        Window window = field.getScene().getWindow();
        return fileChooser.showOpenDialog(window);
    }

    @FXML
    void eval() throws IOException {
        String path = filePathField.getText().trim();
        if (terminalWindow == null)
            terminalWindow = new TerminalWindow(this::stopInterpreter);

        if (path.isEmpty()) {
            FXMLPipeline.showError("Please choose a source file.", true, status, filePathField);
            errOn = true;
            return;
        }

        stopInterpreter();
        final AtomicBoolean isErr = new AtomicBoolean(false);
        final String slicedPath = Path.of(path).getParent().toString();
        final String tmpFileSrc = System.getProperty("java.io.tmpdir") + "/smarthouse" + path.substring(path.lastIndexOf("/"));
        Path dir = Paths.get(System.getProperty("java.io.tmpdir"), "smarthouse", "include");
        Files.createDirectories(dir);
        dumpCloudVars(dir);
        String err = ProcessSpawner.createProcess((errReason) -> {
            isErr.set(true);
            FXMLPipeline.setManagedVisibleTo(true, saFeildRoot);
            FXMLPipeline.showError(errReason
                    .equals("Cannot run program \"suvarnam\": Exec failed, error: 2 (No such file or directory) ")
                            ? "Cannot find the suvarnam complier"
                            : errReason,
                    true, status, saExecPathField);
            errOn = false;
        }, compiler instanceof Path j ? j.toString() : (String) compiler, path, "--emit-desugared", tmpFileSrc, "-I",
                slicedPath, "-I", dir.toString());

        if (isErr.get())
            return;

        terminalWindow.show();
        terminalWindow.clear();
        if (err != null && !err.isBlank()) {
            terminalWindow.append(err);
            return;
        }
        interpreterThread = new Thread(() -> {
            var outFn = new Consumer<String>() {
                public void accept(String err) { Platform.runLater(() -> terminalWindow.append(err)); }
            };

            Parser parser = new Parser(tmpFileSrc, outFn);
            ASTNode root = parser.startParsing();

            if (parser.err.getErrorCount() != 0 || Thread.currentThread().isInterrupted())
                return;

            Evaluvator eval = new Evaluvator(parser.err, outFn);
            eval.pullAllGlobVarAndFn(parser.helper);
            eval.runProgram(root);
        }, "Interpreter Thread");
        interpreterThread.setDaemon(true);
        interpreterThread.start();
    }

    private static void dumpCloudVars(Path dir) {
        try {
            Files.writeString(dir.resolve("cloudVars.sa"), """
                    '' This file is auto-generated by the SmartHouse application.
                    '' It contains cloud variable definitions for use in Suvarnam programs.
                    '' Do not modify this file manually, as changes may be overwritten.
                    """);

            FXMLPipeline.dashCtrl.cardRootController.getVariableData().forEach((varName, var) -> {
                String typeStr = switch (var.type()) {
                    case BOOL -> "bool";
                    case FLOAT -> "float";
                    case INT -> "i64";
                    case STRING -> "str";
                    case ANY -> "any";
                    case CHAR -> "char";
                };
                var val = WifiSender.sendDataToESP32(FXMLPipeline.deviceMngCtrl.activeDevice,
                        CloudRequest.createGetVarJson(varName).toString());
                var initValStr = DeviceMngCtrl.getVal(var.type(), val);
                String varDef;
                if (var.permission() == Permissions.RO)
                    varDef = String.format("var %s %s = %s;\n", typeStr, varName, initValStr);
                else
                    varDef = String.format("var mut %s %s = %s;\n", varName, typeStr, initValStr);
                
                try {
                    Files.writeString(dir.resolve("cloudVars.sa"), varDef, java.nio.file.StandardOpenOption.APPEND);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopInterpreter() {
        if (interpreterThread != null && interpreterThread.isAlive())
            interpreterThread.interrupt();
    }
}
