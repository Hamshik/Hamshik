%option noyywrap
%option bison-location
%{
#include "../parser/parser.h"
#include "../ast/ASTNode.h"
#define YY_DECL int yylex(YYSTYPE *yylval, YYLTYPE *yylloc)
%}

%{
#include "../parser/parser.h"
#include "../ast/ASTNode.h"
#define YY_DECL int yylex(YYSTYPE *yylval, YYLTYPE *yylloc)
%}

%%
"//".*               { /* skip single-line comments */ }
"/*"([^*]|\*+[^*/])*\*+"/"   { /* skip multi-line comments */ }

[ \t\r\n]+            { /* skip */ }
if                  { return IF; }
else                { return ELSE; }
int                { yylval->datatype = INT; return DATATYPES; }
short                { yylval->datatype = SHORT; return DATATYPES; }
float                { yylval->datatype = FLOAT; return DATATYPES; }
double                { yylval->datatype = DOUBLE; return DATATYPES; }
[0-9]+(\.[0-9]+)? {
    node->literal.raw = strdup(yytext);
    return NUMBER;
}
"++"                { return INC; }
"--"                { return DEC; }
"+"                 { return PLUS; }
"-"                 { return MINUS; }
"*"                 { return STAR; }
"/"                 { return SLASH; }
"%"                 { return MOD; }
"**"                { return POWER; }
"("                 { return LPAREN; }
")"                 { return RPAREN; }
"="                 { return ASSIGN; }
"+="                { return PLUS_ASSIGN; }
"-="                { return MINUS_ASSIGN; }
"*="                { return STAR_ASSIGN; }
"/="                { return SLASH_ASSIGN; }
"%="                { return MOD_ASSIGN; }
"**="               { return POWER_ASSIGN; }
";"                 { return SEMICOLON; }
">>"                { return RSHIFT; }
"<<"                { return LSHIFT; }
"<<="               { return LSHIFT_ASSIGN; }
">>="               { return RSHIFT_ASSIGN; }
"&&"                { return AND; }
"||"                { return OR; }
"!"                 { return NOT; }
"&"                 { return BITAND; }
"|"                 { return BITOR; }
"^"                 { return BITXOR; }
"~"                 { return BITNOT; }
"=="                { return EQ; }
"!="                { return NEQ; }
"<="                { return LE; }
">="                { return GE; }
"<"                 { return LT; }
">"                 { return GT; }
"{"                 { return LBRACE; }
"}"                 { return RBRACE; }
[a-zA-Z_][a-zA-Z0-9_]* {
    yylval->node = new_var(yytext, INT, yylloc->first_line, yylloc->first_column);
    return IDENTIFIER;
}
.                   { printf("Unknown char: %s\n", yytext); }
<<EOF>>             { return 0;  }
%%
