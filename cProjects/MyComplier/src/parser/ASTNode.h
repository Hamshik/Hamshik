#define do_assign_operation_num(v, r, cur, op)             \
    do {                                        \
        switch (op) {                           \
            case OP_ASSIGN:                     \
                (v) = (r);                      \
                break;                          \
            case OP_PLUS_ASSIGN:                \
                (v) = (cur) + (r);              \
                break;                          \
            case OP_MINUS_ASSIGN:               \
                (v) = (cur) - (r);              \
                break;                          \
            case OP_MUL_ASSIGN:                 \
                (v) = (cur) * (r);              \
                break;                          \
            case OP_DIV_ASSIGN:                 \
                (v) = (cur) / (r);              \
                break;                          \
            case OP_MOD_ASSIGN:                 \
                (v) = fmod((cur), (r));         \
                break;                          \
            case OP_POW_ASSIGN:                 \
                (v) = pow((cur), (r));          \
                break;                          \
            case OP_LSHIFT_ASSIGN:              \
                (v) = (int)(cur) << (int)(r);   \
                break;                          \
            case OP_RSHIFT_ASSIGN:              \
                (v) = (int)(cur) >> (int)(r);   \
                break;                          \
            default:                            \
                fprintf(stderr,                 \
                        "Error: unknown assignment operator\n"); \
                exit(EXIT_FAILURE);             \
        }                                       \
    } while (0)

#define do_operation_str(v, r, cur, op) \
    do { \
        switch (op) { \
            case OP_ASSIGN: \
                (v) = strdup(r); \
                break; \
            case OP_PLUS_ASSIGN: \
                (v) = strcat((char*)cur, (char*)r); \
                break; \
            case OP_ADD: \
                (v) = strcat((char*)cur, (char*)r); \
                break; \
            default: \
                fprintf(stderr, "Error: unsupported string operator\n"); \
                exit(EXIT_FAILURE); \
        } \
    } while (0)
#define do_boolean_operation(v, r, cur, op) \
    do {                                        \
        switch (op) {                           \
            case OP_ASSIGN:                     \
                (v) = (r);                      \
                break;                          \
            case OP_AND:                        \
                (v) = (cur) && (r);             \
                break;                          \
            case OP_OR:                         \
                (v) = (cur) || (r);             \
                break;                          \
            case OP_NOT:                        \
                (v) = !(r);                     \
                break;                          \
            case OP_BITAND:                     \
                (v) = (cur) & (r);             \
                break;                          \
            case OP_BITOR:                      \
                (v) = (cur) | (r);             \
                break;                          \
            case OP_BITXOR:                     \
                (v) = (cur) ^ (r);             \
                break;                          \
            case OP_BITNOT:                     \
                (v) = ~(r);                    \
                break;                          \
            default:                            \
                fprintf(stderr,                 \
                        "Error: unsupported operatoration for booleans\n"); \
                exit(EXIT_FAILURE);             \
        }                                       \
    } while (0)

#define do_num_operation(v, r1, r2, op)             \
    do {                                        \
        switch (op) {                           \
            case OP_ADD:                         \
                (v) = (r1) + (r2);              \
                break;                          \
            case OP_SUB:                         \
                (v) = (r1) - (r2);              \
                break;                          \
            case OP_MUL:                         \
                (v) = (r1) * (r2);              \
                break;                          \
            case OP_DIV:                         \
                if (fabs(r2) < 1e-12) {          \
                    fprintf(stderr,             \
                            "Error: division by zero\n"); \
                    exit(EXIT_FAILURE);         \
                }                               \
                (v) = (r1) / (r2);              \
                break;                          \
            case OP_MOD:                         \
                if (fabs(r2) < 1e-12) {          \
                    fprintf(stderr,             \
                            "Error: division by zero\n"); \
                    exit(EXIT_FAILURE);         \
                }                               \
                (v) = fmod((r1), (r2));         \
                break;                          \
            case OP_POW:                         \
                (v) = pow((r1), (r2));          \
                break;                          \
            case OP_LSHIFT:                      \
                (v) = (int)(r1) << (int)(r2);   \
                break;                          \
            case OP_RSHIFT:                      \
                (v) = (int)(r1) >> (int)(r2);   \
                break;                          \
            default:                            \
                fprintf(stderr,                 \
                        "Error: unknown binary operator\n");  \
                exit(EXIT_FAILURE);             \
        }                                       \
    } while (0)

