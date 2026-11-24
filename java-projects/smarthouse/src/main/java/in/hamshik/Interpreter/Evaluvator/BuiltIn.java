package in.hamshik.Interpreter.Evaluvator;

import java.util.ArrayList;
import java.util.function.Function;

import in.hamshik.Interpreter.shared.Union.Literal;

class Println implements Function<ArrayList<Literal>, Literal> {
    Evaluvator eval;
    Println(Evaluvator eval) {
        this.eval = eval;
    }
    @Override
    public Literal apply(ArrayList<Literal> args) {
        for(Literal arg : args)
            eval.out.accept(String.valueOf(arg.value()));
        eval.out.accept("\n");
        return null;
    }
}

class Print implements Function<ArrayList<Literal>, Literal> {
    Evaluvator eval;
    Print(Evaluvator eval) {
        this.eval = eval;
    }
    @Override
    public Literal apply(ArrayList<Literal> args) {
        for(Literal arg : args)
            eval.out.accept(String.valueOf(arg.value()));
        return null;
    }
}
