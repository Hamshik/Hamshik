package in.hamshik.Interpreter.Lexer;

public enum TokenType {
    NUMBER, BOOL, STRINGS, CHARACTER, IDENTIFIER, KEYWORD, DATATYPE, EOF, 

    PLUS("+"), MINUS("-"), STAR("*"), SLASH("/"), MOD("%"),
    AMP("&"), PIPE("|"), XOR("^"), BIT_NOT("~"),
    GT(">"), LT("<"), EGT(">="), ELT("<="),
    EQ("=="), NEQ("!="),
    AND("&&"), OR("||"), NOT("!"), ASSIGN("="), 
    LBRACE("{"), RBRACE("}"), LPAREN("("), RPAREN(")"),
    SEMICOLON(";"), COLON(":"), COMMA(",");

    public final String sym;
    TokenType(String symbols){ sym = symbols; }
    TokenType(){ sym = null; }
};
