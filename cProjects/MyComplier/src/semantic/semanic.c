#include <stdio.h>
#include <stdlib.h>
#include "../ast/ASTNode.h"
#include "semantic.h"

void semantic_check(ASTNode_t *root) {
    if (!root) return;
    check_expr(root);
}

/* Helpers */
void type_error(ASTNode_t *n, const char *msg) {
    fprintf(stderr, "Semantic error [%d:%d]: %s\n", n->line, n->col, msg);
    exit(1);
}

int is_numeric(DataTypes_t t) {
    return t == INT || t == FLOAT || t == DOUBLE || t == SHORT;
}

DataTypes_t promote(DataTypes_t a, DataTypes_t b) {
    if (a == DOUBLE || b == DOUBLE) return DOUBLE;
    if (a == FLOAT  || b == FLOAT)  return FLOAT;
    if (a == INT    || b == INT)    return INT;
    return SHORT;
}

/* Main recursive checker */
DataTypes_t check_expr(ASTNode_t *n) {
    if (!n) return UNKNOWN;

    switch (n->kind) {

    case AST_NUM:
        return n->datatype;

    case AST_STR:
        return STRINGS;

    case AST_VAR:
        /* getvar() already errors if undefined */
        getvar(n->var, n->datatype, n->line, n->col);
        return n->datatype;

    case AST_BINOP: {
        DataTypes_t lt = check_expr(n->bin.left);
        DataTypes_t rt = check_expr(n->bin.right);

        /* string ops */
        if (lt == STRINGS || rt == STRINGS) {
            if (n->bin.op != OP_ADD || lt != STRINGS || rt != STRINGS) {
                type_error(n, "Only string + string is allowed");
            }
            n->datatype = STRINGS;
            return STRINGS;
        }

        /* numeric ops */
        if (!is_numeric(lt) || !is_numeric(rt)) {
            type_error(n, "Invalid operands for binary operator");
        }

        n->datatype = promote(lt, rt);
        return n->datatype;
    }

    case AST_UNOP: {
        DataTypes_t t = check_expr(n->unop.operand);

        if (n->unop.op == OP_NOT) {
            if (t != BOOL) type_error(n, "Operator ! expects bool");
            n->datatype = BOOL;
            return BOOL;
        }

        if (!is_numeric(t)) {
            type_error(n, "Unary operator requires numeric type");
        }

        n->datatype = t;
        return t;
    }

    case AST_ASSIGN: {
        if (n->assign.lhs->kind != AST_VAR) {
            type_error(n, "Left side of assignment must be a variable");
        }

        DataTypes_t rhs_t = check_expr(n->assign.rhs);
        DataTypes_t lhs_t = n->assign.lhs->datatype;

        if (lhs_t != rhs_t) {
            type_error(n, "Type mismatch in assignment");
        }

        return lhs_t;
    }

    case AST_SEQ:
        check_expr(n->seq.a);
        return check_expr(n->seq.b);

    case NODE_IF: {
        DataTypes_t ct = check_expr(n->ifnode.cond);
        if (ct != BOOL) type_error(n, "if condition must be boolean");

        check_expr(n->ifnode.then_branch);
        if (n->ifnode.else_branch)
            check_expr(n->ifnode.else_branch);

        return UNKNOWN;
    }

    default:
        type_error(n, "Unknown AST node in semantic analysis");
        return UNKNOWN;
    }
}