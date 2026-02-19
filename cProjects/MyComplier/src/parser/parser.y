%code requires {
#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include "ASTNode.h"
}
%{%}

%define api.pure full
%define parse.error verbose
%locations
%union {
    double nval;
    ASTNode_t *node;
}
%code {
ASTNode_t *root;
int yylex(YYSTYPE *yylval, YYLTYPE *yylloc);
void yyerror(YYLTYPE *loc, const char *s);
}
%token <nval> NUMBER
%token PLUS MINUS STAR SLASH MOD POWER LPAREN RPAREN

%type <node> expr

%right POWER        /* ^ binds tightest, right-assoc */
%left MOD           /* % comes after *, / */
%left STAR SLASH    /* *, / */
%left PLUS MINUS    /* +, - */

%%

program:
      /* empty */
    | program line
    ;

line:
    '\n' |  expr '\n'         {root = $1; printf("= %lf\n", ast_eval(root));ast_free(root);}
    ;

expr:
      NUMBER             { $$ = new_num($1, @1.first_line, @1.first_column); }
    | expr PLUS expr     { $$ = new_binop($1, $3, @$.first_line, @$.first_column, OP_ADD); }
    | expr MINUS expr    { $$ = new_binop($1, $3, @$.first_line, @$.first_column, OP_SUB); }
    | expr STAR expr     { $$ = new_binop($1, $3, @$.first_line, @$.first_column, OP_MUL); }
    | expr SLASH expr
    {
        if(fabs($3->num) < 1e-12) yyerror(&@$, "Division by zero");
        else $$ = new_binop($1, $3, @$.first_line, @$.first_column, OP_MOD);

    }
    | expr MOD expr
    {
        if(fabs($3->num) < 1e-12) yyerror(&@$, "Division by zero");
        else $$ = new_binop($1, $3, @$.first_line, @$.first_column, OP_MOD);
    }
    | expr POWER expr    { $$ = new_binop($1, $3, @$.first_line, @$.first_column, OP_POW); }
    | LPAREN expr RPAREN { $$ = $2; }
    ;
%%

int main(void) {
    yyparse();
    return 0;
}
void yyerror(YYLTYPE *loc, const char *s) {
    fprintf(stderr, "Error at %d:%d: %s\n",
            loc->first_line, loc->first_column, s);
}
