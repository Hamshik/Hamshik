package in.hamshik.Interpreter.shared;

import in.hamshik.Helpers.VarHelpers.Type;
import in.hamshik.Interpreter.Lexer.Lexer.Location;

public class ASTNode {
    public ASTKind kind;
    public Type type;
    public boolean isglobal;
    public boolean is_variadic;
    public boolean ismut;
    public Location loc; /* 0-based byte offset (start) */ /* 0-based byte offset (end) */

    public Union val;

    public ASTNode(ASTKind kind, Type type, Location loc, Union val, boolean... bools){
        this.kind = kind;
        this.type = type;
        this.loc = loc;
        if(bools != null){
            this.isglobal = bools[0];
            this.is_variadic = bools[1];
            this.ismut = bools[2];
        }
        this.val = val;
    }
    public ASTNode(ASTKind kind, Type type, Location loc, Union val){
        this.kind = kind;
        this.type = type;
        this.loc = loc;
        this.val = val;
    }
}