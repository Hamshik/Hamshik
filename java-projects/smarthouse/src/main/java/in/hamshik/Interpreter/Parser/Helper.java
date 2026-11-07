package in.hamshik.Interpreter.Parser;


import in.hamshik.Interpreter.Lexer.Lexer;
import in.hamshik.Interpreter.Lexer.Lexer.Token;
import in.hamshik.Interpreter.Lexer.TokenType;
import in.hamshik.Interpreter.shared.Union.HIRType;

public class Helper {

    private final Parser parser;
    private final Lexer lexer;
    private Token pendingToken;

    public Helper(Parser parser) {
        this.parser = parser;
        this.lexer = parser.lexer;
    }

    public Object handleExpr(Token next) {
        return null;
    }


    public void skipBodyOrTerminator() {
        Token token = nextToken();
        if (token.type() == TokenType.SEMICOLON)
            return;
        if (token.type() != TokenType.LBRACE)
            throw syntaxError("Expected ';' or a function body after the signature", token);

        int depth = 1;
        while (depth > 0) {
            token = nextToken();
            if (token.type() == TokenType.EOF)
                throw syntaxError("Unexpected end of file inside the function body", token);
            if (token.type() == TokenType.LBRACE)
                depth++;
            else if (token.type() == TokenType.RBRACE)
                depth--;
        }
    }

    public HIRType primitive(String name, boolean isMutable) {
        return new HIRType("primitive", name, isMutable, 0, null);
    }

    public HIRType listType(HIRType inner, boolean isMutable, long size) {
        return new HIRType("primitive", "list", isMutable, size, inner);
    }

    public HIRType withMutability(HIRType type, boolean isMutable) {
        return new HIRType(type.kind(), type.name(), isMutable, type.size(), type.inner());
    }

    public void rejectPointer(HIRType type, String context) {
        if (type == null)
            return;
        if ("primitive".equals(type.kind()) && "ptr".equals(type.name()))
            throw new IllegalArgumentException("Java runtime does not support pointer " + context);
        rejectPointer(type.inner(), context);
    }

    public boolean isType(Token token) {
        return token.type() == TokenType.DATATYPE || token.type() == TokenType.IDENTIFIER;
    }

    public boolean isKeyword(Token token, String keyword) {
        return token.type() == TokenType.KEYWORD && keyword.equals(token.val());
    }

    public Token nextToken() {
        if (pendingToken != null) {
            Token token = pendingToken;
            pendingToken = null;
            return token;
        }
        return lexer.nextToken();
    }

    public void unreadToken(Token token) {
        if (pendingToken != null)
            throw new IllegalStateException("Only one token of lookahead is supported");
        pendingToken = token;
    }

    public IllegalArgumentException syntaxError(String message, Token token) {
        String found = token.type() == TokenType.EOF
                ? "end of file"
                : "'" + token.val() + "' (" + parser.getTokenTyName(token.type()) + ")";
        return new IllegalArgumentException(message + "; found " + found);
    }
}
