package ro.redeul.google.go.formatter.builder;

import com.intellij.formatting.Block;
import ro.redeul.google.go.formatter.blocks.Code;
import ro.redeul.google.go.formatter.blocks.GoBlockUtil;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralFunction;

/**
 * <p/>
 * Created on Jan-14-2014 23:29
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
abstract class Literals extends Types {

    @Override
    public Block visitLiteralFunction(GoLiteralFunction literal, State state) {
        return new Code<GoLiteralFunction>(literal, state.settings)
            .withCustomSpacing(GoBlockUtil.CustomSpacings.LITERAL_FUNCTION);
    }
}
