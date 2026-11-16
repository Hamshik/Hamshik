package in.hamshik.Interpreter.Lexer;

import in.hamshik.Helpers.VarHelpers.Type;
import in.hamshik.Interpreter.Parser.Helper;

public class LexHelper {
    private Lexer lexer;

    public record Token(String val, Location initLoc, Location lastLoc, TokenType type) {
        public Token {
            initLoc = initLoc == null ? null : initLoc.copy();
            lastLoc = lastLoc == null ? null : lastLoc.copy();
            Lexer.currToken = this;
        }

        public Token(TokenType type, Location initLoc, Location lastLoc) {
            if (type.sym == null)
                throw new IllegalArgumentException("this constrcutor is only supported for symbols");
            this(type.sym, initLoc, lastLoc, type);
        }

    }

    public class Location {
        public int pos, col, ln,
            lastPos, lastCol, lastLn = 0;
        private boolean hasRange;

        public int incPosCol() {
            col++;
            return ++pos;
        }

        public int incPosCol(char consumed) {
            pos++;
            if (consumed == '\n') {
                ln++;
                col = 0;
            } else {
                col++;
            }

            return pos;
        }

        public Location() {}

        public Location(Location loc){
            pos = loc.pos;
            col = loc.col;
            ln = loc.ln;
            lastPos = loc.lastPos;
            lastCol = loc.lastCol;
            lastLn = loc.lastLn;
            hasRange = loc.hasRange;
        }

        public Location copy() {
            return new Location(this);
        }

        public Location combineLocation(Location b){
            Location loc = new Location();
            loc.pos = pos; loc.col = col; loc.ln = ln;
            loc.lastPos = b.hasRange ? b.lastPos : b.pos;
            loc.lastCol = b.hasRange ? b.lastCol : b.col;
            loc.lastLn = b.hasRange ? b.lastLn : b.ln;
            loc.hasRange = true;
            return loc;
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

    public static boolean iskeyword(String chars) {
        return KeyWords.keyMap.containsKey(chars);
    }

    public static Type isDatatype(String type) {
        return Type.datatype.get(type);
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
            lexer.loc.incPosCol(lexer.sourceCode[lexer.loc.pos]);
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