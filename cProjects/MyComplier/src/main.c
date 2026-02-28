#include <stdio.h>
#include "ast/ASTNode.h"
#include "parser/parser.h"

int main() {
    yyparse();
    if (root) {
        /* run semantic analysis before evaluation; this will
           annotate the tree with concrete datatypes and detect
           mismatches/undeclared identifiers. */
        sema_check(root);

        Value r = ast_eval(root);
        printf("Program result: %g\n", r.lfnum);
        ast_free(root);
    }
    return 0;
}