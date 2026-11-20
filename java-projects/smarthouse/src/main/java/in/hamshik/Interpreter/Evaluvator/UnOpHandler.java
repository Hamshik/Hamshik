package in.hamshik.Interpreter.Evaluvator;

import java.math.BigDecimal;
import java.math.BigInteger;

import in.hamshik.Helpers.VarHelpers.Type;
import in.hamshik.Interpreter.shared.ASTNode;
import in.hamshik.Interpreter.shared.Union.Literal;
import in.hamshik.Interpreter.shared.Union.Unary;

public class UnOpHandler {
    final Evaluvator eval;

    UnOpHandler(Evaluvator eval) { this.eval = eval; }

    Literal dispatch(ASTNode expression, Unary unary) {
        Literal operand = eval.eval(unary.operand());
        Object value = operand.value();

        return switch (unary.op()) {
            case PLUS -> operand;
            case MINUS -> new Literal(operand.type(), negate((Number) value));
            case NOT -> new Literal(Type.BOOL, !(Boolean) value);
            case BIT_NOT -> new Literal(Type.INT, ((BigInteger) value).not());
            case AMP, STAR -> throw eval.failure(expression,
                    "Address-of and dereference operators require pointer support");
            default -> throw eval.failure(expression,
                    "Unsupported unary operator: " + unary.op());
        };
    }

    private Number negate(Number value) {
        if (value instanceof BigInteger integer)
            return integer.negate();
        if (value instanceof BigDecimal decimal)
            return decimal.negate();
        if (value instanceof Double || value instanceof Float)
            return -value.doubleValue();
        return -value.longValue();
    }

}
