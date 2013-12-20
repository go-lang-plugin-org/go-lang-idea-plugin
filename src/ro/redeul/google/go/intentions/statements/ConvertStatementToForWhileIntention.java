package ro.redeul.google.go.intentions.statements;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.intentions.Intention;
import ro.redeul.google.go.intentions.IntentionExecutionException;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.binary.GoLogicalAndExpression;
import ro.redeul.google.go.lang.psi.expressions.binary.GoLogicalOrExpression;
import ro.redeul.google.go.lang.psi.expressions.binary.GoRelationalExpression;
import ro.redeul.google.go.lang.psi.statements.GoExpressionStatement;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypePsiBacked;
import ro.redeul.google.go.lang.psi.utils.GoTypeUtils;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findParentOfType;
import static ro.redeul.google.go.util.EditorUtil.reformatPositions;

public class ConvertStatementToForWhileIntention extends Intention {

    private GoExpressionStatement statement;
    private GoExpr expr;

    @Override
    protected boolean satisfiedBy(PsiElement element) {
        statement = element instanceof GoExpressionStatement ? (GoExpressionStatement) element : findParentOfType(element, GoExpressionStatement.class);
        if (statement != null) {
            expr = statement.getExpression();
            if (expr instanceof GoRelationalExpression
                    || expr instanceof GoLogicalAndExpression
                    || expr instanceof GoLogicalOrExpression)
                return true;

            for (GoType goType : expr.getType()) {
                if (goType != null) {
                    if (goType instanceof GoTypePsiBacked) {
                        GoPsiType psiType = GoTypeUtils.resolveToFinalType(((GoTypePsiBacked) goType).getPsiType());
                        if (psiType instanceof GoPsiTypeName)
                            return psiType.getText().equals("bool") && ((GoPsiTypeName) psiType).isPrimitive();
                    }
                }
            }

        }
        return false;
    }

    @Override
    protected void processIntention(@NotNull PsiElement element, Editor editor)
            throws IntentionExecutionException {
        Document document = editor.getDocument();
        TextRange textRange = statement.getTextRange();
        RangeMarker range = document.createRangeMarker(textRange.getStartOffset(), textRange.getEndOffset());
        document.deleteString(textRange.getStartOffset(), textRange.getEndOffset());

        document.insertString(textRange.getStartOffset(), "for " + expr.getText() + "{" + "}");
        if (editor != null) editor.getCaretModel().moveToOffset(textRange.getStartOffset() + 5 + expr.getTextLength());
        reformatPositions(statement.getContainingFile(), range);
    }

}
