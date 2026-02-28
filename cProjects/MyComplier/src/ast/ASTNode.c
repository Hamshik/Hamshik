#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <string.h>
#include <ctype.h>
#include "ASTNode.h"

ASTNode_t* new_num(char *rawval, DataTypes_t datatype, int line, int col) {
    ASTNode_t *node = ast_alloc();
    node->kind = AST_NUM;
    node->datatype = datatype;
    node->line = line;
    node->col = col;

    node->literal.raw = strdup(rawval);   // copy value
    free(rawval);          // free wrapper only

    return node;
}

ASTNode_t* new_var(const char *name, DataTypes_t datatype, int line, int col) {
    ASTNode_t *node = ast_alloc();
    node->kind = AST_VAR;
    node->var = strdup(name);
    node->datatype = datatype;
    node->line = line;
    node->col = col;
    return node;
}

ASTNode_t* new_unop(ASTNode_t *operand, int line, int col, OP_kind_t op) {
    ASTNode_t *node = ast_alloc();
    node->kind = AST_UNOP;
    node->unop.op = op;
    node->datatype = operand->datatype;
    node->unop.operand = operand;
    node->line = line;
    node->col = col;
    return node;
}

ASTNode_t* new_binop(ASTNode_t *left, ASTNode_t *right, int line, int col, OP_kind_t op) {
    ASTNode_t *node = ast_alloc();
    node->kind = AST_BINOP;
    node->datatype = UNKNOWN;
    node->bin.op = op;
    node->bin.left = left;
    node->bin.right = right;
    node->line = line;
    node->col = col;
    return node;
}

ASTNode_t* new_assign(ASTNode_t *lhs, ASTNode_t *rhs, DataTypes_t datatype, int line, int col,OP_kind_t op) {
    ASTNode_t *node = ast_alloc();
    node->kind = AST_ASSIGN;
    node->assign.op = op;
    node->assign.lhs = lhs;
    node->assign.rhs = rhs;
    node->datatype = datatype;
    node->line = line;
    node->col = col;
    return node;
}

ASTNode_t* new_seq(ASTNode_t *a, ASTNode_t *b) {
    ASTNode_t *node = ast_alloc();
    node->kind = AST_SEQ;
    node->seq.a = a;
    node->seq.b = b;
    return node;
}

ASTNode_t* new_if(ASTNode_t *cond, ASTNode_t *thenB, ASTNode_t *elseB, int line, int col) {
    ASTNode_t *node = ast_alloc();
    node->kind = NODE_IF;
    node->ifnode.cond = cond;
    node->ifnode.then_branch = thenB;
    node->ifnode.else_branch = elseB;
    node->line = line;
    node->col = col;
    return node;
}


Value ast_eval(ASTNode_t *node) {
    if (!node) return (Value){0};
    Value v;


    switch (node->kind) {

    case AST_NUM: return v;

    case AST_VAR: return getvar(node->var, node->datatype, node->line, node->col);

    case AST_BINOP: {
        Value l = ast_eval(node->bin.left);
        Value r = ast_eval(node->bin.right);

        switch (node->datatype) {
            case INT: return eval_binop_int(node->bin.op, false, l.inum, r.inum);
            case FLOAT: return eval_binop_float(node->bin.op, l.fnum, r.fnum);
            case DOUBLE: return eval_binop_double(node->bin.op, l.lfnum, r.lfnum);
            case SHORT: return eval_binop_int(node->bin.op, true, l.shnum, r.shnum);
            default:
                fprintf(stderr, "Error: unsupported data type for binary operation\n");
                exit(EXIT_FAILURE);
        }
    }
    case AST_UNOP: {
        Value r = ast_eval(node->unop.operand);
        do_unop_operation(&v, &r , node->datatype, node->unop.op);
        return v;
    }

    case AST_ASSIGN: {
        Value val = eval_assign(node->assign.lhs,
                                node->assign.rhs,
                                node->assign.op,
                                node->datatype,
                                node->line,
                                node->col);

        // 💥 IMPORTANT: rhs is no longer needed
        ast_free(node->assign.rhs);
        node->assign.rhs = NULL;

        return val;
    }

    case AST_SEQ:
        ast_eval(node->seq.a);
        return ast_eval(node->seq.b);

    case NODE_IF:
        if (ast_eval(node->ifnode.cond).bval)
            return ast_eval(node->ifnode.then_branch);
        if (node->ifnode.else_branch)
            return ast_eval(node->ifnode.else_branch);
        return (Value){0};

    default:
        fprintf(stderr, "Error: unknown AST node\n");
        exit(-1);
    }
}
