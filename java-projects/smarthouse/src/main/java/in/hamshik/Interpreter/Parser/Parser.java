package in.hamshik.Interpreter.Parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

import in.hamshik.Interpreter.Lexer.Lexer;
import in.hamshik.Interpreter.Lexer.TokenType;
import in.hamshik.Interpreter.Lexer.LexHelper.Token;
import in.hamshik.Interpreter.shared.ASTKind;
import in.hamshik.Interpreter.shared.ASTNode;
import in.hamshik.Interpreter.shared.Union.Body;

public class Parser {
    String path;
    private final Consumer<String> out;
    Lexer lexer;
    Body seqs = new Body(new ArrayList<>());
	public Helper helper;
    FnParser fnParser;

    public Parser(String path, Consumer<String> out) throws IOException {
        this.lexer = new Lexer(path, null);
        this.path = path;
        this.out = out;
        helper = new Helper(this);
        fnParser = new FnParser(this);
    }

    public ASTNode startParsing(){
        try {
            Token next = lexer.nextToken();
            while (next.type() != TokenType.EOF) {
                if (next.type() == TokenType.KEYWORD) {
                    ASTNode node = handleKeywords(next);
                    if (node != null)
                        seqs.body().add(node);
                }
                next = lexer.nextToken();
            }
            showMessage("Parsing completed successfully.", "\u001B[32m");
            return new ASTNode(ASTKind.AST_SEQ, null, null, seqs);
        } catch (IllegalArgumentException e) {
            showMessage("Parse error: " + e.getMessage(), "\u001B[31m");
            return null;
        }
    }

    private void showMessage(String message, String color) {
        if (out != null)
            out.accept(color + message + "\u001B[0m\n");
    }

    private Token createToken(String val, TokenType type){
        return new Token(val, null, null, type);
    }
    private Token createToken(String val){
        return new Token(val, null, null, lexer.helper.iskeyword(val) ? TokenType.KEYWORD : TokenType.IDENTIFIER );
    }

    private ASTNode handleKeywords(Token next) {
        switch (lexer.keywords.keyMap.get(next.val())) {
            case FN -> {
                return fnParser.parseFunctionDefinition(next);
            }
            default -> {}
        }
        return null;
    }

    static String getTokenTyName(TokenType type){
        return type.sym != null ? type.sym : type.name();
    }

    boolean iskeyword(String value) {
        return lexer.helper.iskeyword(value);
    }
}
