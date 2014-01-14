package ro.redeul.google.go.formatter.builder;

import com.intellij.formatting.Block;
import com.intellij.psi.tree.TokenSet;
import ro.redeul.google.go.formatter.blocks.Code;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.GoPsiElementList;

import static ro.redeul.google.go.formatter.blocks.GoBlockUtil.CustomSpacings;

/**
 * TODO: Document this
 * <p/>
 * Created on Jan-13-2014 22:58
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
abstract class Others extends Expressions {

    @Override
    public Block visitElementList(GoPsiElementList<? extends GoPsiElement> list, State s) {
        return new Code<GoPsiElement>(list, s.settings)
            .setIndentedChildTokens(TokenSet.create(
                GoElementTypes.FUNCTION_PARAMETER,
                GoElementTypes.FUNCTION_PARAMETER_VARIADIC))
            .setCustomSpacing(CustomSpacings.LISTS);
    }
}
