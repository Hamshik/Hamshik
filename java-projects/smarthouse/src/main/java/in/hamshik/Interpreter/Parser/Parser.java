package in.hamshik.Interpreter.Parser;

import java.util.ArrayList;
import java.util.function.Consumer;

import in.hamshik.Interpreter.Lexer.KeyWords;
import in.hamshik.Interpreter.Lexer.LexHelper;
import in.hamshik.Interpreter.Lexer.Lexer;
import in.hamshik.Interpreter.Lexer.TokenType;
import in.hamshik.Interpreter.Lexer.LexHelper.Token;
import in.hamshik.Interpreter.shared.ASTNode;
import in.hamshik.Interpreter.shared.Union.Return;
import in.hamshik.Interpreter.shared.Union.Body;

public class Parser {
    String path;
    public final Consumer<String> out;
    Lexer lexer;
    Body seqs = new Body(new ArrayList<>());
	public Helper helper;
    FnParser fnParser;
    boolean isParsingBody = false;

    public Parser(String path, Consumer<String> out)  {
        this.path = path;
        this.out = out;
        this.lexer = new Lexer(path, out);
        helper = new Helper(this);
        fnParser = new FnParser(this);
        exprParser = new ExprParser(this);
    }

    private final ExprParser exprParser;

    public ASTNode startParsing(){
        try {
            if(lexer.sourceCode == null) return null;
            Token next = lexer.nextToken();
            while (!(next.type() == TokenType.EOF || (isParsingBody && next.type() == TokenType.RBRACE))) {
                if (next.type() == TokenType.KEYWORD) {
                    ASTNode node = handleKeywords(next);
                    if (node != null)
                        seqs.body().add(node);
                }
                next = lexer.nextToken();
            }
            if(!isParsingBody) showMessage("Parsing completed successfully.", "\u001B[32m");
            return new ASTNode(null, null, seqs);
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
        return new Token(val, null, null, LexHelper.iskeyword(val) ? TokenType.KEYWORD : TokenType.IDENTIFIER );
    }

    private ASTNode handleKeywords(Token next) {
        switch (KeyWords.keyMap.get(next.val())) {
            case FN: return fnParser.parseFunctionDefinition(next);
            case VAR: return exprParser.parseAssign(next);
            case RETURN:
            case RET: {
                ASTNode expr = exprParser.parseExpr(helper.nextToken(), 0);
                return new ASTNode(null, next.initLoc(), new Return(expr));
            }
            default: return null; 
        }
    }

    static String getTokenTyName(TokenType type){
        return type.sym != null ? type.sym : type.name();
    }

    boolean iskeyword(String value) {
        return LexHelper.iskeyword(value);
    }
}
