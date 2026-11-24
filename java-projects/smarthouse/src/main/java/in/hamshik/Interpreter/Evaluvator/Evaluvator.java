package in.hamshik.Interpreter.Evaluvator;

import java.util.ArrayList;
import java.util.function.Consumer;

import in.hamshik.Interpreter.shared.ASTNode;
import in.hamshik.Interpreter.shared.Error;
import in.hamshik.Interpreter.shared.Error.ErrorCode;
import in.hamshik.Interpreter.shared.Union.*;

public class Evaluvator {
    final Error errOut;
    final Consumer<String> out;
    final Scope scope = new Scope(this);
    final FnHandler fnHandler = new FnHandler(this);
    final Helper helper = new Helper(this);
    final BinOpHandler binOpHandler = new BinOpHandler(this);
    final UnOpHandler unOpHandler = new UnOpHandler(this);

    public Evaluvator(Error err, Consumer<String> out) {
        this.errOut = err;
        this.out = out;
    }

    public void runProgram(ASTNode root) {
        if (stopRequested())
            return;

        var fn = fnHandler.getFn("setup");
        if (fn == null)
            errOut.panic(null, "Plese define the setup funtion to run an program");
        fnHandler.call("setup", new ArrayList<>());
        if (fnHandler.getFn("loop") != null)
            while (!stopRequested())
                fnHandler.call("loop", new ArrayList<>());
    }

    public Literal eval(ASTNode node) {
        if (node == null)
            return null;

        return switch (node.val) {
            case RawLiteral l -> helper.dispatch(node, l);
            case Binary binOp -> binOpHandler.dispatch(node, binOp);
            case Unary unaryOp -> unOpHandler.dispatch(node, unaryOp);
            case FnCall fnCall -> fnHandler.call(fnCall);
            case Body body -> evalBody(body);
            case IfConds ifStmt -> helper.handleIfCond(ifStmt);
            case While whileNode -> helper.handleWhile(whileNode);
            case Index idx -> helper.handleIndex(idx);
            case Lists lists -> helper.handleLists(lists);
            default -> null;
        };
    }

    public RuntimeException failure(ASTNode expression, String detail) {
        errOut.panic(expression.loc, ErrorCode.SEM_OTHER_ERROR, detail);
        return null;
    }

    public void pullAllGlobVarAndFn(in.hamshik.Interpreter.Parser.Helper _helper) {
        fnHandler.fnMap.putAll(_helper.fnMaps);
        _helper.globalVars.forEach((name, raw) -> {
            scope.stack.getFirst().varMap.put(name, helper.dispatch(null, raw));
        });
    }

    Literal evalBody(Body body) {
        for (ASTNode statement : body.body()) {
            if (stopRequested())
                return null;

            if (statement.val instanceof Return ret)
                return eval(ret.expr());

            eval(statement);
        }
        return null; // function reached the end without returning
    }

    private boolean stopRequested() {
        return Thread.currentThread().isInterrupted();
    }
}
