package ro.redeul.google.go.lang.psi.statements;

import ro.redeul.google.go.lang.psi.expressions.GoExpr;

/**
 * IncDec statement
 * <p/>
 * Created on Jan-04-2014 00:16
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public interface GoIncDecStatement extends GoStatement {

    enum Op {
        Null,
        Inc, Dec
    }

    GoExpr getExpression();

    Op getOperator();

}
