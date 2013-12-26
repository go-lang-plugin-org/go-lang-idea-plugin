package ro.redeul.google.go.intentions.statements;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.intentions.Intention;
import ro.redeul.google.go.intentions.IntentionExecutionException;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.statements.GoExpressionStatement;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeArray;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeMap;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeSlice;
import ro.redeul.google.go.lang.psi.typing.*;
import ro.redeul.google.go.lang.psi.utils.GoTypeUtils;
import ro.redeul.google.go.util.GoUtil;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findParentOfType;
import static ro.redeul.google.go.util.EditorUtil.reformatPositions;

public class ConvertStatementToForRangeIntention extends Intention {

    private GoExpressionStatement statement;
    private GoExpr expr;

    @Override
    protected boolean satisfiedBy(PsiElement element) {
        statement = element instanceof GoExpressionStatement ? (GoExpressionStatement) element : findParentOfType(element, GoExpressionStatement.class);
        if (statement == null && element instanceof PsiWhiteSpace && element.getPrevSibling() instanceof GoExpressionStatement) {
            statement = (GoExpressionStatement) element.getPrevSibling();
        }
        if (statement != null) {
            expr = statement.getExpression();
            if (expr != null) {
                GoType[] types = expr.getType();

                for (GoType goType : types) {
                    if (goType != null) {
                        if (goType instanceof GoTypePsiBacked) {
                            GoPsiType psiType = ((GoTypePsiBacked) goType).getPsiType();
                            psiType = GoTypeUtils.resolveToFinalType(psiType);
                            return psiType instanceof GoPsiTypeMap || psiType instanceof GoPsiTypeSlice || psiType instanceof GoPsiTypeArray;
                        }
                        return goType instanceof GoTypeMap || goType instanceof GoTypeSlice || goType instanceof GoTypeArray;
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

        StringBuilder iFString = new StringBuilder();
        String k = "k";
        String v = "v";
        int i = 0;
        while (GoUtil.TestDeclVar(expr, k)) {
            k = String.format("k%d", i);
            i++;
        }

        i = 0;
        while (GoUtil.TestDeclVar(expr, v)) {
            v = String.format("v%d", i);
            i++;
        }

        iFString.append("for ")
                .append(k)
                .append(",")
                .append(v)
                .append(":= range ")
                .append(expr.getText())
                .append("{");

        document.insertString(textRange.getStartOffset(), iFString.toString() + "}");

        if (editor != null)
            editor.getCaretModel().moveToOffset(textRange.getStartOffset() + 15 + v.length() + k.length() + expr.getTextLength());
        reformatPositions(statement.getContainingFile(), range);
    }

}
