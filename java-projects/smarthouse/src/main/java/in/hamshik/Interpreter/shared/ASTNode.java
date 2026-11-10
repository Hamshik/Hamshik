package in.hamshik.Interpreter.shared;

import in.hamshik.Helpers.VarHelpers.Type;
import in.hamshik.Interpreter.Lexer.LexHelper.Location;

public class ASTNode {
    public ASTKind kind;
    public Type type;
    public boolean isglobal;
    public boolean ismut;
    public Location loc; /* 0-based byte offset (start) */ /* 0-based byte offset (end) */

    public Union val;

    public ASTNode(ASTKind kind, Type type, Location loc, Union val, boolean... bools){
        this.kind = kind;
        this.type = type;
        this.loc = loc;
        this.isglobal = bools[0];
        this.ismut = bools[1];
        this.val = val;
    }
    public ASTNode(ASTKind kind, Type type, Location loc, Union val){
        this.kind = kind;
        this.type = type;
        this.loc = loc;
        this.val = val;
    }
}