package ro.redeul.google.go.intentions.statements;

import com.intellij.codeInsight.template.impl.TemplateImpl;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.editor.TemplateUtil;
import ro.redeul.google.go.intentions.Intention;
import ro.redeul.google.go.intentions.IntentionExecutionException;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.statements.GoExpressionStatement;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeConstant;
import ro.redeul.google.go.lang.psi.typing.GoTypePrimitive;
import ro.redeul.google.go.lang.psi.typing.GoTypes;
import ro.redeul.google.go.lang.psi.typing.TypeVisitor;

import java.util.ArrayList;

public class ConvertStatementToIfIntention extends Intention {

    @Override
    protected boolean satisfiedBy(PsiElement element) {
        GoExpressionStatement statement = getParentAs(element, GoExpressionStatement.class);
        if (statement == null) return false;

        GoExpr expr = statement.getExpression();
        if (expr == null) return false;

        for (GoType goType : expr.getType()) {
            if (goType == null) continue;

            return goType.underlyingType().accept(new TypeVisitor<Boolean>(false) {
                @Override
                public Boolean visitPrimitive(GoTypePrimitive type) {
                    return type.getType() == GoTypes.Builtin.Bool;
                }

                @Override
                public Boolean visitConstant(GoTypeConstant constant) {
                    return constant.kind() == GoTypeConstant.Kind.Boolean;
                }
            });
        }

        return false;
    }

    protected String getKeyword() {
        return "if";
    }

    @Override
    protected void processIntention(@NotNull PsiElement element, Editor editor)
            throws IntentionExecutionException {

        GoExpressionStatement statement = getParentAs(element, GoExpressionStatement.class);
        if ( statement == null )
            return;

        GoExpr expr = statement.getExpression();
        if ( expr == null )
            return;

        TextRange textRange = statement.getTextRange();

        TemplateImpl template;

        template = TemplateUtil.createTemplate(String.format("%s %s {$END$}", getKeyword(), expr.getText()));

        TemplateUtil.runTemplate(editor, textRange, new ArrayList<String>(), template);
    }
}
