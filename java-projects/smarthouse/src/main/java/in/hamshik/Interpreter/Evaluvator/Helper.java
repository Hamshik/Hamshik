package in.hamshik.Interpreter.Evaluvator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import in.hamshik.Helpers.VarHelpers.Type;
import in.hamshik.Interpreter.shared.ASTNode;
import in.hamshik.Interpreter.shared.Union.*;

public final class Helper {
    final Evaluvator eval;

    Helper(Evaluvator eval) { this.eval = eval; }

    public Literal dispatch(ASTNode node, RawLiteral l) {
        var raw = l.raw();
        return switch (l.literalTy()) {
            case NUMBER -> new Literal(raw.contains(".") ? Type.FLOAT : Type.INT,
                    raw.contains(".") ? new BigDecimal(raw) : new BigInteger(raw));

            case STRINGS -> new Literal(Type.STRING, raw);
            case CHARACTER -> new Literal(Type.CHAR, raw.charAt(0));
            case BOOL -> new Literal(Type.BOOL, l.raw().charAt(0) == 't');

            case IDENTIFIER -> {
                Literal safeVal = null;
                if (node == null)
                    yield eval.scope.getVar(raw);

                if (node.isglobal)
                    safeVal = eval.scope.getGlobalVar(raw);
                else
                    safeVal = eval.scope.getVar(raw);

                yield safeVal;
            }
            default -> null;
        };
    }

    public Literal handleIfCond(IfConds ifConds) {
        Literal cond = eval.eval(ifConds.cond());
        if ((boolean) cond.value())
            return eval.evalBody(ifConds.thenBranch());
        return ifConds.elseBranch() == null ? null : eval.evalBody(ifConds.elseBranch());
    }

    public Literal handleWhile(While whileNode) {
        for (Literal cond = eval.eval(whileNode.cond()); (boolean) cond.value(); cond = eval.eval(whileNode.cond()))
            eval.evalBody(whileNode.body());
        return null;
    }

    public Literal handleLists(Lists lists) {
        ArrayList<Literal> elements = new ArrayList<>(lists.elements().size());
        for (ASTNode element : lists.elements())
            elements.add(eval.eval(element));
        return new Literal(Type.ANY, elements);
    }

    public Literal handleIndex(Index idx) {
        Literal target = eval.eval(idx.target());
        if (target == null)
            throw eval.failure(idx.target(), "Index target is not defined");

        Object container = target.value();
        Literal result = target;

        for (ASTNode expression : idx.expr()) {
            Literal indexLiteral = eval.eval(expression);
            if (indexLiteral == null)
                throw eval.failure(expression, "Index expression is not defined");

            int index = indexValue(expression, indexLiteral.value());
            if (index < 0)
                throw eval.failure(expression, "Index cannot be negative");

            result = indexedValue(expression, container, index);
            container = result.value();
        }

        return result;
    }

    private int indexValue(ASTNode expression, Object value) {
        BigInteger integer;
        if (value instanceof BigInteger bigInteger) {
            integer = bigInteger;
        } else if (value instanceof BigDecimal decimal) {
            try {
                integer = decimal.toBigIntegerExact();
            } catch (ArithmeticException e) {
                throw eval.failure(expression, "Index must be an integer");
            }
        } else if (value instanceof Number number) {
            integer = BigInteger.valueOf(number.longValue());
        } else {
            throw eval.failure(expression, "Index must be an integer");
        }

        try {
            return integer.intValueExact();
        } catch (ArithmeticException e) {
            throw eval.failure(expression, "Index is outside the supported range");
        }
    }

    private Literal indexedValue(ASTNode expression, Object container, int index) {
        if (container instanceof List<?> list) {
            if (index >= list.size())
                throw eval.failure(expression, "Index is out of bounds");
            Object value = list.get(index);
            return value instanceof Literal literal
                    ? literal
                    : new Literal(Type.ANY, value);
        }

        if (container != null && container.getClass().isArray()) {
            int size = Array.getLength(container);
            if (index >= size)
                throw eval.failure(expression, "Index is out of bounds");
            return new Literal(Type.ANY, Array.get(container, index));
        }

        if (container instanceof String string) {
            if (index >= string.length())
                throw eval.failure(expression, "Index is out of bounds");
            return new Literal(Type.CHAR, string.charAt(index));
        }

        throw eval.failure(expression, "Value is not indexable");
    }
}
