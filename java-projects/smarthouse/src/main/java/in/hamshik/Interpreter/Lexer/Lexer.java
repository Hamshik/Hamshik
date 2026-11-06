package in.hamshik.Interpreter.Lexer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javafx.scene.control.TextArea;

public class Lexer {
    TextArea out;
    String filePath;

    Location loc = new Location();
    public KeyWords keywords = new KeyWords();
    char[] sourceCode;

    public record Token(String val, Location initLoc, Location lastLoc, TokenType type) {
    }

    public class Location {
        int pos, col, ln = 0;

        int incPosCol(){ col++; return ++pos; }
    }

    public Lexer(String path, TextArea out) throws IOException, FileNotFoundException {
        this.out = out;
        this.filePath = path;
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            sourceCode = reader.readAllAsString().toCharArray();
        }
    }
    
    public Token getNextToken() {
        if (loc.pos >= sourceCode.length)
            return new Token(null, null, loc, TokenType.EOF);

        var _char = sourceCode[loc.pos];
        if (isAlpha(_char))
            return idOrKeywords();
        if(_char == '"')
            return handleStrs();
        if(_char == '\'' && peek(1) == '\'' && peek(2) == '\'')
            return handleComments();
        if(_char == '\'' && peek(1) == '\'')
            return handleSingleCommet();
        if(_char == '\'')
            return handleChar();
        if(isDigit(_char))
            return handleDigits();
        
        return handleSymbols(_char);
    }

    
    private Token handleSingleCommet() {
        var _char = sourceCode[loc.pos];
        while (_char != '\n') {
            if(loc.incPosCol() >= sourceCode.length) //consuming the single line commet & checking bounds
                break;
            _char = sourceCode[loc.pos];
        }
        return getNextToken();
    }

    private Token handleDigits() {
        StringBuilder chars = new StringBuilder();
        var initLoc = loc;
        var _char = sourceCode[loc.pos];
        while (isDigit(_char)) {
            chars.append(_char);
            if(loc.incPosCol() >= sourceCode.length) break;
            _char = sourceCode[loc.pos];
        }
        return new Token(chars.toString(), initLoc, loc, TokenType.NUMBER);
    }

    private char peek(int idx) {
        if(idx >= sourceCode.length || loc.pos + idx >= sourceCode.length) return 0;
       return sourceCode[loc.pos + idx];
    }

    private Token handleSymbols(char _char) {
        loc.incPosCol();
        switch (_char) {
            case '{': return new Token("{", loc, null, TokenType.LBRACE);
            case '}': return new Token("}", loc, null, TokenType.RBRACE);
            case '(': return new Token("(", loc, null, TokenType.LPAREN);
            case ')': return new Token(")", loc, null, TokenType.RPAREN);
            case '=': return new Token("=", loc, null, TokenType.ASSIGN);
            case ';': return new Token(";", loc, null, TokenType.SEMICOLON);
            case '+': return new Token(";", loc, null, TokenType.PLUS);
            case '-': return new Token(";", loc, null, TokenType.MINUS);
            case '*': return new Token(";", loc, null, TokenType.STAR);
            case '/': return new Token(";", loc, null, TokenType.SLASH);
            default: return getNextToken();
        }
    }

    private Token handleComments() {
        var initLoc = loc;
        //comsuming tri-quates
        for(int i = 0; i < 3; i++) loc.incPosCol();
        var _char = sourceCode[loc.pos]; 
        while (_char != '\'' && peek(1) != '\'' && peek(2) != '\'') {
            if(loc.incPosCol() >= sourceCode.length){
                System.err.println("pls close the commet from " + initLoc.pos);
                break;
            }
            _char = sourceCode[loc.pos];
        }
        for(int i = 0; i < 3; i++) loc.incPosCol();
        return getNextToken();
    }

    private Token handleChar() {
        StringBuilder chars = new StringBuilder();
        var initLoc = loc;
        var _char = sourceCode[loc.incPosCol()]; //consuming opening-quotes
        while (_char != '\'') {
            chars.append(_char);
            if(loc.incPosCol() >= sourceCode.length && sourceCode[loc.pos] != '\'') { //already consumed by 'loc.incPosCol'
                System.err.println("Only one char is allowed");
                break;
            }
            _char = sourceCode[loc.pos];
        }
        loc.incPosCol(); //consuming closing-quates
        return new Token(chars.toString(), initLoc, loc, TokenType.CHARACTER);
    }

    private Token handleStrs() {
        StringBuilder chars = new StringBuilder();
        var initLoc = loc;
        var _char = sourceCode[loc.incPosCol()];  //consuming opening-quates
        while (_char != '"') {
            chars.append(_char); 
            if(_char == '\n' || loc.incPosCol() >= sourceCode.length) {
                System.err.println("pls close the qutes");
                break;
            }
            _char = sourceCode[loc.pos];
        }
        loc.incPosCol();  //consuming closing-quates
        return new Token(chars.toString(), initLoc, loc, TokenType.STRINGS);
    }

    boolean isAlpha(char _char) {
        return _char >= 'A' && _char <= 'Z' || _char >= 'a' && _char <= 'z';
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
            if(loc.incPosCol() >= sourceCode.length) break;
            _char = sourceCode[loc.pos];
        }
        return new Token(chars.toString(), initLoc, loc,
                iskeyword(chars.toString()) ? TokenType.KEYWORD : TokenType.IDENTIFIER);
    }

    boolean iskeyword(String chars) {
        return keywords.keyMap.containsKey(chars);
    }
}
