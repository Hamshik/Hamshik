package in.hamshik.Interpreter.shared;

import in.hamshik.Helpers.VarHelpers;

/**
 * ASTKind
 */
public enum ASTKind {
    AST_NUM,
    AST_BINOP,
    AST_UNOP,
    AST_VAR,
    AST_BOOL,
    AST_ASSIGN,
    AST_SEQ,
    AST_NULL,
    AST_IF,
    AST_FOR,
    AST_STR,
    AST_CHAR,
    AST_WHILE,
    AST_FN,
    AST_CALL,
    AST_RETURN,
    AST_IMPORT,
    AST_LIST,
    AST_INDEX,
    AST_CONTINUE,
    AST_BREAK,
}

record Type(VarHelpers.Type type, Type innerType){}