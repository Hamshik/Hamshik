package in.hamshik.Interpreter.Parser;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import in.hamshik.Interpreter.Lexer.Lexer;
import in.hamshik.Interpreter.Lexer.TokenType;
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
                    left.loc,
                    new Union.Binary(op.type(), left, right));
        }
    }

    private ASTNode parsePrefix(Token first) {
        Function<Token, ASTNode> prefixParser = prefixOperators.get(first.type());
        if (prefixParser != null)
            return prefixParser.apply(first);

        return switch (first.type()) {
            case NUMBER -> new ASTNode(null, first.initLoc(), new Union.Literals(first.val(), first.type()));
            case STRINGS -> new ASTNode( null, first.initLoc(), new Union.Literals(first.val(), first.type()));
            case CHARACTER -> new ASTNode( null, first.initLoc(), new Union.Literals(first.val(), first.type()));
            case BOOL -> new ASTNode( null, first.initLoc(), new Union.Literals(first.val(), first.type()));
            case IDENTIFIER -> new ASTNode( null, first.initLoc(), new Union.Literals(first.val(), first.type()));
            case LPAREN -> {
                ASTNode expression = parseExpr(helper.nextToken(), 1);
                helper.consumeToken(TokenType.RPAREN, "Expected ')' after the expression");
                yield expression;
            }
            default -> throw Helper.syntaxError("Expected an expression", first);
        };
    }

    private ASTNode parseUnary(Token operator) {
        ASTNode operand = parsePrefix(helper.nextToken());
        return new ASTNode(
                null,
                operator.initLoc(),
                new Union.Unary(operator.type(), operand));
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
            default -> -1;
        };
    }

    public ASTNode parseAssign(Token firstToken) {
        Token token = firstToken;
        boolean isMutable = false;
        String name = null;

        if (token.type() == TokenType.KEYWORD && token.val().equals("var")) {
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

        if (token.type() != TokenType.ASSIGN)
            throw Helper.syntaxError("Expected '=' in assignment", token);

        ASTNode lhs = new ASTNode(
                null,
                firstToken.initLoc(),
                new Union.Literals(name, TokenType.IDENTIFIER));

        Token rhsFirst = helper.nextToken();
        ASTNode rhs = parseExpr(rhsFirst, 1);
        if (!helper.consumeToken(TokenType.SEMICOLON, "Expected ';' after assignment"))
            return null;

        return new ASTNode(
                null,
                firstToken.initLoc(),
                new Union.Assign(TokenType.ASSIGN, lhs, rhs));
    }
}
