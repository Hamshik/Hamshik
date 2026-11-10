package in.hamshik.Interpreter.Parser;

import java.util.ArrayList;

import in.hamshik.Interpreter.Lexer.Lexer;
import in.hamshik.Interpreter.Lexer.LexHelper.Token;
import in.hamshik.Interpreter.Lexer.TokenType;
import in.hamshik.Interpreter.shared.ASTKind;
import in.hamshik.Interpreter.shared.ASTNode;
import in.hamshik.Interpreter.shared.Union.Fn;
import in.hamshik.Interpreter.shared.Union.HIRType;

public class FnParser {
    private final Parser parser;
    private final Lexer lexer;
    private Token pendingToken;
    private final Helper helper;

    public FnParser(Parser parser) {
        this.helper = parser.helper;
        this.parser = parser;
        this.lexer = parser.lexer;
    }

    public record Params(String name, HIRType type) {}

    public ASTNode parseFunctionDefinition(Token fnToken) {
        Token first = helper.nextToken();

        Token afterFirst = helper.nextToken();
        String name;
        HIRType returnType;

        if (first.type() == TokenType.IDENTIFIER && afterFirst.type() == TokenType.LPAREN) {
            name = first.val();
            returnType = helper.primitive(null, false);
        } else {
            returnType = helper.parseType(first);
            helper.rejectPointer(returnType, "return type");
            if (afterFirst.type() != TokenType.IDENTIFIER)
                throw Helper.syntaxError("Expected a function name after the return type", afterFirst);
            name = afterFirst.val();
            helper.consumeToken(TokenType.LPAREN, "Expected '(' after the function name");
        }

        Params[] params = parseParameters();
        helper.skipBodyOrTerminator();
        return new ASTNode(
                ASTKind.AST_FN,
                null,
                fnToken.initLoc(),
                new Fn(returnType, name, params, null),
                false,
                false,
                false);
    }

    public Params[] parseParameters() {
        ArrayList<Params> params = new ArrayList<>();
        Token token = helper.nextToken();
        if (token.type() == TokenType.RPAREN)
            return new Params[0];

        while (true) {
            boolean isMutable = false;
            if (helper.isKeyword(token, "var")) {
                token = helper.nextToken();
                if (helper.isKeyword(token, "mut")) {
                    isMutable = true;
                    token = helper.nextToken();
                }
            } else if (helper.isKeyword(token, "mut")) {
                throw Helper.syntaxError("'mut' must follow 'var' in a parameter", token);
            }
            Token afterToken = helper.nextToken();
            Token paramName = null;
            HIRType type = null;
            if(token.type() == TokenType.IDENTIFIER && afterToken.type() != TokenType.IDENTIFIER){
                paramName = token;
            } else {
                type = helper.parseType(token);
                type = helper.withMutability(type, isMutable);
            }
            paramName = paramName == null ? helper.nextToken() : paramName;

            if (paramName.type() != TokenType.IDENTIFIER)
                throw Helper.syntaxError("Expected a parameter name", paramName);

            Params parameter = new Params(paramName.val(), type);
            params.add(parameter);

            token = afterToken;
            if (token.type() == TokenType.RPAREN)
                return params.toArray(Params[]::new);
            if (token.type() != TokenType.COMMA)
                throw Helper.syntaxError("Expected ',' or ')' after a parameter", token);
            token = helper.nextToken();
            if (token.type() == TokenType.RPAREN)
                throw Helper.syntaxError("Trailing commas are not allowed in parameter lists", token);
        }
    }

}
