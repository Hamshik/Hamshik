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

ASTNode_t* new_num(char *rawval, DataTypes_t datatype, int line, int col) {
    ASTNode_t *node = ast_alloc();
    node->kind = AST_NUM;
    node->datatype = datatype;
    node->line = line;
    node->col = col;

    node->literal.raw = *rawval;   // copy value
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

ASTNode_t* new_assign(ASTNode_t *lhs, ASTNode_t *rhs, DataTypes_t datatype,OP_kind_t op, int line, int col) {
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
        assign_value(datatype, &v->val, *val);
    else {
        v = malloc(sizeof(*v));
        v->name = strdup(name);
        v->datatype = datatype;
        assign_value(datatype, &v->val, *val);
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

void assign_value(DataTypes_t dt, Value *dst, Value src) {
    switch (dt) {
        case INT:    dst->inum = src.inum; break;
        case FLOAT:  dst->fnum = src.fnum; break;
        case DOUBLE: dst->lfnum = src.lfnum; break;
        case SHORT:  dst->shnum = src.shnum; break;
        case BOOL:   dst->bval = src.bval; break;
        case STRINGS:
            free(dst->str);
            dst->str = strdup(src.str);
            break;
        case CHARACTER:
            dst->characters = src.characters;
            break;
        default:
            fprintf(stderr, "Invalid assignment type\n");
            exit(1);
    }
}

Value eval_assign(ASTNode_t *lhs, ASTNode_t *rhs, OP_kind_t op, DataTypes_t datatypes , 
    int line, int col) {
    if (!lhs || lhs->kind != AST_VAR) {
        printf("Error [%d:%d]: assignment target must be a variable\n", line, col);
		exit(-1);
    }

    Value r = ast_eval(rhs);
    Value cur = getvar(lhs->var, lhs->datatype, line, col);
    Value v = {0};
    OP_kind_t operation = get_assign_op(op);
    switch (datatypes) {
        case INT:
            v = eval_binop_int(operation, false, r.inum, cur.inum);
            break;
        case FLOAT:
            v = eval_binop_float(operation, r.fnum, cur.fnum);
            break;
        case DOUBLE:
            v = eval_binop_double(operation, r.lfnum, cur.lfnum);
            break;
        case SHORT:
            v = eval_binop_int(operation, true, r.shnum, cur.shnum);
            break;
        case BOOL:
            v = eval_bool(operation, r.bval, cur.bval);
            break;
        case STRINGS:
            do_operation_str(v.str, r.str, cur.str, operation);
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
    Value v;


    switch (node->kind) {

    case AST_NUM: return node->val;

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
        do_unop_operation(&node->val, &r , node->datatype, node->unop.op);
        return node->val;
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
