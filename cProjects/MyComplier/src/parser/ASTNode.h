#ifndef ASTNODE_H
#define ASTNODE_H

#include "../utils/uhash.h"

typedef enum ASTKind {
    AST_NUM,
    AST_BINOP,
    AST_UNOP,
    AST_VAR,
    AST_ASSIGN,
    AST_SEQ,
    NODE_IF
} ASTKind_t;

typedef enum OP_kind {
    OP_ADD, OP_SUB, OP_MUL, OP_DIV, OP_MOD, OP_POW,
    OP_AND, OP_OR, OP_NOT,
    OP_EQ, OP_NEQ, OP_LT, OP_LE, OP_GT, OP_GE,
    OP_LSHIFT, OP_RSHIFT,
    OP_BITAND, OP_BITOR, OP_BITXOR, OP_BITNOT,
    OP_NEG, OP_POS,
    OP_ASSIGN, OP_PLUS_ASSIGN, OP_MINUS_ASSIGN,
    OP_MUL_ASSIGN, OP_DIV_ASSIGN, OP_MOD_ASSIGN, OP_POW_ASSIGN,
    OP_LSHIFT_ASSIGN, OP_RSHIFT_ASSIGN,
    OP_INC, OP_DEC
} OP_kind_t;

typedef struct ASTNode {
    ASTKind_t kind;
    int line, col;

    union {
        double num;
        char *var;

        struct {
            OP_kind_t op;
            struct ASTNode *left, *right;
        } bin;

        struct {
            OP_kind_t op;
            struct ASTNode *operand;
        } unop;

        struct {
            struct ASTNode *lhs;
            struct ASTNode *rhs;
            OP_kind_t op;
        } assign;

        struct {
            struct ASTNode *a, *b;
        } seq;

        struct {
            struct ASTNode *cond;
            struct ASTNode *then_branch;
            struct ASTNode *else_branch;
        } ifnode;
    };
} ASTNode_t;

typedef struct {
    char *name;
    double value;
    UT_hash_handle hh;
} VarEntry;

/* Constructors */
ASTNode_t *new_num(double v, int line, int col);
ASTNode_t *new_var(const char *name, int line, int col);
ASTNode_t *new_binop(ASTNode_t *l, ASTNode_t *r, int line, int col, OP_kind_t op);
ASTNode_t *new_unop(ASTNode_t *e, int line, int col, OP_kind_t op);
ASTNode_t *new_assign(ASTNode_t *lhs, ASTNode_t *rhs, int line, int col, OP_kind_t op);
ASTNode_t *new_seq(ASTNode_t *a, ASTNode_t *b);
ASTNode_t *new_if(ASTNode_t *cond, ASTNode_t *thenB, ASTNode_t *elseB, int line, int col);

/* Eval + memory */
double ast_eval(ASTNode_t *n);
void ast_free(ASTNode_t *n);

/* Env */
void set_var(const char *name, double val);
VarEntry  *getvar(const char *name, int line, int col);

#endif
