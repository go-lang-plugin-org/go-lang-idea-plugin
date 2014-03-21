package ro.redeul.google.go.intentions.statements;

import com.intellij.codeInsight.template.impl.TemplateImpl;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiWhiteSpace;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.editor.TemplateUtil;
import ro.redeul.google.go.intentions.Intention;
import ro.redeul.google.go.intentions.IntentionExecutionException;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.statements.GoExpressionStatement;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeArray;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeMap;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeSlice;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeArray;
import ro.redeul.google.go.lang.psi.typing.GoTypePsiBacked;
import ro.redeul.google.go.lang.psi.utils.GoTypeUtils;
import ro.redeul.google.go.util.GoUtil;

import java.util.ArrayList;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findParentOfType;

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
                            if (psiType instanceof GoPsiTypeMap || psiType instanceof GoPsiTypeSlice || psiType instanceof GoPsiTypeArray)
                                return true;
                            break;
                        }
                        if (goType instanceof GoTypeArray)
                            return true;
                    }
                }

                if (expr instanceof GoLiteralExpression) {
                    for (PsiReference identifier : ((GoLiteralExpression) expr).getLiteral().getReferences()) {
                        if (identifier != null) {
                            PsiElement resolve = identifier.resolve();
                            if (resolve != null) {
                                resolve = resolve.getParent();
                                if (resolve instanceof GoFunctionParameter) {
                                    return ((GoFunctionParameter) resolve).isVariadic();
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected void processIntention(@NotNull PsiElement element, Editor editor)
            throws IntentionExecutionException {


        TextRange textRange = statement.getTextRange();
        ArrayList<String> arguments = new ArrayList<String>();


        StringBuilder templateString = new StringBuilder();

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


        arguments.add(k);
        arguments.add(v);

        TemplateImpl template = TemplateUtil.createTemplate(String.format("for $v0$,$v1$ := range %s{$END$}", expr.getText()));
        TemplateUtil.runTemplate(editor, textRange, arguments, template);

    }

}
