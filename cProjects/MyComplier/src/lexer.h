#ifndef LEXER_H
#define LEXER_H

#include <stddef.h>

typedef enum {
    TOKEN_IDENTIFIER,
    TOKEN_NUMBER,
    TOKEN_STRING,
    TOKEN_CHAR,
    TOKEN_KEYWORD,
    TOKEN_OPERATOR,
    TOKEN_EOF,
    TOKEN_ARROW
} TokenType;

typedef enum {
    KEYWORD_FUNC,
    KEYWORD_RETURN,
    KEYWORD_IF,
    KEYWORD_ELSE,
    KEYWORD_WHILE,
    KEYWORD_FOR,
    KEYWORD_VOID,
    KEYWORD_INT,
    KEYWORD_CHAR,
    KEYWORD_FLOAT,
    KEYWORD_DOUBLE,
    KEYWORD_VAR // Added a generic variable declaration keyword
} KeywordType;

typedef struct {
    TokenType type;

    union {
        char   *identifier;   // identifiers, strings
        int     number;       // integers
        char    character;    // char literals
        KeywordType keyword;  // keywords
    } value;

    size_t line;
    size_t column;
} Token;

#endif
