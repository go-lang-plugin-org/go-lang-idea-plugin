package ro.redeul.google.go.editor.selection;

import com.intellij.codeInsight.editorActions.ExtendWordSelectionHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;

import java.util.ArrayList;
import java.util.List;

/**
 * WordSelection extension to handle selection in call expressions better.
 *
 * <p/>
 * Created on Jan-12-2014 15:58
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public class SelectArgumentListInCallExpressions implements ExtendWordSelectionHandler {

    @Override
    public boolean canSelect(PsiElement e) {
        return e instanceof GoExpr && e.getParent() != null && e.getParent() instanceof GoCallOrConvExpression;
    }

    @Override
    public List<TextRange> select(PsiElement e, CharSequence editorText, int cursorOffset, Editor editor) {
        GoCallOrConvExpression parent = (GoCallOrConvExpression) e.getParent();
        GoExpr []args = parent.getArguments();
        if ( args.length <= 1)
            return null;

        List<TextRange> result = new ArrayList<TextRange>();

        GoExpr firstArg = args[0];
        GoExpr lastArg = args[args.length - 1];

        result.add(
            new TextRange(
                firstArg.getTextOffset(),
                lastArg.getTextOffset() + lastArg.getTextLength()
            )
        );

        return result;
    }
}
