#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <string.h>
#include "ASTNode.h"

ASTNode_t* new_num(double val, int line, int col)
{
    ASTNode_t *node = ast_alloc();
    node->kind = AST_NUM;
    node->num = val;
    node->line = line;
    node->col = col;
    return node;
}

ASTNode_t* new_var(const char *name, int col, int line)
{
    ASTNode_t *node = ast_alloc();
    node->kind = AST_VAR;
    node->var = malloc(strlen(name) + 1);
    strcpy(node->var, name);
    node->line = line;
    node->col = col;
    return node;
}

ASTNode_t* new_binop(ASTNode_t *left, ASTNode_t *right, int line, int col, OP_kind_t op)
{
    ASTNode_t *node = ast_alloc();

    node->kind = AST_BINOP;
    node->bin.op = op;
    node->bin.left = left;
    node->bin.right = right;
    node->line = line;
    node->col = col;
    return node;
}

void ast_free(ASTNode_t *n) {
    if (!n) return;

    switch (n->kind) {
    case AST_VAR:
        free(n->var);
        break;
    case AST_BINOP:
        ast_free(n->bin.left);
        ast_free(n->bin.right);
        break;
    default:
        break;
    }

    free(n);
}

ASTNode_t* ast_alloc(void) {
    ASTNode_t *n = malloc(sizeof(ASTNode_t)); // zero init
    if (!n) { perror("malloc is failed"); exit(1); }
    return n;
}

double ast_eval(ASTNode_t *node) {
    if (!node) return 0.0;

    switch (node->kind) {

    case AST_NUM:
        return node->num;

    case AST_BINOP: {
        switch (node->bin.op) {
            case OP_ADD: return ast_eval(node->bin.left) + ast_eval(node->bin.right);
            case OP_SUB: return ast_eval(node->bin.left) - ast_eval(node->bin.right);
            case OP_MUL: return ast_eval(node->bin.left) * ast_eval(node->bin.right);
            case OP_DIV: return ast_eval(node->bin.left) / ast_eval(node->bin.right);
            case OP_MOD: return fmod(ast_eval(node->bin.left), ast_eval(node->bin.right));
            case OP_POW: return pow(ast_eval(node->bin.left), ast_eval(node->bin.right));
            default:
                printf("UNKNOWN OP CODE\n");
                return 0.0;
        }
    }

    case AST_VAR:
        printf("Cannot eval variable without environment\n");
        return 0.0;

    default:
        printf("Unknown AST node kind\n");
        return 0.0;
    }
}
