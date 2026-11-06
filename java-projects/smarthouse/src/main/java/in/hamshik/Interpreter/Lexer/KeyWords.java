package in.hamshik.Interpreter.Lexer;

import java.util.HashMap;

public class KeyWords {
    public final HashMap<String, KeyWordTy> keyMap = new HashMap<>();

    public enum KeyWordTy {
        IF, ELSE, FOR, WHILE, IN, FN, CONST,
        I8, I16, I32, I64, F32, F64,
        U8, U16, U32, U64,
        BOOL, STR, CHAR, VOID,
    }

    public KeyWords() {
        for(var key : KeyWordTy.values())
            keyMap.put(key.toString().toLowerCase(), key);
    }
}
