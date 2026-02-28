#include <stdio.h>
#include "ast/ASTNode.h"
#include "parser/parser.h"

int main() {
    yyparse();
    if (root) {
        Value r = ast_eval(root);
        printf("Program result: %g\n", r.lfnum);
        ast_free(root);
    }
    return 0;
}