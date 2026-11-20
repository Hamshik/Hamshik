package in.hamshik.Interpreter.Lexer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.function.Consumer;

import in.hamshik.Interpreter.Lexer.LexHelper.*;

public class Lexer {

    String filePath;
    Token peekedToken;
    public static Token currToken;

    public char[] sourceCode = null;
    public LexHelper helper = new LexHelper(this);
    public Location loc = helper.new Location();
    public Consumer<String> out;

    public Lexer(String path, Consumer<String> out) {
        this.filePath = path;
        this.out = out;
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            sourceCode = reader.readAllAsString().toCharArray();
        } catch (IOException e) {
            out.accept("\u001B[31m" + "Could not read source file: "
                + e.getMessage() + "\u001B[0m\n");
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
            return new Token(null, loc, TokenType.EOF);

        var _char = sourceCode[loc.pos];
        if (LexHelper.isAlpha(_char))
            return idOrKeywords();
        if (_char == '"')
            return handleStrs();
        if (_char == '\'' && helper.peek(1) == '\'' && helper.peek(2) == '\'')
            return handleComments();
        if (_char == '\'' && helper.peek(1) == '\'')
            return handleSingleCommet();
        if (_char == '\'')
            return handleChar();
        if (LexHelper.isDigit(_char))
            return handleDigits();

        return handleSymbols(_char);
    }

    public boolean consumeToken(TokenType type, String errMsg) {
        return helper.consumeToken(type, errMsg);
    }

    public boolean consumeToken(TokenType type) {
        return helper.consumeToken(type);
    }

    private Token handleSingleCommet() {
        for (char _char = sourceCode[loc.pos]; _char != '\n' && helper.consumeToken();
            _char = sourceCode[loc.pos]);
        return nextToken();
    }

    private Token handleDigits() {
        StringBuilder chars = new StringBuilder();
        var initLoc = helper.new Location(loc);

        boolean hasDot = false;
        for (var _char = sourceCode[loc.pos];
             (LexHelper.isDigit(_char) || (!hasDot && _char == '.')) && helper.consumeToken();
             _char = sourceCode[loc.pos]) {
            if (_char == '.') hasDot = true;
            chars.append(_char);
        }

        return new Token(chars.toString(), initLoc.combineLocation(loc), TokenType.NUMBER);
    }

    private Token handleSymbols(char _char) {
        var initLoc = helper.new Location(loc);
        String triple = "" + _char + helper.peek(0) + helper.peek(1);
        TokenType tripleType = TokenType.getSymbols(triple);
        if (tripleType != null) {
            helper.consumeToken(3);
            return new Token(tripleType, initLoc.combineLocation(loc));
        }
        helper.consumeToken();
        TokenType sym = TokenType.getSymbols("" + _char + helper.peek(0));
        if (sym == null) {
            sym = TokenType.getSymbols(Character.toString(_char));
            if (sym != null) {
                return new Token(sym, initLoc.combineLocation(loc));
            } else 
                return nextToken();

        } 
        helper.consumeToken();
        return new Token(sym, initLoc.combineLocation(loc));
    }

    private Token handleComments() {
        var initLoc = helper.new Location(loc);
        // comsuming tri-quates
        helper.consumeToken(3);
       
        for( var _char = sourceCode[loc.pos];
            _char != '\'' && helper.peek(1) != '\'' && helper.peek(2) != '\''; _char = sourceCode[loc.pos]) {
            if (!helper.consumeToken()) 
                throw helper.syntaxError("pls close the commet from " + initLoc.pos, new Token(TokenType.EOF, initLoc.combineLocation(loc)));

        }
        helper.consumeToken(3);
        return nextToken();
    }

    private Token handleChar() {
        StringBuilder chars = new StringBuilder();
        var initLoc = helper.new Location(loc);
         // consuming opening-quotes
        for(var _char = sourceCode[loc.incPosCol(sourceCode[loc.pos])]; _char != '\''; _char = sourceCode[loc.pos]) {
            chars.append(_char);
            if (!helper.consumeToken() && sourceCode[loc.pos] != '\'')
                throw helper.syntaxError("Pls Close the quates", new Token(TokenType.EOF, initLoc.combineLocation(loc)));
            
        }
        loc.incPosCol(sourceCode[loc.pos]); // consuming closing-quates
        return new Token(chars.toString(), initLoc.combineLocation(loc), TokenType.CHARACTER);
    }

    private Token handleStrs() {
        StringBuilder chars = new StringBuilder();
        var initLoc = helper.new Location(loc);
         // consuming opening-quates
        for (var _char = sourceCode[loc.incPosCol(sourceCode[loc.pos])]; _char != '"'; _char = sourceCode[loc.pos]) {
            chars.append(_char);
            if (_char == '\n' || !helper.consumeToken())
                throw helper.syntaxError("pls close the qutes", new Token(TokenType.EOF, initLoc.combineLocation(loc)));
        }
        helper.consumeToken(); // consuming closing-quates
        return new Token(chars.toString(), initLoc.combineLocation(loc), TokenType.STRINGS);
    }

    Token idOrKeywords() {
        StringBuilder chars = new StringBuilder();
        var initLoc = helper.new Location(loc);
        
        for (var _char = sourceCode[loc.pos];
            (LexHelper.isAlpha(_char) || LexHelper.isDigit(_char)) && helper.consumeToken(); _char = sourceCode[loc.pos])
            chars.append(_char);

        String value = chars.toString();
        if (value.equals("true") || value.equals("false"))
            return new Token(value, initLoc.combineLocation(loc), TokenType.BOOL);
        return new Token(value, initLoc.combineLocation(loc),
                LexHelper.isDatatype(value) ? TokenType.DATATYPE
                        : LexHelper.iskeyword(value) ? TokenType.KEYWORD : TokenType.IDENTIFIER);
    }
}
