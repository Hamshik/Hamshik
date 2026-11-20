package in.hamshik.Interpreter.Evaluvator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Objects;

import in.hamshik.Helpers.VarHelpers.Type;
import in.hamshik.Interpreter.Lexer.TokenType;
import in.hamshik.Interpreter.shared.ASTNode;
import in.hamshik.Interpreter.shared.Union.Binary;
import in.hamshik.Interpreter.shared.Union.Literal;

public class BinOpHandler {
    final Evaluvator eval;

    BinOpHandler(Evaluvator eval) { this.eval = eval; }

    Literal dispatch(ASTNode expression, Binary binary) {
        Literal left = eval.eval(binary.left());
        Object leftValue = left.value();
        TokenType op = binary.op();

        if (op == TokenType.AND && !(Boolean) leftValue)
            return boolResult(expression, false);
        if (op == TokenType.OR && (Boolean) leftValue)
            return boolResult(expression, true);

        Literal right = eval.eval(binary.right());
        Object rightValue = right.value();

        return switch (op) {
            case AND -> boolResult(expression, (Boolean) leftValue && (Boolean) rightValue);
            case OR -> boolResult(expression, (Boolean) leftValue || (Boolean) rightValue);
            case EQ -> boolResult(expression, valuesEqual(leftValue, rightValue));
            case NEQ -> boolResult(expression, !valuesEqual(leftValue, rightValue));
            case GT -> boolResult(expression, compare(leftValue, rightValue) > 0);
            case LT -> boolResult(expression, compare(leftValue, rightValue) < 0);
            case EGT -> boolResult(expression, compare(leftValue, rightValue) >= 0);
            case ELT -> boolResult(expression, compare(leftValue, rightValue) <= 0);
            case PLUS -> add(expression, left, right);
            case MINUS -> arithmetic(expression, left, right, TokenType.MINUS);
            case STAR -> arithmetic(expression, left, right, TokenType.STAR);
            case SLASH -> arithmetic(expression, left, right, TokenType.SLASH);
            case MOD -> arithmetic(expression, left, right, TokenType.MOD);
            case POW, XOR_POW -> power(expression, left, right);
            case AMP -> integerResult(asInteger(leftValue).and(asInteger(rightValue)));
            case PIPE -> integerResult(asInteger(leftValue).or(asInteger(rightValue)));
            case XOR -> integerResult(asInteger(leftValue).xor(asInteger(rightValue)));
            case LSHIFT -> integerResult(
                    asInteger(leftValue).shiftLeft(shiftAmount(rightValue, expression)));
            case RSHIFT -> integerResult(
                    asInteger(leftValue).shiftRight(shiftAmount(rightValue, expression)));
            default -> throw eval.failure(expression, "Unsupported binary operator: " + op);
        };
    }

    private Literal add(ASTNode expression, Literal left, Literal right) {
        if (left.value() instanceof String || right.value() instanceof String)
            return new Literal(Type.STRING, String.valueOf(left.value()) + right.value());
        return arithmetic(expression, left, right, TokenType.PLUS);
    }

