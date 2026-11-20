package in.hamshik.Interpreter.Evaluvator;

import java.math.BigDecimal;
import java.math.BigInteger;

import in.hamshik.Helpers.VarHelpers.Type;
import in.hamshik.Interpreter.shared.ASTNode;
import in.hamshik.Interpreter.shared.Union.*;

public final class LiteralHandler {
    final Evaluvator eval;

    LiteralHandler(Evaluvator eval) { this.eval = eval; }

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
                if(node == null)
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
}
