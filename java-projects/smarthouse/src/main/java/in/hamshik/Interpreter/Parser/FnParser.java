package in.hamshik.Interpreter.Parser;

import java.util.ArrayList;

import in.hamshik.Helpers.VarHelpers.Type;
import in.hamshik.Interpreter.Lexer.Lexer;
import in.hamshik.Interpreter.Lexer.TokenType;
import in.hamshik.Interpreter.Lexer.Lexer.Token;
import in.hamshik.Interpreter.shared.ASTKind;
import in.hamshik.Interpreter.shared.ASTNode;
import in.hamshik.Interpreter.shared.Union;
import in.hamshik.Interpreter.shared.Union.Fn;

public class FnParser extends Parser {
    public record Params(String name, Type type) {
        Params(String name) {
            this(name, Type.ANY);
        }
    }

    FnParser(Parser parser) throws Exception {
        super(parser.path, parser.out, false);
    }

    private Object handleParam() {
        TokenType[] expectedTokens = {
                TokenType.IDENTIFIER,
                TokenType.COLON,
                TokenType.DATATYPE,
        };
        byte idx = 0;
        Token[] tokens = new Token[expectedTokens.length];
        for (Token next = lexer.getNextToken(); next.type() != TokenType.COMMA
                || next.type() != TokenType.RPAREN; next = lexer.getNextToken(), idx++) {

            if (next.type() == TokenType.EOF)
                return String.format("Unexpected 'EOF', while expecting %s", getTokenTyName(expectedTokens[idx]));

            if(next.type() == expectedTokens[0]){
                next = lexer.getNextToken();
                idx++;
                if(next.type() != TokenType.COLON) return new Params(next.val());
            }

            if (next.type() != expectedTokens[idx])
                return String.format("Unexpected %s while expecting %s", getTokenTyName(next.type()),
                        getTokenTyName(expectedTokens[idx]));
            tokens[idx] = next;
        }
        return new Params(tokens[0].val(), toType(tokens[2].val()));
    }

    private Type toType(String val) {
        switch(val){
            case "int": return Type.INT;
            case "float": return Type.FLOAT;
            case "bool": return Type.BOOL;
            case "any": return Type.ANY;
            default:
                return null;
        }
    }

    public Object handleParams() {
        ArrayList<Params> params = new ArrayList<>();
        if (lexer.peekToken().type() != TokenType.LPAREN)
                return String.format("Unexepected '%s', while expecting '('", lexer.peekToken().val());

        for (Token next = lexer.peekToken();; next = lexer.peekToken()) {
            next = lexer.getNextToken(); //consumes comma and lparen
            Object raw = handleParam();
            if(raw instanceof Params param)
                params.add(param);
            else if(raw instanceof String err)
                return err;
            if(Lexer.currToken.type() == TokenType.RPAREN){
                return params.toArray(new Params[0]);
            }
        }
    }

    public ASTNode parserFn() {
        ASTNode fn = new ASTNode(ASTKind.AST_FN, null, lexer.loc, null, false, false, true);
        Type type = isDatatype(lexer.getNextToken());
        Token name = lexer.getNextToken();
        if(name.type() != TokenType.IDENTIFIER){
            System.err.println("Expected identifier");
            return null;
        }
        Object raw = handleParams();
        Params[] params = null;
        if (raw instanceof Params[] param)
            params = param;

        else if(raw instanceof String err){
            System.err.println(err);
            return null;
        }
        Union fnUnion = new Fn(type, name.val(), params, null);
        fn.val = fnUnion;
        return fn;
    }
}
