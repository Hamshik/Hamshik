#ifndef ASTNODE_H
#define ASTNODE_H

typedef enum {
    AST_NUM,
    AST_BINOP,
    AST_VAR
} ASTKind;

typedef enum {
    OP_ADD,
    OP_SUB,
    OP_MUL,
    OP_DIV,
    OP_MOD,
    OP_POW
} OP_kind_t;

typedef struct ASTNode {
    ASTKind kind;
    union
    {
        char *var;
        double num;
        struct {
            OP_kind_t op;
            struct ASTNode *left;
            struct ASTNode *right;
        } bin;
    };
    int line;
    int col;
} ASTNode_t;

ASTNode_t *new_num(double v, int line, int col);
ASTNode_t *new_binop(ASTNode_t *l, ASTNode_t *r, int line, int col, OP_kind_t op);
ASTNode_t *new_var(const char *name, int line, int col);
double ast_eval(ASTNode_t *n);
void ast_free(ASTNode_t *n);
ASTNode_t* ast_alloc(void);

#endif
