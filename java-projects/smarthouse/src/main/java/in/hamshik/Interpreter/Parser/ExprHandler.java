package in.hamshik.Interpreter.Parser;

import in.hamshik.Interpreter.Lexer.Lexer.Token;

/**
 * ExprHandler
 */
public class ExprHandler extends Parser {

    public ExprHandler(Parser parser) throws Exception{
        super(parser.path, parser.out, false);
    }
    public Object handleExpr(Token next) {
        return null;
    }

}
