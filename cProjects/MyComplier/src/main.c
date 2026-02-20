#include <stdio.h>
// #include <string.h>
// #include <stdlib.h>
// #include <ctype.h>
// #include <stdbool.h>
// #include <limits.h>
// #include <unistd.h>
// #include <linux/limits.h>
// #include "parser/parser.h"

// #define MAX_LINE_LEN 1024
// char current_line[MAX_LINE_LEN];
// extern YY_BUFFER_STATE yy_scan_bytes(const char *bytes, int len);
// int yyparse();
// int yy_delete_buffer(YY_BUFFER_STATE buf);

// char* resolve_path(const char* input) {
//     return realpath(input, NULL); // returns malloc'd absolute path
// }

// char* load_file(char* PATH){
//     FILE *f = fopen(PATH, "rb");
//     if (!f) { perror("file open failed"); exit(1); }
//     fseek(f, 0, SEEK_END);
//     size_t size = ftell(f);
//     char* sourceCode = calloc(sizeof(char *), size);
//     fread(sourceCode,1, size,f);
//     rewind(f);
//     fclose(f);
//     return sourceCode;
// }

int main(int argc, char **argv) {
    // while (fgets(current_line, sizeof(current_line), stdin)) {
    //     yy_scan_string(current_line); // tell lexer to scan this line
    //     yyparse();
    // }
    int y = 0;
    int x = y++ 2;
    printf("x: %d, y: %d\n", x, y);
    return 0;

}