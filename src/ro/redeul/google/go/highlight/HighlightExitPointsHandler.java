package ro.redeul.google.go.highlight;

import com.intellij.codeInsight.highlighting.HighlightUsagesHandlerBase;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.Consumer;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteral;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralFunction;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoBuiltinCallExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.statements.GoReturnStatement;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;

import java.util.Collections;
import java.util.List;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findParentOfType;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.isNodeOfType;

public class HighlightExitPointsHandler extends HighlightUsagesHandlerBase<PsiElement> {
    private final PsiElement target;
    private final GoFunctionDeclaration function;

    private HighlightExitPointsHandler(Editor editor, PsiFile file, PsiElement target, GoFunctionDeclaration function) {
        super(editor, file);
        this.target = target;
        this.function = function;
    }

    @Override
    public List<PsiElement> getTargets() {
        return Collections.singletonList(target);
    }

    @Override
    protected void selectTargets(List<PsiElement> targets, Consumer<List<PsiElement>> selectionConsumer) {
        selectionConsumer.consume(targets);
    }

    @Override
    public void computeUsages(List<PsiElement> targets) {
        new GoRecursiveElementVisitor() {
            @Override
            public void visitFunctionLiteral(GoLiteralFunction literal) {
                // don't search exit point in closures.
            }

            @Override
            public void visitReturnStatement(GoReturnStatement statement) {
                addOccurrence(statement);
            }

            @Override
            public void visitBuiltinCallExpression(GoBuiltinCallExpression expression) {
                GoPrimaryExpression baseExpression = expression.getBaseExpression();
                if (baseExpression instanceof GoLiteralExpression) {
                    GoLiteral literal = ((GoLiteralExpression) baseExpression).getLiteral();
                    if (literal instanceof GoLiteralIdentifier && "panic".equals(literal.getText())) {
                        addOccurrence(expression);
                    }
                }
            }
        }.visitFunctionDeclaration(function);
    }

    public static HighlightExitPointsHandler createForElement(Editor editor, PsiFile file, PsiElement element) {
        element = findParentOfType(element, GoPsiElement.class);
        GoFunctionDeclaration function = findParentOfType(element, GoFunctionDeclaration.class);
        if (function == null) {
            return null;
        }

        if (element instanceof GoReturnStatement || isPanicCall(element)) {
            return new HighlightExitPointsHandler(editor, file, element, function);
        }

        return null;
    }

    private static boolean isPanicCall(PsiElement element) {
        if (!(element instanceof GoLiteralIdentifier) || !"panic".equals(element.getText())) {
            return false;
        }

        PsiElement parent = element.getParent();
        if (!(parent instanceof GoLiteralExpression) || parent.getStartOffsetInParent() != 0) {
            return false;
        }

        return isNodeOfType(parent.getParent(), GoElementTypes.BUILTIN_CALL_EXPRESSION);
    }
}
