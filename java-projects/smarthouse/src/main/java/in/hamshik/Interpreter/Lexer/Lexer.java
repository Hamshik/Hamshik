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

    public KeyWords keywords = new KeyWords();
    char[] sourceCode;
    public LexHelper helper = new LexHelper(this);
    public Location loc = helper.new Location();

    public Lexer(String path, Consumer<String> out) {
        this.filePath = path;
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
            return new Token(null, null, loc, TokenType.EOF);

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
        
        for(var _char = sourceCode[loc.pos]; LexHelper.isDigit(_char) && helper.consumeToken();
            _char = sourceCode[loc.pos])
            chars.append(_char);

        return new Token(chars.toString(), initLoc, loc, TokenType.NUMBER);
    }

    private Token handleSymbols(char _char) {
        helper.consumeToken();
        char[] mayBeSym = { _char, helper.peek(1) };
        TokenType sym = TokenType.getSymbols(new StringBuilder().append(mayBeSym).toString());
        if (sym == null) {
            sym = TokenType.getSymbols(new StringBuilder().append(_char).toString());
            if (sym != null) {
                return new Token(sym, loc, loc);
            } else
                return nextToken();
        } 
        helper.consumeToken();
        return new Token(sym, loc, null);
    }

    private Token handleComments() {
        var initLoc = helper.new Location(loc);
        // comsuming tri-quates
        helper.consumeToken(3);
       
        for( var _char = sourceCode[loc.pos];
            _char != '\'' && helper.peek(1) != '\'' && helper.peek(2) != '\''; _char = sourceCode[loc.pos]) {
            if (!helper.consumeToken()) {
                System.err.println("pls close the commet from " + initLoc.pos);
                break;
            }
        }
        helper.consumeToken(3);
        return nextToken();
    }

    private Token handleChar() {
        StringBuilder chars = new StringBuilder();
        var initLoc = helper.new Location(loc);
         // consuming opening-quotes
        for(var _char = sourceCode[loc.incPosCol()]; _char != '\''; _char = sourceCode[loc.pos]) {
            chars.append(_char);
            if (!helper.consumeToken() && sourceCode[loc.pos] != '\'') { // already consumed by
                                                                  // 'loc.incPosCol'
                System.err.println("Pls Close the quates");
                break;
            }
            
        }
        loc.incPosCol(); // consuming closing-quates
        return new Token(chars.toString(), initLoc, loc, TokenType.CHARACTER);
    }

    private Token handleStrs() {
        StringBuilder chars = new StringBuilder();
        var initLoc = helper.new Location(loc);
         // consuming opening-quates
        for (var _char = sourceCode[loc.incPosCol()]; _char != '"'; _char = sourceCode[loc.pos]) {
            chars.append(_char);
            if (_char == '\n' || !helper.consumeToken()) {
                System.err.println("pls close the qutes");
                break;
            }
        }
        helper.consumeToken(); // consuming closing-quates
        return new Token(chars.toString(), initLoc, loc, TokenType.STRINGS);
    }

    Token idOrKeywords() {
        StringBuilder chars = new StringBuilder();
        var initLoc = helper.new Location(loc);
        
        for (var _char = sourceCode[loc.pos];
            (LexHelper.isAlpha(_char) || LexHelper.isDigit(_char)) && helper.consumeToken(); _char = sourceCode[loc.pos])
            chars.append(_char);

        String value = chars.toString();
        return new Token(value, initLoc, loc,
                LexHelper.isDatatype(value) != null ? TokenType.DATATYPE
                        : helper.iskeyword(value) ? TokenType.KEYWORD : TokenType.IDENTIFIER);
    }
}
