package in.hamshik.Interpreter.Parser;

import in.hamshik.Interpreter.Lexer.Lexer;
import in.hamshik.Interpreter.Lexer.Lexer.Token;
import javafx.scene.control.TextArea;

public class Parser {
    String path;
    TextArea out;
    Lexer lexer;

    public Parser(String path, TextArea out) throws Exception{
        this.lexer = new Lexer(path, out);
        this.path = path;
        this.out = out;
    }

    int startParsing(){
        Token next = lexer.getNextToken();
        switch (next.type()) {
            case KEYWORD -> {
                return handleKeywords(next);
            }
            default -> {}
        }
        return 0;
    }

    private int handleKeywords(Token next) {
        switch (lexer.keywords.keyMap.get(next.val())) {
            case FN -> {
                
            }
            default -> {}
        }
        return 0;
    }
}
