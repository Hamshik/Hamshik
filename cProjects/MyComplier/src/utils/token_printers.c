#include "../lexer/token.h"
#include <stdio.h>
void handle_kws(KeywordType kw) {
    switch (kw) {
        case KEYWORD_IF: printf("Token: KEYWORD_IF\n"); break;
        case KEYWORD_ELSE: printf("Token: KEYWORD_ELSE\n"); break;
        case KEYWORD_VOID: printf("Token: KEYWORD_VOID\n"); break;
        case KEYWORD_INT: printf("Token: KEYWORD_INT\n"); break;
        case KEYWORD_CHAR: printf("Token: KEYWORD_CHAR\n"); break;
        case KEYWORD_DOUBLE: printf("Token: KEYWORD_DOUBLE\n"); break;
        case KEYWORD_WHILE: printf("Token: KEYWORD_WHILE\n"); break;
        case KEYWORD_FOR: printf("Token: KEYWORD_FOR\n"); break;
        case KEYWORD_VAR: printf("Token: KEYWORD_VAR\n"); break;
        case KEYWORD_RETURN: printf("Token: KEYWORD_RETURN\n"); break;
        case KEYWORD_FLOAT: printf("Token: KEYWORD_FLOAT\n"); break;
        case KEYWORD_FUNC: printf("Token: KEYWORD_FUNC\n"); break;
        case KEYWORD_NONE: printf("Token: KEYWORD_NONE\n"); break;
        case KEYWORD_ENUM: printf("Token: KEYWORD_ENUM\n"); break;
        case KEYWORD_STRUCT: printf("Token: KEYWORD_STRUCT\n"); break;
        default: printf("Token: UNKNOWN_KEYWORD\n"); break;
    }
}
void printtok(Token token) {
    switch (token.type)
    {
    case TOKEN_LPAREN: printf("Token: LPAREN, Line: %d, Column: %d\n", token.line, token.column); break;
    case TOKEN_RPAREN: printf("Token: RPAREN, Line: %d, Column: %d\n", token.line, token.column); break;
    case TOKEN_LBRACE: printf("Token: LBRACE, Line: %d, Column: %d\n", token.line, token.column); break;
    case TOKEN_RBRACE: printf("Token: RBRACE, Line: %d, Column: %d\n", token.line, token.column); break;
    case TOKEN_LBRACKET: printf("Token: LBRACKET, Line: %d, Column: %d\n", token.line, token.column); break;
    case TOKEN_RBRACKET: printf("Token: RBRACKET, Line: %d, Column: %d\n", token.line, token.column); break;
    case TOKEN_SEMICOLON: printf("Token: SEMICOLON, Line: %d, Column: %d\n", token.line, token.column); break;
    case TOKEN_COMMA: printf("Token: COMMA, Line: %d, Column: %d\n", token.line, token.column); break;
    case TOKEN_COLON : printf("Token : COLON , Line :% d , Column :% d \n" ,token . line ,token . column ) ;break ;
    case TOKEN_TERNARY: printf("Token: TERNARY, Line: %d, Column: %d\n", token.line, token.column); break;
    case TOKEN_PLUS: printf("Token: PLUS, Line: %d, Column: %d\n", token.line, token.column); break;
    case TOKEN_MINUS: printf("Token: MINUS, Line: %d, Column: %d\n", token.line, token.column); break;
    case TOKEN_STAR: printf("Token: STAR, Line: %d, Column: %d\n", token.line, token.column); break;
    case TOKEN_DIVIDE: printf("Token: DIVIDE, Line: %d, Column: %d\n", token.line, token.column); break;
    case TOKEN_MOD: printf("Token: MOD, Line: %d, Column: %d\n", token.line, token.column); break;
    case TOKEN_POWER: printf("Token: POWER, Line: %d, Column: %d\n", token.line, token.column); break;
    case TOKEN_ASSIGN: printf("Token: ASSIGN, Line: %d, Column: %d\n", token.line, token.column); break;    
    case TOKEN_PLUS_ASSIGN: printf("Token: PLUS_ASSIGN, Line: %d, Column: %d\n", token.line, token.column); break;
    case TOKEN_MINUS_ASSIGN: printf("Token: MINUS_ASSIGN, Line: %d, Column: %d\n", token.line, token.column); break;
    case TOKEN_MUL_ASSIGN: printf("Token: MUL_ASSIGN, Line: %d, Column: %d\n", token.line, token.column); break;
    case TOKEN_DIV_ASSIGN: printf("Token: DIV_ASSIGN, Line: %d, Column: %d\n", token.line, token.column); break;
    case TOKEN_MOD_ASSIGN: printf("Token: MOD_ASSIGN, Line: %d, Column: %d\n", token.line, token.column); break;
    case TOKEN_INC: printf("Token: INC, Line: %d, Column: %d\n", token.line, token.column); break;
    case TOKEN_DEC: printf("Token: DEC, Line: %d, Column: %d\n", token.line, token.column); break;
    case TOKEN_EQ: printf("Token: EQ, Line: %d, Column: %d\n", token.line, token.column); break;
    case TOKEN_NEQ: printf("Token: NEQ, Line: %d, Column: %d\n", token.line, token.column); break;
    case TOKEN_LT: printf("Token: LT, Line: %d, Column: %d\n", token.line, token.column); break;
    case TOKEN_LTE: printf("Token: LTE, Line: %d, Column: %d\n", token.line, token.column); break;
    case TOKEN_GT: printf("Token: GT, Line: %d, Column: %d\n", token.line, token.column); break;
    case TOKEN_GTE: printf("Token: GTE, Line: %d, Column: %d\n", token.line, token.column); break;
    case TOKEN_SHL: printf("Token: SHL, Line: %d, Column: %d\n", token.line, token.column); break;
    case TOKEN_SHR: printf("Token: SHR, Line: %d, Column: %d\n", token.line, token.column); break;
    case TOKEN_SHL_ASSIGN: printf("Token: SHL_ASSIGN, Line: %d, Column: %d\n", token.line, token.column); break;
    case TOKEN_SHR_ASSIGN: printf("Token: SHR_ASSIGN, Line: %d, Column: %d\n", token.line, token.column); break;
    case TOKEN_BITNOT: printf("Token: BITNOT, Line: %d, Column: %d\n", token.line, token.column); break;
    case TOKEN_BITAND: printf("Token: BITAND, Line: %d, Column: %d\n", token.line, token.column); break;
    case TOKEN_BITOR: printf("Token: BITOR, Line: %d, Column: %d\n", token.line, token.column); break;
    case TOKEN_BITXOR: printf("Token: BITXOR, Line: %d, Column: %d\n", token.line, token.column); break;
    case TOKEN_BITAND_ASSIGN: printf("Token: BITAND_ASSIGN, Line: %d, Column: %d\n", token.line, token.column); break;
    case TOKEN_BITOR_ASSIGN: printf("Token: BITOR_ASSIGN, Line: %d, Column: %d\n", token.line, token.column); break;
    case TOKEN_BITXOR_ASSIGN: printf("Token: BITXOR_ASSIGN, Line: %d, Column: %d\n", token.line, token.column); break;
    case TOKEN_NOT: printf("Token: NOT, Line: %d, Column: %d\n", token.line, token.column); break;
    case TOKEN_ARROW: printf("Token: ARROW, Line: %d, Column: %d\n", token.line, token.column); break;
    case TOKEN_DOT: printf("Token: DOT, Line: %d, Column: %d\n", token.line, token.column); break;  
    case TOKEN_CHAR: printf("Token: CHAR, Value: '%c', Line: %d, Column: %d\n", token.value.character, token.line, token.column); break;
    case TOKEN_IDENTIFIER: printf("Token: IDENTIFIER, Value: %s, Line: %d, Column: %d\n", token.value.id, token.line, token.column); break;
    case TOKEN_NUMBER: printf("Token: NUMBER, Value: %.s, Line: %d, Column: %d\n", token.value.id, token.line, token.column); break;
    case TOKEN_KW: handle_kws(token.value.kw); break;
    case TOKEN_STRING: printf("Token: STRING, Value: %s, Line: %d, Column: %d\n", token.value.id, token.line, token.column); break;
    case TOKEN_EOF: printf("Token: EOF, Line: %d, Column: %d\n", token.line, token.column); break;
    case TOKEN_INVALID: printf("Token: INVALID, Line: %d, Column: %d\n", token.line, token.column); break;
    case TOKEN_AND: printf("Token: AND, Line: %d, Column: %d\n", token.line, token.column); break;
    case TOKEN_OR: printf("Token: OR, Line: %d, Column: %d\n", token.line, token.column); break;

    default:
        printf("Token: %d, Line: %d, Column: %d\n", token.type, token.line, token.column);
        break;
    }
}