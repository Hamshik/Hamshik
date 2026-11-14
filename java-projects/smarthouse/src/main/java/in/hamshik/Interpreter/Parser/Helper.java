package in.hamshik.Interpreter.Parser;

import java.util.ArrayList;

import in.hamshik.Interpreter.Lexer.Lexer;
import in.hamshik.Interpreter.Lexer.LexHelper.Token;
import in.hamshik.Interpreter.Lexer.TokenType;
import in.hamshik.Interpreter.shared.Union.Body;
import in.hamshik.Interpreter.shared.Union.HIRType;
import in.hamshik.Interpreter.shared.Union.TypeKind;

public class Helper {

    private final Parser parser;
    private final Lexer lexer;
    private Token pendingToken;

    public Helper(Parser parser) {
        this.parser = parser;
        this.lexer = parser.lexer;
    }

    public HIRType primitive(String name, boolean isMutable) {
        return new HIRType(TypeKind.PRIMITIVE, name, isMutable, 0, null);
    }

    public HIRType listType(HIRType inner, boolean isMutable, long size) {
        return new HIRType(TypeKind.PRIMITIVE, "list", isMutable, size, inner);
    }

    public HIRType withMutability(HIRType type, boolean isMutable) {
        return new HIRType(type.kind(), type.name(), isMutable, type.size(), type.inner());
    }

    public boolean isType(TokenType token) {
        return token == TokenType.DATATYPE || token == TokenType.IDENTIFIER;
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

    public boolean consumeToken(TokenType type, String errMsg) {
        if (pendingToken == null)
            return lexer.consumeToken(type, errMsg);

        Token token = nextToken();
        if (token.type() != type)
            throw syntaxError(errMsg, token);
        return true;
    }

    public boolean consumeToken(TokenType type) {
        String expected = type.sym == null ? type.name() : "'" + type.sym + "'";
        return consumeToken(type, "Expected " + expected);
    }

    public void unreadToken(Token token) {
        if (pendingToken != null)
            throw new IllegalStateException("Only one token of lookahead is supported");
        pendingToken = token;
    }

    public static IllegalArgumentException syntaxError(String message, Token token) {
        String found = token.type() == TokenType.EOF
                ? "end of file"
                : "'" + token.val() + "' (" + Parser.getTokenTyName(token.type()) + ")";
        return new IllegalArgumentException(message + "; found " + found);
    }

    public HIRType parseType(Token first) {
        HIRType type = first.type() == TokenType.IDENTIFIER
                ? new HIRType(TypeKind.USER_DEFINED, first.val(), false, 0, null)
                : primitive(first.val(), false);

        while (true) {
            Token suffix = nextToken();
            switch (suffix.type()) {
                case AMP ->
                    type = new HIRType(TypeKind.PRIMITIVE, "ptr", false, 0, type);
                case AND ->
                    type = new HIRType(TypeKind.PRIMITIVE, "ptr", false, 0,
                            new HIRType(TypeKind.PRIMITIVE, "ptr", false, 0, type));
                case LBRACKET -> {
                    Token sizeToken = nextToken();
                    long size = 0;
                    if (sizeToken.type() == TokenType.NUMBER) {
                        try {
                            size = Long.parseLong(sizeToken.val());
                        } catch (NumberFormatException e) {
                            syntaxError("List size is outside the supported range", sizeToken);
                        }
                        sizeToken = nextToken();
                    }
                    if (sizeToken.type() != TokenType.RBRACKET) {
                        syntaxError("Expected ']' in the type", sizeToken);
                        return null;
                    }
                    type = listType(type, false, size);
                }
                default -> {
                    unreadToken(suffix);
                    return type;
                }
            }
        }
    }

    Body parseBody() {
        lexer.consumeToken(TokenType.LBRACE, "Expected '{' to start a block");
        var tempSeq = parser.seqs;
        parser.seqs = new Body(new ArrayList<>());
        parser.isParsingBody = true;
        try {
            parser.startParsing();
            var fnBody = parser.seqs;
            parser.seqs = tempSeq;
            return fnBody;
        } finally {
            parser.isParsingBody = false;
            parser.seqs = tempSeq;
        }
    }
}
