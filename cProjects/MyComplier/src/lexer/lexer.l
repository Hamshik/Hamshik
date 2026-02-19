%option noyywrap
%option bison-locations

%{
#include "../parser/parser.h"
#include <stdlib.h>
%}

%%
\n                  {return '\n';}
[0-9]+(\.[0-9]+)?   { yylval->nval = atof(yytext); return NUMBER; }
"+"                 { return PLUS; }
"-"                 { return MINUS; }
"*"                 { return STAR; }
"/"                 { return SLASH; }
"%"                 { return MOD; }
"**"                { return POWER; }
"("                 { return LPAREN; }
")"                 { return RPAREN; }
[ \t\r]+            { /* skip */ }
.                   { printf("Unknown char: %s\n", yytext); }
<<EOF>>             { return 0; }
%%
