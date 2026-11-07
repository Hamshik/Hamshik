package in.hamshik.Interpreter.Parser;

import java.util.ArrayList;

import in.hamshik.Interpreter.Lexer.Lexer;
import in.hamshik.Interpreter.Lexer.Lexer.Token;
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

    public record Params(String name, HIRType type, boolean isVariadic) {}

    public ASTNode parseFunctionDefinition(Token fnToken) {
        Token first = helper.nextToken();
        if (!helper.isType(first))
            throw helper.syntaxError("Expected a return type or function name", first);

        HIRType firstType = parseType(first);
        Token afterFirst = helper.nextToken();
        String name;
        HIRType returnType;

        if (first.type() == TokenType.IDENTIFIER && afterFirst.type() == TokenType.LPAREN) {
            name = first.val();
            returnType = helper.primitive("void", false);
        } else {
            returnType = firstType;
            helper.rejectPointer(returnType, "return type");
            if (afterFirst.type() != TokenType.IDENTIFIER)
                throw helper.syntaxError("Expected a function name after the return type", afterFirst);
            name = afterFirst.val();
            Token openParen = helper.nextToken();
            if (openParen.type() != TokenType.LPAREN)
                throw helper.syntaxError("Expected '(' after the function name", openParen);
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
                throw helper.syntaxError("'mut' must follow 'var' in a parameter", token);
            }

            if (!helper.isType(token))
                throw helper.syntaxError("Expected a parameter type", token);
            HIRType type = parseType(token);
            helper.rejectPointer(type, "parameter type");

            Token paramName = helper.nextToken();
            boolean isVariadic = paramName.type() == TokenType.ELLIPSIS;
            if (isVariadic) {
                type = helper.listType(type, isMutable, 0);
                paramName = helper.nextToken();
            } else {
                type = helper.withMutability(type, isMutable);
            }
            if (paramName.type() != TokenType.IDENTIFIER)
                throw helper.syntaxError("Expected a parameter name", paramName);

            Params parameter = new Params(paramName.val(), type, isVariadic);
            params.add(parameter);
            if (isVariadic) {
                params.add(new Params(
                        paramName.val() + "\u0003_count",
                        helper.primitive("i64", false),
                        false));
            }

            token = helper.nextToken();
            if (token.type() == TokenType.RPAREN)
                return params.toArray(Params[]::new);
            if (token.type() != TokenType.COMMA)
                throw helper.syntaxError("Expected ',' or ')' after a parameter", token);
            token = helper.nextToken();
            if (token.type() == TokenType.RPAREN)
                throw helper.syntaxError("Trailing commas are not allowed in parameter lists", token);
        }
    }

    public HIRType parseType(Token first) {
        HIRType type = first.type() == TokenType.IDENTIFIER
                ? new HIRType("user_defined", first.val(), false, 0, null)
                : helper.primitive(first.val(), false);

        while (true) {
            Token suffix = helper.nextToken();
            if (suffix.type() == TokenType.AMP) {
                type = new HIRType("helper.primitive", "ptr", false, 0, type);
            } else if (suffix.type() == TokenType.AND) {
                type = new HIRType("helper.primitive", "ptr", false, 0,
                        new HIRType("helper.primitive", "ptr", false, 0, type));
            } else if (suffix.type() == TokenType.LBRACKET) {
                Token sizeToken = helper.nextToken();
                long size = 0;
                if (sizeToken.type() == TokenType.SEMICOLON) {
                    Token count = helper.nextToken();
                    if (count.type() != TokenType.NUMBER)
                        throw helper.syntaxError("Expected a list size after ';'", count);
                    try {
                        size = Long.parseLong(count.val());
                    } catch (NumberFormatException e) {
                        throw helper.syntaxError("List size is outside the supported range", count);
                    }
                    sizeToken = helper.nextToken();
                }
                if (sizeToken.type() != TokenType.RBRACKET)
                    throw helper.syntaxError("Expected ']' in the type", sizeToken);
                type = helper.listType(type, false, size);
            } else {
                helper.unreadToken(suffix);
                return type;
            }
        }
    }

}
