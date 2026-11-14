package in.hamshik.Interpreter.shared;

import in.hamshik.Helpers.VarHelpers.Type;
import in.hamshik.Interpreter.Lexer.LexHelper.Location;

public class ASTNode {
    public Type type;
    public boolean isglobal = false;
    public boolean ismut = true;
    public Location loc; /* 0-based byte offset (start) */ /* 0-based byte offset (end) */

    public Object val;

    public ASTNode(Type type, Location loc, Union val, boolean... bools){
        this.type = type;
        this.loc = loc;
        this.isglobal = bools[0];
        this.ismut = bools[1];
        this.val = val;
    }
    public ASTNode(Type type, Location loc, Object val){
        this.type = type;
        this.loc = loc;
        this.val = val;
    }
}