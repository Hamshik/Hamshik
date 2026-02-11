#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <ctype.h>
#include <stdbool.h>
#include "lexer.h"

typedef struct {
    char *source;
    size_t position;
    char current_char;

    size_t line;
    size_t column;

    Token current_token;
} Lexer;

void lexer_init(Lexer *lexer);
void lexer_advance(Lexer *lexer);
char lexer_peek(Lexer *lexer);
char* lexer_handle_str(Lexer *lexer);
char lexer_handle_escape_sequence(Lexer *lexer);
char* handle_identity(Lexer *lexer);
bool isSymbolBracets(char c, Lexer *lexer);
void extract_keyword(char* id);
char* handle_number(Lexer *lexer);
void handle_MultiCommet(Lexer *lexer);
void handle_SingleComment(Lexer *lexer); 
Token make_token(TokenType type, size_t line, size_t column);
Token next_token(Lexer *lexer);


void lexer_start(const char* PATH){
    Lexer lexer = {0};

    FILE *f = fopen(PATH, "rb");
    if (!f) {
        perror("file open failed");
        exit(1);
    }

    fseek(f, 0, SEEK_END);
    long size = ftell(f);
    rewind(f);

    lexer.source = malloc(size + 1);
    fread(lexer.source, 1, size, f);
    lexer.source[size] = '\0';

    lexer_init(&lexer);

    printf("source code:\n%s\n\n", lexer.source);

    Token token;

    do {
        token = next_token(&lexer);

        printf("Token type: %d at line %zu\n", token.type, token.line);

    } while (token.type != TOKEN_EOF);

    fclose(f);
    free(lexer.source);
}

void lexer_init(Lexer *lexer) {
    lexer->position = 0;
    lexer->current_char = lexer->source[0];
    lexer->line = 1;
    lexer->column = 1;
}

void lexer_advance(Lexer *lexer) {
    if (lexer->current_char == '\n') {
        lexer->line++;
        lexer->column = 0;
    } else {
        lexer->column++;
    }

    lexer->position++;
    lexer->current_char = lexer->source[lexer->position];
}

char lexer_peek(Lexer *lexer) {
    if (lexer->position + 1 < strlen(lexer->source)) {
        return lexer->source[lexer->position + 1];
    } else {
        return '\0';
    }
}

char* lexer_handle_str(Lexer *lexer){
    lexer_advance(lexer); // Skip the opening quote
    int capacity = 256;
    char* buffer = malloc(capacity);
    int index = 0;
    bool isRunning = true;
    while (lexer->current_char != '"' && lexer->current_char != '\0') {

        if (lexer->current_char == '\\') {
            buffer[index++] = lexer_handle_escape_sequence(lexer);
        } else {
            buffer[index++] = lexer->current_char;
            lexer_advance(lexer);
        }

        if (index >= capacity - 1) {
            capacity *= 2;
            buffer = realloc(buffer, capacity);
        }
    }
    buffer[index] = '\0';
    lexer_advance(lexer); // Skip the closing quote
    return buffer;
}

char lexer_handle_escape_sequence(Lexer *lexer) {
    lexer_advance(lexer); // move to escape character
    char result;

    switch (lexer->current_char) {
        case 'n':  result = '\n'; break;
        case 't':  result = '\t'; break;
        case '\\': result = '\\'; break;
        case '"':  result = '"'; break;
        default:
            fprintf(stderr, "Error: Invalid escape sequence \\%c at line %zu, column %zu\n",
                    lexer->current_char, lexer->line, lexer->column);
            exit(1);
    }

    lexer_advance(lexer); // move past escape character
    return result;
}

char*handle_identity(Lexer *lexer) {
    int capacity = 10;
    char *ident = malloc(capacity);
    int index = 0;
    char current_char = lexer->current_char;
    while(!isspace(current_char) && !isSymbolBracets(current_char, lexer)) {
        ident[index++] = current_char;
        lexer_advance(lexer);
        current_char = lexer->current_char;

        if (index >= capacity - 1) {
            capacity += 5;
            ident = realloc(ident, capacity);
        }
    }
    ident[index] = '\0';
    if(isspace(lexer->current_char)) lexer_advance(lexer); // Skip the whitespace after the identifier
    return ident;
}

bool isSymbolBracets(char c, Lexer *lexer){
    return c == '(' || c == ')' || c == '{' || c == '}' || c == '[' || c == ']' ||
           c == ';' || c == ',' || c == '.' || c == '_' || (c == '-' && lexer_peek(lexer) != '>');
}

void extract_keyword(char* id) {
    
    if (strcmp(id, "if") == 0) {
        printf("keyword: %s\n", id);
        return;
    }else if (strcmp(id, "else") == 0) {
        printf("keyword: %s\n", id);
        return;
    }else if (strcmp(id, "while") == 0) {
        printf("keyword: %s\n", id);
        return;
    }else if (strcmp(id, "for") == 0) {
        printf("keyword: %s\n", id);
        return;
    }else if (strcmp(id, "return") == 0) {
        printf("keyword: %s\n", id);
        return;
    }else if (strcmp(id, "int") == 0) {
        printf("keyword: %s\n", id);
        return;
    }else if (strcmp(id, "char") == 0) {
        printf("keyword: %s\n", id);
        return;
    }else if (strcmp(id, "void") == 0) {
        printf("keyword: %s\n", id);
        return;
    }else if(strcmp(id, "float") == 0) {
        printf("keyword: %s\n", id);
        return;
    }else if(strcmp(id, "double") == 0) {
        printf("keyword: %s\n", id);
        return;
    }
    else {
        printf("Extracted identifier: %s\n", id);
        return;
    }
}

