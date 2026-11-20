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
import in.hamshik.Interpreter.shared.Error.PanicException;
import in.hamshik.Interpreter.shared.Union;

public class Parser {
    String path;
    public final Consumer<String> out;
    Lexer lexer;
    Body seqs = new Body(new ArrayList<>());
	public final Helper helper;
    final FnParser fnParser;
    public final ExprParser exprParser;
    boolean isParsingBody = false;

    public Parser(String path, Consumer<String> out)  {
        this.path = path;
        this.out = out;
        this.lexer = new Lexer(path, out);
        helper = new Helper(this);
        fnParser = new FnParser(this);
        exprParser = new ExprParser(this);
    }


    public ASTNode startParsing(){
        try {
            if(lexer.sourceCode == null) return null;
            Token next = lexer.nextToken();
            while (!(next.type() == TokenType.EOF || (isParsingBody && next.type() == TokenType.RBRACE))) {
                switch (next.type()) {
                    case KEYWORD -> seqs.add(handleKeywords(next));
                    case IDENTIFIER ->{
                        if(lexer.helper.peekToken().type().name().contains("ASSIGN"))
                            seqs.add(exprParser.parseAssign(next));
                        else seqs.add(exprParser.parseExpr(next, 0));
                    }
                    case LPAREN -> {
                        helper.unreadToken(next);
                        seqs.add(new ASTNode(null, next.loc(), helper.parseBody()));
                    }
                    default -> {}
                }
                next = lexer.nextToken();
            }
            return new ASTNode(null, null, seqs);
        } catch (PanicException e) {
            return null;
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
        return new Token(val, null, type);
    }
    private Token createToken(String val){
        return new Token(val, null, LexHelper.iskeyword(val) ? TokenType.KEYWORD : TokenType.IDENTIFIER );
    }

    private ASTNode handleKeywords(Token next) {
        switch (KeyWords.keyMap.get(next.val())) {
            case FN: return fnParser.parseFunctionDefinition(next);
            case VAR: return exprParser.parseAssign(next);
            case IF: return parseIf();
            case WHILE: return parseWhile();
            case RETURN, RET: {
                ASTNode expr = exprParser.parseExpr(helper.nextToken(), 0);
                return new ASTNode(null, next.loc(), new Return(expr));
            }
            case IMPORT: {
                Token path = helper.nextToken();
                if (path.type() != TokenType.STRINGS)
                    throw Helper.syntaxError("Expected a string literal after import", path);
                return new ASTNode(null, next.loc(),
                        new Union.Import(path.val()));
            }
            default: return null; 
        }
    }

    private ASTNode parseIf() {
        ASTNode cond = exprParser.parseExpr(helper.nextToken(), 1);
        Body thenBranch = helper.parseBody();

        Body elseBranch = null;
        Token next = helper.nextToken();
        if (helper.isKeyword(next, "else")) {
            Token afterElse = helper.nextToken();
            if (helper.isKeyword(afterElse, "if")) {
                ASTNode nestedIf = parseIf();
                ArrayList<ASTNode> nodes = new ArrayList<>();
                nodes.add(nestedIf);
                elseBranch = new Body(nodes);
            } else {
                helper.unreadToken(afterElse);
                elseBranch = helper.parseBody();
            }
        } else {
            helper.unreadToken(next);
        }

        return new ASTNode(
                null,
                cond.loc,
                new Union.IfConds(cond, thenBranch, elseBranch));
    }

    private ASTNode parseWhile() {
        ASTNode cond = exprParser.parseExpr(helper.nextToken(), 1);

        ASTNode update = null;
        Token next = helper.nextToken();
        if (next.type() == TokenType.COLON) {
            update = exprParser.parseAssign(helper.nextToken());
        } else {
            helper.unreadToken(next);
        }

        Body body = helper.parseBody();
        return new ASTNode(
                null,
                cond.loc,
                new Union.While(cond, new ASTNode(null, cond.loc, body), update));
    }

    static String getTokenTyName(TokenType type){
        return type.sym != null ? type.sym : type.name();
    }

    boolean iskeyword(String value) {
        return LexHelper.iskeyword(value);
    }
}
