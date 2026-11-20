package in.hamshik.Interpreter.Parser;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import in.hamshik.Interpreter.Lexer.Lexer;
import in.hamshik.Interpreter.Lexer.TokenType;
import in.hamshik.Interpreter.Lexer.LexHelper.Location;
import in.hamshik.Interpreter.Lexer.LexHelper.Token;
import in.hamshik.Interpreter.shared.ASTNode;
import in.hamshik.Interpreter.shared.Union;

public class ExprParser {
    private final Parser parser;
    private final Lexer lexer;
    private final Helper helper;
    private final Map<TokenType, Function<Token, ASTNode>> prefixOperators = new HashMap<>();

    public ExprParser(Parser parser) {
        this.parser = parser;
        this.lexer = parser.lexer;
        this.helper = parser.helper;
        prefixOperators.put(TokenType.PLUS, this::parseUnary);
        prefixOperators.put(TokenType.MINUS, this::parseUnary);
        prefixOperators.put(TokenType.NOT, this::parseUnary);
        prefixOperators.put(TokenType.BIT_NOT, this::parseUnary);
        prefixOperators.put(TokenType.STAR, this::parseUnary);
        prefixOperators.put(TokenType.AMP, this::parseUnary);
    }

    public ASTNode handleExpr(Token firstTok) {
        return parseExpr(firstTok, 1);
    }

    public ASTNode parseExpr(Token first, int minPrecedence) {
        ASTNode left = parsePrefix(first);

        while (true) {
            Token op = helper.nextToken();
            int precedence = precedence(op.type());

            if (precedence < minPrecedence) {
                helper.unreadToken(op); // leave delimiters for the caller
                return left;
            }

            Token rightFirst = helper.nextToken();
            ASTNode right = parseExpr(rightFirst, precedence + 1);

            left = new ASTNode(
                    null,
                    left.loc.combineLocation(right.loc),
                    new Union.Binary(op.type(), left, right));
        }
    }

    private ASTNode parsePrefix(Token first) {
        Function<Token, ASTNode> prefixParser = prefixOperators.get(first.type());
        if (prefixParser != null)
            return prefixParser.apply(first);

        return switch (first.type()) {
            case NUMBER, STRINGS, CHARACTER, BOOL ->
                new ASTNode(null, tokenLocation(first), new Union.Literals(first.val(), first.type()));
            case IDENTIFIER -> {
                Token next = helper.nextToken();
                if (next.type() == TokenType.LPAREN) {
                    ASTNode callNode = parser.fnParser.parseCallArgs(next);
                    Union.FnCall fc = (Union.FnCall) callNode.val;
                    yield new ASTNode(null, tokenLocation(first),
                            new Union.FnCall(first.val(), fc.args()));
                }
                if (next.type() == TokenType.LBRACKET)
                    yield parseIndex(
                            new ASTNode(null, tokenLocation(first),
                                    new Union.Literals(first.val(), first.type())),
                            next);

                helper.unreadToken(next);
                yield new ASTNode(null, tokenLocation(first),
                        new Union.Literals(first.val(), first.type()));
            }
            case LPAREN -> {
                ASTNode expression = parseExpr(helper.nextToken(), 1);
                helper.consumeToken(TokenType.RPAREN, "Expected ')' after the expression");
                expression.loc = first.loc().combineLocation(lexer.loc);
                yield expression;
            }
            case LBRACKET -> {
                java.util.ArrayList<ASTNode> elements = new java.util.ArrayList<>();
                Token next = helper.nextToken();
                while (next.type() != TokenType.RBRACKET) {
                    if (next.type() == TokenType.EOF)
                        throw Helper.syntaxError("Expected ']' after the list", next);
                    elements.add(parseExpr(next, 1));
                    Token separator = helper.nextToken();
                    if (separator.type() == TokenType.RBRACKET)
                        break;
                    if (separator.type() != TokenType.COMMA)
                        throw Helper.syntaxError("Expected ',' or ']' in the list", separator);
                    next = helper.nextToken();
                    if (next.type() == TokenType.RBRACKET)
                        throw Helper.syntaxError("Trailing commas are not allowed in lists", separator);
                }
                yield new ASTNode(null, first.loc().combineLocation(lexer.loc),
                        new Union.Lists(elements));
            }
            default -> throw Helper.syntaxError("Expected an expression", first);
        };
    }

