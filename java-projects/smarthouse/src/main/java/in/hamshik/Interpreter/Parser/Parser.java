package in.hamshik.Interpreter.Parser;

import in.hamshik.Interpreter.Lexer.Lexer;
import in.hamshik.Interpreter.Lexer.TokenType;
import in.hamshik.Interpreter.Lexer.Lexer.Token;
import in.hamshik.Interpreter.shared.ASTKind;
import in.hamshik.Interpreter.shared.ASTNode;
import in.hamshik.Interpreter.shared.Union.Body;
import javafx.scene.control.TextArea;

import java.util.ArrayList;

public class Parser {
    String path;
    TextArea out;
    Lexer lexer;
    Body seqs = new Body(new ArrayList<>());
	public Helper helper;
    FnParser fnParser;

    public Parser(String path, TextArea out, boolean isMainParser) throws Exception{
        this.lexer = new Lexer(path, out);
        this.path = path;
        this.out = out;
        helper = new Helper(this);
        fnParser = new FnParser(this);
    }

    public ASTNode startParsing(){
        Token next = lexer.nextToken();
        while (next.type() != TokenType.EOF) {
            if (next.type() == TokenType.KEYWORD) {
                ASTNode node = handleKeywords(next);
                if (node != null)
                    seqs.body().add(node);
            }
            next = lexer.nextToken();
        }
        return new ASTNode(ASTKind.AST_SEQ, null, null, seqs);
    }

    private Token createToken(String val, TokenType type){
        return new Token(val, null, null, type);
    }
    private Token createToken(String val){
        return new Token(val, null, null, lexer.iskeyword(val) ? TokenType.KEYWORD : TokenType.IDENTIFIER );
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

    String getTokenTyName(TokenType type){
        return type.sym != null ? type.sym : type.name();
    }
}
