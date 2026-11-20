package in.hamshik.Interpreter.Evaluvator;

import java.util.ArrayList;
import java.util.function.Consumer;

import in.hamshik.Interpreter.Parser.Helper;
import in.hamshik.Interpreter.shared.ASTNode;
import in.hamshik.Interpreter.shared.Error;
import in.hamshik.Interpreter.shared.Error.ErrorCode;
import in.hamshik.Interpreter.shared.Union.*;

public class Evaluvator {
    final Error errOut;
    final Consumer<String> out;
    final Scope scope = new Scope(this);
    final FnHandler fnHandler = new FnHandler(this);
    final LiteralHandler literalHandler = new LiteralHandler(this);
    final BinOpHandler binOpHandler = new BinOpHandler(this);
    final UnOpHandler unOpHandler = new UnOpHandler(this);

    public Evaluvator(Error err, Consumer<String> out) {
        this.errOut = err;
        this.out = out;
    }

    public void runProgram(ASTNode root) {
        var fn = fnHandler.getFn("setup");
        if (fn == null)
            errOut.panic(null, "Plese define the setup funtion to run an program");
        fnHandler.call("setup", new ArrayList<>());
        if (fnHandler.getFn("loop") != null)
            while (true)
                fnHandler.call("loop", new ArrayList<>());
    }

    public Literal eval(ASTNode node) {
        if (node == null)
            return null;

        return switch (node.val) {
            case RawLiteral l -> literalHandler.dispatch(node, l);
            case Binary binOp -> binOpHandler.dispatch(node, binOp);
            case Unary unaryOp -> unOpHandler.dispatch(node, unaryOp);
            case FnCall fnCall -> fnHandler.call(fnCall);
            default -> null;
        };
    }

    public RuntimeException failure(ASTNode expression, String detail) {
        errOut.panic(expression.loc, ErrorCode.SEM_OTHER_ERROR, detail);
        return null;
    }

    public void pullAllGlobVarAndFn(Helper helper) {
        fnHandler.fnMap.putAll(helper.fnMaps);
        helper.globalVars.forEach((name, raw) -> {
            scope.stack.getFirst().varMap.put(name, literalHandler.dispatch(null, raw));
        });
    }
}
