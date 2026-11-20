package in.hamshik.Interpreter.Evaluvator;

import java.util.HashMap;
import java.util.Stack;

import in.hamshik.Interpreter.shared.Union.Literal;

class VarHandler {
    final HashMap<String, Literal> varMap = new HashMap<>();

    final Evaluvator eval;

    VarHandler(Evaluvator eval) { this.eval = eval; }

    void setVar(String name, Literal val) { varMap.put(name, val); }

    Literal getVar(String name) { return varMap.get(name); }

}

public class Scope {
    final Stack<VarHandler> stack = new Stack<>();
    final Evaluvator eval;

    Scope(Evaluvator eval) {
        this.eval = eval;
        push();
    }

    void push() { stack.push(new VarHandler(eval)); }

    void pop() { stack.pop(); }

    Literal getVar(String name) {
        for (int i = stack.size() - 1; i >= 0; i--) {
            VarHandler scope = stack.get(i);
            Literal value = scope.getVar(name);
            if (value != null)
                return value;
        }
        return null;
    }

    Literal getCurrentVar(String name) { return stack.peek().getVar(name); }

    Literal getGlobalVar(String name) { return stack.getFirst().getVar(name); }

    void setVar(String name, Literal val) { stack.peek().setVar(name, val); }

    void setGlobVar(String name, Literal val) { stack.getFirst().varMap.put(name, val); }
    
}