    private Literal arithmetic(
            ASTNode expression, Literal left, Literal right, TokenType op) {
        Number leftNumber = (Number) left.value();
        Number rightNumber = (Number) right.value();
        boolean floating = left.type() == Type.FLOAT || right.type() == Type.FLOAT
                || leftNumber instanceof BigDecimal || rightNumber instanceof BigDecimal;

        if (floating) {
            BigDecimal lhs = decimalValue(leftNumber);
            BigDecimal rhs = decimalValue(rightNumber);
            BigDecimal result = switch (op) {
                case PLUS -> lhs.add(rhs);
                case MINUS -> lhs.subtract(rhs);
                case STAR -> lhs.multiply(rhs);
                case SLASH -> {
                    if (rhs.signum() == 0)
                        throw eval.failure(expression, "Division by zero");
                    yield lhs.divide(rhs, MathContext.DECIMAL128);
                }
                case MOD -> {
                    if (rhs.signum() == 0)
                        throw eval.failure(expression, "Modulo by zero");
                    yield lhs.remainder(rhs);
                }
                default -> throw eval.failure(expression, "Unsupported arithmetic operator: " + op);
            };
            return new Literal(Type.FLOAT, result);
        }

        BigInteger lhs = integerValue(leftNumber);
        BigInteger rhs = integerValue(rightNumber);
        BigInteger result = switch (op) {
            case PLUS -> lhs.add(rhs);
            case MINUS -> lhs.subtract(rhs);
            case STAR -> lhs.multiply(rhs);
            case SLASH -> {
                if (rhs.signum() == 0)
                    throw eval.failure(expression, "Division by zero");
                yield lhs.divide(rhs);
            }
            case MOD -> {
                if (rhs.signum() == 0)
                    throw eval.failure(expression, "Modulo by zero");
                yield lhs.remainder(rhs);
            }
            default -> throw eval.failure(expression, "Unsupported arithmetic operator: " + op);
        };
        return integerResult(result);
    }

    private Literal power(ASTNode expression, Literal left, Literal right) {
        Number base = (Number) left.value();
        Number exponent = (Number) right.value();
        BigInteger integralExponent = integerValue(exponent);

        int power = integralExponent.intValue();

        if (left.type() != Type.FLOAT && right.type() != Type.FLOAT
                && !(base instanceof BigDecimal) && !(exponent instanceof BigDecimal)
                && power >= 0) {
            return integerResult(integerValue(base).pow(power));
        }

        if (power >= 0)
            return new Literal(Type.FLOAT, decimalValue(base).pow(power, MathContext.DECIMAL128));

        if (power == Integer.MIN_VALUE)
            throw eval.failure(expression, "Exponent is outside the supported range");
        BigDecimal denominator = decimalValue(base).pow(-power, MathContext.DECIMAL128);
        if (denominator.signum() == 0)
            throw eval.failure(expression, "Division by zero");
        return new Literal(Type.FLOAT,
                BigDecimal.ONE.divide(denominator, MathContext.DECIMAL128));
    }

    private boolean valuesEqual(Object left, Object right) {
        if (left instanceof Number leftNumber && right instanceof Number rightNumber)
            return decimalValue(leftNumber).compareTo(decimalValue(rightNumber)) == 0;
        return Objects.equals(left, right);
    }

    private int compare(Object left, Object right) {
        if (left instanceof Number leftNumber && right instanceof Number rightNumber)
            return decimalValue(leftNumber).compareTo(decimalValue(rightNumber));
        if (left instanceof String leftString && right instanceof String rightString)
            return leftString.compareTo(rightString);
        throw new ClassCastException("Comparison operands must have a compatible type");
    }

    private BigInteger asInteger(Object value) {
        return integerValue((Number) value);
    }

    private BigInteger integerValue(Number value) {
        if (value instanceof BigInteger integer)
            return integer;
        if (value instanceof BigDecimal decimal)
            return decimal.toBigIntegerExact();
        return BigInteger.valueOf(value.longValue());
    }

    private BigDecimal decimalValue(Number value) {
        if (value instanceof BigDecimal decimal)
            return decimal;
        if (value instanceof BigInteger integer)
            return new BigDecimal(integer);
        return new BigDecimal(value.toString());
    }

    private int shiftAmount(Object value, ASTNode expression) {
        BigInteger amount = asInteger(value);
        try {
            return amount.intValueExact();
        } catch (ArithmeticException e) {
            throw eval.failure(expression, "Shift amount is outside the supported range");
        }
    }

    private Literal boolResult(ASTNode expression, boolean value) {
        return new Literal(Type.BOOL, value);
    }

    private Literal integerResult(BigInteger value) {
        return new Literal(Type.INT, value);
    }

}
