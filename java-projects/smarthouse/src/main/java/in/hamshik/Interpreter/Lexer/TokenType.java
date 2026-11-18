package in.hamshik.Interpreter.Lexer;

import java.util.HashMap;

public enum TokenType {
    NUMBER, BOOL, STRINGS, CHARACTER, IDENTIFIER, KEYWORD, DATATYPE, EOF,

    PLUS("+"), MINUS("-"), STAR("*"), SLASH("/"), MOD("%"), POW("**"), XOR_POW("^^"),
    INC("++"), DEC("--"),
    AMP("&"), PIPE("|"), XOR("^"), BIT_NOT("~"),
    LSHIFT("<<"), RSHIFT(">>"),
    GT(">"), LT("<"), EGT(">="), ELT("<="),
    EQ("=="), NEQ("!="),
    AND("&&"), OR("||"), NOT("!"), ASSIGN("="),
    PLUS_ASSIGN("+="), MINUS_ASSIGN("-="), MUL_ASSIGN("*="),
    DIV_ASSIGN("/="), MOD_ASSIGN("%="), POW_ASSIGN("**="), XOR_POW_ASSIGN("^^="),
    LSHIFT_ASSIGN("<<="), RSHIFT_ASSIGN(">>="),

    LBRACE("{"), RBRACE("}"), LBRACKET("["), RBRACKET("]"),
    LPAREN("("), RPAREN(")"),
    SEMICOLON(";"), COLON(":"), COMMA(",");

    public final String sym;
    public static final HashMap<String, TokenType> syms = new HashMap<>();
    
    static {
        for (TokenType t : values()) {
            if (t.sym != null)
                syms.put(t.sym, t);
        }
    }

    TokenType(String symbols) {
        sym = symbols;
    }

    TokenType() {
        sym = null;
    }

    public static TokenType getSymbols(String sym) {
        return syms.get(sym);
    }

};
