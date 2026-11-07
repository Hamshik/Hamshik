package in.hamshik.Interpreter.Parser;

import in.hamshik.Interpreter.Lexer.Lexer;
import in.hamshik.Interpreter.Lexer.TokenType;
import in.hamshik.Interpreter.Lexer.Lexer.Token;
import in.hamshik.Interpreter.shared.ASTKind;
import in.hamshik.Interpreter.shared.ASTNode;
import in.hamshik.Interpreter.shared.Union.Body;
import javafx.scene.control.TextArea;

import java.util.ArrayList;

import in.hamshik.Helpers.VarHelpers.Type;


public class Parser {
    String path;
    TextArea out;
    Lexer lexer;
    FnParser fnParser;
    ExprHandler exprHandler;
    Body seqs = new Body(new ArrayList<>());

    public Parser(String path, TextArea out, boolean isMainParser) throws Exception{
        this.lexer = new Lexer(path, out);
        this.path = path;
        this.out = out;
        if(isMainParser) { //used  to avoid infinate recuration
            fnParser = new FnParser(this);
            exprHandler = new ExprHandler(this);
        }
    }

    public ASTNode startParsing(){
        Token next = lexer.getNextToken();
        switch (next.type()) {
            case KEYWORD -> 
                seqs.body().add(handleKeywords(next));
            case IDENTIFIER ->
                exprHandler.handleExpr(next);

            default -> {}
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
                return fnParser.parserFn();
            }
            default -> {}
        }
        return null;
    }

    Type isDatatype(Token nextToken) {
        return lexer.isDatatype(nextToken.val());
    }

    String getTokenTyName(TokenType type){
        return type.sym != null ? type.sym : type.name();
    }
}
