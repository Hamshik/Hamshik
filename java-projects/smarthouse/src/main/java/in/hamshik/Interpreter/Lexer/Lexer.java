package in.hamshik.Interpreter.Lexer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;
import java.util.Set;

import in.hamshik.Helpers.VarHelpers.Type;
import javafx.scene.control.TextArea;

public class Lexer {
    private static final Set<String> DATATYPE_NAMES = Set.of(
            "i8", "i16", "i32", "i64", "i128",
            "u8", "u16", "u32", "u64", "u128",
            "f32", "f64", "f128", "uf32", "uf64", "uf128",
            "str", "char", "bool", "void");

    TextArea out;
    String filePath;
    Token peekedToken;
    public static Token currToken;

    public Location loc = new Location();
    public KeyWords keywords = new KeyWords();
    char[] sourceCode;

    public record Token(String val, Location initLoc, Location lastLoc, TokenType type) {
        public Token{Lexer.currToken=this;}
    }

    public class Location {
        int pos, col, ln = 0;

        int incPosCol() {
            col++;
            return ++pos;
        }
    }

    public Lexer(String path, TextArea out) throws IOException, FileNotFoundException {
        this.out = out;
        this.filePath = path;
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            sourceCode = reader.readAllAsString().toCharArray();
        }
    }

    public Token nextToken() {
        if (peekedToken != null) {
            Token temp = peekedToken;
            peekedToken = null;
            currToken = temp;
            return temp;
        }
        if (loc.pos >= sourceCode.length)
            return new Token(null, null, loc, TokenType.EOF);

        var _char = sourceCode[loc.pos];
        if (isAlpha(_char))
            return idOrKeywords();
        if (_char == '"')
            return handleStrs();
        if (_char == '\'' && peek(1) == '\'' && peek(2) == '\'')
            return handleComments();
        if (_char == '\'' && peek(1) == '\'')
            return handleSingleCommet();
        if (_char == '\'')
            return handleChar();
        if (isDigit(_char))
            return handleDigits();

        if (_char == '.' && peek(1) == '.' && peek(2) == '.') {
            loc.incPosCol();
            loc.incPosCol();
            loc.incPosCol();
            return new Token("...", null, loc, TokenType.ELLIPSIS);
        }
        if (_char == '&' && peek(1) == '&') {
            loc.incPosCol();
            loc.incPosCol();
            return new Token("&&", null, loc, TokenType.AND);
        }
        return handleSymbols(_char);
    }

    private Token handleSingleCommet() {
        var _char = sourceCode[loc.pos];
        while (_char != '\n') {
            if (loc.incPosCol() >= sourceCode.length) // consuming the single line commet & checking bounds
                break;
            _char = sourceCode[loc.pos];
        }
        return nextToken();
    }

    private Token handleDigits() {
        StringBuilder chars = new StringBuilder();
        var initLoc = loc;
        var _char = sourceCode[loc.pos];
        while (isDigit(_char)) {
            chars.append(_char);
            if (loc.incPosCol() >= sourceCode.length)
                break;
            _char = sourceCode[loc.pos];
        }
        return new Token(chars.toString(), initLoc, loc, TokenType.NUMBER);
    }

    private char peek(int idx) {
        if (idx >= sourceCode.length || loc.pos + idx >= sourceCode.length)
            return 0;
        return sourceCode[loc.pos + idx];
    }

    private Token handleSymbols(char _char) {
        loc.incPosCol();
        switch (_char) {
            case '{': return new Token("{", loc, null, TokenType.LBRACE);
            case '}': return new Token("}", loc, null, TokenType.RBRACE);
            case '[': return new Token("[", loc, null, TokenType.LBRACKET);
            case ']': return new Token("]", loc, null, TokenType.RBRACKET);
            case '(': return new Token("(", loc, null, TokenType.LPAREN);
            case ')': return new Token(")", loc, null, TokenType.RPAREN);
            case ',': return new Token(",", loc, null, TokenType.COMMA);
            case '&': return new Token("&", loc, null, TokenType.AMP);
            case '=': return new Token("=", loc, null, TokenType.ASSIGN);
            case ';': return new Token(";", loc, null, TokenType.SEMICOLON);
            case '+': return new Token(";", loc, null, TokenType.PLUS);
            case '-': return new Token(";", loc, null, TokenType.MINUS);
            case '*': return new Token(";", loc, null, TokenType.STAR);
            case '/': return new Token(";", loc, null, TokenType.SLASH);
            case ':': return new Token(":", loc, null, TokenType.COLON);
            default: return nextToken();
        }
    }

    private Token handleComments() {
        var initLoc = loc;
        // comsuming tri-quates
        for (int i = 0; i < 3; i++)
            loc.incPosCol();
        var _char = sourceCode[loc.pos];
        while (_char != '\'' && peek(1) != '\'' && peek(2) != '\'') {
            if (loc.incPosCol() >= sourceCode.length) {
                System.err.println("pls close the commet from " + initLoc.pos);
                break;
            }
            _char = sourceCode[loc.pos];
        }
        for (int i = 0; i < 3; i++)
            loc.incPosCol();
        return nextToken();
    }

    private Token handleChar() {
        StringBuilder chars = new StringBuilder();
        var initLoc = loc;
        var _char = sourceCode[loc.incPosCol()]; // consuming opening-quotes
        while (_char != '\'') {
            chars.append(_char);
            if (loc.incPosCol() >= sourceCode.length && sourceCode[loc.pos] != '\'') { // already consumed by
                                                                                       // 'loc.incPosCol'
                System.err.println("Only one char is allowed");
                break;
            }
            _char = sourceCode[loc.pos];
        }
        loc.incPosCol(); // consuming closing-quates
        return new Token(chars.toString(), initLoc, loc, TokenType.CHARACTER);
    }

    private Token handleStrs() {
        StringBuilder chars = new StringBuilder();
        var initLoc = loc;
        var _char = sourceCode[loc.incPosCol()]; // consuming opening-quates
        while (_char != '"') {
            chars.append(_char);
            if (_char == '\n' || loc.incPosCol() >= sourceCode.length) {
                System.err.println("pls close the qutes");
                break;
            }
            _char = sourceCode[loc.pos];
        }
        loc.incPosCol(); // consuming closing-quates
        return new Token(chars.toString(), initLoc, loc, TokenType.STRINGS);
    }

    boolean isAlpha(char _char) {
        return _char >= 'A' && _char <= 'Z' || _char >= 'a' && _char <= 'z' || _char == '_';
    }

    boolean isDigit(char _char) {
        return _char >= '0' && _char <= '9';
    }

    Token idOrKeywords() {
        StringBuilder chars = new StringBuilder();
        var initLoc = loc;
        var _char = sourceCode[loc.pos];
        while (isAlpha(_char) || isDigit(_char)) {
            chars.append(_char);
            if (loc.incPosCol() >= sourceCode.length)
                break;
            _char = sourceCode[loc.pos];
        }
        String value = chars.toString();
        return new Token(value, initLoc, loc,
                DATATYPE_NAMES.contains(value.toLowerCase(Locale.ROOT)) ? TokenType.DATATYPE
                        : iskeyword(value) ? TokenType.KEYWORD : TokenType.IDENTIFIER);
    }

    public boolean iskeyword(String chars) {
        return keywords.keyMap.containsKey(chars);
    }

    public Type isDatatype(String type) {
        for (var i : Type.values())
            if (i.name().equals(type))
                return i;
        return null;
    }

    public Token peekToken() {
        if (peekedToken != null)
            return peekedToken;
        Token temp = currToken;
        Token token = nextToken();
        loc = token.initLoc();
        peekedToken = token;
        currToken = temp;
        return token;
    }
}