#define do_unop_operation(v, r, op)             \
    do {                                        \
        switch (op) {                           \
            case OP_NEG:                         \
                (v) = -(r);                     \
                break;                          \
            case OP_POS:                         \
                (v) = +(r);                     \
                break;                          \
            case OP_NOT:                        \
                (v) = !(r);                     \
                break;                          \
            case OP_BITNOT:                     \
                (v) = ~((int)(r));              \
                break;                          \
            default:                            \
                fprintf(stderr,                 \
                        "Error: unknown unary operator\n");  \
                exit(EXIT_FAILURE);             \
        }                                       \
    } while (0)
#define do_bool_op(lhs, rhs, op) \
    do{\
        if (op == OP_AND) { \
                bool l = ast_eval(node->bin.left).bval;\
                if (!l) return (Value){.bval = false};\
                return ast_eval(node->bin.right).bval;\
            }\
        if (op == OP_OR) {\
            bool l = ast_eval(node->bin.left).bval;\
            if (l) return (Value){.bval = true};\
            return !ast_eval(node->bin.right).bval;\
        }\
    }while (0);
    
#define assign_val(datatype, var ,val)\
    do{\
    switch ((datatype)){\
        case SHORT: (var).shnum = (short)(val).shnum; break;\
        case INT: (var).inum = (int)(val).inum; break;\
        case FLOAT: (var).fnum = (float)(val).fnum; break;\
        case DOUBLE: (var).lfnum = (double)(val).lfnum; break;\
        default:\
            fprintf(stderr, "Error: unsupported data type for assignment\n");\
            exit(EXIT_FAILURE);\
            break;\
    }\
    }while(0)

#ifndef ASTNODE_H
#define ASTNODE_H

#include "../utils/uhash.h"
#include <stdbool.h>

typedef enum ASTKind {
    AST_NUM,
    AST_BINOP,
    AST_UNOP,
    AST_VAR,
    AST_ASSIGN,
    AST_SEQ,
    NODE_IF
} ASTKind_t;

typedef enum DataTypes{
    INT,
    FLOAT,
    DOUBLE,
    SHORT,
    BOOL,
    STRINGS,
    CHARACTER
} DataTypes_t;

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
    DataTypes_t datatype;
    int line, col;

    union {
        Value val;
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
            struct ASTNode *lhs, *rhs;
            OP_kind_t op;
        } assign;

        struct {
            struct ASTNode *a, *b;
        } seq;

        struct {
            struct ASTNode *cond, *then_branch, *else_branch;
        } ifnode;
    };
} ASTNode_t;
typedef union {
    int inum;
    float fnum;
    double lfnum;
    short shnum;
    bool bval;
    char characters;
    char* str;
} Value;
typedef struct {
    char *name;
    Value val;
    DataTypes_t datatype;
    UT_hash_handle hh;
} VarEntry;

/* Constructors */
ASTNode_t *new_num(Value *val, DataTypes_t datatype, int line, int col);
ASTNode_t *new_var(const char *name, DataTypes_t datatype, int line, int col);
ASTNode_t *new_binop(ASTNode_t *l, ASTNode_t *r, int line, int col, OP_kind_t op);
ASTNode_t *new_unop(ASTNode_t *e, int line, int col, OP_kind_t op);
ASTNode_t *new_assign(ASTNode_t *lhs, ASTNode_t *rhs, int line, int col, OP_kind_t op);
ASTNode_t *new_seq(ASTNode_t *a, ASTNode_t *b);
ASTNode_t *new_if(ASTNode_t *cond, ASTNode_t *thenB, ASTNode_t *elseB, int line, int col);

/* Eval + memory */
Value ast_eval(ASTNode_t *n);
void ast_free(ASTNode_t *n);

/* Env */
void set_var(const char *name, Value *val, DataTypes_t datatype);
Value getvar(const char *name, DataTypes_t datatype, int line, int col);

#endif
