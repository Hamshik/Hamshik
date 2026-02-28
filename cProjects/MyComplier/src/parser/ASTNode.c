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

ASTNode_t* new_num(Value *val, DataTypes_t datatype, int line, int col) {
    ASTNode_t *node = ast_alloc();
    node->kind = AST_NUM;
    assign_val(datatype,node->val, *val);
    node->datatype = datatype;
    node->line = line;
    node->col = col;
    if (datatype == STRINGS) { free(val->str); }
    free(val);
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
    switch (n->datatype){
        case STRINGS:free(n->val.str);break;
        default:break;
    }
    free(n);
}

/* ================= ENV ================= */

VarEntry *env = NULL;

void set_var(const char *name, Value *val, DataTypes_t datatype) {
    VarEntry *v;
    HASH_FIND_STR(env, name, v);
    if (v != NULL)
        assign_val(datatype, v->val, *val);
    else {
        v = malloc(sizeof(*v));
        v->name = strdup(name);
        v->datatype = datatype;
        assign_val(datatype, v->val, *val);
        HASH_ADD_KEYPTR(hh, env, v->name, strlen(v->name), v);
    }
}

Value getvar(const char *name, DataTypes_t datatype, int line, int col) {
	VarEntry *v;
    HASH_FIND_STR(env, name, v);
    if (v == NULL) {
        printf("Error [%d:%d]: variable '%s' not defined\n", line, col, name);
		exit(-1);
    }
    if(v->datatype != datatype) {
        printf("Error [%d:%d]: type mismatch for variable '%s'\n", line, col, name);
        exit(-1);
    }
    return v->val;
}

/* ================= ASSIGN ================= */

static Value eval_assign(ASTNode_t *lhs, ASTNode_t *rhs, OP_kind_t op, DataTypes_t datatypes , 
    int line, int col) {
    if (!lhs || lhs->kind != AST_VAR) {
        printf("Error [%d:%d]: assignment target must be a variable\n", line, col);
		exit(-1);
    }

    Value r = ast_eval(rhs);
    Value cur = getvar(lhs->var, lhs->datatype, line, col);
    Value v = {0};

    switch (datatypes) {
        case INT:
            do_assign_operation_num(v.inum, r.inum, cur.inum, op);
            break;
        case FLOAT:
            do_assign_operation_num(v.fnum, r.fnum, cur.fnum, op);
            break;
        case DOUBLE:
            do_assign_operation_num(v.lfnum, r.lfnum, cur.lfnum, op);
            break;
        case SHORT:
            do_assign_operation_num(v.shnum, r.shnum, cur.shnum, op);
            break;
        case BOOL:
            do_boolean_operation(v.bval, r.bval, cur.bval, op);
            break;
        case STRINGS:
            do_operation_str(v.str, r.str, cur.str, op);
            break;
        case CHARACTER:
            v.characters = r.characters;
            break;
        default:
            fprintf(stderr, "Error: unsupported data type for assignment\n");
            exit(EXIT_FAILURE);
    }

    set_var(lhs->var, &v, datatypes);
    return v;
}

/* ================= EVAL ================= */
Value ast_eval(ASTNode_t *node) {
    if (!node) return (Value){0};


    switch (node->kind) {

    case AST_NUM: return node->val;

    case AST_VAR: return getvar(node->var, node->datatype, node->line, node->col);

    case AST_BINOP:
        switch (node->datatype) {
            case INT:
                do_num_operation(node->val.inum, ast_eval(node->bin.left).inum, ast_eval(node->bin.right).inum, node->bin.op);
                break;
            case FLOAT:
                do_num_operation(node->val.fnum, ast_eval(node->bin.left).fnum, ast_eval(node->bin.right).fnum, node->bin.op);
                break;
            case DOUBLE:
                do_num_operation(node->val.lfnum, ast_eval(node->bin.left).lfnum, ast_eval(node->bin.right).lfnum, node->bin.op);
                break;
            case SHORT:
                do_num_operation(node->val.shnum, ast_eval(node->bin.left).shnum, ast_eval(node->bin.right).shnum, node->bin.op);
                break;
            default:
                fprintf(stderr, "Error: unsupported data type for binary operation\n");
                exit(EXIT_FAILURE);
        }
        return node->val;

    case AST_UNOP: {
        Value r = ast_eval(node->unop.operand);
        switch (node->datatype) {
            case INT:
                do_unop_operation(node->val.inum, r.inum, node->unop.op);
                break;
            case FLOAT:
                do_unop_operation(node->val.fnum, r.fnum, node->unop.op);
                break;
            case DOUBLE:
                do_unop_operation(node->val.lfnum, r.lfnum, node->unop.op);
                break;
            default:
                fprintf(stderr, "Error: unsupported unary op\n");
                exit(1);
        }
        return node->val;
    }

    case AST_ASSIGN: {
        Value val = eval_assign(node->assign.lhs, node->assign.rhs,
                            node->assign.op, node->datatype,
                            node->line, node->col);
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
        return (Value)NULL;

    default:
        fprintf(stderr, "Error: unknown AST node\n");
        exit(-1);
    }
}
