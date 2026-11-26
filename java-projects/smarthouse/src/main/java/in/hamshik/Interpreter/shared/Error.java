package in.hamshik.Interpreter.shared;

import in.hamshik.Interpreter.Lexer.LexHelper.Location;
import java.util.function.Consumer;

public class Error {
    private static final String RED = "\u001B[31m";
    private static final String BOLD = "\u001B[1m";
    private static final String DIM = "\u001B[2m";
    private static final String RESET = "\u001B[0m";

    private String filename = "<input>";
    private char[] source;
    private int errorCount;
    private Consumer<String> output;

    public enum ErrorCode {
        INTERNAL_ERROR(0),
        LEX_UNKNOWN_CHAR(1),
        LEX_INVALID_ESCAPE(2),
        LEX_UNCLOSED_COMMENT(3),
        PARSE_SYNTAX(100),
        PARSE_MISSING_SEMI(101),
        PARSE_UNCLOSED_PAREN(102),
        PARSE_UNCLOSED_BRACE(103),
        PARSE_UNCLOSED_BRACKET(104),
        PARSE_INVALID_IMPORT(105),
        PARSE_IMPORT_NOT_TOP(106),
        PARSE_INVALID_ARRAY_SIZE(107),
        SEM_VAR_UNDECL(1001),
        SEM_NUMOP_NEEDS_NUM(1002),
        SEM_UNARY_NEEDS_NUM(1003),
        SEM_ASSIGN_TARGET_NOT_VAR(1004),
        SEM_VAR_REDECL(1005),
        SEM_ASSIGN_TYPE_MISMATCH(1006),
        SEM_ASSIGN_IMMUTABLE(1007),
        SEM_CALL_UNDEF_FN(1008),
        SEM_ARGC_MISMATCH(1009),
        SEM_ARG_TYPE_MISMATCH(1010),
        SEM_RETURN_TYPE_MISMATCH(1011),
        SEM_IMPORT_FILE_NOT_FOUND(1012),
        SEM_LIST_SIZE_MISMATCH(1013),
        SEM_INDEX_NOT_ARRAY(1014),
        SEM_INDEX_NOT_INT(1015),
        SEM_OTHER_ERROR(1016),
        INVAILD_UTF8_CHAR(2000),
        RET_NOT_DECLARED(2001),
        NUM_LITERAL_UNSUPPORTED(2002);

        public final int code;

        ErrorCode(int code) {
            this.code = code;
        }
    }

    public Error(String filename, char[] source, Consumer<String> output) {
        this.filename = filename == null ? "<input>" : filename;
        this.source = source == null ? null : source.clone();
        this.output = output;
    }

    public void panic(Location loc, ErrorCode code, String detail) {
        panic(loc, code.code, detail);
    }

    public void panic(Location loc, int code, String detail) {
        errorCount++;
        String base = messageForCode(code);
        String safeDetail = detail == null ? "" : detail.trim();

        StringBuilder firstLine = new StringBuilder();
        if (!safeDetail.isEmpty()) {
            if (safeDetail.startsWith(base)) {
                firstLine.append(String.format("%serror[SA%04d]:%s %s",
                        BOLD + RED, code, RESET, safeDetail));
            } else {
                firstLine.append(String.format("%serror[SA%04d]: %s, %s%s",
                        BOLD + RED, code, base, safeDetail, RESET));
            }
        } else {
            firstLine.append(String.format("%serror[SA%04d]: %s%s",
                    BOLD + RED, code, base, RESET));
        }

        StringBuilder diagnostic = new StringBuilder(firstLine);
        diagnostic.append('\n').append(formatLocation(loc));
        write(diagnostic.toString());
        throw new PanicException(firstLine.toString().replaceAll("\u001B\\[[0;1;2]*m", ""));
    }

    public void panic(Location loc, String detail) {
        panic(loc, 0, detail);
    }

