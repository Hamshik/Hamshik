package in.hamshik.Interpreter.Lexer;

import java.util.HashMap;

public class KeyWords {
    public static final HashMap<String, KeyWordTy> keyMap = new HashMap<>();
    public static HashMap<String, KeyWordTy> datatype = new HashMap<>();

    public enum KeyWordTy {
        IF, ELSE, FOR, WHILE, IN, FN, VAR, MUT,
        I8(true), I16(true), I32(true), I64(true),
        F32(true), F64(true),
        U8(true), U16(true), U32(true), U64(true),
        BOOL(true), STR(true), CHAR(true), VOID(true),
        RET, RETURN;
        final boolean isDatatype;
        KeyWordTy() { this.isDatatype = false; }
        KeyWordTy(boolean isDatatype) { this.isDatatype = isDatatype; }
    }    

    static {
        for(var key : KeyWordTy.values())
            keyMap.put(key.toString().toLowerCase(), key);
    }

    static {
        for (var i : KeyWordTy.values())
            datatype.put(i.name().toLowerCase(), i);
    }
}
