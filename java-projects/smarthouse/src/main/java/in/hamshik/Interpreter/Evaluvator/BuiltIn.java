package in.hamshik.Interpreter.Evaluvator;

import java.util.ArrayList;
import java.util.function.Function;

import in.hamshik.Helpers.VarHelpers.Type;
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

class Lenght implements Function<ArrayList<Literal>, Literal> {
    Evaluvator eval;
    Lenght(Evaluvator eval) {
        this.eval = eval;
    }
    @Override
    public Literal apply(ArrayList<Literal> args) {
        if(args.size() != 1)
            throw new RuntimeException("lenght() takes exactly one argument");
        var arg = args.get(0);
        if(arg.type() == Type.LISTS) {
            var list = (ArrayList<?>) arg.value();
            return new Literal(Type.INT, list.size());
        }
        throw new RuntimeException("lenght() argument must be a list");
    }
}
