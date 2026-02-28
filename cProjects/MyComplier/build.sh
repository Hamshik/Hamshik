#!/bin/bash

PARSER_DIR="src/parser"
LEXER_DIR="src/lexer"
AST_DIR="src/ast"

echo "Running bison to generate the parser..."
bison -d -o "$PARSER_DIR/parser.c" "$PARSER_DIR/parser.y" || exit 1

echo "Running flex to generate the lexer..."
flex -o "$LEXER_DIR/lexer.c" "$LEXER_DIR/lexer.l" || exit 1

echo "[3/3] Compiling..."
gcc -Wall -Wextra -g -Isrc \
    "$PARSER_DIR/parser.c" \
    "$AST_DIR/ASTNode.c" \
    "$LEXER_DIR/lexer.c" \
    "$AST_DIR/eval.c" \
    src/main.c \
    -o MyCompiler -lm || exit 1

echo "Compiled successfully."