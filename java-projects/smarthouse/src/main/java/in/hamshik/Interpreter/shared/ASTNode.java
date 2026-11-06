package in.hamshik.Interpreter.shared;

import in.hamshik.Helpers.VarHelpers.Type;
import in.hamshik.Interpreter.Lexer.Lexer.Location;

public class ASTNode {
    ASTKind kind;
    Type type;
    boolean isglobal;
    boolean is_variadic;
    boolean ismut;
    Location loc; /* 0-based byte offset (start) */ /* 0-based byte offset (end) */

    Union val;
}