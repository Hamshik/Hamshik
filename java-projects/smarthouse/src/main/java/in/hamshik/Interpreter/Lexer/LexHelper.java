package in.hamshik.Interpreter.Lexer;

import in.hamshik.Helpers.VarHelpers.Type;
import in.hamshik.Interpreter.Parser.Helper;

public class LexHelper {
    private Lexer lexer;

    public record Token(String val, Location initLoc, Location lastLoc, TokenType type) {
        public Token {
            Lexer.currToken = this;
        }

        public Token(TokenType type, Location initLoc, Location lastLoc) {
            if (type.sym == null)
                throw new IllegalArgumentException("this constrcutor is only supported for symbols");
            this(type.sym, initLoc, lastLoc, type);
        }

    }

    public class Location {
        public int pos, col, ln = 0;

        public int incPosCol() {
            col++;
            return ++pos;
        }
        public Location() {}

        public Location(Location loc){
            pos = loc.pos;
            col = loc.col;
            ln = loc.ln;
        }
    }

    public LexHelper(Lexer lexer) {
        this.lexer = lexer;
    }

    public static boolean isAlpha(char _char) {
        return _char >= 'A' && _char <= 'Z' || _char >= 'a' && _char <= 'z' || _char == '_';
    }

    public static boolean isDigit(char _char) {
        return _char >= '0' && _char <= '9';
    }

    public boolean iskeyword(String chars) {
        return lexer.keywords.keyMap.containsKey(chars);
    }

    public static Type isDatatype(String type) {
        for (var i : Type.values())
            if (i.name().equals(type))
                return i;
        return null;
    }

    char peek(int idx) {
        if (idx >= lexer.sourceCode.length || lexer.loc.pos + idx >= lexer.sourceCode.length)
            return 0;
        return lexer.sourceCode[lexer.loc.pos + idx];
    }

    boolean consumeToken(int no) {
        for (int i = 0; i < no; i++) {
            if (lexer.loc.pos >= lexer.sourceCode.length)
                return false;
            lexer.loc.incPosCol();
        }
        return true;
    }

    boolean consumeToken() {
        return consumeToken(1);
    }

    public Token peekToken() {
        if (lexer.peekedToken != null)
            return lexer.peekedToken;
        Token temp = Lexer.currToken;
        Token token = lexer.nextToken();
        lexer.loc = token.initLoc();
        lexer.peekedToken = token;
        Lexer.currToken = temp;
        return token;
    }

    public boolean consumeToken(TokenType type, String errMsg) {
        Token next = lexer.nextToken();
        if(next.type() != type)
            throw Helper.syntaxError(errMsg, next);

        return true;
    }

    public boolean consumeToken(TokenType type) {
        String expected = type.sym == null ? type.name() : "'" + type.sym + "'";
        return consumeToken(type, "Expected " + expected);
    }
}