    private String formatLocation(Location loc) {
        StringBuilder result = new StringBuilder();
        if (source == null || source.length == 0 || loc == null) {
            int line = loc == null ? 1 : Math.max(loc.ln, 1);
            int column = loc == null ? 1 : Math.max(loc.col, 1);
            appendLocation(result, line, column);
            result.append(DIM).append(" note: could not read source to show caret").append(RESET);
            return result.toString();
        }

        int pos = loc.pos;
        if (pos < 0) pos = 0;
        if (pos >= source.length) pos = source.length - 1;

        int line = 1;
        int column = 1;
        for (int i = 0; i < pos; i++) {
            if (source[i] == '\n') {
                line++;
                column = 1;
            } else {
                column++;
            }
        }

        int lineStart = pos;
        while (lineStart > 0 && source[lineStart - 1] != '\n') lineStart--;
        int lineEnd = pos;
        while (lineEnd < source.length && source[lineEnd] != '\n' && source[lineEnd] != '\r') lineEnd++;

        int lineWidth = Integer.toString(line).length();
        result.append(DIM)
                .append(" ".repeat(lineWidth))
                .append(" |")
                .append(RESET)
                .append('\n');
        appendSourceLine(result, line, lineWidth, lineStart, lineEnd);

        StringBuilder padding = new StringBuilder();
        for (int i = lineStart; i < pos; i++) {
            padding.append(source[i] == '\t' ? '\t' : ' ');
        }

        int spanWidth = 1;
        if (loc.lastPos >= pos && loc.lastPos < lineEnd) {
            spanWidth = loc.lastPos - pos + 1;
        }
        if (spanWidth < 1) spanWidth = 1;

        appendMarker(result, lineWidth, padding, spanWidth);
        result.append('\n');
        appendLocation(result, line, column);
        return result.toString();
    }

    private void appendSourceLine(
            StringBuilder result, int line, int lineWidth, int lineStart, int lineEnd) {
        int leadingSpaces = lineWidth - Integer.toString(line).length();
        result.append(DIM)
                .append(" ".repeat(Math.max(0, leadingSpaces)))
                .append(line)
                .append(" | ")
                .append(new String(source, lineStart, lineEnd - lineStart))
                .append(RESET)
                .append('\n');
    }

    private static void appendMarker(
            StringBuilder result, int lineWidth, StringBuilder padding, int spanWidth) {
        result.append(DIM)
                .append(" ".repeat(lineWidth))
                .append(" | ")
                .append(padding)
                .append(RED)
                .append('^');
        result.append("-".repeat(Math.max(0, spanWidth - 1)));
        result.append(RESET);
    }

    private void appendLocation(StringBuilder result, int line, int column) {
        result.append(DIM)
                .append(" --> ")
                .append(filename)
                .append(':')
                .append(line)
                .append(':')
                .append(column)
                .append(RESET);
    }

    private void write(String message) {
        if (output != null) {
            output.accept(message + "\n");
            System.err.println(message);
        } else 
            System.err.println(message);
        
    }

    private static String messageForCode(int code) {
        return switch (code) {
            case 1 -> "unknown character";
            case 2 -> "invalid escape sequence";
            case 3 -> "unterminated block comment";
            case 100 -> "syntax error";
            case 101 -> "missing semicolon after statement";
            case 102 -> "missing closing parenthesis";
            case 103 -> "missing closing brace";
            case 104 -> "missing closing bracket";
            case 105 -> "invalid import statement";
            case 106 -> "import statement must be at the top level";
            case 107 -> "invalid array size or number is too large";
            case 1001 -> "variable not declared";
            case 1002 -> "numeric operator needs numeric operands";
            case 1003 -> "unary operator expects numeric operand";
            case 1004 -> "assignment target must be a variable";
            case 1005 -> "redeclaration of variable";
            case 1006 -> "type mismatch in assignment";
            case 1007 -> "cannot assign to immutable variable";
            case 1008 -> "call to undefined function";
            case 1009 -> "argument count mismatch";
            case 1010 -> "argument type mismatch";
            case 1011 -> "return type mismatch";
            case 1012 -> "import file not found";
            case 1013 -> "list literal size does not match expected size";
            case 1014 -> "indexing non-array type";
            case 1015 -> "array index must be an integer";
            case 1016 -> "semantic error";
            case 2000 -> "a character literal must contain exactly one Unicode code point";
            case 2001 -> "return value is not declared in a function";
            case 2002 -> "numeric literal is not supported in this context";
            default -> code == 0 ? "internal error" : "?error?";
        };
    }

    public int getErrorCount() {
        return errorCount;
    }

    public static class PanicException extends IllegalArgumentException {
        public PanicException(String message) {
            super(message);
        }
    }
}
