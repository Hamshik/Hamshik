#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <ctype.h>
#include <stdbool.h>
#include "lexer.c"

int main(int argc, char *argv[]) {

    if (argc < 1) {
        printf("Usage: %s <source_file>\n", argv[0]);
        return 1;
    }
    lexer_start(argv[1]);
    return 0;
}
