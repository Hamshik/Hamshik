#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <string.h>
#include <ctype.h>
#include "ASTNode.h"

static ASTNode_t *ast_alloc(void) {
    ASTNode_t *n = calloc(1, sizeof(ASTNode_t));
    if (!n) { perror("malloc"); exit(1); }
    return n;
}

ASTNode_t* new_num(double val, int line, int col) {
    ASTNode_t *node = ast_alloc();
    node->kind = AST_NUM;
    node->num = val;
    node->line = line;
    node->col = col;
    return node;
}

ASTNode_t* new_var(const char *name, int line, int col) {
    ASTNode_t *node = ast_alloc();
    node->kind = AST_VAR;
    node->var = strdup(name);
    node->line = line;
    node->col = col;
    return node;
}

ASTNode_t* new_unop(ASTNode_t *operand, int line, int col, OP_kind_t op) {
    ASTNode_t *node = ast_alloc();
    node->kind = AST_UNOP;
    node->unop.op = op;
    node->unop.operand = operand;
    node->line = line;
    node->col = col;
    return node;
}

ASTNode_t* new_binop(ASTNode_t *left, ASTNode_t *right, int line, int col, OP_kind_t op) {
    ASTNode_t *node = ast_alloc();
    node->kind = AST_BINOP;
    node->bin.op = op;
    node->bin.left = left;
    node->bin.right = right;
    node->line = line;
    node->col = col;
    return node;
}

