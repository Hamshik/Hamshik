package in.hamshik.Controller.MainCtrl;

import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.File;

import in.hamshik.Interpreter.Lexer.Lexer;
import in.hamshik.Interpreter.Lexer.TokenType;
import in.hamshik.Interpreter.Parser.Parser;
import in.hamshik.Interpreter.Lexer.Lexer.Token;

public class CodeRunner extends Controller {

    @FXML private TextField filePathField;
    @FXML private Button codeRunBtn;
    @FXML private TextArea out;

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

    @FXML void eval() {
        if(filePathField.getText().isEmpty()){
            System.err.println("Pls fill the path");
            return;
        }
        try{
            Parser parser = new Parser(filePathField.getText().trim(), out);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