    private ASTNode parseUnary(Token operator) {
        ASTNode operand = parsePrefix(helper.nextToken());
        return new ASTNode(
                null,
                operator.loc().combineLocation(operand.loc),
                new Union.Unary(operator.type(), operand));
    }

    public ASTNode parseIndex(ASTNode target, Token firstBracket) {
        java.util.ArrayList<ASTNode> indices = new java.util.ArrayList<>();
        Token bracket = firstBracket;
        while (bracket.type() == TokenType.LBRACKET) {
            ASTNode index = parseExpr(helper.nextToken(), 1);
            helper.consumeToken(TokenType.RBRACKET, "Expected ']' after the index expression");
            indices.add(index);
            bracket = helper.nextToken();
        }
        helper.unreadToken(bracket);
        return new ASTNode(
                null,
                target.loc.combineLocation(lexer.loc),
                new Union.Index(target, indices));
    }

    private Location tokenLocation(Token token) {
        return token.loc();
    }

    private int precedence(TokenType type) {
        return switch (type) {
            case OR -> 1;
            case AND -> 2;
            case PIPE -> 3;
            case XOR -> 4;
            case AMP -> 5;
            case EQ, NEQ -> 6;
            case GT, LT, EGT, ELT -> 7;
            case PLUS, MINUS -> 8;
            case STAR, SLASH, MOD -> 9;
            case POW, XOR_POW -> 10;
            case LSHIFT, RSHIFT -> 8;
            default -> -1;
        };
    }

    public ASTNode parseAssign(Token firstToken) {
        Token token = firstToken;
        boolean isMutable = false;
        String name = null;
        boolean isAmbrigedAssginLegal = false;

        if (token.type() == TokenType.KEYWORD && token.val().equals("var")) {
            isAmbrigedAssginLegal = true;
            token = helper.nextToken();
            if (helper.isKeyword(token, "mut")) {
                isMutable = true;
                token = helper.nextToken();
            }
        }

        if (token.type() == TokenType.DATATYPE) {
            helper.parseType(token);
            Token nameToken = helper.nextToken();
            if (nameToken.type() != TokenType.IDENTIFIER)
                throw Helper.syntaxError("Expected a variable name after the type", nameToken);
            name = nameToken.val();
            token = helper.nextToken();
        } else if (token.type() == TokenType.IDENTIFIER) {
            name = token.val();
            token = helper.nextToken();
        } else {
            throw Helper.syntaxError("Expected a variable name or assignment target", token);
        }

        ASTNode lhs = new ASTNode(
                null,
                firstToken.loc(),
                new Union.Literals(name, TokenType.IDENTIFIER),
                false,
                isMutable);

        if (token.type() == TokenType.LBRACKET) {
            lhs = parseIndex(lhs, token);
            token = helper.nextToken();
        }

        if (!token.type().name().contains("ASSIGN")) {
            if (isAmbrigedAssginLegal && token.type() != TokenType.ASSIGN)
                throw Helper.syntaxError("Expected '=' in assignment", token);
            throw Helper.syntaxError("Expected assignment operators", token);
        }

        TokenType assignOp = token.type();

        Token rhsFirst = helper.nextToken();
        ASTNode rhs = parseExpr(rhsFirst, 1);
        Token terminator = helper.nextToken();
        if (terminator.type() == TokenType.SEMICOLON);
        else 
            helper.unreadToken(terminator);

        return new ASTNode(
                null,
                firstToken.loc().combineLocation(lexer.loc),
                new Union.Assign(assignOp, lhs, rhs),
                false,
                isMutable);
    }
}
