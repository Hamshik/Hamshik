package in.hamshik.Interpreter.shared;

import java.util.ArrayList;

import in.hamshik.Interpreter.Lexer.TokenType;

public interface Union {}

record Literals(String raw) implements Union{}
record Binary(TokenType op, ASTNode left, ASTNode right) implements Union {}
record Sequence(ArrayList<ASTNode> blocks) implements Union {}
record Assign(TokenType op, ASTNode lhs, ASTNode rhAstNode) implements Union{}
record IfConds(ASTNode cond, ASTNode thenBranch, ASTNode elseBranch) implements Union {}
record While(ASTNode cond, ASTNode body, ASTNode expr) implements Union {}
record Fn(String name, String[] params) implements Union {}
record Lists(ArrayList<ASTNode> elements) implements Union {}
record Index(ArrayList<ASTNode> expr) implements Union {}