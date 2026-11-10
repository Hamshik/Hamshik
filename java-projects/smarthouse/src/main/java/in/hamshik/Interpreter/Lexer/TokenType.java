package in.hamshik.Interpreter.Lexer;

import java.util.HashMap;

public enum TokenType {
    NUMBER, BOOL, STRINGS, CHARACTER, IDENTIFIER, KEYWORD, DATATYPE, EOF,

    PLUS("+"), MINUS("-"), STAR("*"), SLASH("/"), MOD("%"),
    AMP("&"), PIPE("|"), XOR("^"), BIT_NOT("~"),
    GT(">"), LT("<"), EGT(">="), ELT("<="),
    EQ("=="), NEQ("!="),
    AND("&&"), OR("||"), NOT("!"), ASSIGN("="), 
    LBRACE("{"), RBRACE("}"), LBRACKET("["), RBRACKET("]"),
    LPAREN("("), RPAREN(")"),
    SEMICOLON(";"), COLON(":"), COMMA(",");

    public final String sym;
    public static final HashMap<String, TokenType> syms = new HashMap<>();

    TokenType(String symbols){ sym = symbols; }
    TokenType(){ sym = null; }
    public static TokenType getSymbols(String sym){
        if(syms.isEmpty())
            for(var i : TokenType.values())
                if(i.sym != null) syms.put(i.sym, i);

        return syms.get(sym);
    }
};
