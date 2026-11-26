package in.hamshik.Interpreter.Evaluvator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

import in.hamshik.Interpreter.shared.ASTNode;
import in.hamshik.Interpreter.shared.Union;
import in.hamshik.Interpreter.shared.Union.Literal;

public class FnHandler {
    final HashMap<String, Union.Fn> fnMap = new HashMap<>();
    final HashMap<String, Function<ArrayList<Literal>, Literal>> builtInFnMap = new HashMap<>();
    final Evaluvator eval;
    final Scope scope;

    FnHandler(Evaluvator eval) {
        this.eval = eval;
        this.scope = eval.scope;
        builtInFnMap.put("printf", new Print(eval));
        builtInFnMap.put("println", new Println(eval));
        builtInFnMap.put("len", new Lenght(eval));
    }

    void setFn(String name, Union.Fn fnBody) { fnMap.put(name, fnBody); }

    Union.Fn getFn(String name) {
        return fnMap.get(name);
    }

    Literal call(String name, ArrayList<ASTNode> args) {
        ArrayList<Literal> values = new ArrayList<>(args.size());
        for (ASTNode arg : args)
            values.add(eval.eval(arg));

        var builtIn = builtInFnMap.get(name);
        if (builtIn != null)
            return builtIn.apply(values);

        var fn = getFn(name);
        scope.push();
        try {
            for (var i = 0; i < values.size(); i++)
                scope.setVar(fn.params()[i].name(), values.get(i));

            return eval.evalBody(fn.body());
        } finally {
            scope.pop();
        }
    }

    Literal call(Union.FnCall call) { return call(call.name(), call.args()); }
}
