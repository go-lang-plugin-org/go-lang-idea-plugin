package ro.redeul.google.go.intentions.statements;

import com.intellij.codeInsight.template.impl.TemplateImpl;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.editor.TemplateUtil;
import ro.redeul.google.go.intentions.Intention;
import ro.redeul.google.go.intentions.IntentionExecutionException;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.statements.GoExpressionStatement;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeArray;
import ro.redeul.google.go.lang.psi.typing.GoTypeMap;
import ro.redeul.google.go.lang.psi.typing.GoTypeSlice;
import ro.redeul.google.go.lang.psi.typing.TypeVisitor;
import ro.redeul.google.go.util.GoUtil;

import java.util.ArrayList;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findParentOfType;

public class ConvertStatementToForRangeIntention extends Intention {

    private GoExpressionStatement statement;
    private GoExpr expr;

    @Override
    protected boolean satisfiedBy(PsiElement element) {
        PsiElement node = element;

        if (node == null)
            return false;

        if (node instanceof PsiWhiteSpace)
            node = node.getPrevSibling();

        statement = findParentOfType(node, GoExpressionStatement.class);
        if (statement == null) return false;

        expr = statement.getExpression();
        if (expr == null) return false;

        GoType[] types = expr.getType();

        for (GoType goType : types) {
            if (goType == null) continue;

            return goType.underlyingType().accept(new TypeVisitor<Boolean>(false) {
                @Override
                public Boolean visitArray(GoTypeArray type) {
                    return true;
                }

                @Override
                public Boolean visitSlice(GoTypeSlice type) {
                    return true;
                }

                @Override
                public Boolean visitMap(GoTypeMap type) {
                    return true;
                }
            });
        }

        return false;
    }

    @Override
    protected void processIntention(@NotNull PsiElement element, Editor editor)
            throws IntentionExecutionException {


        TextRange textRange = statement.getTextRange();
        ArrayList<String> arguments = new ArrayList<String>();

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