ASTNode_t* new_assign(ASTNode_t *lhs, ASTNode_t *rhs, int line, int col, OP_kind_t op) {
    ASTNode_t *node = ast_alloc();
    node->kind = AST_ASSIGN;
    node->assign.lhs = lhs;
    node->assign.rhs = rhs;
    node->assign.op = op;
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

void ast_free(ASTNode_t *n) {
    if (!n) return;

    switch (n->kind) {
        case AST_VAR: free(n->var); break;
        case AST_BINOP: ast_free(n->bin.left); ast_free(n->bin.right); break;
        case AST_UNOP: ast_free(n->unop.operand); break;
        case AST_ASSIGN: ast_free(n->assign.lhs); ast_free(n->assign.rhs); break;
        case AST_SEQ: ast_free(n->seq.a); ast_free(n->seq.b); break;
        case NODE_IF:
            ast_free(n->ifnode.cond);
            ast_free(n->ifnode.then_branch);
            ast_free(n->ifnode.else_branch);
            break;
        default: break;
    }
    free(n);
}

/* ================= ENV ================= */

VarEntry *env = NULL;

void set_var(const char *name, ASTNode_t *node) {
    VarEntry *v;
    HASH_FIND_STR(env, name, v);
    if (v) v->node = node;
    else {
        v = malloc(sizeof(*v));
        v->name = strdup(name);
        v->node = node;
        HASH_ADD_KEYPTR(hh, env, v->name, strlen(v->name), v);
    }
}

ASTNode_t *getvar(const char *name, int line, int col) {
	if(strcmp(name,"")) return NULL;
	VarEntry *v;
    HASH_FIND_STR(env, name, v);
    if (v == NULL) {
        printf("Error [%d:%d]: variable '%s' not defined\n", line, col, name);
		exit(-1);
        return NULL;
    }
    return v->node;
}

/* ================= ASSIGN ================= */

static double eval_assign(ASTNode_t *lhs, ASTNode_t *rhs, OP_kind_t op, int line, int col) {
    if (!lhs || lhs->kind != AST_VAR) {
        printf("Error [%d:%d]: assignment target must be a variable\n", line, col);
		exit(-1);
    }

    double r = ast_eval(rhs);
    double cur = getvar(lhs->var, line, col)->num;
    double v = 0;

    if ((op == OP_DIV_ASSIGN || op == OP_MOD_ASSIGN) && fabs(r) < 1e-12) {
        printf("Error [%d:%d]: division by zero\n", line, col);
		exit(-1);
    }

    switch (op) {
        case OP_ASSIGN: v = r; break;
        case OP_PLUS_ASSIGN: v = cur + r; break;
        case OP_MINUS_ASSIGN: v = cur - r; break;
        case OP_MUL_ASSIGN: v = cur * r; break;
        case OP_DIV_ASSIGN: v = cur / r; break;
        case OP_MOD_ASSIGN: v = fmod(cur, r); break;
        case OP_POW_ASSIGN: v = pow(cur, r); break;
        case OP_LSHIFT_ASSIGN: v = (int)cur << (int)r; break;
        case OP_RSHIFT_ASSIGN: v = (int)cur >> (int)r; break;
        default:
            printf("Error: unknown assignment operator\n");
			exit(-1);
    }

    set_var(lhs->var, new_num(v, lhs->line, lhs->col));
    return v;
}

/* ================= EVAL ================= */
double ast_eval(ASTNode_t *node) {
    if (!node) return 0;

    switch (node->kind) {

    case AST_NUM: return node->num;

    case AST_VAR: return getvar(node->var, node->line, node->col)->num;

    case AST_BINOP: {
        /* short-circuit handled only for logical ops */
        if (node->bin.op == OP_AND) {
            double l = ast_eval(node->bin.left);
            if (!l) return 0;
            return ast_eval(node->bin.right) != 0;
        }

        if (node->bin.op == OP_OR) {
            double l = ast_eval(node->bin.left);
            if (l) return 1;
            return ast_eval(node->bin.right) != 0;
        }

        double l = ast_eval(node->bin.left);
        double r = ast_eval(node->bin.right);

        switch (node->bin.op) {
        case OP_ADD: return l + r;
        case OP_SUB: return l - r;
        case OP_MUL: return l * r;

        case OP_DIV:
            if (fabs(r) < 1e-12) {
                fprintf(stderr, "Error [%d:%d]: division by zero\n",
                        node->line, node->col);
                exit(-1);
            }
            return l / r;

        case OP_MOD:
       		if (fabs(r) < 1e-12) {
       			fprintf(stderr, "Error [%d:%d]: division",
       				node->line, node->col);
       			exit(-1);
       			return -1;
        	}
            return fmod(l, r);

        case OP_POW:
            return pow(l, r);

        case OP_LSHIFT:
            return (int)l << (int)r;

        case OP_RSHIFT:
            return (int)l >> (int)r;

        case OP_BITAND:
            return (int)l & (int)r;

        case OP_BITOR:
            return (int)l | (int)r;

        case OP_BITXOR:
            return (int)l ^ (int)r;

        case OP_EQ:  return l == r;
        case OP_NEQ: return l != r;
        case OP_LT:  return l < r;
        case OP_LE:  return l <= r;
        case OP_GT:  return l > r;
        case OP_GE:  return l >= r;

        default:
            /* assignment-like ops routed here */
            return eval_assign(node->bin.left,
                               node->bin.right,
                               node->bin.op,
                               node->line,
                               node->col);
        }
    }

    case AST_UNOP: {
        double v = ast_eval(node->unop.operand);

        switch (node->unop.op) {
        case OP_NEG:    return -v;
        case OP_POS:    return v;
        case OP_NOT:    return !v;
        case OP_BITNOT: return ~(int)v;

        case OP_INC:
            return eval_assign(node->unop.operand,
                               new_num(1, 0, 0),
                               OP_PLUS_ASSIGN,
                               node->line,
                               node->col);

        case OP_DEC:
            return eval_assign(node->unop.operand,
                               new_num(1, 0, 0),
                               OP_MINUS_ASSIGN,
                               node->line,
                               node->col);

        default:
            fprintf(stderr, "Error: unknown unary operator\n");
            exit(-1);
        }
    }

    case AST_ASSIGN: {
        ASTNode_t *lhs = node->assign.lhs;
        ASTNode_t *rhs = node->assign.rhs;

        if (!lhs || lhs->kind != AST_VAR) {
            fprintf(stderr, "Error [%d:%d]: invalid assignment target\n",
                    node->line, node->col);
            return 0;
        }

        double r = ast_eval(rhs);
        double cur = 0;

        if (node->assign.op != OP_ASSIGN) {
            ASTNode_t *v = getvar(lhs->var, node->line, node->col);
            if (!v) return -1;
            cur = v->num;
        }

        double result;
        switch (node->assign.op) {
        case OP_ASSIGN:        result = r; break;
        case OP_PLUS_ASSIGN:  result = cur + r; break;
        case OP_MINUS_ASSIGN: result = cur - r; break;
        case OP_MUL_ASSIGN:   result = cur * r; break;

        case OP_DIV_ASSIGN:
            if (fabs(r) < 1e-12) {
                fprintf(stderr, "Error [%d:%d]: division by zero\n",
                        node->line, node->col);
                return 0;
            }
            result = cur / r;
            break;

        default:
            fprintf(stderr, "Error: unsupported assignment operator\n");
            return 0;
        }

        set_var(lhs->var, new_num(result, lhs->line, lhs->col));
        return result;
    }

    case AST_SEQ:
        ast_eval(node->seq.a);
        return ast_eval(node->seq.b);

    case NODE_IF:
        if (ast_eval(node->ifnode.cond))
            return ast_eval(node->ifnode.then_branch);
        if (node->ifnode.else_branch)
            return ast_eval(node->ifnode.else_branch);
        return 0;

    default:
        fprintf(stderr, "Error: unknown AST node\n");
        return 0;
    }
}
