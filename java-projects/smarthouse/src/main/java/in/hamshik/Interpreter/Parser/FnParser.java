package in.hamshik.Interpreter.Parser;

import java.util.ArrayList;

import in.hamshik.Interpreter.Lexer.Lexer;
import in.hamshik.Interpreter.Lexer.LexHelper.Token;
import in.hamshik.Interpreter.Lexer.TokenType;
import in.hamshik.Interpreter.shared.ASTNode;
import in.hamshik.Interpreter.shared.Union;
import in.hamshik.Interpreter.shared.Union.Fn;
import in.hamshik.Interpreter.shared.Union.HIRType;

public class FnParser {
    private final Parser parser;
    private final Lexer lexer;
    private final Helper helper;

    public FnParser(Parser parser) {
        this.helper = parser.helper;
        this.parser = parser;
        this.lexer = parser.lexer;
    }

    public record Params(String name, HIRType type) {
    }

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
            if (afterFirst.type() != TokenType.IDENTIFIER)
                throw helper.syntaxError("Expected a function name after the return type", afterFirst);
            name = afterFirst.val();
            helper.consumeToken(TokenType.LPAREN, "Expected '(' after the function name");
        }

        Params[] params = parseParameters();
        return new ASTNode(
                null,
                fnToken.loc(),
                new Fn(returnType, name, params, helper.parseBody()));
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
                throw helper.syntaxError("'mut' must follow 'var' in a parameter", token);
            }
            Token afterToken = helper.nextToken();
            Token paramName = null;
            HIRType type = null;
            if (token.type() == TokenType.IDENTIFIER && afterToken.type() != TokenType.IDENTIFIER) {
                paramName = token;
            } else {
                type = helper.parseType(token);
                type = helper.withMutability(type, isMutable);
            }
            paramName = paramName == null ? helper.nextToken() : paramName;

            if (paramName.type() != TokenType.IDENTIFIER)
                throw helper.syntaxError("Expected a parameter name", paramName);

            Params parameter = new Params(paramName.val(), type);
            params.add(parameter);

            token = afterToken;
            if (token.type() == TokenType.RPAREN)
                return params.toArray(Params[]::new);
            if (token.type() != TokenType.COMMA)
                throw helper.syntaxError("Expected ',' or ')' after a parameter", token);
            token = helper.nextToken();
            if (token.type() == TokenType.RPAREN)
                throw helper.syntaxError("Trailing commas are not allowed in parameter lists", token);
        }
    }

    public ArrayList<ASTNode> parseArgList(Token first, String terminatingSym) {
        ArrayList<ASTNode> args = new ArrayList<>();
        Token next = first;

        while (!next.val().equals(terminatingSym)) {
            ASTNode expr = parser.exprParser.parseExpr(next, 0);
            args.add(expr);

            Token sep = helper.nextToken();
            if (sep.val().equals(terminatingSym))
                break; // end of list
            
            if (sep.type() != TokenType.COMMA)
                throw helper.syntaxError("Expected ',' or '" + terminatingSym + "'", sep);
            

            next = helper.nextToken();
            if (next.val().equals(terminatingSym))
                throw helper.syntaxError("Trailing comma is not allowed", sep);
        }

        return args;
    }

    public ASTNode parseCallArgs(Token first) {
        ArrayList<ASTNode> args = new ArrayList<>();
        Token current = first;

        if (current.type() == TokenType.LPAREN) 
            current = helper.nextToken();
        

        if (current.type() == TokenType.RPAREN) 
            return new ASTNode(null, first.loc(), new Union.FnCall(null, args));
        

        args = parseArgList(current, TokenType.RPAREN.sym);
        return new ASTNode(null, first.loc(), new Union.FnCall(null, args));
    }


}