char* handle_number(Lexer *lexer) {
    int capacity = 10;
    char *number = malloc(capacity);
    int index = 0;
    char current_char = lexer->current_char;
    while(isdigit(current_char)) {
        number[index++] = current_char;
        lexer_advance(lexer);
        current_char = lexer->current_char;

        if (index >= capacity - 1) {
            capacity += 5;
            number = realloc(number, capacity);
        }
    }
    number[index] = '\0';
    return number;
}

void handle_MultiCommet(Lexer *lexer) {
    lexer_advance(lexer); // Skip the first '*'
    while (lexer->current_char != '\0') {
        if (lexer->current_char == '*' && lexer_peek(lexer) == '/') {
            lexer_advance(lexer); // Skip the '*'
            lexer_advance(lexer); // Skip the '/'
            return;
        }
        lexer_advance(lexer);
    }
    fprintf(stderr, "Error: Unterminated multi-line comment at line %zu, column %zu\n",
            lexer->line, lexer->column);
    exit(1);
}

void handle_SingleComment(Lexer *lexer) {

    while (lexer->current_char != '\n' && lexer->current_char != '\0') {
        lexer_advance(lexer);
    }
}

bool is_keyword(const char* id) {
    return strcmp(id, "if") == 0 || strcmp(id, "else") == 0 || strcmp(id, "while") == 0 ||
           strcmp(id, "for") == 0 || strcmp(id, "return") == 0 || strcmp(id, "int") == 0 ||
           strcmp(id, "char") == 0 || strcmp(id, "void") == 0 || strcmp(id, "float") == 0 ||
           strcmp(id, "double") == 0 || strcmp(id, "var") == 0 || strcmp(id, "func") == 0 || strcmp(id, "string") == 0;
}

KeywordType get_keyword_type(const char* id) {
    if (strcmp(id, "if") == 0) return KEYWORD_IF;
    if (strcmp(id, "else") == 0) return KEYWORD_ELSE;
    if (strcmp(id, "while") == 0) return KEYWORD_WHILE;
    if (strcmp(id, "for") == 0) return KEYWORD_FOR;
    if (strcmp(id, "return") == 0) return KEYWORD_RETURN;
    if (strcmp(id, "int") == 0) return KEYWORD_INT;
    if (strcmp(id, "char") == 0) return TOKEN_CHAR; // Assuming char is treated as a variable declaration
    if (strcmp(id, "void") == 0) return KEYWORD_VOID; // Assuming void is treated as a variable declaration
    if (strcmp(id, "float") == 0) return KEYWORD_FLOAT; // Assuming float is treated as a variable declaration
    if (strcmp(id, "double") == 0) return KEYWORD_DOUBLE; // Assuming double is treated as a variable declaration
    if (strcmp(id, "var") == 0) return KEYWORD_VAR; // Assuming var is treated as a generic variable declaration
    if (strcmp(id, "func") == 0) return KEYWORD_FUNC; // Assuming func is treated as a function declaration
    if (strcmp(id, "string") == 0) return TOKEN_STRING; // Assuming string is treated as a variable declaration
    return -1; // Not a keyword
}
Token make_token(TokenType type, size_t line, size_t column) {
    Token token;
    token.type = type;
    token.line = line;
    token.column = column;
    return token;
}

Token next_token(Lexer *lexer) {

    while (lexer->current_char != '\0') {

        // skip whitespace
        if (isspace(lexer->current_char)) {
            lexer_advance(lexer);
            continue;
        }

        // identifier or keyword
        if (isalpha(lexer->current_char) || lexer->current_char == '_') {
            size_t line = lexer->line;
            size_t column = lexer->column;

            char* id = handle_identity(lexer);

            Token token;

            if (is_keyword(id)) {
                token = make_token(TOKEN_KEYWORD, line, column);
                token.value.keyword = get_keyword_type(id);
                free(id);
            } else {
                token = make_token(TOKEN_IDENTIFIER, line, column);
                token.value.identifier = id;  // keep allocated
            }

            return token;
        }

        // numbers
        if (isdigit(lexer->current_char)) {
            size_t line = lexer->line;
            size_t column = lexer->column;

            int num = handle_number(lexer);

            Token token = make_token(TOKEN_NUMBER, line, column);
            token.value.number = num;
            return token;
        }

        // strings
        if (lexer->current_char == '"') {
            size_t line = lexer->line;
            size_t column = lexer->column;

            char* str = lexer_handle_str(lexer);

            Token token = make_token(TOKEN_STRING, line, column);
            token.value.identifier = str;
            return token;
        }

        // single char example
        if (lexer->current_char == '(') {
            lexer_advance(lexer);
            return make_token(TOKEN_LPAREN, lexer->line, lexer->column);
        }

        // unknown
        printf("Unknown character at line %zu\n", lexer->line);
        exit(1);
    }

    return make_token(TOKEN_EOF, lexer->line, lexer->column);
}

