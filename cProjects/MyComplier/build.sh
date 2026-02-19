#!/usr/bin/env bash
set -e

echo "============================"
echo " Building MyCompiler"
echo "============================"

SRC=src
BUILD=build

echo "[1/3] Running Bison..."
bison -d "$SRC/parser/parser.y" -o "$SRC/parser/parser.c"
echo "Bison completed successfully."

echo "[2/3] Running flex..."
flex -o $SRC/lexer/lexer.c $SRC/lexer/lexer.l
echo "Flex completed successfully."

echo "[3/3] Compiling..."
gcc -Isrc "$SRC/parser/parser.c" "$SRC/parser/ASTNode.c" "$SRC/lexer/lexer.c" -o "MyComplier" -lm
echo "Complied successfully."

echo "============================"
echo " Build successful! 🎉"
echo " Output: $BIN/MyComplier"
echo "============================"
