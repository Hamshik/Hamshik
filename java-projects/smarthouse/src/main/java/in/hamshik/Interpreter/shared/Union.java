package in.hamshik.Interpreter.shared;

import java.util.ArrayList;

import in.hamshik.Helpers.VarHelpers.Type;
import in.hamshik.Interpreter.Lexer.TokenType;
import in.hamshik.Interpreter.Parser.FnParser.Params;

public interface Union {
    record RawLiteral(String raw, TokenType literalTy) {}

    record Literal(Type type, Object value) {}

    record Body(ArrayList<ASTNode> body) {
        public void add(ASTNode nodes) { body.add(nodes); }
    }

    record Binary(TokenType op, ASTNode left, ASTNode right) {}

    record Unary(TokenType op, ASTNode operand) {}

    record Assign(TokenType op, ASTNode lhs, ASTNode rhAstNode) {}

    record IfConds(ASTNode cond, Body thenBranch, Body elseBranch) {}

    record While(ASTNode cond, ASTNode body, ASTNode expr) {}

    record Fn(HIRType ret, String name, Params[] params, Body body) {}

    record HIRType(TypeKind kind, String name, boolean mutable, long size, HIRType inner) {}

    record Lists(ArrayList<ASTNode> elements) {}

    record Index(ASTNode target, ArrayList<ASTNode> expr) {}

    record FnCall(String name, ArrayList<ASTNode> args) {}

    public record Return(ASTNode expr) {}

    public record Import(String path) {}

    public enum TypeKind {
        USER_DEFINED, PRIMITIVE
    }
}