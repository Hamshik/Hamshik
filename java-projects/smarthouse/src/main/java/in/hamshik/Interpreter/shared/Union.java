package in.hamshik.Interpreter.shared;

import java.util.ArrayList;

import in.hamshik.Helpers.VarHelpers.Type;
import in.hamshik.Interpreter.Lexer.TokenType;
import in.hamshik.Interpreter.Parser.FnParser.Params;

public interface Union {
    record Literals(String raw) implements Union{}
    record Body(ArrayList<ASTNode> body) implements Union {}
    record Binary(TokenType op, ASTNode left, ASTNode right) implements Union {}
    record Assign(TokenType op, ASTNode lhs, ASTNode rhAstNode) implements Union{}
    record IfConds(ASTNode cond, Body thenBranch, Body elseBranch) implements Union {}
    record While(ASTNode cond, ASTNode body, ASTNode expr) implements Union {}
    record Fn(Type ret, String name, Params[] params, Body body) implements Union {}
    record Lists(ArrayList<ASTNode> elements) implements Union {}
    record Index(ArrayList<ASTNode> expr) implements Union {}

}